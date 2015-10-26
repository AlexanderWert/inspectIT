package info.novatec.inspectit.rcp.diagnoseit.overview;

import org.diagnoseit.spike.traceservices.aggregation.AbstractAggregatedTimedCallable;
import org.diagnoseit.spike.traceservices.aggregation.AggregatedBusinessTransaction;
import org.diagnoseit.spike.traceservices.aggregation.AggregatedDatabaseInvocation;
import org.diagnoseit.spike.traceservices.aggregation.AggregatedHTTPRequestProcessing;
import org.diagnoseit.spike.traceservices.aggregation.AggregatedMethodInvocation;
import org.diagnoseit.spike.traceservices.aggregation.BusinessTransactionCall;
import org.diagnoseit.spike.traceservices.aggregation.Signature;
import org.eclipse.jface.viewers.StyledString;

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
		} else if (data.getType().isAssignableFrom(BusinessTransactionCall.class)) {
			AggregatedBusinessTransaction aggBT =  (AggregatedBusinessTransaction) data;
			return aggBT.getBusinessTransactionName();
		} else {
			throw new RuntimeException("Invalid Element type!");
		}
	}
	
	public static StyledString getTextualRepresentation(AbstractAggregatedTimedCallable<? extends TimedCallable> data) {
		StyledString styledString = new StyledString();
		Class<?> causeType = data.getType();
		if (causeType.isAssignableFrom(MethodInvocation.class)) {
			AggregatedMethodInvocation methodInvocationData = (AggregatedMethodInvocation) data;
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
			AggregatedDatabaseInvocation dbInvocationData = (AggregatedDatabaseInvocation) data;
			String sql = dbInvocationData.getSQLStatement();
			int prevLength = sql.length();
			sql = sql.substring(0, Math.min(100, prevLength));
			if (prevLength > sql.length()) {
				sql += "...";
			}
			styledString.append("SQL");
			styledString.append(" - " + sql, StyledString.QUALIFIER_STYLER);
		} else if (causeType.isAssignableFrom(HTTPRequestProcessing.class)) {
			AggregatedHTTPRequestProcessing httpInvocationData = (AggregatedHTTPRequestProcessing) data;
			String uri = httpInvocationData.getUri();
			int prevLength = uri.length();
			uri = uri.substring(uri.length() - Math.min(100, uri.length()), uri.length());
			if (prevLength < uri.length()) {
				uri = "..." + uri;
			}
			styledString.append("HTTP" + httpInvocationData.getRequestMethod().toString());
			styledString.append(" - " + uri, StyledString.QUALIFIER_STYLER);
		} else if (causeType.isAssignableFrom(BusinessTransactionCall.class)) {
			AggregatedBusinessTransaction aggBT =  (AggregatedBusinessTransaction) data;
			styledString.append(aggBT.getBusinessTransactionName());
		} 
		
		return styledString;
	}
}
