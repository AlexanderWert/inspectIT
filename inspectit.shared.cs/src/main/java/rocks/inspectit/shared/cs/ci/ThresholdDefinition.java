/**
 *
 */
package rocks.inspectit.shared.cs.ci;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import rocks.inspectit.shared.all.exception.BusinessException;
import rocks.inspectit.shared.all.exception.enumeration.ConfigurationInterfaceErrorCodeEnum;

/**
 * XML element which represents a threshold used for alerting purpose.
 *
 * @author Marius Oehler
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "threshold-definition")
public class ThresholdDefinition extends AbstractCiData {

	/**
	 * The threshold.
	 */
	@XmlAttribute(name = "threshold")
	private Double threshold;

	/**
	 * The measurement to monitor.
	 */
	@XmlAttribute(name = "measurement")
	private String measurement;

	/**
	 * The tags used to select the monitored data.
	 */
	@XmlElementWrapper(name = "tags")
	@XmlElementRef
	private final List<String> tags = new ArrayList<String>();

	/**
	 * The duration between consecutive checks.
	 */
	@XmlAttribute(name = "timerange")
	private Long timerange;

	/**
	 * Gets {@link #threshold}.
	 *
	 * @return {@link #threshold}
	 */
	public double getThreshold() {
		return threshold.doubleValue();
	}

	/**
	 * Sets {@link #threshold}.
	 *
	 * @param threshold
	 *            New value for {@link #threshold}
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	/**
	 * Gets {@link #measurement}.
	 *
	 * @return {@link #measurement}
	 */
	public String getMeasurement() {
		return measurement;
	}

	/**
	 * Sets {@link #measurement}.
	 *
	 * @param measurement
	 *            New value for {@link #measurement}
	 */
	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}

	/**
	 * Gets {@link #timerange}.
	 *
	 * @return {@link #timerange}
	 */
	public Long getTimerange() {
		return timerange.longValue();
	}

	/**
	 * Sets {@link #timerange}.
	 *
	 * @param timerange
	 *            New value for {@link #timerange}
	 */
	public void setTimerange(Long timerange) {
		this.timerange = Long.valueOf(timerange);
	}

	/**
	 * Gets {@link #tags}.
	 *
	 * @return {@link #tags}
	 */
	public List<String> getTags() {
		List<String> allTags = new ArrayList<String>(tags);
		return Collections.unmodifiableList(allTags);
	}

	/**
	 * Adds a tag to the threshold definition.
	 *
	 * @param tag
	 *            the tag to add
	 * @return result of the adding (as specified by Collection.add)
	 * @throws BusinessException
	 *             if the tag cannot be added
	 */
	public boolean addTag(String tag) throws BusinessException {
		if (tag == null) {
			throw new BusinessException("Adding tag 'null'.", ConfigurationInterfaceErrorCodeEnum.TAG_IS_NULL);
		} else if (tag.isEmpty()) {
			throw new BusinessException("Adding empty tag.", ConfigurationInterfaceErrorCodeEnum.TAG_IS_EMPTY);
		} else {
			return tags.add(tag);
		}
	}
}
