/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.java.waypoints;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;

/**
 * A change event that uses copies the changes from an old event that can be applied to a
 * new waypoint.
 * @author Del Myers
 */

class ReplacedWaypointEvent implements IWaypointChangeEvent {

	private IWaypoint newWaypoint;
	private IWaypointChangeEvent event;

	/**
	 * @param event the event to copy the change from.
	 * @param newWaypont the waypoint to actually perform the change on.
	 */
	public ReplacedWaypointEvent(IWaypointChangeEvent event, IWaypoint newWaypoint) {
		this.newWaypoint = newWaypoint;
		this.event = event;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointChangeEvent#getChangedAttributes()
	 */
	public String[] getChangedAttributes() {
		return event.getChangedAttributes();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointChangeEvent#getNewTagName()
	 */
	public String getNewTagName() {
		return event.getNewTagName();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointChangeEvent#getNewTags()
	 */
	public String[] getNewTags() {
		return event.getNewTags();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointChangeEvent#getNewValue(java.lang.String)
	 */
	public Object getNewValue(String key) {
		return event.getNewValue(key);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointChangeEvent#getOldTagName()
	 */
	public String getOldTagName() {
		return event.getOldTagName();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointChangeEvent#getOldTags()
	 */
	public String[] getOldTags() {
		return event.getOldTags();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointChangeEvent#getOldValue(java.lang.String)
	 */
	public Object getOldValue(String key) {
		return event.getOldValue(key);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointChangeEvent#getType()
	 */
	public int getType() {
		return event.getType();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointChangeEvent#getWaypoint()
	 */
	public IWaypoint getWaypoint() {
		return newWaypoint;
	}

}
