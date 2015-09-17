package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.editor.inputdefinition.EditorPropertiesData;
import info.novatec.inspectit.rcp.editor.inputdefinition.EditorPropertiesData.PartType;
import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition;
import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition.IdDefinition;
import info.novatec.inspectit.rcp.model.SensorTypeEnum;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler for opening the diagnoseIT results view.
 * 
 * @author Alexander Wert
 *
 */
public class OpenDiagnoseITResultsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof StructuredSelection) {
			Object selectedObject = ((StructuredSelection) selection).getFirstElement();
			if (selectedObject instanceof ICmrRepositoryProvider) {
				final CmrRepositoryDefinition cmrRepositoryDefinition = ((ICmrRepositoryProvider) selectedObject).getCmrRepositoryDefinition();

				InputDefinition inputDefinition = new InputDefinition();
				inputDefinition.setRepositoryDefinition(cmrRepositoryDefinition);
				inputDefinition.setId(SensorTypeEnum.DIAGNOSEIT_RESULTS);

				EditorPropertiesData editorPropertiesData = new EditorPropertiesData();
				editorPropertiesData.setSensorName(SensorTypeEnum.DIAGNOSEIT_RESULTS.getDisplayName());
				editorPropertiesData.setSensorImage(SensorTypeEnum.DIAGNOSEIT_RESULTS.getImage());
				editorPropertiesData.setViewName("All");
				editorPropertiesData.setViewImage(InspectIT.getDefault().getImage(InspectITImages.IMG_SHOW_ALL));
				editorPropertiesData.setPartNameFlag(PartType.SENSOR);
				inputDefinition.setEditorPropertiesData(editorPropertiesData);

				inputDefinition.setIdDefinition(new IdDefinition());
				// open the view via command
				ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);

				Command command = commandService.getCommand(OpenViewHandler.COMMAND);
				IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();
				context.addVariable(OpenViewHandler.INPUT, inputDefinition);

				try {
					command.executeWithChecks(event);
				} catch (Exception e) {
					InspectIT.getDefault().createErrorDialog(e.getMessage(), e, -1);
				}

			}
		}

		return null;
	}

}
