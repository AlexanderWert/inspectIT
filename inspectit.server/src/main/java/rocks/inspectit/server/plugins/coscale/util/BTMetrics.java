package rocks.inspectit.server.plugins.coscale.util;

/**
 * @author Alexander Wert
 *
 */
public class BTMetrics {
	private long responseTimeMetricId;
	private long rtThresholdMetricId;

	/**
	 * Gets {@link #responseTimeMetricId}.
	 * 
	 * @return {@link #responseTimeMetricId}
	 */
	public long getResponseTimeMetricId() {
		return this.responseTimeMetricId;
	}

	/**
	 * Sets {@link #responseTimeMetricId}.
	 * 
	 * @param responseTimeMetricId
	 *            New value for {@link #responseTimeMetricId}
	 */
	public void setResponseTimeMetricId(long responseTimeMetricId) {
		this.responseTimeMetricId = responseTimeMetricId;
	}

	/**
	 * Gets {@link #rtThresholdMetricId}.
	 * 
	 * @return {@link #rtThresholdMetricId}
	 */
	public long getRtThresholdMetricId() {
		return this.rtThresholdMetricId;
	}

	/**
	 * Sets {@link #rtThresholdMetricId}.
	 * 
	 * @param rtThresholdMetricId
	 *            New value for {@link #rtThresholdMetricId}
	 */
	public void setRtThresholdMetricId(long rtThresholdMetricId) {
		this.rtThresholdMetricId = rtThresholdMetricId;
	}

}
