package info.novatec.inspectit.cmr.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;

/**
 * @author Ivan Senic
 *
 */
public final class Utils {

	/**
	 * Private constructor.
	 */
	private Utils() {
	}

	/**
	 * Subtracts collection b from a (a-b) in safe manner. Following rules apply:
	 * <p>
	 * - If collection a is <code>null</code> or empty, then result is empty list<br>
	 * - If collection b is <code>null</code> or empty, then result is a new list with same content
	 * as a <br>
	 * - If both collections have at least one element, results is
	 * {@link CollectionUtils#subtract(Collection, Collection)}.<br>
	 *
	 * @param <E>
	 *            Element types in collections.
	 * @param a
	 *            collection to subtract from
	 * @param b
	 *            collection to use in subtract
	 * @return subtraction result
	 * @see {@link CollectionUtils#subtract(Collection, Collection)}.
	 */
	@SuppressWarnings("unchecked")
	public static <E> Collection<E> subtractSafe(final Collection<E> a, final Collection<E> b) {
		// if a is empty then return empty
		if (CollectionUtils.isEmpty(a)) {
			return Collections.emptyList();
		}

		// if b is empty, then return complete a as new list
		if (CollectionUtils.isEmpty(b)) {
			return new ArrayList<>(a);
		}

		// otherwise perform subtract
		return CollectionUtils.subtract(a, b);
	}
}
