package info.novatec.inspectit.rcp.repository.service.storage;

import info.novatec.inspectit.cmr.service.ICachedDataService;
import info.novatec.inspectit.cmr.service.IDITResultsAccessService;
import info.novatec.inspectit.cmr.service.IInvocationDataAccessService;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.service.cmr.CmrServiceProvider;

import java.util.List;

import org.diagnoseit.spike.result.ProblemInstance;

public class StorageDITResultsAccessService implements IDITResultsAccessService {

	private IDITResultsAccessService cmrDiagnoseITService;
	private String storageDataId;

	public void setCmrRepositoryDefinition(CmrRepositoryDefinition cmrRepositoryDefinition) {
		cmrDiagnoseITService = InspectIT.getService(CmrServiceProvider.class).getDiagnoseITResultsAccessService(cmrRepositoryDefinition);
	}

	public void setStorageDataId(String id) {
		storageDataId = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProblemInstance> getProblemInstances() {
		return this.analyzeInteractively(storageDataId, -1, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProblemInstance> analyzeInteractively(long platformId, List<Long> traceIds) {
		return this.analyzeInteractively(storageDataId, platformId, traceIds);
	}

	@Override
	public List<ProblemInstance> analyzeInteractively(String storageDataId, long platformId, List<Long> traceIds) {
		return cmrDiagnoseITService.analyzeInteractively(storageDataId, platformId, traceIds);
	}

}
