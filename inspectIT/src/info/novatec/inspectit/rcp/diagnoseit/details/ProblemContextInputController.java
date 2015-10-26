package info.novatec.inspectit.rcp.diagnoseit.details;

import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.diagnoseit.overview.NameUtils;
import info.novatec.inspectit.rcp.editor.table.input.AbstractTableInputController;
import info.novatec.inspectit.rcp.editor.viewers.StyledCellIndexLabelProvider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.diagnoseit.spike.result.ProblemInstance;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import rocks.cta.api.core.Trace;

public class ProblemContextInputController extends AbstractTableInputController {
	private Formatter formatter = new Formatter();
	private ProblemInstance problemInstance;

	@Override
	public void createColumns(TableViewer tableViewer) {
		for (DITProblemContextColumn column : DITProblemContextColumn.values()) {
			TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			viewerColumn.getColumn().setMoveable(true);
			viewerColumn.getColumn().setResizable(true);
			viewerColumn.getColumn().setText(column.getName());
			viewerColumn.getColumn().setWidth(column.getWidth());
			if (null != column.getImage()) {
				viewerColumn.getColumn().setImage(column.getImage());
			}
			mapTableViewerColumn(column, viewerColumn);
		}

	}

	@Override
	public IContentProvider getContentProvider() {
		return new ArrayContentProvider();
	}

	@Override
	public IBaseLabelProvider getLabelProvider() {
		return new ProblemContextTableLableProvider();
	}

	@Override
	public ViewerComparator getComparator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReadableString(Object object) {
		if (object instanceof ContextInformationInputElement) {
			ContextInformationInputElement data = (ContextInformationInputElement) object;
			return data.getName();
		}
		throw new RuntimeException("Could not create the human readable string!");
	}

	@Override
	public List<String> getColumnValues(Object object) {
		if (object instanceof ContextInformationInputElement) {
			ContextInformationInputElement data = (ContextInformationInputElement) object;
			List<String> values = new ArrayList<String>();
			for (DITProblemContextColumn column : DITProblemContextColumn.values()) {
				values.add(getStyledTextForColumn(data, column).toString());
			}
			return values;
		}
		throw new RuntimeException("Could not create the column values!");
	}

	@Override
	public Object getTableInput() {
		
		
		if (problemInstance == null) {
			return new ContextInformationInputElement[0];
		}
		ContextInformationInputElement[] input = new ContextInformationInputElement[4];
		input[0] = new ContextInformationInputElement(NameUtils.getStringRepresentationFromElementData(problemInstance.getBusinessTransactionData()), "business transaction", InspectITImages.IMG_DIAGNOSEIT_BT,
				problemInstance.getBusinessTransactionData(), null, null, null);
		input[1] = new ContextInformationInputElement(NameUtils.getStringRepresentationFromElementData(problemInstance.getEntryPointData()), "entry point", InspectITImages.IMG_DIAGNOSEIT_EP,
				problemInstance.getEntryPointData(), null, null, null);
		input[2] = new ContextInformationInputElement(NameUtils.getStringRepresentationFromElementData(problemInstance.getProblemContextData()), "problem context", InspectITImages.IMG_DIAGNOSEIT_PC,
				problemInstance.getProblemContextData(), null, null, null);
		input[3] = new ContextInformationInputElement(NameUtils.getStringRepresentationFromElementData(problemInstance.getCauseData()), "cause", InspectITImages.IMG_DIAGNOSEIT_C,
				problemInstance.getCauseData(), problemInstance.getCauseEclusiveTimeSumStats(), problemInstance.getCauseCPUEclusiveTimeSumStats(), problemInstance.getCauseCountStats());
		return input;
	}

	
	public ProblemInstance getProblemInstance() {
		return problemInstance;
	}
	

	public void setProblemInstance(ProblemInstance problemInstance) {
		this.problemInstance = problemInstance;
	}

