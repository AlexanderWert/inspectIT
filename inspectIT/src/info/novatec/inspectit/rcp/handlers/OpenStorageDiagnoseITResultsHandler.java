package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.exception.BusinessException;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition;
import info.novatec.inspectit.rcp.provider.IStorageDataProvider;
import info.novatec.inspectit.rcp.repository.RepositoryDefinition;
import info.novatec.inspectit.rcp.storage.InspectITStorageManager;
import info.novatec.inspectit.storage.LocalStorageData;
import info.novatec.inspectit.storage.serializer.SerializationException;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenStorageDiagnoseITResultsHandler extends AbstractDiagnoseITResultsHandler {
	private static InputDefinition inDef;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof StructuredSelection) {
			Object firstSelectedObject = ((StructuredSelection) selection).getFirstElement();
			if (firstSelectedObject instanceof IStorageDataProvider) {
				IStorageDataProvider storageProvider = (IStorageDataProvider) firstSelectedObject;

				InspectITStorageManager storageManager = InspectIT.getDefault().getInspectITStorageManager();
				LocalStorageData localStorageData = storageManager.getLocalDataForStorage(storageProvider.getStorageData());
				RepositoryDefinition repositoryDefinition = null;
				try {
					repositoryDefinition = storageManager.getStorageRepositoryDefinition(localStorageData);
				} catch (IOException | BusinessException | SerializationException e) {
					throw new ExecutionException(e.getMessage());
				}

				openDiagnoseITResultsView(event, repositoryDefinition);

			}
		}
		return null;
	}

}
