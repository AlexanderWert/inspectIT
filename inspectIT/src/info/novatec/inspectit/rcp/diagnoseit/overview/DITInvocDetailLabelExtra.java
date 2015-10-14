package info.novatec.inspectit.rcp.diagnoseit.overview;

import info.novatec.inspectit.rcp.editor.inputdefinition.extra.IInputDefinitionExtra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DITInvocDetailLabelExtra implements IInputDefinitionExtra {

	private Map<Long, DITResultLabel> diagnoseITResultLabels = new HashMap<Long, DITResultLabel>();
    private List<Long> traceIds = new ArrayList<Long>();
	
	
	public DITResultLabel getDiagnoseITResultLabel(long id) {
		return diagnoseITResultLabels.get(id);
	}

	public void addBusinessTransaction(long id) {
		DITResultLabel label = diagnoseITResultLabels.get(id);
		if (label == null) {
			label = new DITResultLabel();
			diagnoseITResultLabels.put(id, label);
		}

		label.setBusinessTransaction();
	}

	public void addEntryPoint(long id) {
		DITResultLabel label = diagnoseITResultLabels.get(id);
		if (label == null) {
			label = new DITResultLabel();
			diagnoseITResultLabels.put(id, label);
		}

		label.setEntryPoint();
	}

	public void addProblemContext(long id) {
		DITResultLabel label = diagnoseITResultLabels.get(id);
		if (label == null) {
			label = new DITResultLabel();
			diagnoseITResultLabels.put(id, label);
		}

		label.setProblemContext();
	}

	public void addCause(long id) {
		DITResultLabel label = diagnoseITResultLabels.get(id);
		if (label == null) {
			label = new DITResultLabel();
			diagnoseITResultLabels.put(id, label);
		}

		label.setCause();
	}
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((diagnoseITResultLabels == null) ? 0 : diagnoseITResultLabels.hashCode());
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
		DITInvocDetailLabelExtra other = (DITInvocDetailLabelExtra) obj;
		if (diagnoseITResultLabels == null) {
			if (other.diagnoseITResultLabels != null)
				return false;
		} else if (!diagnoseITResultLabels.equals(other.diagnoseITResultLabels))
			return false;
		return true;
	}



	public List<Long> getTraceIds() {
		return traceIds;
	}

	public void setTraceIds(List<Long> traceIds) {
		this.traceIds = traceIds;
	}



	public class DITResultLabel {
		private static final int BUSINESS_TRANSACTION = 1;
		private static final int ENTRY_POINT = 2;
		private static final int PROBLEM_CONTEXT = 4;
		private static final int PROBLEM_CAUSE = 8;

		private int value = 0;

		public void setBusinessTransaction() {
			value = value | BUSINESS_TRANSACTION;
		}

		public boolean isBusinessTransaction() {
			return (value & BUSINESS_TRANSACTION) != 0;
		}

		public void setEntryPoint() {
			value = value | ENTRY_POINT;
		}

		public boolean isEntryPoint() {
			return (value & ENTRY_POINT) != 0;
		}

		public void setProblemContext() {
			value = value | PROBLEM_CONTEXT;
		}

		public boolean isProblemContext() {
			return (value & PROBLEM_CONTEXT) != 0;
		}

		public void setCause() {
			value = value | PROBLEM_CAUSE;
		}

		public boolean isCause() {
			return (value & PROBLEM_CAUSE) != 0;
		}
	}

}
