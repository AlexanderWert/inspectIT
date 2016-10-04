/**
 *
 */
package rocks.inspectit.shared.cs.ci;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import org.mockito.InjectMocks;
import org.testng.annotations.Test;

import rocks.inspectit.shared.all.exception.BusinessException;
import rocks.inspectit.shared.all.testbase.TestBase;

/**
 * @author Marius Oehler
 *
 */
public class ThresholdDefinitionTest extends TestBase {

	@InjectMocks
	ThresholdDefinition thresholdDefinition;

	@Test
	public void addTag() throws BusinessException {
		assertThat(thresholdDefinition.getTags(), hasSize(0));

		thresholdDefinition.addTag("Tag");

		assertThat(thresholdDefinition.getTags(), hasSize(1));
	}

}
