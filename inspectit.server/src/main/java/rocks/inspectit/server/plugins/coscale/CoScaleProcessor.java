package rocks.inspectit.server.plugins.coscale;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;

import rocks.inspectit.server.plugins.IPluginStateListener;
import rocks.inspectit.server.processor.AbstractCmrDataProcessor;
import rocks.inspectit.shared.all.cmr.property.spring.PropertyUpdate;
import rocks.inspectit.shared.all.communication.DefaultData;
import rocks.inspectit.shared.all.communication.data.InvocationSequenceData;
import rocks.inspectit.shared.all.communication.data.cmr.BusinessTransactionData;
import rocks.inspectit.shared.all.util.Pair;
import rocks.inspectit.shared.cs.cmr.service.cache.CachedDataService;

/**
 *
 *
 * @author Alexander Wert
 *
 */
public class CoScaleProcessor extends AbstractCmrDataProcessor implements IPluginStateListener {

	@Autowired
	private CoScalePlugin coScalePlugin;

	@Autowired
	private CachedDataService cachedDataService;

	private final Map<Pair<Integer, Integer>, CoScaleBusinessTransactionHandler> businessTransactionsHandlers = new HashMap<Pair<Integer, Integer>, CoScaleBusinessTransactionHandler>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processData(DefaultData defaultData, EntityManager entityManager) {
		InvocationSequenceData invocation = (InvocationSequenceData) defaultData;
		Pair<Integer, Integer> businessTxId = new Pair<Integer, Integer>(invocation.getApplicationId(), invocation.getBusinessTransactionId());
		if (!businessTransactionsHandlers.containsKey(businessTxId)) {
			BusinessTransactionData businessTransaction = cachedDataService.getBusinessTransactionForId(invocation.getApplicationId(), invocation.getBusinessTransactionId());
			businessTransactionsHandlers.put(businessTxId, new CoScaleBusinessTransactionHandler(coScalePlugin, businessTxId.getFirst(), businessTransaction.getName()));
		}
		businessTransactionsHandlers.get(businessTxId).processData(invocation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBeProcessed(DefaultData defaultData) {
		return coScalePlugin.isActive() && coScalePlugin.isConnected() && defaultData instanceof InvocationSequenceData;
	}

	@PostConstruct
	public void init() {
		coScalePlugin.addPluginStateListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pluginActivated() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pluginDeactivated() {
		businessTransactionsHandlers.clear();
	}

	@PropertyUpdate(properties = { "cmr.data.extensions.coscale.writeTimings" })
	public void updateMetrics() {
		if (coScalePlugin.isWriteTimingsToCoScale()) {
			for (CoScaleBusinessTransactionHandler btHandler : businessTransactionsHandlers.values()) {
				btHandler.updateBusinessTransactionMetric();
			}
		}
	}

}
