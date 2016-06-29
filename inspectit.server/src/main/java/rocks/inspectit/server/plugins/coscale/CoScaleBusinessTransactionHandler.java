package rocks.inspectit.server.plugins.coscale;

import java.util.Comparator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.coscale.sdk.client.data.DataInsertBuilder;

import rocks.inspectit.server.plugins.coscale.util.BTMetrics;
import rocks.inspectit.server.plugins.coscale.util.LimitedSortedList;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceDataHelper;

public class CoScaleBusinessTransactionHandler implements Runnable {

	private final int businessTxId;
	private final String businessTxName;

	private final LimitedSortedList<InvocationSequenceData> slowestInvocationSequences;

	private double currentResponseTimeSum = 0.0;
	private int currentResponseTimeCount = 0;
	private final AtomicInteger anomaly = new AtomicInteger(0);
	private final ScheduledExecutorService executorService;
	private final DoubleExponentialSmoothing exponentialSmooting;
	private final CoScalePlugin plugin;
	private BTMetrics btMetrics;
	private int initializationCounter = 5;

	/**
	 * @param businessTxId
	 * @param businessTxName
	 */
	public CoScaleBusinessTransactionHandler(CoScalePlugin plugin, int businessTxId, String businessTxName) {
		super();
		this.businessTxId = businessTxId;
		this.businessTxName = businessTxName;
		this.executorService = plugin.getExecutorService();
		slowestInvocationSequences = new LimitedSortedList<>((int) plugin.getNumInvocationsToSend(), new Comparator<InvocationSequenceData>() {

			@Override
			public int compare(InvocationSequenceData o1, InvocationSequenceData o2) {
				double diff = InvocationSequenceDataHelper.calculateDuration(o1) - InvocationSequenceDataHelper.calculateDuration(o2);
				if (diff > 0) {
					return 1;
				} else if (diff < 0) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		this.plugin = plugin;
		this.executorService.scheduleAtFixedRate(this, plugin.getTimeInterval(), plugin.getTimeInterval(), TimeUnit.MINUTES);
		this.exponentialSmooting = new DoubleExponentialSmoothing(plugin.getSmoothingFactor(), plugin.getTrendSmoothingFactor(), 100);

		if (plugin.isWriteTimingsToCoScale()) {
			updateBusinessTransactionMetric();
		}
	}

	public void updateBusinessTransactionMetric() {
		btMetrics = plugin.getBusinessTransactionMetrics(businessTxName);
	}

	public synchronized void processData(InvocationSequenceData invocation) {
		double duration = InvocationSequenceDataHelper.calculateDuration(invocation);
		currentResponseTimeSum += duration;
		currentResponseTimeCount++;

		if (duration > exponentialSmooting.getBaselineThreshold()) {
			slowestInvocationSequences.add(invocation);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		if (currentResponseTimeCount > 0) {
			double meanResponseTime;
			synchronized (this) {
				meanResponseTime = currentResponseTimeSum / currentResponseTimeCount;
				currentResponseTimeSum = 0.0;
				currentResponseTimeCount = 0;

			}

			if (initializationCounter <= 0) {
				if (meanResponseTime > exponentialSmooting.getBaselineThreshold()) {
					anomaly.incrementAndGet();
				} else {
					if (anomaly.get() > 0) {
						plugin.sendInvocationSequencesAsStorage(businessTxName, slowestInvocationSequences, anomaly.get());
						anomaly.set(0);
					}
					slowestInvocationSequences.clear();
				}
			}

			if (plugin.isWriteTimingsToCoScale() && (null != btMetrics)) {
				DataInsertBuilder builder = new DataInsertBuilder();
				builder.addDoubleData(btMetrics.getResponseTimeMetricId(), 0, meanResponseTime);
				if (initializationCounter <= 0) {
					builder.addDoubleData(btMetrics.getRtThresholdMetricId(), 0, exponentialSmooting.getBaselineThreshold());
				}

				plugin.writeMetricData(builder.build());
			}

			if (initializationCounter > 0) {
				initializationCounter--;
				slowestInvocationSequences.clear();
			}

			exponentialSmooting.push(meanResponseTime);
		} else {
			synchronized (this) {
				currentResponseTimeSum = 0.0;
				currentResponseTimeCount = 0;
				slowestInvocationSequences.clear();
			}
		}

	}

	/**
	 * Gets {@link #businessTxId}.
	 *
	 * @return {@link #businessTxId}
	 */
	public int getBusinessTxId() {
		return businessTxId;
	}
}
