package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.editor.inputdefinition.EditorPropertiesData;
import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition;
import info.novatec.inspectit.rcp.editor.inputdefinition.EditorPropertiesData.PartType;
import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition.IdDefinition;
import info.novatec.inspectit.rcp.model.SensorTypeEnum;
import info.novatec.inspectit.rcp.repository.RepositoryDefinition;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

public abstract class AbstractDiagnoseITResultsHandler extends AbstractHandler {
	protected void openDiagnoseITResultsView(ExecutionEvent event, RepositoryDefinition repositoryDefinition) {
		InputDefinition inputDefinition = new InputDefinition();
		inputDefinition.setRepositoryDefinition(repositoryDefinition);
		inputDefinition.setId(SensorTypeEnum.DIAGNOSEIT_RESULTS);

		EditorPropertiesData editorPropertiesData = new EditorPropertiesData();
		editorPropertiesData.setSensorName(SensorTypeEnum.DIAGNOSEIT_RESULTS.getDisplayName());
		editorPropertiesData.setSensorImage(SensorTypeEnum.DIAGNOSEIT_RESULTS.getImage());
		editorPropertiesData.setViewName("All Results");
		editorPropertiesData.setViewImage(InspectIT.getDefault().getImage(InspectITImages.IMG_SHOW_ALL));
		editorPropertiesData.setPartNameFlag(PartType.VIEW);
		inputDefinition.setEditorPropertiesData(editorPropertiesData);
		
		IdDefinition idDefinition = new IdDefinition();
		idDefinition.setSensorTypeId(SensorTypeEnum.DIAGNOSEIT_RESULTS.hashCode());
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
