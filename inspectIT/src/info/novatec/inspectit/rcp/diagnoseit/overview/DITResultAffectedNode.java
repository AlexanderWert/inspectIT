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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((affectedNode == null) ? 0 : affectedNode.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DITResultAffectedNode other = (DITResultAffectedNode) obj;
		if (affectedNode == null) {
			if (other.affectedNode != null)
				return false;
		} else if (!affectedNode.equals(other.affectedNode))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}
	
	

}
