package info.novatec.inspectit.cmr.service;

import info.novatec.inspectit.cmr.spring.aop.MethodLog;
import info.novatec.inspectit.communication.data.InvocationSequenceData;
import info.novatec.inspectit.spring.logger.Log;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.diagnoseit.spike.inspectit.trace.impl.IITTraceImpl;
import org.diagnoseit.spike.result.ProblemInstaceRegistry;
import org.diagnoseit.spike.result.ProblemInstance;
import org.diagnoseit.spike.rules.processing.DiagnoseIT;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rocks.cta.api.core.Trace;

@Service
public class DITResultsAccessService implements IDITResultsAccessService {
	/** The logger of this class. */
	@Log
	Logger log;

	@Autowired
	private IInvocationDataAccessService invocationDataAccessService;

	@Autowired
	private ICachedDataService cachedDataService;

	/**
	 * {@inheritDoc}
	 */
	@MethodLog
	public List<ProblemInstance> getProblemInstances() {
		return ProblemInstaceRegistry.getInstance().getProblemInstances();
	}

	/**
	 * {@inheritDoc}
	 */
	@MethodLog
	public List<ProblemInstance> analyzeInteractively(long platformId, List<Long> traceIds) {

		List<InvocationSequenceData> invocationSequences = invocationDataAccessService.getInvocationSequenceOverview(platformId, traceIds, Integer.MAX_VALUE, null);
		List<ProblemInstance> results = new ArrayList<ProblemInstance>();
		for (InvocationSequenceData isData : invocationSequences) {
			Trace trace = new IITTraceImpl(isData, cachedDataService);
			results.addAll(DiagnoseIT.getInstance().analyzeInteractively(trace));
		}

		return results;
	}

	/**
	 * Is executed after dependency injection is done to perform any initialization.
	 * 
	 * @throws Exception
	 *             if an error occurs during {@link PostConstruct}
	 */
	@PostConstruct
	public void postConstruct() throws Exception {
		if (log.isInfoEnabled()) {
			log.info("|-diagnoseIT Results Access Service active...");
		}
	}

}
