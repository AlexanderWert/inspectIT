package rocks.inspectit.server.mail.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import rocks.inspectit.server.mail.IEMailSender;
import rocks.inspectit.shared.all.cmr.property.spring.PropertyUpdate;
import rocks.inspectit.shared.all.spring.logger.Log;

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
	 * Unwrapped list of default recipients.
	 */
	private final List<String> defaultRecipients = new ArrayList<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sendEMail(List<String> recipients, String subject, String htmlMessage, String textMessage) {
		try {
			HtmlEmail email = prepareHtmlEmail(recipients);
			email.setSubject(subject);
			email.setHtmlMsg(htmlMessage);
			email.setTextMsg(textMessage);
			email.send();
			return true;
		} catch (EmailException e) {
			log.warn("Failed sneding e-mail!", e);
			return false;
		}
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
	@PostConstruct
	@PropertyUpdate(properties = { "mail.default.to" })
	public void parseRecipientsString() {
		if (null != defaultRecipientString) {
			defaultRecipients.clear();
			String[] strArray = defaultRecipientString.split(",");
			for (String element : strArray) {
				defaultRecipients.add(element.trim());
			}
		}
	}
}
