package rocks.inspectit.server.mail;

import java.util.List;

/**
 * Interface for an e-mail sending service.
 *
 * @author Alexander Wert
 *
 */
public interface IEMailSender {
	/**
	 * Sends a HTML/Text email with the given parameters.
	 *
	 * @param recipients
	 *            List of recipient e-mail addresses.
	 * @param subject
	 *            The e-mail subject
	 * @param htmlMessage
	 *            The HTML e-mail message.
	 * @param textMessage
	 *            The alternative textual e-mail message.
	 * @return <code>true</code>, if the e-mail has been sent successfully, otherwise false.
	 */
	boolean sendEMail(List<String> recipients, String subject, String htmlMessage, String textMessage);
}
