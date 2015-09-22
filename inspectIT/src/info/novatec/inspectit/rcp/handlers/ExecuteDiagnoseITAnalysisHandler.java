package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.cmr.model.MethodIdent;
import info.novatec.inspectit.communication.data.InvocationSequenceData;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITAnalysisInputDefinitionExtra;
import info.novatec.inspectit.rcp.editor.inputdefinition.EditorPropertiesData;
import info.novatec.inspectit.rcp.editor.inputdefinition.EditorPropertiesData.PartType;
import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition;
import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition.IdDefinition;
import info.novatec.inspectit.rcp.editor.inputdefinition.extra.InputDefinitionExtrasMarkerFactory;
import info.novatec.inspectit.rcp.editor.root.AbstractRootEditor;
import info.novatec.inspectit.rcp.formatter.TextFormatter;
import info.novatec.inspectit.rcp.model.SensorTypeEnum;
import info.novatec.inspectit.rcp.repository.RepositoryDefinition;

import java.util.Iterator;

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

public class ExecuteDiagnoseITAnalysisHandler extends AbstractHandler {
private static InputDefinition inDef;
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof StructuredSelection) {
			AbstractRootEditor rootEditor = (AbstractRootEditor) HandlerUtil.getActiveEditor(event);
			RepositoryDefinition repositoryDefinition = rootEditor.getInputDefinition().getRepositoryDefinition();
			Object firstSelectedObject = ((StructuredSelection) selection).getFirstElement();
			if (firstSelectedObject instanceof InvocationSequenceData) {
				Iterator<?> iterator = ((StructuredSelection) selection).iterator();
				DITAnalysisInputDefinitionExtra ditExtra = new DITAnalysisInputDefinitionExtra();
				long methodId = 0;
				while (iterator.hasNext()) {
					Object next = iterator.next();
					if (next instanceof InvocationSequenceData) {
						ditExtra.addInvocationSequencId(((InvocationSequenceData) next).getId());
						methodId = ((InvocationSequenceData) next).getMethodIdent();
					}
				}
				if (ditExtra.getInvocationSequenceIds().isEmpty()) {
					return null;
				}

				InputDefinition inputDefinition = new InputDefinition();
				inputDefinition.setRepositoryDefinition(repositoryDefinition);
				inputDefinition.setId(SensorTypeEnum.DIAGNOSEIT_RESULTS);

				inputDefinition.addInputDefinitonExtra(InputDefinitionExtrasMarkerFactory.DIAGNOSEIT_ANALYSIS_EXTRAS_MARKER, ditExtra);

				EditorPropertiesData editorPropertiesData = new EditorPropertiesData();
				editorPropertiesData.setSensorName(SensorTypeEnum.DIAGNOSEIT_RESULTS.getDisplayName());
				editorPropertiesData.setSensorImage(SensorTypeEnum.DIAGNOSEIT_RESULTS.getImage());
				
				String viewName;
				if (ditExtra.getInvocationSequenceIds().size() == 1) {
					long id = ditExtra.getInvocationSequenceIds().iterator().next();
					MethodIdent methodIdent = repositoryDefinition.getCachedDataService().getMethodIdentForId(methodId);
					String methodName = TextFormatter.getMethodString(methodIdent);
					viewName = methodName + " [" + id + "]";
				} else {
					viewName = "multiple invocation sequences";
				}
				editorPropertiesData.setViewName(viewName);
				editorPropertiesData.setViewImage(InspectIT.getDefault().getImage(InspectITImages.IMG_EXCEPTION_TREE));
				editorPropertiesData.setPartNameFlag(PartType.SENSOR);
				inputDefinition.setEditorPropertiesData(editorPropertiesData);

				IdDefinition idDefinition = new IdDefinition();
				idDefinition.setSensorTypeId(SensorTypeEnum.DIAGNOSEIT_RESULTS.hashCode());
				if (ditExtra.getInvocationSequenceIds().size() == 1) {
					idDefinition.setMethodId(methodId);
				} else {
					idDefinition.setMethodId(ditExtra.getInvocationSequenceIds().hashCode());
				}
				
				

				idDefinition.setPlatformId(rootEditor.getInputDefinition().getIdDefinition().getPlatformId());
				inputDefinition.setIdDefinition(idDefinition);
				
				boolean b = inputDefinition.equals(inDef);
				inDef = inputDefinition;
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
