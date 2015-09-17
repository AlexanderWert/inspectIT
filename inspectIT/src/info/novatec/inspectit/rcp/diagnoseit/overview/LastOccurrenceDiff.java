package info.novatec.inspectit.rcp.diagnoseit.overview;

import java.util.concurrent.TimeUnit;

public class LastOccurrenceDiff {
	private int diff;
	private TimeUnit timeUnit;

	@Override
	public String toString() {
		String result = String.valueOf(diff);
		switch (timeUnit) {
		case NANOSECONDS:
		case MICROSECONDS:
		case MILLISECONDS:
		case SECONDS:
			result += " seconds";
			break;
		case MINUTES:
			result += " minutes";
			break;
		case HOURS:
			result += " hours";
			break;
		case DAYS:
			result += " days";
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * Gets {@link #diff}.
	 *   
	 * @return {@link #diff}  
	 */
	public int getDiff() {
		return diff;
	}

	/**  
	 * Sets {@link #diff}.  
	 *   
	 * @param diff  
	 *            New value for {@link #diff}  
	 */
	public void setDiff(int diff) {
		this.diff = diff;
	}

	/**
	 * Gets {@link #timeUnit}.
	 *   
	 * @return {@link #timeUnit}  
	 */
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	/**  
	 * Sets {@link #timeUnit}.  
	 *   
	 * @param timeUnit  
	 *            New value for {@link #timeUnit}  
	 */
	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}
	
}
