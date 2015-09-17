package info.novatec.inspectit.rcp.diagnoseit.overview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DITResultAffectedNode extends DITResultElement {

	private DITResultProblemInstance parent;

	private String affectedNode;

	public DITResultAffectedNode(String affectedNode) {
		super();
		this.affectedNode = affectedNode;
	}

	@Override
	public DITResultElement getParent() {
		return parent;
	}

	protected void setParent(DITResultProblemInstance parent) {
		this.parent = parent;
	}

	@Override
	public List<? extends DITResultElement> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public String getColumnContent(DITOverviewColumn column) {
		switch (column) {
		case LAST_OCCURENCE:
			LastOccurrenceDiff diff = DITResultProblemInstance.getLastOccurrenceDiff(parent.getProblemInstance().getAffectedNodeLastOccurrence(getIdentifier()), new Date());
			return diff.toString();
		case NUM_AFFECTED_NODES:
			return "---";
		case NUM_INSTANCES:
			return "---";
		case NUM_OCCURRENCES:
			return String.valueOf(parent.getProblemInstance().getAffectedNodeCount(getIdentifier()));
		case PROBLEM_OVERVIEW:
			return getStringRepresentation();
		case SEVERITY:
			return "---";
		default:
			throw new IllegalArgumentException("Unsupported Column!");
		}
	}

	@Override
	public ResultElementType getResultElementType() {
		return ResultElementType.AFFECTED_NODE;
	}

	public String getIdentifier() {
		return affectedNode;
	}

	@Override
	public double getSeverity() {
		return -1;
	}

	@Override
	public String getStringRepresentation() {
		String identifier = parent.getProblemInstance().getNodeType() + " (Host: " + getIdentifier() + ")";
		return identifier.substring(0, Math.min(80, identifier.length()));
	}

	@Override
	public List<DITResultProblemInstance> collectProblemInstances() {
		List<DITResultProblemInstance>  result = new ArrayList<DITResultProblemInstance>();
		result.add((DITResultProblemInstance)getParent());
		return result;
	}

}
