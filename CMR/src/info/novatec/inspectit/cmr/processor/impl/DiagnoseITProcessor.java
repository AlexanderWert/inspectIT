package info.novatec.inspectit.cmr.processor.impl;

import info.novatec.inspectit.cmr.processor.AbstractCmrDataProcessor;
import info.novatec.inspectit.cmr.property.spring.PropertyUpdate;
import info.novatec.inspectit.cmr.service.ICachedDataService;
import info.novatec.inspectit.communication.DefaultData;
import info.novatec.inspectit.communication.data.InvocationSequenceData;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.diagnoseit.spike.inspectit.trace.impl.IITTraceImpl;
import org.diagnoseit.spike.rules.processing.DiagnoseIT;
import org.hibernate.StatelessSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import rocks.cta.api.core.Trace;

public class DiagnoseITProcessor extends AbstractCmrDataProcessor {

	@Autowired
	private ICachedDataService cachedDataService;

	@Value("${diagnoseIT.responseTimeThreshold}")
	private long threshold;

	@Value("${diagnoseIT.numWorkers}")
	private long numWorkers;


	@Override
	protected void processData(DefaultData defaultData, StatelessSession session) {
		if (!(defaultData instanceof InvocationSequenceData)) {
			return;
		}
		InvocationSequenceData invocationSequence = (InvocationSequenceData) defaultData;

		Trace trace = new IITTraceImpl(invocationSequence, cachedDataService);
		DiagnoseIT.getInstance().appendTrace(trace);
	}

	@Override
	public boolean canBeProcessed(DefaultData defaultData) {
		return (defaultData instanceof InvocationSequenceData) && ((InvocationSequenceData) defaultData).getDuration() > (double) threshold;
	}

	@PropertyUpdate(properties = { "diagnoseIT.responseTimeThreshold" })
	public synchronized void updateResponseTImeThreshold() {
		Properties config = DiagnoseIT.getInstance().getConfig();
		config.setProperty(DiagnoseIT.RT_THRESHOLD, String.valueOf(threshold * Trace.MILLIS_TO_NANOS_FACTOR));
	}

	@PropertyUpdate(properties = { "diagnoseIT.numWorkers" })
	public synchronized void updateNumWorkers() {
		Properties config = DiagnoseIT.getInstance().getConfig();
		config.setProperty(DiagnoseIT.NUM_WORKERS, String.valueOf((int) numWorkers));
		DiagnoseIT.getInstance().restart();
	}
	
	@PostConstruct
	public void postConstruct() throws Exception {
		Properties config = new Properties();
		config.setProperty(DiagnoseIT.RT_THRESHOLD, String.valueOf(threshold * Trace.MILLIS_TO_NANOS_FACTOR));
		config.setProperty(DiagnoseIT.NUM_WORKERS, String.valueOf((int) numWorkers));
		DiagnoseIT.getInstance().setConfig(config);
		DiagnoseIT.getInstance().start();
	}

}
