package info.novatec.inspectit.rcp.diagnoseit.overview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.diagnoseit.spike.result.ProblemInstance;
import org.diagnoseit.spike.traceservices.aggregation.AbstractAggregatedTimedCallable;

import rocks.cta.api.core.callables.TimedCallable;

public class DITResultEntryPoint extends DITResultElement {

	private DITResultNodeType parent;
	private List<DITResultProblemInstance> children;

	private boolean sorted = false;

	protected List<DITResultProblemInstance> getProblemInstances() {
		return children;
	}

	protected void addProblemInstance(DITResultProblemInstance problemInstance) {
		if (children == null) {
			children = new ArrayList<DITResultProblemInstance>(2);
		}
		children.add(problemInstance);
		problemInstance.setParent(this);
		sorted = false;
	}

	@Override
	public DITResultElement getParent() {
		return parent;
	}

	protected void setParent(DITResultNodeType parent) {
		this.parent = parent;
	}

	@Override
	public List<? extends DITResultElement> getChildren() {
		if (!sorted) {
			Collections.sort(children, new SeverityComperator());
		}
		return children;
	}

	@Override
	public String getColumnContent(DITOverviewColumn column) {
		if (children == null || children.isEmpty()) {
			throw new IllegalStateException("Entry Point result element must always have at least one child!");
		}
		switch (column) {
		case LAST_OCCURENCE:
			Date max = calculateLastOccurrence();
			LastOccurrenceDiff diff = DITResultProblemInstance.getLastOccurrenceDiff(max, new Date());
			return diff.toString();
		case NUM_AFFECTED_NODES:
			Set<String> affectedNodes = calculateAffectedNodesSet();
			return String.valueOf(affectedNodes.size());
		case NUM_INSTANCES:
			return String.valueOf(calculateNumProblemInstances());
		case NUM_OCCURRENCES:
			int sumOccurrences = calculateNumOccurrences();
			return String.valueOf(sumOccurrences);
		case PROBLEM_OVERVIEW:
			return getStringRepresentation();

		case SEVERITY:
			double maxSeverity = getSeverity();
			if (maxSeverity > 0.8) {
				return "high";
			} else if (maxSeverity > 0.5) {
				return "medium";
			} else {
				return "low";
			}
		default:
			throw new IllegalArgumentException("Unsupported Column!");
		}

	}

	public String getIdentifier() {
		return getRepresentativeProblemInstance().getEntryPoint();
	}

	public double getSeverity() {
		double maxSeverity = -1.0;
		for (DITResultProblemInstance child : children) {
			maxSeverity = child.getProblemInstance().getSeverity() > maxSeverity ? child.getProblemInstance().getSeverity() : maxSeverity;
		}
		return maxSeverity;
	}

	protected ProblemInstance getRepresentativeProblemInstance() {
		return children.get(0).getProblemInstance();
	}

	protected int calculateNumOccurrences() {
		int sumOccurrences = 0;
		for (DITResultProblemInstance child : children) {
			sumOccurrences += child.getProblemInstance().getNumOccurrences();
		}
		return sumOccurrences;
	}

	protected int calculateNumProblemInstances() {
		return children.size();
	}

	protected Set<String> calculateAffectedNodesSet() {
		Set<String> affectedNodes = new HashSet<String>();
		for (DITResultProblemInstance child : children) {
			affectedNodes.addAll(child.getProblemInstance().getAffectedNodes());
		}
		return affectedNodes;
	}

	protected Date calculateLastOccurrence() {
		Date max = new Date(0L);
		for (DITResultProblemInstance child : children) {
			Date value = child.getProblemInstance().getLastOccurrenceDate();
			max = value.getTime() > max.getTime() ? value : max;
		}
		return max;
	}

	@Override
	public ResultElementType getResultElementType() {
		return ResultElementType.ENTRY_POINT;
	}

	@Override
	public String getStringRepresentation() {
		AbstractAggregatedTimedCallable<? extends TimedCallable> entryPointData = getRepresentativeProblemInstance().getEntryPointData();
		return NameUtils.getStringRepresentationFromElementData(entryPointData);
	}


	
	

}
