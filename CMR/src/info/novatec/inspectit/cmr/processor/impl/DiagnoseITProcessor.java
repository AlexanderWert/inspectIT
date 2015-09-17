package info.novatec.inspectit.cmr.processor.impl;

import info.novatec.inspectit.cmr.processor.AbstractCmrDataProcessor;
import info.novatec.inspectit.cmr.service.ICachedDataService;
import info.novatec.inspectit.communication.DefaultData;
import info.novatec.inspectit.communication.data.InvocationSequenceData;

import java.util.Properties;

import org.diagnoseit.spike.inspectit.trace.impl.IITTraceImpl;
import org.diagnoseit.spike.rules.processing.DiagnoseIT;
import org.hibernate.StatelessSession;
import org.springframework.beans.factory.annotation.Autowired;

import rocks.cta.api.core.Trace;

public class DiagnoseITProcessor extends AbstractCmrDataProcessor {

	@Autowired
	private ICachedDataService cachedDataService;

	private long threshold = 1000L;

	public DiagnoseITProcessor() {

		if (!DiagnoseIT.getInstance().isRunning()) {
			Properties config = new Properties();
			config.setProperty(DiagnoseIT.RT_THRESHOLD, String.valueOf(threshold * Trace.MILLIS_TO_NANOS_FACTOR));
			DiagnoseIT.getInstance().setConfig(config);
			DiagnoseIT.getInstance().start();
		}
	}

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

}
