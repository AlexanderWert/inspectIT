package info.novatec.inspectit.rcp.diagnoseit.overview;

import info.novatec.inspectit.rcp.editor.inputdefinition.extra.IInputDefinitionExtra;

import java.util.HashSet;
import java.util.Set;

public class DITAnalysisInputDefinitionExtra implements IInputDefinitionExtra {
	private Set<Long> invocationSequenceIds = new HashSet<Long>();

	public void addInvocationSequencId(long id) {
		invocationSequenceIds.add(id);
	}

	public Set<Long> getInvocationSequenceIds() {
		return invocationSequenceIds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((invocationSequenceIds == null) ? 0 : invocationSequenceIds.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DITAnalysisInputDefinitionExtra other = (DITAnalysisInputDefinitionExtra) obj;
		if (invocationSequenceIds == null) {
			if (other.invocationSequenceIds != null)
				return false;
		} else if (!invocationSequenceIds.equals(other.invocationSequenceIds))
			return false;
		return true;
	}
	
	
}
