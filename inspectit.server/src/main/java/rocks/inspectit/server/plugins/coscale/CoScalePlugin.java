package rocks.inspectit.server.plugins.coscale;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.coscale.sdk.client.ApiFactory;
import com.coscale.sdk.client.Credentials;
import com.coscale.sdk.client.commons.Options.Builder;
import com.coscale.sdk.client.data.DataApi;
import com.coscale.sdk.client.data.DataInsert;
import com.coscale.sdk.client.events.Event;
import com.coscale.sdk.client.events.EventDataInsert;
import com.coscale.sdk.client.events.EventsApi;
import com.coscale.sdk.client.metrics.DataType;
import com.coscale.sdk.client.metrics.Metric;
import com.coscale.sdk.client.metrics.MetricGroup;
import com.coscale.sdk.client.metrics.MetricGroupInsert;
import com.coscale.sdk.client.metrics.MetricInsert;
import com.coscale.sdk.client.metrics.MetricsApi;
import com.coscale.sdk.client.metrics.SubjectType;
import com.coscale.sdk.client.requests.RequestsApi;
import com.coscale.sdk.client.servers.ServersApi;

import rocks.inspectit.server.plugins.AbstractPlugin;
import rocks.inspectit.server.plugins.coscale.util.BTMetrics;
import rocks.inspectit.server.storage.CmrStorageManager;
import rocks.inspectit.shared.all.cmr.property.spring.PropertyUpdate;
import rocks.inspectit.shared.all.communication.DefaultData;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;
import rocks.inspectit.shared.all.exception.BusinessException;
import rocks.inspectit.shared.cs.cmr.service.IConfigurationInterfaceService;
import rocks.inspectit.shared.cs.cmr.service.IStorageService;
import rocks.inspectit.shared.cs.storage.StorageData;
import rocks.inspectit.shared.cs.storage.processor.AbstractDataProcessor;
import rocks.inspectit.shared.cs.storage.processor.impl.DataSaverProcessor;
import rocks.inspectit.shared.cs.storage.processor.impl.InvocationClonerDataProcessor;
import rocks.inspectit.shared.cs.storage.processor.impl.InvocationExtractorDataProcessor;

/**
 * CoScale data publishing plugin.
 *
 * @author Alexander Wert
 *
 */
@Component
public class CoScalePlugin extends AbstractPlugin {

	/**
	 * Plugin state.
	 */
	@Value(value = "${cmr.data.extensions.coscale.active}")
	private boolean pluginActive;

	/**
	 * Number of the slowest invocation sequences to send in case of an anomaly.
	 */
	@Value(value = "${cmr.data.extensions.coscale.numInvocationsToSend}")
	private long numInvocationsToSend;

	/**
	 * Time interval for aggregation in minutes.
	 */
	@Value(value = "${cmr.data.extensions.coscale.anomaly.interval}")
	private long timeInterval;

	/**
	 * Smoothing factor for baseline calculation.
	 */
	@Value(value = "${cmr.data.extensions.coscale.anomaly.smoothingFactor}")
	private double smoothingFactor;

	/**
	 * Smoothing factor for the trend component of the baseline.
	 */
	@Value(value = "${cmr.data.extensions.coscale.anomaly.trendSmoothingFactor}")
	private double trendSmoothingFactor;

	/**
	 * Id of the application managed with CoScale.
	 */
	@Value(value = "${cmr.data.extensions.coscale.appId}")
	private String appId;

	/**
	 * CoScale access token.
	 */
	@Value(value = "${cmr.data.extensions.coscale.token}")
	private String coScaleToken;

	/**
	 * Write response times to CoScales.
	 */
	@Value(value = "${cmr.data.extensions.coscale.writeTimings}")
	private boolean writeTimingsToCoScale;


	/**
	 * Log-in state.
	 */
	private boolean loggedIn = false;

	/**
	 * Storage service used for storage creation when sending data to CoScale.
	 */
	@Autowired
	IStorageService storageService;

