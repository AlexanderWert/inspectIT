package info.novatec.inspectit.cmr.service;

import info.novatec.inspectit.cmr.spring.aop.MethodLog;
import info.novatec.inspectit.spring.logger.Log;

import java.util.List;

import javax.annotation.PostConstruct;

import org.diagnoseit.spike.result.ProblemInstaceRegistry;
import org.diagnoseit.spike.result.ProblemInstance;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class DITResultsAccessService implements IDITResultsAccessService {
	/** The logger of this class. */
	@Log
	Logger log;

	/**
	 * {@inheritDoc}
	 */
	@MethodLog
	public List<ProblemInstance> getProblemInstances() {
		return ProblemInstaceRegistry.getInstance().getProblemInstances();
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