	private StyledString getStyledTextForColumn(ContextInformationInputElement infoElement, DITProblemContextColumn column) {
		StyledString styledString = new StyledString();
		switch (column) {

		case MIN_COUNT:
			if (infoElement.getCountStatistics() == null) {
				return styledString.append("1");
			} else {
				return styledString.append(String.valueOf(infoElement.getCountStatistics().getMin()));
			}
		case AVG_COUNT:
			if (infoElement.getCountStatistics() == null) {
				return styledString.append("1");
			} else {
				return styledString.append(String.valueOf(infoElement.getCountStatistics().getMean()));
			}
		case MAX_COUNT:
			if (infoElement.getCountStatistics() == null) {
				return styledString.append("1");
			} else {
				return styledString.append(String.valueOf(infoElement.getCountStatistics().getMax()));
			}
		case MIN_DURATION:
			return styledString.append(formatter.format(((double) infoElement.getData().getResponseTimeStats().getMin()) * Trace.NANOS_TO_MILLIS_FACTOR));
		case AVG_DURATION:
			return styledString.append(formatter.format(((double) infoElement.getData().getResponseTimeStats().getMean()) * Trace.NANOS_TO_MILLIS_FACTOR));
		case MAX_DURATION:
			return styledString.append(formatter.format(((double) infoElement.getData().getResponseTimeStats().getMax()) * Trace.NANOS_TO_MILLIS_FACTOR));
		case AVG_EXCL_TIME:
			return styledString.append(formatter.format(((double) infoElement.getData().getExclusiveTimeStats().getMean()) * Trace.NANOS_TO_MILLIS_FACTOR));
		case MIN_EXCL_TIME_SUM:
			if (infoElement.getExclusiveTimeSumStatistics() != null) {
				return styledString.append(formatter.format(((double) infoElement.getExclusiveTimeSumStatistics().getMin()) * Trace.NANOS_TO_MILLIS_FACTOR));
			} else {
				return styledString.append("---");
			}
		case AVG_EXCL_TIME_SUM:
			if (infoElement.getExclusiveTimeSumStatistics() != null) {
				return styledString.append(formatter.format(((double) infoElement.getExclusiveTimeSumStatistics().getMean()) * Trace.NANOS_TO_MILLIS_FACTOR));
			} else {
				return styledString.append("---");
			}
		case MAX_EXCL_TIME_SUM:
			if (infoElement.getExclusiveTimeSumStatistics() != null) {
				return styledString.append(formatter.format(((double) infoElement.getExclusiveTimeSumStatistics().getMax()) * Trace.NANOS_TO_MILLIS_FACTOR));
			} else {
				return styledString.append("---");
			}
		case CALLABLE:
			return NameUtils.getTextualRepresentation(infoElement.getData());
		case CONTEXT:

		default:
			break;
		}
		return styledString;
	}

	private final class ProblemContextTableLableProvider extends StyledCellIndexLabelProvider {

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
			ContextInformationInputElement infoElement = (ContextInformationInputElement) element;

			DITProblemContextColumn column = DITProblemContextColumn.fromOrd(index);

			StyledString styledString = getStyledTextForColumn(infoElement, column);

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
			ContextInformationInputElement infoElement = (ContextInformationInputElement) element;

			DITProblemContextColumn column = DITProblemContextColumn.fromOrd(index);
			switch (column) {
			case CONTEXT:
				return InspectIT.getDefault().getImage(infoElement.getImageIdentifier());
			case AVG_COUNT:
			case AVG_DURATION:
			case AVG_EXCL_TIME:
			case AVG_EXCL_TIME_SUM:
			case CALLABLE:

			default:
				return null;
			}
		}

	}

	private class Formatter {
		private DecimalFormat df = new DecimalFormat("0.##");
		private DecimalFormat df_2 = new DecimalFormat("0");

		public String format(double number) {
			if (number < 10.0) {
				return df.format(number);
			} else {
				return df_2.format(number);
			}

		}
	}

}
