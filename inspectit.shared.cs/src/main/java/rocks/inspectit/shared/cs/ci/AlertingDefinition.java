/**
 *
 */
package rocks.inspectit.shared.cs.ci;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rocks.inspectit.shared.all.exception.BusinessException;
import rocks.inspectit.shared.all.exception.enumeration.AlertingDefinitionErrorCodeEnum;

/**
 * XML element which represents a threshold used for alerting purpose.
 *
 * @author Marius Oehler
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "alerting-definition")
public class AlertingDefinition extends AbstractCiData {

	private static final Logger LOG = LoggerFactory.getLogger(AlertingDefinition.class);

	/**
	 * Threshold types.
	 *
	 * @author Marius Oehler
	 *
	 */
	enum ThresholdType {
		/**
		 * The specified threshold is an upper threshold. Alert is issued if the threshold is
		 * exceeded.
		 */
		UPPER_THRESHOLD,

		/**
		 * The specified threshold is an lower threshold. Alert is issued if the threshold is
		 * undercut.
		 */
		LOWER_THRESHOLD
	}

	/**
	 * The threshold.
	 */
	@XmlAttribute(name = "threshold")
	private Double threshold;

	/**
	 * The type of the specified threshold.
	 */
	@XmlAttribute(name = "threshold-type")
	private ThresholdType thresholdType;

	/**
	 * The measurement to monitor.
	 */
	@XmlAttribute(name = "measurement")
	private String measurement;

	/**
	 * The tags used to select the monitored data.
	 */
	@XmlElementWrapper(name = "tags")
	private final HashMap<String, String> tags = new HashMap<>();

	/**
	 * The duration between consecutive checks in minutes.
	 */
	@XmlAttribute(name = "timerange")
	private Integer timerange;

	/**
	 * List of e-mails which receives a notification when the threshold is violated.
	 */
	@XmlElementWrapper(name = "notification-email-addresses")
	@XmlElement(name = "notification-email-address")
	private final List<String> notificationEmailAddresses = new ArrayList<>();

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
	public Integer getTimerange() {
		return timerange.intValue();
	}

	/**
	 * Sets {@link #timerange}.
	 *
	 * @param timerange
	 *            New value for {@link #timerange}
	 */
	public void setTimerange(Integer timerange) {
		this.timerange = Integer.valueOf(timerange);
	}

	/**
	 * Gets {@link #tags}.
	 *
	 * @return {@link #tags}
	 */
	public Map<String, String> getTags() {
		return Collections.unmodifiableMap(tags);
	}

	/**
	 * Puts a tag represented by the given key-value to the alerting definition. A previously added
	 * tag containing the same key will be overridden.
	 *
	 * @param tagKey
	 *            the tag key
	 * @param tagValue
	 *            the tag value
	 * @return returns the previous value related to the given key (as specified by
	 *         {@link Map#put(Object, Object)}
	 * @throws BusinessException
	 *             if the tag cannot be put
	 */
	public String putTag(String tagKey, String tagValue) throws BusinessException {
		if (tagKey == null) {
			throw new BusinessException("Putting tag with key 'null'.", AlertingDefinitionErrorCodeEnum.TAG_KEY_IS_NULL);
		} else if (tagValue == null) {
			throw new BusinessException("Putting tag with value 'null'.", AlertingDefinitionErrorCodeEnum.TAG_VALUE_IS_NULL);
		} else if (tagKey.isEmpty()) {
			throw new BusinessException("Putting tag with empty key.", AlertingDefinitionErrorCodeEnum.TAG_KEY_IS_EMPTY);
		} else if (tagValue.isEmpty()) {
			throw new BusinessException("Putting tag with empty value.", AlertingDefinitionErrorCodeEnum.TAG_VALUE_IS_EMPTY);
		} else {
			return tags.put(tagKey, tagValue);
		}
	}

	public synchronized void replaceTags(Map<String, String> newTags) throws BusinessException {
		if (newTags == null) {
			throw new BusinessException("Replacing the current tags with a null map.", AlertingDefinitionErrorCodeEnum.REPLACING_WITH_NULL);
		}

		tags.clear();

		for (Entry<String, String> tag : newTags.entrySet()) {
			try {
				putTag(tag.getKey(), tag.getValue());
			} catch (BusinessException e) {
				LOG.info(e.getActionPerformed());
			}
		}
	}

	/**
	 * Removes the tag with the given key from the alerting definition.
	 *
	 * @param tagKey
	 *            the key of the tag to remove
	 * @throws BusinessException
	 *             if the tag cannot be removed
	 */
	public void removeTag(String tagKey) throws BusinessException {
		if (tagKey == null) {
			throw new BusinessException("Adding tag with key 'null'.", AlertingDefinitionErrorCodeEnum.TAG_KEY_IS_NULL);
		} else if (tagKey.isEmpty()) {
			throw new BusinessException("Adding tag with empty key.", AlertingDefinitionErrorCodeEnum.TAG_KEY_IS_EMPTY);
		} else if (!tags.containsKey(tagKey)) {
			throw new BusinessException("Removing tag with key '" + tagKey + "'.", AlertingDefinitionErrorCodeEnum.TAG_KEY_DOES_NOT_EXISTS);
		} else {
			tags.remove(tagKey);
		}
	}

	/**
	 * Gets {@link #thresholdType}.
	 *
	 * @return {@link #thresholdType}
	 */
	public ThresholdType getThresholdType() {
		return thresholdType;
	}

	/**
	 * Sets {@link #thresholdType}.
	 *
	 * @param thresholdType
	 *            New value for {@link #thresholdType}
	 */
	public void setThresholdType(ThresholdType thresholdType) {
		this.thresholdType = thresholdType;
	}

	/**
	 * Gets {@link #notificationEmailAddresses}.
	 *
	 * @return {@link #notificationEmailAddresses}
	 */
	public List<String> getNotificationEmailAddresses() {
		return Collections.unmodifiableList(notificationEmailAddresses);
	}

	/**
	 * Adds a email address to the alerting definition.
	 *
	 * @param email
	 *            the email address to add
	 * @return result of the adding (as specified by {@link Collection#add(Object)})
	 * @throws BusinessException
	 *             if the email address cannot be added
	 */
	public boolean addNotificationEmailAddress(String email) throws BusinessException {
		if (email == null) {
			throw new BusinessException("Adding email adress 'null'.", AlertingDefinitionErrorCodeEnum.EMAIL_IS_NULL);
		} else if (email.isEmpty()) {
			throw new BusinessException("Adding empty email address.", AlertingDefinitionErrorCodeEnum.EMAIL_IS_EMPTY);
		} else if (false) { // TODO use validator
			throw new BusinessException("Adding invalid email address '" + email + "'.", AlertingDefinitionErrorCodeEnum.EMAIL_IS_NOT_VALID);
		} else {
			return notificationEmailAddresses.add(email);
		}
	}

	/**
	 * Removes the given email address from the alerting definition.
	 *
	 * @param email
	 *            the email address to remove
	 * @return result of the removing (as specified by {@link Collection#remove(Object)}
	 * @throws BusinessException
	 *             if the email address cannot be removed
	 */
	public boolean removeNotificationEmailAddress(String email) throws BusinessException {
		if (email == null) {
			throw new BusinessException("Adding email adress 'null'.", AlertingDefinitionErrorCodeEnum.EMAIL_IS_NULL);
		} else if (email.isEmpty()) {
			throw new BusinessException("Adding empty email address.", AlertingDefinitionErrorCodeEnum.EMAIL_IS_EMPTY);
		} else {
			return notificationEmailAddresses.remove(email);
		}
	}

	/**
	 * Replaces the current notification email addresses with the ones of the given list.
	 *
	 * @param newNotificationEmailAddresses
	 *            new email addresses
	 * @throws BusinessException
	 *             if the given list is null
	 */
	public synchronized void replaceNotificationEmailAddresses(List<String> newNotificationEmailAddresses) throws BusinessException {
		if (newNotificationEmailAddresses == null) {
			throw new BusinessException("Replacing notification email list with 'null'.", AlertingDefinitionErrorCodeEnum.REPLACING_WITH_NULL);
		}

		notificationEmailAddresses.clear();

		for (String emailAddress : newNotificationEmailAddresses) {
			try {
				addNotificationEmailAddress(emailAddress);
			} catch (BusinessException e) {
				LOG.info(e.getActionPerformed());
			}
		}
	}
}
