package info.novatec.inspectit.rcp.diagnoseit.overview;

import java.util.Comparator;

public class SeverityComperator implements Comparator<DITResultElement> {

	@Override
	public int compare(DITResultElement o1, DITResultElement o2) {
		double diff = o1.getSeverity() - o2.getSeverity();
		if (diff < 0.0) {
			return 1;
		} else if (diff > 0.0) {
			return -1;
		} else {
			return 0;
		}
	}

}
