/**
 *
 */
package rocks.inspectit.shared.cs.ci;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.mockito.InjectMocks;
import org.testng.annotations.Test;

import rocks.inspectit.shared.all.exception.BusinessException;
import rocks.inspectit.shared.all.testbase.TestBase;

/**
 * @author Marius Oehler
 *
 */
public class AlertingDefinitionTest extends TestBase {

	@InjectMocks
	AlertingDefinition alertingDefinition;

	/**
	 * Test the {@link AlertingDefinition#putTag(String, String)},
	 * {@link AlertingDefinition#getTags()} and {@link AlertingDefinition#removeTag(String)}
	 * methods.
	 */
	public static class Tags extends AlertingDefinitionTest {

		@Test
		public void putValidTag() throws BusinessException {
			String tagKey = "tagKey";
			String tagValue = "tagValue";

			assertThat(alertingDefinition.getTags().size(), equalTo(0));
			alertingDefinition.putTag(tagKey, tagValue);
			assertThat(alertingDefinition.getTags().size(), equalTo(1));

			String returnedTagValue = alertingDefinition.getTags().get(tagKey);
			assertThat(returnedTagValue, equalTo(tagValue));
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void putNullTagKey() throws BusinessException {
			alertingDefinition.putTag(null, "tagValue");
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void putNullTagValue() throws BusinessException {
			alertingDefinition.putTag("tagKey", null);
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void putEmptyTagKey() throws BusinessException {
			alertingDefinition.putTag("", "tagValue");
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void putEmptyTagValue() throws BusinessException {
			alertingDefinition.putTag("tagKey", "");
		}

		@Test
		public void removeTag() throws BusinessException {
			String key = "tagKey";

			assertThat(alertingDefinition.getTags().size(), equalTo(0));
			alertingDefinition.putTag(key, "tagValue");
			assertThat(alertingDefinition.getTags().size(), equalTo(1));
			alertingDefinition.removeTag(key);
			assertThat(alertingDefinition.getTags().size(), equalTo(0));
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void removeNullTag() throws BusinessException {
			assertThat(alertingDefinition.getTags().size(), equalTo(0));
			alertingDefinition.putTag("tagKey", "tagValue");
			assertThat(alertingDefinition.getTags().size(), equalTo(1));
			alertingDefinition.removeTag(null);
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void removeEmptyTag() throws BusinessException {
			assertThat(alertingDefinition.getTags().size(), equalTo(0));
			alertingDefinition.putTag("tagKey", "tagValue");
			assertThat(alertingDefinition.getTags().size(), equalTo(1));
			alertingDefinition.removeTag("");
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void removeUnknownTag() throws BusinessException {
			assertThat(alertingDefinition.getTags().size(), equalTo(0));
			alertingDefinition.putTag("tagKey", "tagValue");
			assertThat(alertingDefinition.getTags().size(), equalTo(1));
			alertingDefinition.removeTag("unknownKey");
		}
	}

	/**
	 * Test the {@link AlertingDefinition#addNotificationEmailAddress(String)},
	 * {@link AlertingDefinition#getNotificationEmailAddresses()} and
	 * {@link AlertingDefinition#removeNotificationEmailAddress(String)} methods.
	 */
	public static class NotificationEmailAddresses extends AlertingDefinitionTest {
		@Test
		public void putValidEmail() throws BusinessException {
			String mailAddress = "test@example.com";

			assertThat(alertingDefinition.getNotificationEmailAddresses(), hasSize(0));
			alertingDefinition.addNotificationEmailAddress(mailAddress);
			assertThat(alertingDefinition.getNotificationEmailAddresses(), hasSize(1));

			String returnedEmail = alertingDefinition.getNotificationEmailAddresses().get(0);
			assertThat(returnedEmail, equalTo(mailAddress));
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void putEmptyEmail() throws BusinessException {
			alertingDefinition.addNotificationEmailAddress("");
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void putNullEmail() throws BusinessException {
			alertingDefinition.addNotificationEmailAddress(null);
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void putInvalidEmail() throws BusinessException {
			alertingDefinition.addNotificationEmailAddress("not_an_email@");
		}

		@Test
		public void removeEmail() throws BusinessException {
			String mailAddress = "test@example.com";

			assertThat(alertingDefinition.getNotificationEmailAddresses(), hasSize(0));
			alertingDefinition.addNotificationEmailAddress(mailAddress);
			assertThat(alertingDefinition.getNotificationEmailAddresses(), hasSize(1));

			boolean result = alertingDefinition.removeNotificationEmailAddress(mailAddress);

			assertThat(result, is(true));
			assertThat(alertingDefinition.getNotificationEmailAddresses(), hasSize(0));
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void removeNullEmail() throws BusinessException {
			assertThat(alertingDefinition.getNotificationEmailAddresses(), hasSize(0));
			alertingDefinition.addNotificationEmailAddress("test@example.com");
			assertThat(alertingDefinition.getNotificationEmailAddresses(), hasSize(1));
			alertingDefinition.removeNotificationEmailAddress(null);
		}

		@Test(expectedExceptions = { BusinessException.class })
		public void removeEmptyEmail() throws BusinessException {
			assertThat(alertingDefinition.getNotificationEmailAddresses(), hasSize(0));
			alertingDefinition.addNotificationEmailAddress("test@example.com");
			assertThat(alertingDefinition.getNotificationEmailAddresses(), hasSize(1));
			alertingDefinition.removeNotificationEmailAddress("");
		}

		@Test
		public void removeUnknownEmail() throws BusinessException {
			assertThat(alertingDefinition.getNotificationEmailAddresses(), hasSize(0));
			alertingDefinition.addNotificationEmailAddress("test@example.com");
			assertThat(alertingDefinition.getNotificationEmailAddresses(), hasSize(1));
			boolean result = alertingDefinition.removeNotificationEmailAddress("other@example.com");

			assertThat(result, is(false));
		}
	}
}
