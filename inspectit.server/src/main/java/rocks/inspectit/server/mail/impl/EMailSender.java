package rocks.inspectit.server.mail.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import rocks.inspectit.server.mail.IEMailSender;
import rocks.inspectit.shared.all.cmr.property.spring.PropertyUpdate;
import rocks.inspectit.shared.all.spring.logger.Log;
import rocks.inspectit.shared.all.util.StringUtils;

/**
 * Central component for sending e-mails.
 *
 * @author Alexander Wert
 *
 */
@Component
public class EMailSender implements IEMailSender {
	/**
	 * Logger for the class.
	 */
	@Log
	Logger log;

	/**
	 * SMTP Server host.
	 */
	@Value("${mail.smpt.host}")
	private String smptHost;

	/**
	 * SMTP Server port.
	 */
	@Value("${mail.smpt.port}")
	private int smptPort;

	/**
	 * SMTP user name.
	 */
	@Value("${mail.smtp.user}")
	private String smptUser;

	/**
	 * Password for SMTP authentication.
	 */
	@Value("${mail.smtp.passwd}")
	private String smptPassword;

	/**
	 * The e-mail address used as sender.
	 */
	@Value("${mail.from}")
	private String senderAddress;

	/**
	 * Displayed name of the sender.
	 */
	@Value("${mail.from.name}")
	private String senderName;

	/**
	 * A comma separated list of default recipient e-mail addresses.
	 */
	@Value("${mail.default.to}")
	private String defaultRecipientString;

	/**
	 * Additional SMTP properties as a comma separated string.
	 */
	@Value("${mail.smtp.properties}")
	private String smtpPropertiesString;

	/**
	 * Unwrapped list of default recipients.
	 */
	private final List<String> defaultRecipients = new ArrayList<>();

	/**
	 * SMTP connection state.
	 */
	private boolean connected = false;

	/**
	 * Additional SMTP properties that might be required for certain SMTP servers.
	 */
	private final Properties additionalProperties = new Properties();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sendEMail(String subject, String htmlMessage, String textMessage, List<String> recipients) {
		if (!connected) {
			log.warn("Failed sending e-mail! E-Mail service cannot connect to the SMTP server. Check the connection settings!");
			return false;
		}
		try {
			HtmlEmail email = prepareHtmlEmail(recipients);
			email.setSubject(subject);
			email.setHtmlMsg(htmlMessage);
			email.setTextMsg(textMessage);
			email.send();
			return true;
		} catch (EmailException e) {
			log.warn("Failed sending e-mail!", e);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sendEMail(String subject, String htmlMessage, String textMessage, String... recipients) {
		List<String> recipientsList = new ArrayList<>();
		for (String recipient : recipients) {
			recipientsList.add(recipient);
		}

		return sendEMail(subject, htmlMessage, textMessage, recipientsList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Prepares an email object.
	 *
	 * @param recipients
	 *            recipient to send to.
	 * @return Returns a prepared {@link HtmlEmail} object.
	 */
	private HtmlEmail prepareHtmlEmail(List<String> recipients) {
		HtmlEmail email = new HtmlEmail();
		email.setHostName(smptHost);
		email.setSmtpPort(smptPort);
		email.setAuthentication(smptUser, smptPassword);

		try {
			email.setFrom(senderAddress, senderName);
		} catch (EmailException e) {
			log.warn("Invalid sender e-mail address!", e);
		}
		for (String defaultTo : defaultRecipients) {
			try {
				email.addTo(defaultTo);
			} catch (EmailException e) {
				log.warn("Invalid recipient e-mail address!", e);
			}
		}
		for (String to : recipients) {
			try {
				email.addTo(to);
			} catch (EmailException e) {
				log.warn("Invalid recipient e-mail address!", e);
			}
		}

		return email;
	}

	/**
	 * Unwrap the comma separated list string of default recipients into a real list.
	 */
	@PropertyUpdate(properties = { "mail.default.to" })
	protected void parseRecipientsString() {
		if (null != defaultRecipientString) {
			defaultRecipients.clear();
			String[] strArray = defaultRecipientString.split(",");
			for (String element : strArray) {
				String address = element.trim();
				if (StringUtils.isValidEmailAddress(address)) {
					defaultRecipients.add(address);
				}
			}
		}

	}

	/**
	 * Unwrap the comma separated list string of additional properties into real properties object.
	 */
	@PropertyUpdate(properties = { "mail.smtp.properties" })
	protected void parseAdditionalPropertiesString() {
		if (null != smtpPropertiesString) {
			additionalProperties.clear();
			String[] strArray = smtpPropertiesString.split(",");
			for (String property : strArray) {
				int equalsIndex = property.indexOf('=');
				if ((equalsIndex > 0) && (equalsIndex < (property.length() - 1))) {
					additionalProperties.put(property.substring(0, equalsIndex), property.substring(equalsIndex + 1));
				}
			}
		}
	}
	/**
	 * Checks connection to SMTP server.
	 */
	@PropertyUpdate(properties = { "mail.smpt.host", "mail.smpt.port", "mail.smpt.user", "mail.smpt.passwd" })
	protected void checkConnection() {
		try {
			Session session = Session.getInstance(additionalProperties, new DefaultAuthenticator(smptUser, smptPassword));
			Transport transport = session.getTransport("smtp");
			transport.connect(smptHost, smptPort, smptUser, smptPassword);
			transport.close();
			log.info("|-eMail Service active and connected...");
			connected = true;
		} catch (MessagingException e) {
			log.warn("|-eMail Service was not able to connect! Check connection settings!");
			connected = false;
		}
	}

	/**
	 * Initialize E-Mail service.
	 */
	@PostConstruct
	protected void init() {
		parseRecipientsString();
		parseAdditionalPropertiesString();
		checkConnection();
	}
}