	/**
	 * Storage manager used for storage creation when sending data to CoScale.
	 */
	@Autowired
	CmrStorageManager storageManager;

	/**
	 * Configuration interface service to retrieve and map business transaction definitions.
	 */
	@Autowired
	IConfigurationInterfaceService ciService;

	/**
	 * Executor service for periodic aggregation of response times.
	 */
	private ScheduledExecutorService executorService;

	/**
	 * CoScale Data API.
	 */
	private DataApi dataApi;

	/**
	 * CoScale Servers API.
	 */
	private ServersApi serversApi;

	/**
	 * CoScale Requests API.
	 */
	private RequestsApi requestsApi;

	/**
	 * CoScale Events API.
	 */
	private EventsApi eventsApi;

	/**
	 * CoScale Metrics API.
	 */
	private MetricsApi metricsApi;

	/**
	 * Send invocation sequences as storage file to CoScale.
	 *
	 * @param businessTxName
	 *            name of the business transaction.
	 * @param invocations
	 *            invocation sequences to send.
	 */
	public void sendInvocationSequencesAsStorage(String businessTxName, Collection<InvocationSequenceData> invocations, int durationSeconds) {
		if (isActive()) {
			if (!loggedIn) {
				throw new IllegalStateException("CoScale cplugin is not connected! Check Application Id and Access token in the CoScale settings!");
			}
			// TestCode:
			// EventInsert newEvent = new EventInsert(name, description, attributeDescriptions,
			// type, icon);
			try {
				Builder queryBuilder = new Builder();
				queryBuilder.selectBy("name", "inspectIT Anomaly");

				List<Event> events = eventsApi.all(queryBuilder.build());
				Event event = events.get(0);

				EventDataInsert eventData = new EventDataInsert("Anomaly - " + businessTxName, -1l * durationSeconds, 0l, "{\"businessTransaction\":\"" + businessTxName + "\"}", "a");
				eventsApi.insertData(event.id, eventData);
			} catch (Exception e) {
				System.out.println("error");
			}

			// try {
			// // create storage name
			// StorageData storageData = createStorage(businessTxName, invocations);
			//
			// // send storage to CoScale
			// // TODO: connect OutputStream to the REST call to CoScale SDK
			// OutputStream outputStream = null;
			// storageManager.zipStorageData(storageData, outputStream);
			// outputStream.flush();
			// outputStream.close();
			// } catch (BusinessException | IOException e) {
			// throw new RuntimeException(e);
			// }
		}
	}

