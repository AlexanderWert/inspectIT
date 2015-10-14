package info.novatec.inspectit.rcp.editor.testers;

import info.novatec.inspectit.rcp.editor.root.AbstractRootEditor;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.RepositoryDefinition;
import info.novatec.inspectit.rcp.repository.StorageRepositoryDefinition;

import org.eclipse.core.expressions.PropertyTester;

public class RepositoryDefinitionTypeTester extends PropertyTester {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof AbstractRootEditor) {
			AbstractRootEditor rootEditor = (AbstractRootEditor) receiver;
			if ("isCMRRepositoryAvailable".equals(property)) {
				RepositoryDefinition repositoryDefinition = rootEditor.getInputDefinition().getRepositoryDefinition();
				if (repositoryDefinition instanceof CmrRepositoryDefinition) {
					return true;
				} else if (repositoryDefinition instanceof StorageRepositoryDefinition) {
					StorageRepositoryDefinition storageRD = (StorageRepositoryDefinition) repositoryDefinition;
					return null != storageRD.getCmrRepositoryDefinition();
				} else {
					return false;
				}
			}
		}
		return false;
	}

}