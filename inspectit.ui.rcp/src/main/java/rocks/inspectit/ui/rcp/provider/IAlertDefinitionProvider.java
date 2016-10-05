package rocks.inspectit.ui.rcp.provider;

import rocks.inspectit.shared.cs.ci.AlertingDefinition;

/**
 * @author Alexander Wert
 *
 */
public interface IAlertDefinitionProvider extends ICmrRepositoryProvider, Comparable<IAlertDefinitionProvider> {
	public AlertingDefinition getAlertDefinition();
}