	public BTMetrics getBusinessTransactionMetrics(String businessTxName) {
		if (isActive() && isConnected()) {
			try {
				Builder queryBuilder = new Builder();
				queryBuilder.selectBy("name", "inspectIT");

				List<MetricGroup> metricGroups = metricsApi.getAllMetricGroups(queryBuilder.build());
				MetricGroup group;
				if (metricGroups.isEmpty()) {
					MetricGroupInsert groupInsert = new MetricGroupInsert("inspectIT", "inspectIT metrics", "inspectIT timings", SubjectType.APPLICATION);
					group = metricsApi.insertMetricGroup(groupInsert);
				} else {
					group = metricGroups.get(0);
				}
				BTMetrics metrics = new BTMetrics();

				long responseTimeMetricId = createMetric("RT - " + businessTxName, "Response Time of business transaction " + businessTxName, group.id);
				long thresholdMetricId = createMetric("T - " + businessTxName, "Threshold for business transaction " + businessTxName, group.id);
				metrics.setResponseTimeMetricId(responseTimeMetricId);
				metrics.setRtThresholdMetricId(thresholdMetricId);
				return metrics;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	private long createMetric(String metricName, String description, long groupId) throws IOException {
		Builder queryBuilder = new Builder();
		queryBuilder.selectBy("name", metricName);

		List<Metric> metrics = metricsApi.all(queryBuilder.build());
		Metric metric;
		if (metrics.isEmpty()) {
			MetricInsert metricInsert = new MetricInsert(metricName, description, DataType.DOUBLE, SubjectType.APPLICATION, "ms", (int) timeInterval * 60);
			metric = metricsApi.insert(metricInsert);
			metricsApi.addMetricToGroup(metric.id, groupId);
		} else {
			metric = metrics.get(0);
		}
		return metric.id;
	}

	public void writeMetricData(DataInsert dataInsert) {
		try {
			dataApi.insert("a", dataInsert);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isActive() {
		return pluginActive;
	}

	/**
	 * Indicates whether the plugin is connected to the CoScale service.
	 *
	 * @return Returns true if plugin is connected.
	 */
	public boolean isConnected() {
		return loggedIn;
	}

	/**
	 * returns the executor service of this plugin.
	 *
	 * @return Returns an {@link ExecutorService} instance.
	 */
	public ScheduledExecutorService getExecutorService() {
		if ((null == executorService) || executorService.isShutdown()) {
			executorService = Executors.newScheduledThreadPool(1);
		}
		return executorService;
	}

	/**
	 * Gets {@link #numInvocationsToSend}.
	 *
	 * @return {@link #numInvocationsToSend}
	 */
	public long getNumInvocationsToSend() {
		return numInvocationsToSend;
	}

	/**
	 * Sets {@link #numInvocationsToSend}.
	 *
	 * @param numInvocationsToSend
	 *            New value for {@link #numInvocationsToSend}
	 */
	public void setNumInvocationsToSend(long numInvocationsToSend) {
		this.numInvocationsToSend = numInvocationsToSend;
	}

	/**
	 * Gets {@link #timeInterval}.
	 *
	 * @return {@link #timeInterval}
	 */
	public long getTimeInterval() {
		return timeInterval;
	}

	/**
	 * Sets {@link #timeInterval}.
	 *
	 * @param timeInterval
	 *            New value for {@link #timeInterval}
	 */
	public void setTimeInterval(long timeInterval) {
		this.timeInterval = timeInterval;
	}

	/**
	 * Gets {@link #smoothingFactor}.
	 *
	 * @return {@link #smoothingFactor}
	 */
	public double getSmoothingFactor() {
		return smoothingFactor;
	}

	/**
	 * Sets {@link #smoothingFactor}.
	 *
	 * @param smoothingFactor
	 *            New value for {@link #smoothingFactor}
	 */
	public void setSmoothingFactor(double smoothingFactor) {
		this.smoothingFactor = smoothingFactor;
	}

	/**
	 * Gets {@link #trendSmoothingFactor}.
	 *
	 * @return {@link #trendSmoothingFactor}
	 */
	public double getTrendSmoothingFactor() {
		return trendSmoothingFactor;
	}

	/**
	 * Sets {@link #trendSmoothingFactor}.
	 *
	 * @param trendSmoothingFactor
	 *            New value for {@link #trendSmoothingFactor}
	 */
	public void setTrendSmoothingFactor(double trendSmoothingFactor) {
		this.trendSmoothingFactor = trendSmoothingFactor;
	}

	/**
	 * Gets {@link #writeTimingsToCoScale}.
	 *
	 * @return {@link #writeTimingsToCoScale}
	 */
	public boolean isWriteTimingsToCoScale() {
		return writeTimingsToCoScale;
	}

	/**
	 * Sets {@link #writeTimingsToCoScale}.
	 *
	 * @param writeTimingsToCoScale
	 *            New value for {@link #writeTimingsToCoScale}
	 */
	public void setWriteTimingsToCoScale(boolean writeTimingsToCoScale) {
		this.writeTimingsToCoScale = writeTimingsToCoScale;
	}

	/**
	 * Activates the CoScale plugin.
	 */
	private void activatePlugin() {
		try {
			connectToCoScale();
			// TODO: create event type if it does not already exist.
			mapCoScaleRequestsToBusinessTransactionDefinitions();
			loggedIn = true;
			notifyActivated();
		} catch (IOException e) {
			loggedIn = false;
		}
	}

	private void mapCoScaleRequestsToBusinessTransactionDefinitions() throws IOException {
		// TODO: @Mario: Implement here the mapping of business transaction

		// 1. Check if corresponding business transaction definitions already exist
		// 2. If they do not exist yet, do the mapping from CoScale to the BT definitions

		// dummy code from our last call
		// Credentials credentials = Credentials.Token(coScaleToken);
		// ApiFactory apiFactory = new ApiFactory(appId, credentials);
		// Request req = requestsApi.all().get(1);
		//
		// // Check if app and BT already exists
		// ciService.getApplicationDefinitions();
		//
		// ApplicationDefinition appDef = new ApplicationDefinition();
		// BusinessTransactionDefinition btDef = new BusinessTransactionDefinition("Name");
		// try {
		// StringMatchingExpression stringExpr = new
		// StringMatchingExpression(PatternMatchingType.EQUALS, "wert");
		// HttpUriValueSource valueSource = new HttpUriValueSource();
		// stringExpr.setStringValueSource(valueSource);
		// btDef.setMatchingRuleExpression(stringExpr);
		//
		// appDef.addBusinessTransactionDefinition(btDef);
		// ciService.addApplicationDefinition(appDef);
		// } catch (BusinessException e) {
		// // TODO Auto-generated catch block
		// }

	}

	/**
	 * Connects to the CoScale service.
	 *
	 * @throws IOException
	 *             if connection fails
	 */
	private void connectToCoScale() throws IOException {
		Credentials credentials = Credentials.Token(coScaleToken);
		ApiFactory apiFactory = new ApiFactory(appId, credentials);
		apiFactory.getApiClient().setSource("inspectIT");
		dataApi = apiFactory.getDataApi();
		eventsApi = apiFactory.getEventsApi();
		requestsApi = apiFactory.getRequestsApi();
		serversApi = apiFactory.getServersApi();
		metricsApi = apiFactory.getMetricsApi();
		metricsApi.all();
		loggedIn = true;
	}

	/**
	 * Deactivates the coscale plugin.
	 */
	private void deactivatePlugin() {
		if (null != executorService) {
			executorService.shutdownNow();
		}
		loggedIn = false;
		notifyDeactivated();
	}

	/**
	 * Creates a storage for the given set of invocation sequences.
	 *
	 * @param businessTxName
	 *            Name of the business transaction this invocation sequences belong to.
	 * @param invocations
	 *            collection of invocation sequences
	 * @return The created Storage Object.
	 * @throws BusinessException
	 *             thrown if Storage cannot be created.
	 */
	private StorageData createStorage(String businessTxName, Collection<InvocationSequenceData> invocations) throws BusinessException {
		int id = UUID.randomUUID().hashCode();
		String storageName = "CoScaleExport (" + businessTxName + ") - " + id;
		StorageData storageData = new StorageData();
		storageData.setName(storageName);

		// create processors for storage creation
		List<Class<? extends DefaultData>> classes = new ArrayList<Class<? extends DefaultData>>(Collections.singleton(InvocationSequenceData.class));
		DataSaverProcessor saverProcessor = new DataSaverProcessor(classes, true);
		InvocationExtractorDataProcessor invocExtractorDataProcessor = new InvocationExtractorDataProcessor(Collections.singletonList((AbstractDataProcessor) saverProcessor));
		List<AbstractDataProcessor> processors = new ArrayList<>();
		processors.add(saverProcessor);
		processors.add(invocExtractorDataProcessor);
		processors.add(new InvocationClonerDataProcessor());

		// create storage
		storageData = storageService.createAndOpenStorage(storageData);
		storageService.writeToStorage(storageData, invocations, processors, true);
		storageService.closeStorage(storageData);
		return storageData;
	}

	/**
	 * Initialize plugin.
	 */
	@PostConstruct
	@PropertyUpdate(properties = { "cmr.data.extensions.coscale.active", "cmr.data.extensions.coscale.token", "cmr.data.extensions.coscale.appId" })
	public void init() {
		if (pluginActive) {
			activatePlugin();
		} else {
			deactivatePlugin();
		}
	}

}
