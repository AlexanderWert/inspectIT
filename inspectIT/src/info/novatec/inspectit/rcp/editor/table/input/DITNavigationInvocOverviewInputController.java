package info.novatec.inspectit.rcp.editor.table.input;

import info.novatec.inspectit.cmr.model.PlatformIdent;
import info.novatec.inspectit.cmr.service.IGlobalDataAccessService;
import info.novatec.inspectit.communication.comparator.ResultComparator;
import info.novatec.inspectit.communication.data.InvocationSequenceData;
import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition;
import info.novatec.inspectit.rcp.editor.inputdefinition.extra.InputDefinitionExtrasMarkerFactory;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceId;
import info.novatec.inspectit.rcp.editor.root.IRootEditor;
import info.novatec.inspectit.rcp.editor.table.TableViewerComparator;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * A extension of the {@link InvocOverviewInputController} that displays the invocations that are
 * statically transfered to the view via invocation aware data.
 * 
 * @author Ivan Senic
 * 
 */
public class DITNavigationInvocOverviewInputController extends InvocOverviewInputController {

	/**
	 * The ID of this subview / controller.
	 */
	public static final String ID = "inspectit.subview.table.navigationinvocoverview";

	/**
	 * List of all invocation sequences ids that can be displayed.
	 */
	private List<Long> invocationSequenceIds;

	private IGlobalDataAccessService globalDataAccessService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInputDefinition(InputDefinition inputDefinition) {
		if (inputDefinition.hasInputDefinitionExtra(InputDefinitionExtrasMarkerFactory.DIAGNOSEIT_INVOC_DETAILS_LABEL_EXTRAS_MARKER)) {
			invocationSequenceIds = inputDefinition.getInputDefinitionExtra(InputDefinitionExtrasMarkerFactory.DIAGNOSEIT_INVOC_DETAILS_LABEL_EXTRAS_MARKER).getTraceIds();
		}
		globalDataAccessService = inputDefinition.getRepositoryDefinition().getGlobalDataAccessService();

		super.setInputDefinition(inputDefinition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<PreferenceId> getPreferenceIds() {
		Set<PreferenceId> preferences = EnumSet.noneOf(PreferenceId.class);
		if (getInputDefinition().getRepositoryDefinition() instanceof CmrRepositoryDefinition) {
			preferences.add(PreferenceId.CLEAR_BUFFER);
		}
		preferences.add(PreferenceId.ITEMCOUNT);
		return preferences;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ViewerComparator getComparator() {
		TableViewerComparator<InvocationSequenceData> invocationDataViewerComparator = new TableViewerComparator<InvocationSequenceData>();
		for (Column column : Column.values()) {
			ResultComparator<InvocationSequenceData> resultComparator = new ResultComparator<InvocationSequenceData>(column.dataComparator, getCachedDataService());
			invocationDataViewerComparator.addColumn(getMappedTableViewerColumn(column).getColumn(), resultComparator);
		}

		return invocationDataViewerComparator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doRefresh(IProgressMonitor monitor, IRootEditor rootEditor) {
		monitor.beginTask("Updating Invocation Overview", IProgressMonitor.UNKNOWN);
		monitor.subTask("Retrieving the Invocation Overview from the CMR");
		List<InvocationSequenceData> invocData = new ArrayList<InvocationSequenceData>();

		Set<PlatformIdent> pIdents = globalDataAccessService.getAgentsOverview().keySet();
		for (PlatformIdent pIdent : pIdents) {
			invocData.addAll(getDataAccessService().getInvocationSequenceOverview(pIdent.getId(), invocationSequenceIds, Integer.MAX_VALUE, null));
		}

		getInvocationSequenceData().clear();
		if (!invocData.isEmpty()) {
			monitor.subTask("Displaying the Invocation Overview");
			getInvocationSequenceData().addAll(invocData);
		}
		monitor.done();
	}

}
