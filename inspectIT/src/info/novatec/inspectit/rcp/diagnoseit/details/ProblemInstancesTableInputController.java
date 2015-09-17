package info.novatec.inspectit.rcp.diagnoseit.details;

import info.novatec.inspectit.communication.DefaultData;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITOverviewColumn;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITResultElement;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITResultProblemInstance;
import info.novatec.inspectit.rcp.diagnoseit.overview.SeverityComperator;
import info.novatec.inspectit.rcp.editor.root.IRootEditor;
import info.novatec.inspectit.rcp.editor.table.input.AbstractTableInputController;
import info.novatec.inspectit.rcp.editor.viewers.StyledCellIndexLabelProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.diagnoseit.spike.traceservices.aggregation.AggregatedDatabaseInvocation;
import org.diagnoseit.spike.traceservices.aggregation.AggregatedHTTPRequestProcessing;
import org.diagnoseit.spike.traceservices.aggregation.AggregatedMethodInvocation;
import org.diagnoseit.spike.traceservices.aggregation.Signature;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import rocks.cta.api.core.callables.DatabaseInvocation;
import rocks.cta.api.core.callables.HTTPRequestProcessing;
import rocks.cta.api.core.callables.MethodInvocation;

public class ProblemInstancesTableInputController extends AbstractTableInputController {
	/**
	 * The resource manager is used for the images etc.
	 */
	private LocalResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());

	public enum Column {
		/** Problem instances column. */
		PROBLEM_INSTANCES("Problem Instances", 400, null),
		/** Severity column. */
		SEVERITY("Severity", 60, null),
		/** Count column. */
		NUM_OCCURRENCES("# Occurrences", 90, null),
		/** # Affected column. */
		NUM_AFFECTED_NODES("# Affected Nodes", 110, null),
		/** Last Occurrence. */
		LAST_OCCURENCE("Last Occurence", 100, null);

		/** The name. */
		private String name;
		/** The width of the column. */
		private int width;
		/** The image descriptor. Can be <code>null</code> */
		private Image image;

		/**
		 * Default constructor which creates a column enumeration object.
		 * 
		 * @param name
		 *            The name of the column.
		 * @param width
		 *            The width of the column.
		 * @param imageName
		 *            The name of the image. Names are defined in {@link InspectITImages}.
		 */
		private Column(String name, int width, String imageName) {
			this.name = name;
			this.width = width;
			this.image = InspectIT.getDefault().getImage(imageName);
		}

		/**
		 * Converts an ordinal into a column.
		 * 
		 * @param i
		 *            The ordinal.
		 * @return The appropriate column.
		 */
		public static Column fromOrd(int i) {
			if (i < 0 || i >= DITOverviewColumn.values().length) {
				throw new IndexOutOfBoundsException("Invalid ordinal");
			}
			return Column.values()[i];
		}

		/**
		 * Gets {@link #name}.
		 * 
		 * @return {@link #name}
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets {@link #name}.
		 * 
		 * @param name
		 *            New value for {@link #name}
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Gets {@link #width}.
		 * 
		 * @return {@link #width}
		 */
		public int getWidth() {
			return width;
		}

		/**
		 * Sets {@link #width}.
		 * 
		 * @param width
		 *            New value for {@link #width}
		 */
		public void setWidth(int width) {
			this.width = width;
		}

		/**
		 * Gets {@link #image}.
		 * 
		 * @return {@link #image}
		 */
		public Image getImage() {
			return image;
		}

		/**
		 * Sets {@link #image}.
		 * 
		 * @param image
		 *            New value for {@link #image}
		 */
		public void setImage(Image image) {
			this.image = image;
		}

	}

	@Override
	public void createColumns(TableViewer tableViewer) {
		for (Column column : Column.values()) {
			TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			viewerColumn.getColumn().setMoveable(true);
			viewerColumn.getColumn().setResizable(true);
			viewerColumn.getColumn().setText(column.name);
			viewerColumn.getColumn().setWidth(column.width);
			if (null != column.image) {
				viewerColumn.getColumn().setImage(column.image);
			}
			mapTableViewerColumn(column, viewerColumn);
		}

	}

	@Override
	public IContentProvider getContentProvider() {
		return new ProblemInstancesContentProvider();
	}

	@Override
	public IBaseLabelProvider getLabelProvider() {
		return new DiagnoseITOverviewLabelProvider();
	}

	@Override
	public ViewerComparator getComparator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReadableString(Object object) {
		if (object instanceof DITResultProblemInstance) {
			DITResultProblemInstance data = (DITResultProblemInstance) object;
			return data.getStringRepresentation();
		}
		throw new RuntimeException("Could not create the human readable string!");
	}

	@Override
	public List<String> getColumnValues(Object object) {
		if (object instanceof DITResultProblemInstance) {
			DITResultProblemInstance data = (DITResultProblemInstance) object;
			List<String> values = new ArrayList<String>();
			for (Column column : Column.values()) {
				values.add(getStyledTextForColumn(data, column).toString());
			}
			return values;
		}
		throw new RuntimeException("Could not create the column values!");
	}

	@Override
	public boolean canOpenInput(List<? extends DefaultData> data) {
		return data != null && data.size() == 1 && data.get(0) instanceof DITResultElement && !(data.get(0) instanceof DITResultProblemInstance);
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		final StructuredSelection selection = (StructuredSelection) event.getSelection();
		if (!selection.isEmpty() && (selection.getFirstElement() instanceof DITResultProblemInstance)) {
			try {
				PlatformUI.getWorkbench().getProgressService().busyCursorWhile(new IRunnableWithProgress() {
					public void run(final IProgressMonitor monitor) {
						monitor.beginTask("Opening problem instance", IProgressMonitor.UNKNOWN);
						DITResultProblemInstance resultElement = (DITResultProblemInstance) selection.getFirstElement();
						final List<DITResultProblemInstance> resultElementList = new ArrayList<DITResultProblemInstance>();
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
	}
	
	private StyledString getStyledTextForColumn(DITResultProblemInstance problemInstanceElement, Column column) {
		StyledString styledString = new StyledString();
		switch (column) {
		case NUM_OCCURRENCES:
			styledString.append(problemInstanceElement.getColumnContent(DITOverviewColumn.NUM_OCCURRENCES));
			break;
		case SEVERITY:

			String severityString = problemInstanceElement.getColumnContent(DITOverviewColumn.SEVERITY);

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
			styledString.append(problemInstanceElement.getColumnContent(DITOverviewColumn.LAST_OCCURENCE));
			break;
		case NUM_AFFECTED_NODES:
			styledString.append(problemInstanceElement.getColumnContent(DITOverviewColumn.NUM_AFFECTED_NODES));
			break;
		case PROBLEM_INSTANCES:

			Class<?> causeType = problemInstanceElement.getProblemInstance().getCauseData().getType();
			if (causeType.isAssignableFrom(MethodInvocation.class)) {
				AggregatedMethodInvocation methodInvocationData = (AggregatedMethodInvocation) problemInstanceElement.getProblemInstance().getCauseData();
				Signature signature = methodInvocationData.getSignature();
				String parameterStr = "";
				boolean first = true;
				for (String parType : signature.getParameterTypes()) {
					if (!first) {
						parameterStr += ", ";
					}
					int idx = parType.lastIndexOf('.');
					if (idx >= 0) {
						parameterStr += parType.substring(idx + 1);
					} else {
						parameterStr += parType;
					}

					first = false;
				}
				String mainText = signature.getClassName() + "." + signature.getMethodName() + "(" + parameterStr + ")";
				styledString.append(mainText);
				styledString.append(" - " + signature.getPackageName(), StyledString.QUALIFIER_STYLER);
			} else if (causeType.isAssignableFrom(DatabaseInvocation.class)) {
				AggregatedDatabaseInvocation dbInvocationData = (AggregatedDatabaseInvocation) problemInstanceElement.getProblemInstance().getCauseData();
				String sql = dbInvocationData.getSQLStatement();
				int prevLength = sql.length();
				sql = sql.substring(0, Math.min(100, prevLength));
				if (prevLength > sql.length()) {
					sql += "...";
				}
				styledString.append("SQL");
				styledString.append(" - " + sql, StyledString.QUALIFIER_STYLER);
			} else if (causeType.isAssignableFrom(HTTPRequestProcessing.class)) {
				AggregatedHTTPRequestProcessing httpInvocationData = (AggregatedHTTPRequestProcessing) problemInstanceElement.getProblemInstance().getCauseData();
				String uri = httpInvocationData.getUri();
				int prevLength = uri.length();
				uri = uri.substring(uri.length() - Math.min(100, uri.length()), uri.length());
				if (prevLength < uri.length()) {
					uri = "..." + uri;
				}
				styledString.append("HTTP" + httpInvocationData.getRequestMethod().toString());
				styledString.append(" - " + uri, StyledString.QUALIFIER_STYLER);
			} else {
				styledString.append(problemInstanceElement.getResultElementType() + ":  ", StyledString.QUALIFIER_STYLER);
				styledString.append(problemInstanceElement.getColumnContent(DITOverviewColumn.PROBLEM_OVERVIEW));
			}
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

	private class ProblemInstancesContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List && !((List<?>) inputElement).isEmpty()) {
				Object first = ((List<?>) inputElement).get(0);
				if (first instanceof DITResultElement) {
					List<DITResultProblemInstance> problemInstances = ((DITResultElement) first).collectProblemInstances();
					Collections.sort(problemInstances, new SeverityComperator());
					return problemInstances.toArray();
				}
			}
			return new Object[0];
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
			DITResultProblemInstance resultElement = (DITResultProblemInstance) element;

			Column column = Column.fromOrd(index);

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

}
