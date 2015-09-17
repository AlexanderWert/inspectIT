package info.novatec.inspectit.rcp.diagnoseit.overview;

import info.novatec.inspectit.cmr.service.IDITResultsAccessService;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceEventCallback.PreferenceEvent;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceId;
import info.novatec.inspectit.rcp.editor.root.IRootEditor;
import info.novatec.inspectit.rcp.editor.tree.input.AbstractTreeInputController;
import info.novatec.inspectit.rcp.editor.viewers.StyledCellIndexLabelProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.diagnoseit.spike.result.ProblemInstance;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class DITOverviewInputController extends AbstractTreeInputController {

	private List<DITResultBusinessTransaction> problemGroups;
	/**
	 * The resource manager is used for the images etc.
	 */
	private LocalResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());

	@Override
	public void createColumns(TreeViewer treeViewer) {
		for (DITOverviewColumn column : DITOverviewColumn.values()) {
			TreeViewerColumn viewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
			viewerColumn.getColumn().setMoveable(true);
			viewerColumn.getColumn().setResizable(true);
			viewerColumn.getColumn().setText(column.getName());
			viewerColumn.getColumn().setWidth(column.getWidth());
			if (null != column.getImage()) {
				viewerColumn.getColumn().setImage(column.getImage());
			}
		}
	}

	@Override
	public void doRefresh(IProgressMonitor monitor, IRootEditor rootEditor) {
		monitor.beginTask("Updating Invocation Overview", IProgressMonitor.UNKNOWN);
		monitor.subTask("Retrieving the Invocation Overview");

		loadDataFromService();

		monitor.done();
	}

	@Override
	public Object getTreeInput() {
		return problemGroups;
	}

	@Override
	public IContentProvider getContentProvider() {
		return new DiagnoseITOverviewContentProvider();
	}

	@Override
	public IBaseLabelProvider getLabelProvider() {
		return new DiagnoseITOverviewLabelProvider();
	}

	@Override
	public Set<PreferenceId> getPreferenceIds() {
		Set<PreferenceId> preferences = EnumSet.noneOf(PreferenceId.class);
		preferences.add(PreferenceId.LIVEMODE);
		preferences.add(PreferenceId.UPDATE);
		return preferences;
	}

	@Override
	public void preferenceEventFired(PreferenceEvent preferenceEvent) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getReadableString(Object object) {
		if (object instanceof DITResultElement) {
			DITResultElement resultElement = (DITResultElement) object;
			StringBuilder sb = new StringBuilder();
			sb.append(resultElement.getResultElementType());
			sb.append(": ");
			sb.append(resultElement.getIdentifier());
			return sb.toString();
		}
		throw new RuntimeException("Could not create the human readable string!");
	}

	@Override
	public List<String> getColumnValues(Object object) {
		if (object instanceof DITResultElement) {
			DITResultElement resultElement = (DITResultElement) object;

			List<String> values = new ArrayList<String>();
			for (DITOverviewColumn column : DITOverviewColumn.values()) {
				values.add(getStyledTextForColumn(resultElement, column).toString());
			}
			return values;
		}
		throw new RuntimeException("Could not create the column values!");
	}

	@Override
	public ViewerFilter[] getFilters() {
		return new ViewerFilter[0];
	}

	@Override
	public Object[] getObjectsToSearch(Object treeInput) {
		return new Object[0];
	}

	@Override
	public void dispose() {
		resourceManager.dispose();

	}

	@Override
	public SubViewClassification getSubViewClassification() {
		return SubViewClassification.MASTER;
	}

	@Override
	public int getExpandLevel() {
		return 1;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		final StructuredSelection selection = (StructuredSelection) event.getSelection();
		if (!selection.isEmpty() && (selection.getFirstElement() instanceof DITResultElement)) {
			try {
				PlatformUI.getWorkbench().getProgressService().busyCursorWhile(new IRunnableWithProgress() {
					public void run(final IProgressMonitor monitor) {
						monitor.beginTask("Opening problem instance", IProgressMonitor.UNKNOWN);
						DITResultElement resultElement = (DITResultElement) selection.getFirstElement();
						final List<DITResultElement> resultElementList = new ArrayList<DITResultElement>();
						resultElementList.add(resultElement);

						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
								IWorkbenchPage page = window.getActivePage();
								IRootEditor rootEditor = (IRootEditor) page.getActiveEditor();
								rootEditor.setDataInput(resultElementList);
							}
						});
						monitor.done();
					}
				});
			} catch (InvocationTargetException e) {
				MessageDialog.openError(Display.getDefault().getActiveShell().getShell(), "Error", e.getCause().toString());
			} catch (InterruptedException e) {
				MessageDialog.openInformation(Display.getDefault().getActiveShell().getShell(), "Cancelled", e.getCause().toString());
			}
		}

	};

	private void loadDataFromService() {
		IDITResultsAccessService dITResultsAccessService = getInputDefinition().getRepositoryDefinition().getDiagnoseITResultsAccessService();

		DITResultOverviewBuilder resultBuilder = new DITResultOverviewBuilder();
		List<ProblemInstance> problemInstances = dITResultsAccessService.getProblemInstances();

		for (ProblemInstance pInstance : problemInstances) {
			resultBuilder.addProblemInstance(pInstance);
		}
		problemGroups = resultBuilder.getResultList();

	}

	/**
	 * Content Provider for diagnoseIT results overview.
	 * 
	 * @author Alexander Wert
	 *
	 */
	private class DiagnoseITOverviewContentProvider implements ITreeContentProvider {

		// /**
		// * The deferred manager is used here to update the tree in a concurrent thread so the UI
		// * responds much better if many items are displayed.
		// */
		// private DeferredTreeContentManager manager;

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// manager = new DeferredTreeContentManager((AbstractTreeViewer) viewer);
			
			if (newInput != null && newInput instanceof List && !((List<?>) newInput).isEmpty()) {
					StructuredSelection structuredSelection = new StructuredSelection(((List<?>) newInput).get(0));
					viewer.setSelection(structuredSelection, true);
			}
		}

		@Override
		public Object[] getElements(Object inputElement) {
			List<DITResultElement> problemGroups = (List<DITResultElement>) inputElement;
			return problemGroups.toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			// if (manager.isDeferredAdapter(parentElement)) {
			// Object[] children = manager.getChildren(parentElement);
			//
			// return children;
			// }
			if (parentElement instanceof DITResultElement) {
				return ((DITResultElement) parentElement).getChildren().toArray();
			}

			return new Object[0];
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof DITResultElement) {
				return ((DITResultElement) element).getParent();
			}

			return null;
		}

		@Override
		public boolean hasChildren(Object parent) {
			if (parent == null) {
				return false;
			}

			if (parent instanceof DITResultElement) {
				DITResultElement resultElement = (DITResultElement) parent;
				if (!resultElement.getChildren().isEmpty()) {
					return true;
				}
			}

			return false;
		}

	}

	private final class DiagnoseITOverviewLabelProvider extends StyledCellIndexLabelProvider {
		/**
		 * Creates the styled text.
		 * 
		 * @param element
		 *            The element to create the styled text for.
		 * @param index
		 *            The index in the column.
		 * @return The created styled string.
		 */
		@Override
		public StyledString getStyledText(Object element, int index) {
			DITResultElement resultElement = (DITResultElement) element;

			DITOverviewColumn column = DITOverviewColumn.fromOrd(index);

			StyledString styledString = getStyledTextForColumn(resultElement, column);

			return styledString;

		}

		/**
		 * Returns the column image for the given element at the given index.
		 * 
		 * @param element
		 *            The element.
		 * @param index
		 *            The index.
		 * @return Returns the Image.
		 */
		@Override
		public Image getColumnImage(Object element, int index) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Color getBackground(Object element, int index) {
			return resourceManager.createColor(new RGB(255, 255, 255));
		}
	}

	private StyledString getStyledTextForColumn(DITResultElement resultElement, DITOverviewColumn column) {
		StyledString styledString = new StyledString();
		switch (column) {
		case NUM_OCCURRENCES:
			styledString.append(resultElement.getColumnContent(column));
			break;
		case SEVERITY:

			String severityString = resultElement.getColumnContent(column);

			if (severityString.equalsIgnoreCase("high")) {
				styledString.append(severityString, new ColorStyler(new RGB(255, 0, 0)));
			} else if (severityString.equalsIgnoreCase("medium")) {
				styledString.append(severityString, new ColorStyler(new RGB(240, 195, 70)));
			} else if (severityString.equalsIgnoreCase("low")) {
				styledString.append(severityString, new ColorStyler(new RGB(0, 0, 0)));
			} else {
				styledString.append(severityString, new ColorStyler(new RGB(100, 100, 100)));
			}
			break;
		case LAST_OCCURENCE:
			styledString.append(resultElement.getColumnContent(column));
			break;
		case NUM_AFFECTED_NODES:
			styledString.append(resultElement.getColumnContent(column));
			break;
		case NUM_INSTANCES:
			styledString.append(resultElement.getColumnContent(column));
			break;
		case PROBLEM_OVERVIEW:
			styledString.append(resultElement.getResultElementType() + ":  ", StyledString.QUALIFIER_STYLER);
			styledString.append(resultElement.getColumnContent(column));
			break;
		default:
			break;
		}
		return styledString;
	}

	private class ColorStyler extends StyledString.Styler {

		private RGB rgb;

		public ColorStyler(RGB rgb) {
			super();
			this.rgb = rgb;
		}

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground = resourceManager.createColor(rgb);
		}

	}
}
