package info.novatec.inspectit.rcp.diagnoseit.details;

import org.diagnoseit.spike.traceservices.aggregation.AbstractAggregatedTimedCallable;
import org.diagnoseit.spike.traceservices.aggregation.NumericStatistics;

import rocks.cta.api.core.callables.TimedCallable;

public class ContextInformationInputElement {
	private String name;
	private String context;
	private AbstractAggregatedTimedCallable<? extends TimedCallable> data;
	private NumericStatistics<Integer> countStatistics;
	private NumericStatistics<Long> exclusiveTimeSumStatistics;
	private NumericStatistics<Long> exclusiveCPUTimeSumStatistics;
	private String imageIdentifier;

	public ContextInformationInputElement(String name, String context, String imageIdentifier, AbstractAggregatedTimedCallable<? extends TimedCallable> data,
			NumericStatistics<Long> exclusiveTimeSumStatistics, NumericStatistics<Long> exclusiveCPUTimeSumStatistics, NumericStatistics<Integer> countStatistics) {
		super();
		this.name = name;
		this.imageIdentifier = imageIdentifier;
		this.context = context;
		this.data = data;
		this.exclusiveTimeSumStatistics = exclusiveTimeSumStatistics;
		this.exclusiveCPUTimeSumStatistics = exclusiveCPUTimeSumStatistics;
		this.setCountStatistics(countStatistics);
	}

	/**
	 * Gets {@link #name}.
	 * 
	 * @return {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@link #name}.
	 * 
	 * @param name
	 *            New value for {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets {@link #context}.
	 * 
	 * @return {@link #context}
	 */
	public String getContext() {
		return context;
	}

	/**
	 * Sets {@link #context}.
	 * 
	 * @param context
	 *            New value for {@link #context}
	 */
	public void setContext(String context) {
		this.context = context;
	}

	/**
	 * Gets {@link #data}.
	 * 
	 * @return {@link #data}
	 */
	public AbstractAggregatedTimedCallable<? extends TimedCallable> getData() {
		return data;
	}

	/**
	 * Sets {@link #data}.
	 * 
	 * @param data
	 *            New value for {@link #data}
	 */
	public void setData(AbstractAggregatedTimedCallable<? extends TimedCallable> data) {
		this.data = data;
	}

	/**
	 * Gets {@link #exclusiveTimeSumStatistics}.
	 * 
	 * @return {@link #exclusiveTimeSumStatistics}
	 */
	public NumericStatistics<Long> getExclusiveTimeSumStatistics() {
		return exclusiveTimeSumStatistics;
	}

	/**
	 * Sets {@link #exclusiveTimeSumStatistics}.
	 * 
	 * @param exclusiveTimeSumStatistics
	 *            New value for {@link #exclusiveTimeSumStatistics}
	 */
	public void setExclusiveTimeSumStatistics(NumericStatistics<Long> exclusiveTimeSumStatistics) {
		this.exclusiveTimeSumStatistics = exclusiveTimeSumStatistics;
	}

	/**
	 * Gets {@link #exclusiveCPUTimeSumStatistics}.
	 * 
	 * @return {@link #exclusiveCPUTimeSumStatistics}
	 */
	public NumericStatistics<Long> getExclusiveCPUTimeSumStatistics() {
		return exclusiveCPUTimeSumStatistics;
	}

	/**
	 * Sets {@link #exclusiveCPUTimeSumStatistics}.
	 * 
	 * @param exclusiveCPUTimeSumStatistics
	 *            New value for {@link #exclusiveCPUTimeSumStatistics}
	 */
	public void setExclusiveCPUTimeSumStatistics(NumericStatistics<Long> exclusiveCPUTimeSumStatistics) {
		this.exclusiveCPUTimeSumStatistics = exclusiveCPUTimeSumStatistics;
	}

	public String getImageIdentifier() {
		return imageIdentifier;
	}

	public void setImageIdentifier(String imageIdentifier) {
		this.imageIdentifier = imageIdentifier;
	}

	public NumericStatistics<Integer> getCountStatistics() {
		return countStatistics;
	}

	public void setCountStatistics(NumericStatistics<Integer> countStatistics) {
		this.countStatistics = countStatistics;
	}

}
