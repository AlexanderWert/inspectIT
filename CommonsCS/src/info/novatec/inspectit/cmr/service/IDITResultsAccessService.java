package info.novatec.inspectit.cmr.service;

import info.novatec.inspectit.storage.StorageData;

import java.util.List;

import org.diagnoseit.spike.result.ProblemInstance;

@ServiceInterface(exporter = ServiceExporterType.HTTP, name = "diagnoseITResultsAccessServiceExporter")
public interface IDITResultsAccessService {

	List<ProblemInstance> getProblemInstances();
	
	List<ProblemInstance> analyzeInteractively(long platformId, List<Long> traceIds);
	
	List<ProblemInstance> analyzeInteractively(String storageDataId, long platformId, List<Long> traceIds);
	
}
