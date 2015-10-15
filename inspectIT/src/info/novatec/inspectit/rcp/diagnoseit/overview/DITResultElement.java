package info.novatec.inspectit.rcp.diagnoseit.overview;

import info.novatec.inspectit.communication.DefaultData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.diagnoseit.spike.traceservices.aggregation.AbstractAggregatedTimedCallable;
import org.diagnoseit.spike.traceservices.aggregation.AggregatedDatabaseInvocation;
import org.diagnoseit.spike.traceservices.aggregation.AggregatedHTTPRequestProcessing;
import org.diagnoseit.spike.traceservices.aggregation.AggregatedMethodInvocation;
import org.diagnoseit.spike.traceservices.aggregation.Signature;

import rocks.cta.api.core.callables.DatabaseInvocation;
import rocks.cta.api.core.callables.HTTPRequestProcessing;
import rocks.cta.api.core.callables.MethodInvocation;
import rocks.cta.api.core.callables.RemoteInvocation;
import rocks.cta.api.core.callables.TimedCallable;

public abstract class DITResultElement extends DefaultData{

	public abstract String getIdentifier();
	
	public abstract String getStringRepresentation();
	
	public abstract DITResultElement getParent();

	public abstract List<? extends DITResultElement> getChildren();

	public abstract String getColumnContent(DITOverviewColumn column);

	public abstract ResultElementType getResultElementType();

	public abstract double getSeverity();
	
	public List<DITResultProblemInstance> collectProblemInstances() {
		List<DITResultProblemInstance>  result = new ArrayList<DITResultProblemInstance>();
		for(DITResultElement child : getChildren()){
			result.addAll(child.collectProblemInstances());
		}
		return result;
	}
	
	enum ResultElementType {
		BUSINESS_TRANSACTION("Business Transaction"), NODE_TYPE("Node Type"), ENTRY_POINT("Entry Point"), PROBLEM_INSTANCE("Problem Instance"), AFFECTED_NODE("Affected Node");

		private String name;

		private ResultElementType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getStringRepresentation() == null) ? 0 : getStringRepresentation().hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DITResultElement other = (DITResultElement) obj;
		if (getStringRepresentation() == null) {
			if (other.getStringRepresentation() != null) {
				return false;
			}
		} else if (!getStringRepresentation().equals(other.getStringRepresentation())) {
			return false;
		}
		return true;
	}


}
