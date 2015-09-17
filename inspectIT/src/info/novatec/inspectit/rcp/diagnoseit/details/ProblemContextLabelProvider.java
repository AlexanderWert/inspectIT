package info.novatec.inspectit.rcp.diagnoseit.details;

import info.novatec.inspectit.rcp.InspectIT;

import java.text.DecimalFormat;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import rocks.cta.api.core.Trace;

public class ProblemContextLabelProvider extends ColumnLabelProvider {
	private Formatter formatter = new Formatter();
	private DITProblemContextColumn column;

	public ProblemContextLabelProvider(DITProblemContextColumn column) {
		super();
		this.column = column;
	}

	@Override
	public String getText(Object element) {
		ContextInformationInputElement ctxInfo = (ContextInformationInputElement) element;
		switch (column) {
		case AVG_COUNT:
			return String.valueOf(ctxInfo.getData().getCount());
		case AVG_DURATION:
			return formatter.format(((double) ctxInfo.getData().getResponseTimeAvg()) * Trace.NANOS_TO_MILLIS_FACTOR);
		case AVG_EXCL_TIME:
			return formatter.format(((double) ctxInfo.getData().getExclusiveTimeAvg()) * Trace.NANOS_TO_MILLIS_FACTOR);
		case AVG_EXCL_TIME_SUM:
			if (ctxInfo.getExclusiveTimeSumStatistics() != null) {
				return formatter.format(((double) ctxInfo.getExclusiveTimeSumStatistics().getMean()) * Trace.NANOS_TO_MILLIS_FACTOR);
			} else {
				return "---";
			}
		case CALLABLE:
			return ctxInfo.getName();
		case CONTEXT:
			return "";
		default:
			break;
		}

		throw new RuntimeException("Unsupported Column!");
	}

	@Override
	public Image getImage(Object element) {
		ContextInformationInputElement ctxInfo = (ContextInformationInputElement) element;
		switch (column) {
		case CONTEXT:
			return InspectIT.getDefault().getImage(ctxInfo.getImageIdentifier());
		case AVG_COUNT:
		case AVG_DURATION:
		case AVG_EXCL_TIME:
		case AVG_EXCL_TIME_SUM:
		case CALLABLE:

		default:
			return null;
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
