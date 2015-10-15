package info.novatec.inspectit.rcp.diagnoseit.overview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.diagnoseit.spike.result.ProblemInstance;
import org.diagnoseit.spike.traceservices.aggregation.AbstractAggregatedTimedCallable;

import rocks.cta.api.core.callables.TimedCallable;

public class DITResultProblemInstance extends DITResultElement {

	private DITResultEntryPoint parent;
	private List<DITResultAffectedNode> children;

	private ProblemInstance problemInstance;

	public DITResultProblemInstance(ProblemInstance problemInstance) {
		super();
		this.problemInstance = problemInstance;
	}

	protected List<DITResultAffectedNode> getAffectedNodes() {
		return children;
	}

	protected void addAffectedNode(DITResultAffectedNode affectedNode) {
		if (children == null) {
			children = new ArrayList<DITResultAffectedNode>(2);
		}
		children.add(affectedNode);
		affectedNode.setParent(this);
	}

	@Override
	public DITResultElement getParent() {
		return parent;
	}

	protected void setParent(DITResultEntryPoint parent) {
		this.parent = parent;
	}

	@Override
	public List<? extends DITResultElement> getChildren() {
		return children;
	}

	@Override
	public String getColumnContent(DITOverviewColumn column) {
		switch (column) {
		case LAST_OCCURENCE:
			LastOccurrenceDiff diff = getLastOccurrenceDiff(problemInstance.getLastOccurrenceDate(), new Date());
			return diff.toString();
		case NUM_AFFECTED_NODES:
			return String.valueOf(problemInstance.getAffectedNodes().size());
		case NUM_INSTANCES:
			return "---";
		case NUM_OCCURRENCES:
			return String.valueOf(problemInstance.getNumOccurrences());
		case PROBLEM_OVERVIEW:
			return getStringRepresentation();

		case SEVERITY:
			if (problemInstance.getSeverity() > 0.8) {
				return "high";
			} else if (problemInstance.getSeverity() > 0.5) {
				return "medium";
			} else {
				return "low";
			}
		default:
			throw new IllegalArgumentException("Unsupported Column!");
		}

	}

	public String getIdentifier() {
		return problemInstance.getCause();
	}

	@Override
	public ResultElementType getResultElementType() {
		return ResultElementType.PROBLEM_INSTANCE;
	}

	public ProblemInstance getProblemInstance() {
		return problemInstance;
	}

	public double getSeverity() {
		return problemInstance.getSeverity();
	}

	protected static LastOccurrenceDiff getLastOccurrenceDiff(Date date, Date refferenceDate) {
		LastOccurrenceDiff result = new LastOccurrenceDiff();
		long diffSeconds = (refferenceDate.getTime() - date.getTime()) / 1000;
		long diffMinutes = diffSeconds / 60L;
		if (diffMinutes == 0L) {
			result.setDiff((int) diffSeconds);
			result.setTimeUnit(TimeUnit.SECONDS);
			return result;
		}

		long diffHours = diffMinutes / 60L;
		if (diffHours == 0L) {
			result.setDiff((int) diffMinutes);
			result.setTimeUnit(TimeUnit.MINUTES);
			return result;
		}

		long diffDays = diffHours / 24L;
		if (diffDays == 0L) {
			result.setDiff((int) diffHours);
			result.setTimeUnit(TimeUnit.HOURS);
			return result;
		}

		result.setDiff((int) diffDays);
		result.setTimeUnit(TimeUnit.HOURS);
		return result;
	}

	@Override
	public String getStringRepresentation() {
		AbstractAggregatedTimedCallable<? extends TimedCallable> causeData = problemInstance.getCauseData();
		return NameUtils.getStringRepresentationFromElementData(causeData);
	}

	@Override
	public List<DITResultProblemInstance> collectProblemInstances() {
		List<DITResultProblemInstance>  result = new ArrayList<DITResultProblemInstance>();
		result.add(this);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((problemInstance == null) ? 0 : problemInstance.hashCode());
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
		DITResultProblemInstance other = (DITResultProblemInstance) obj;
		if (problemInstance == null) {
			if (other.problemInstance != null)
				return false;
		} else if (!problemInstance.equals(other.problemInstance))
			return false;
		return true;
	}

	
}
