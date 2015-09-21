package info.novatec.inspectit.rcp.diagnoseit.overview;

import org.diagnoseit.spike.traceservices.aggregation.AbstractAggregatedTimedCallable;
import org.diagnoseit.spike.traceservices.aggregation.AggregatedDatabaseInvocation;
import org.diagnoseit.spike.traceservices.aggregation.AggregatedHTTPRequestProcessing;
import org.diagnoseit.spike.traceservices.aggregation.AggregatedMethodInvocation;
import org.diagnoseit.spike.traceservices.aggregation.Signature;

import rocks.cta.api.core.callables.DatabaseInvocation;
import rocks.cta.api.core.callables.HTTPRequestProcessing;
import rocks.cta.api.core.callables.MethodInvocation;
import rocks.cta.api.core.callables.RemoteInvocation;
import rocks.cta.api.core.callables.TimedCallable;

public class NameUtils {
	public static final String MAX_CHARACTER = "\u2191";
	public static final String MIN_CHARACTER = "\u2193";
	public static final String AVG_CHARACTER = "\u00D8";
	public static final String SUM_CHARACTER = "\u03A3";
	
	public static String getStringRepresentationFromElementData(AbstractAggregatedTimedCallable<? extends TimedCallable> data) {
		if (data.getType().isAssignableFrom(MethodInvocation.class)) {
			Signature signature = ((AggregatedMethodInvocation) data).getSignature();
			return signature.getClassName() + "." + signature.getMethodName() + "(...)";
		} else if (data.getType().isAssignableFrom(DatabaseInvocation.class)) {
			String sqlStatement = ((AggregatedDatabaseInvocation) data).getSQLStatement();
			int prevLength = sqlStatement.length();
			sqlStatement = sqlStatement.substring(0, Math.min(60, sqlStatement.length()));
			return "SQL (" + sqlStatement + (sqlStatement.length() < prevLength ? "...)" : ")");

		} else if (data.getType().isAssignableFrom(HTTPRequestProcessing.class)) {
			AggregatedHTTPRequestProcessing httpRequest = (AggregatedHTTPRequestProcessing) data;
			return "HTTP " + httpRequest.getRequestMethod().toString() + " (" + httpRequest.getUri() + ")";
		} else if (data.getType().isAssignableFrom(RemoteInvocation.class)) {
			// TODO: not supported yet
			throw new UnsupportedOperationException("Remote Invocation is not supported yet!");
		} else {
			throw new RuntimeException("Invalid Element type!");
		}
	}
}
