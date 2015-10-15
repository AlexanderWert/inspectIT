package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.communication.DefaultData;
import info.novatec.inspectit.communication.data.InvocationSequenceData;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITInvocDetailLabelExtra;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITResultProblemInstance;
import info.novatec.inspectit.rcp.diagnoseit.overview.NameUtils;
import info.novatec.inspectit.rcp.editor.inputdefinition.EditorPropertiesData;
import info.novatec.inspectit.rcp.editor.inputdefinition.EditorPropertiesData.PartType;
import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition;
import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition.IdDefinition;
import info.novatec.inspectit.rcp.editor.inputdefinition.extra.InputDefinitionExtrasMarkerFactory;
import info.novatec.inspectit.rcp.editor.inputdefinition.extra.NavigationSteppingInputDefinitionExtra;
import info.novatec.inspectit.rcp.editor.root.AbstractRootEditor;
import info.novatec.inspectit.rcp.model.SensorTypeEnum;
import info.novatec.inspectit.rcp.repository.RepositoryDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.diagnoseit.spike.result.ProblemOccurrence;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

public class ShowAffectedInvocationSequencesHandler extends AbstractHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		AbstractRootEditor rootEditor = (AbstractRootEditor) HandlerUtil.getActiveEditor(event);
		RepositoryDefinition repositoryDefinition = rootEditor.getInputDefinition().getRepositoryDefinition();
		InputDefinition inputDefinition = null;

		Object selectedObject = selection.getFirstElement();

		if (selectedObject instanceof DITResultProblemInstance) {
			DITResultProblemInstance problemInstanceResult = (DITResultProblemInstance) selectedObject;

			openInvocationSequences(repositoryDefinition, problemInstanceResult);
		}

		return null;
	}

	public static void openInvocationSequences(RepositoryDefinition repositoryDefinition, DITResultProblemInstance problemInstanceResult) {
		InputDefinition inputDefinition;
		DITInvocDetailLabelExtra ditInvocDetailLabelExtra = new DITInvocDetailLabelExtra();

		// ------------------------------------------------------------------------------------
		// TODO: retrieve list of invocation sequence data ids that represent root cause nodes
		// TODO: retrieve list of invocation sequences representing affected traces

		List<Long> traceIds = new ArrayList<Long>();
		List<Long> causeIds = new ArrayList<Long>();
		List<Long> problemContextIds = new ArrayList<Long>();
		List<Long> entryPointIds = new ArrayList<Long>();

		for (ProblemOccurrence occurrence : problemInstanceResult.getProblemInstance().getProblemOccurrences()) {
			traceIds.add(((Optional<Long>) occurrence.getEntryPointIdentifier()).get());
			entryPointIds.add(((Optional<Long>) occurrence.getEntryPointIdentifier()).get());
			problemContextIds.add(((Optional<Long>) occurrence.getProblemContextIdentifier()).get());
			for (Object obj : occurrence.getCauseIdentifiers()) {
				causeIds.add(((Optional<Long>) obj).get());
			}

		}

		// ------------------------------------------------------------------------------------

		for (long epId : entryPointIds) {
			ditInvocDetailLabelExtra.addEntryPoint(epId);
		}
		for (long pcId : problemContextIds) {
			ditInvocDetailLabelExtra.addProblemContext(pcId);
		}

		InvocationSequenceData parentTemplate = new InvocationSequenceData();
		parentTemplate.setId(0L);
		parentTemplate.setMethodIdent(0L);
		for (long causeId : causeIds) {
			ditInvocDetailLabelExtra.addCause(causeId);
			InvocationSequenceData template = new InvocationSequenceData();
			template.setId(causeId);
			parentTemplate.getNestedSequences().add(template);
		}
		
		ditInvocDetailLabelExtra.setTraceIds(traceIds);
		

		List<DefaultData> steppingTemplates = new ArrayList<DefaultData>();
		steppingTemplates.add(parentTemplate);

		String textualDesc = NameUtils.getStringRepresentationFromElementData(problemInstanceResult.getProblemInstance().getCauseData());

		inputDefinition = new InputDefinition();
		inputDefinition.setRepositoryDefinition(repositoryDefinition);
		inputDefinition.setId(SensorTypeEnum.DIAGNOSEIT_RESULTS_SEQUENCES);

		EditorPropertiesData editorPropertiesData = new EditorPropertiesData();
		editorPropertiesData.setSensorImage(SensorTypeEnum.DIAGNOSEIT_RESULTS_SEQUENCES.getImage());
		editorPropertiesData.setSensorName(SensorTypeEnum.DIAGNOSEIT_RESULTS_SEQUENCES.getDisplayName());
		editorPropertiesData.setViewName("containing problem instance " + textualDesc);
		editorPropertiesData.setPartNameFlag(PartType.SENSOR);
		inputDefinition.setEditorPropertiesData(editorPropertiesData);

		IdDefinition idDefinition = new IdDefinition();
		idDefinition.setSensorTypeId(SensorTypeEnum.DIAGNOSEIT_RESULTS_SEQUENCES.hashCode());
		idDefinition.setMethodId(problemInstanceResult.getProblemInstance().getCause().hashCode());
		inputDefinition.setIdDefinition(idDefinition);

		NavigationSteppingInputDefinitionExtra navigationSteppingExtra = new NavigationSteppingInputDefinitionExtra();
		navigationSteppingExtra.setSteppingTemplateList(steppingTemplates);
		inputDefinition.addInputDefinitonExtra(InputDefinitionExtrasMarkerFactory.NAVIGATION_STEPPING_EXTRAS_MARKER, navigationSteppingExtra);

		inputDefinition.addInputDefinitonExtra(InputDefinitionExtrasMarkerFactory.DIAGNOSEIT_INVOC_DETAILS_LABEL_EXTRAS_MARKER, ditInvocDetailLabelExtra);

		// open the view via command
		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);

		Command command = commandService.getCommand(OpenViewHandler.COMMAND);
		ExecutionEvent executionEvent = handlerService.createExecutionEvent(command, new Event());
		IEvaluationContext context = (IEvaluationContext) executionEvent.getApplicationContext();
		context.addVariable(OpenViewHandler.INPUT, inputDefinition);

		try {
			command.executeWithChecks(executionEvent);
		} catch (Exception e) {
			InspectIT.getDefault().createErrorDialog(e.getMessage(), e, -1);
		}
	}

}
