package info.novatec.inspectit.rcp.diagnoseit.overview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.diagnoseit.spike.result.ProblemInstance;

public class DITResultOverviewBuilder {
	private List<DITResultBusinessTransaction> resultList = new ArrayList<DITResultBusinessTransaction>();

	public void addProblemInstance(ProblemInstance problemInstance) {
		String businesstTransactionStr = problemInstance.getBusinessTransaction();
		String nodeTypeStr = problemInstance.getNodeType();
		String entryPointStr = problemInstance.getEntryPoint();
		String causeStr = problemInstance.getCause();

		DITResultBusinessTransaction btElement = null;
		DITResultNodeType ntElement = null;
		DITResultEntryPoint epElement = null;
		DITResultProblemInstance piElement = null;
		int idx = findInstanceWithId(resultList, businesstTransactionStr);
		if (idx >= 0) {
			btElement = resultList.get(idx);
			idx = findInstanceWithId(btElement.getNodeTypes(), nodeTypeStr);
			if (idx >= 0) {
				ntElement = btElement.getNodeTypes().get(idx);
				idx = findInstanceWithId(ntElement.getEntryPoints(), entryPointStr);
				if (idx >= 0) {
					epElement = ntElement.getEntryPoints().get(idx);
					idx = findInstanceWithId(epElement.getProblemInstances(), causeStr);
					if (idx >= 0) {
						piElement = epElement.getProblemInstances().get(idx);

						for (String affNode : problemInstance.getAffectedNodes()) {
							idx = findInstanceWithId(piElement.getAffectedNodes(), affNode);
							if (idx < 0) {
								piElement.addAffectedNode(new DITResultAffectedNode(affNode));
							}
						}

					} else {
						piElement = new DITResultProblemInstance(problemInstance);
						for (String affNode : problemInstance.getAffectedNodes()) {
							piElement.addAffectedNode(new DITResultAffectedNode(affNode));
						}
						epElement.addProblemInstance(piElement);
					}
				} else {
					epElement = new DITResultEntryPoint();

					piElement = new DITResultProblemInstance(problemInstance);
					for (String affNode : problemInstance.getAffectedNodes()) {
						piElement.addAffectedNode(new DITResultAffectedNode(affNode));
					}
					epElement.addProblemInstance(piElement);
					ntElement.addEntryPoint(epElement);

				}
			} else {
				ntElement = new DITResultNodeType();
				epElement = new DITResultEntryPoint();

				piElement = new DITResultProblemInstance(problemInstance);
				for (String affNode : problemInstance.getAffectedNodes()) {
					piElement.addAffectedNode(new DITResultAffectedNode(affNode));
				}
				epElement.addProblemInstance(piElement);
				ntElement.addEntryPoint(epElement);

				btElement.addNodeType(ntElement);

			}
		} else {
			btElement = new DITResultBusinessTransaction();
			ntElement = new DITResultNodeType();
			epElement = new DITResultEntryPoint();

			piElement = new DITResultProblemInstance(problemInstance);
			for (String affNode : problemInstance.getAffectedNodes()) {
				piElement.addAffectedNode(new DITResultAffectedNode(affNode));
			}
			epElement.addProblemInstance(piElement);
			ntElement.addEntryPoint(epElement);

			btElement.addNodeType(ntElement);
			resultList.add(btElement);
		}
	}

	private int findInstanceWithId(List<? extends DITResultElement> targetList, String identifier) {

		for (int i = 0; i < targetList.size(); i++) {
			if (targetList.get(i).getIdentifier().equals(identifier)) {
				return i;
			}
		}
		return -1;
	}

	public List<DITResultBusinessTransaction> getResultList() {
		Collections.sort(resultList, new SeverityComperator());	
		return 	resultList;
	}

}
