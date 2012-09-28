/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core;

/**
 * Event representing a change on a waypoint.
 * @author Del Myers
 */

public interface IWaypointChangeEvent {

	/**
	 * Indicates that the waypoint in this event is new to the model.
	 */
	public static final int NEW = 0;

	/**
	 * Indicates that the waypoint in this event has been removed from the model.
	 */
	public static final int DELETE = 1;

	/**
	 * Indicates that the waypoint in this event has had one or more properties 
	 * changed.
	 */
	public static final int CHANGE = 2;

	/**
	 * Indicates that the tags have been added or removed from this waypoint. Does not
	 * indicate when a tag name has changed. 
	 */
	public static final int TAGS_CHANGED = 3;
	
	/**
	 * Indicates that the name of one of the tags has changed on this waypoint.
	 */
	public static final int TAG_NAME_CHANGED = 4;

	/**
	 * Returns the attributes that have changed in this event. If getType() does not return
	 * CHANGE, this will always return an array of size 0.
	 * @return the attributes that have changed in this event.
	 */
	public abstract String[] getChangedAttributes();

	/**
	 * Returns the old value for the given key, or null if it has not changed.
	 * @param key the key.
	 * @return the old value for the given key, or null if it has not changed.
	 */
	public abstract Object getOldValue(String key);

	/**
	 * Returns the new value for the given key, or null if it has not changed.
	 * @param key the key.
	 * @return the old value for the given key, or null if it has not changed.
	 */
	public abstract Object getNewValue(String key);

	/**
	 * Returns the type of this event. One of NEW, DELETE or CHANGE.
	 * @return the type
	 */
	public abstract int getType();

	/**
	 * Returns the new tag names associated with this waypoint, current as of when this change
	 * occurred. The current tags may have different names, depending on when this event was recieved.
	 * To get the most up-to-date information, query the waypoint on this event.
	 * @return the newTags
	 */
	public abstract String[] getNewTags();

	/**
	 * Returns the old tag names associated with this waypoint. Only the tag names are given, and 
	 * not the tags themselves, as the tags may no longer exist in the model.
	 * @return the old tag names.
	 */
	public abstract String[] getOldTags();

	/**
	 * @return the waypoint
	 */
	public abstract IWaypoint getWaypoint();
	
	/**
	 * Returns the old tag name if this event type is TAG_NAME_CHANGED. Otherwise returns null.
	 * @return the old tag name if this event type is TAG_NAME_CHANGED. Otherwise returns null.
	 */
	public String getOldTagName();
	
	/**
	 * Returns the new tag name if this event type is TAG_NAME_CHANGED. Otherwise returns null.
	 * @return the new tag name if this event type is TAG_NAME_CHANGED. Otherwise returns null.
	 */
	public String getNewTagName();

}