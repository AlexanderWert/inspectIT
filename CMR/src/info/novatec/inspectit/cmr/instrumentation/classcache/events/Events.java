package info.novatec.inspectit.cmr.instrumentation.classcache.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure to hold a list of change events. Note that the structure is not synchronized and
 * can only be used in non parallel access.
 * 
 * @author Stefan Siegl
 */
public class Events {

	/**
	 * List of node changes.
	 */
	public List<NodeEvent> nodeEvents = new ArrayList<>(); // NOCHK NOPMD

	/**
	 * List of reference changes.
	 */
	public List<ReferenceEvent> referenceEvents = new ArrayList<>(); // NOCHK NOPMD

	/**
	 * adds a node event to the cache.
	 * 
	 * @param e
	 *            the node event to add.
	 */
	public void addEvent(NodeEvent e) {
		nodeEvents.add(e);
	}

	/**
	 * adds a reference to the events.
	 * 
	 * @param e
	 *            the reference event to add.
	 */
	public void addEvent(ReferenceEvent e) {
		referenceEvents.add(e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeEvents == null) ? 0 : nodeEvents.hashCode());
		result = prime * result + ((referenceEvents == null) ? 0 : referenceEvents.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Events other = (Events) obj;
		if (nodeEvents == null) {
			if (other.nodeEvents != null) {
				return false;
			}
		} else if (!nodeEvents.equals(other.nodeEvents)) {
			return false;
		}
		if (referenceEvents == null) {
			if (other.referenceEvents != null) {
				return false;
			}
		} else if (!referenceEvents.equals(other.referenceEvents)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "EventCache [nodeEvents=" + nodeEvents + ", referenceEvents=" + referenceEvents + "]";
	}

}
