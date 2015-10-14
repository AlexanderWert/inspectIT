package info.novatec.inspectit.cmr.service;

import info.novatec.inspectit.cmr.model.PlatformIdent;
import info.novatec.inspectit.cmr.spring.aop.MethodLog;
import info.novatec.inspectit.cmr.storage.CmrStorageManager;
import info.novatec.inspectit.communication.DefaultData;
import info.novatec.inspectit.communication.data.InvocationSequenceData;
import info.novatec.inspectit.indexing.query.factory.impl.InvocationSequenceDataQueryFactory;
import info.novatec.inspectit.indexing.query.provider.impl.StorageIndexQueryProvider;
import info.novatec.inspectit.indexing.storage.IStorageDescriptor;
import info.novatec.inspectit.indexing.storage.IStorageTreeComponent;
import info.novatec.inspectit.indexing.storage.impl.CombinedStorageBranch;
import info.novatec.inspectit.indexing.storage.impl.StorageIndexQuery;
import info.novatec.inspectit.spring.logger.Log;
import info.novatec.inspectit.storage.StorageData;
import info.novatec.inspectit.storage.StorageFileType;
import info.novatec.inspectit.storage.nio.stream.InputStreamProvider;
import info.novatec.inspectit.storage.serializer.SerializationException;
import info.novatec.inspectit.storage.serializer.impl.SerializationManager;
import info.novatec.inspectit.storage.serializer.provider.SerializationManagerProvider;
import info.novatec.inspectit.storage.serializer.util.KryoUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.diagnoseit.spike.inspectit.trace.impl.IITTraceImpl;
import org.diagnoseit.spike.result.ProblemInstaceRegistry;
import org.diagnoseit.spike.result.ProblemInstance;
import org.diagnoseit.spike.rules.processing.DiagnoseIT;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rocks.cta.api.core.Trace;

import com.esotericsoftware.kryo.io.Input;

@Service
public class DITResultsAccessService implements IDITResultsAccessService {
	/** The logger of this class. */
	@Log
	Logger log;

	@Autowired
	private IInvocationDataAccessService invocationDataAccessService;

	@Autowired
	private ICachedDataService cachedDataService;

	@Autowired
	private CmrStorageManager storageManager;

	@Autowired
	private SerializationManagerProvider serializationManagerProvider;

	@Autowired
	private StorageIndexQueryProvider queryProvider;

	@Autowired
	private InputStreamProvider inputStreamProvider;

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
		List<Trace> traces = new ArrayList<Trace>();
		for (InvocationSequenceData isDataStub : invocationSequences) {
			InvocationSequenceData isData = invocationDataAccessService.getInvocationSequenceDetail(isDataStub);
			Trace trace = new IITTraceImpl(isData, cachedDataService);
			traces.add(trace);
		}

		return analyzeTraces(traces);
	}

	/**
	 * {@inheritDoc}
	 */
	@MethodLog
	public List<ProblemInstance> analyzeInteractively(String storageDataId, long platformId, List<Long> traceIds) {
		StorageData storageData = storageManager.getStorageData(storageDataId);
		SerializationManager serializer = serializationManagerProvider.createSerializer();
		try {

			PlatformIdent pIdent = retrievePlatformIdent(storageData, serializer);
			List<Trace> traces;
			if (platformId < 0 || traceIds == null) {
				traces = retrieveAllStorageTraces(storageData, serializer, pIdent);
			} else {
				traces = retrieveSelectedTraces(platformId, traceIds, storageData, serializer, pIdent);
			}
			return analyzeTraces(traces);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List<Trace> retrieveSelectedTraces(long platformId, List<Long> traceIds, StorageData storageData, SerializationManager serializer, PlatformIdent pIdent)
			throws IOException, SerializationException {
		Set<Path> indexFilesLocations = storageManager.getFilesLocation(storageData, StorageFileType.INDEX_FILE.getExtension()).keySet();

		List<IStorageTreeComponent<DefaultData>> list = new ArrayList<IStorageTreeComponent<DefaultData>>();
		for (Path filePath : indexFilesLocations) {
			Input input = new Input(Files.readAllBytes(filePath));
			IStorageTreeComponent<DefaultData> object = (IStorageTreeComponent<DefaultData>) serializer.deserialize(input);
			list.add(object);
		}

		CombinedStorageBranch<DefaultData> combStorageBranch = new CombinedStorageBranch<DefaultData>(list);

		InvocationSequenceDataQueryFactory<StorageIndexQuery> dataQueryFactory = new InvocationSequenceDataQueryFactory<StorageIndexQuery>();
		dataQueryFactory.setIndexQueryProvider(queryProvider);

		StorageIndexQuery query = dataQueryFactory.getInvocationSequenceOverview(platformId, traceIds, Integer.MAX_VALUE);
		query.setIncludeIds(traceIds);
		
		
		List<IStorageDescriptor> descriptors = combStorageBranch.query(query);

		InputStream result = inputStreamProvider.getExtendedByteBufferInputStream(storageData, descriptors);
		List<Trace> traces = new ArrayList<Trace>();
		try (Input input = new Input(result)) {
			while (KryoUtil.hasMoreBytes(input)) {
				Object invocation = serializer.deserialize(input);
				if (invocation instanceof InvocationSequenceData) {
					Trace trace = new IITTraceImpl((InvocationSequenceData) invocation, pIdent);
					traces.add(trace);
				}
			}
		}
		return traces;
	}

	private List<Trace> retrieveAllStorageTraces(StorageData storageData, SerializationManager serializer, PlatformIdent pIdent) throws IOException, SerializationException, FileNotFoundException {
		List<Trace> traces = new ArrayList<Trace>();
		Set<Path> dataFilesLocations = storageManager.getFilesLocation(storageData, StorageFileType.DATA_FILE.getExtension()).keySet();
		for (Path filePath : dataFilesLocations) {

			try (Input input = new Input(new FileInputStream(filePath.toFile()))) {
				while (KryoUtil.hasMoreBytes(input)) {
					Object invocation = serializer.deserialize(input);
					if (invocation instanceof InvocationSequenceData) {
						Trace trace = new IITTraceImpl((InvocationSequenceData) invocation, pIdent);
						traces.add(trace);
					}
				}
			}
		}
		return traces;
	}

	private PlatformIdent retrievePlatformIdent(StorageData storageData, SerializationManager serializer) throws IOException, SerializationException, FileNotFoundException {
		Set<Path> agentFilesLocations = storageManager.getFilesLocation(storageData, StorageFileType.AGENT_FILE.getExtension()).keySet();
		for (Path filePath : agentFilesLocations) {

			try (Input input = new Input(new FileInputStream(filePath.toFile()))) {
				while (KryoUtil.hasMoreBytes(input)) {
					Object agent = serializer.deserialize(input);
					if (agent instanceof PlatformIdent) {
						return (PlatformIdent) agent;
					}
				}
			}
		}
		return null;
	}
	
	private List<ProblemInstance> analyzeTraces(Collection<Trace> traces){
		List<ProblemInstance> results = new ArrayList<ProblemInstance>();
		for (Trace trace : traces) {
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
