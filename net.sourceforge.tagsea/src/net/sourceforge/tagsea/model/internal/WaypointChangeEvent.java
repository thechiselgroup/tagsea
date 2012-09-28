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
package net.sourceforge.tagsea.model.internal;

import java.util.Map;
import java.util.TreeMap;

import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;

/**
 * Change event on a waypoint.
 * @author Del Myers
 */

public final class WaypointChangeEvent implements IWaypointChangeEvent  {
	
	private class ValueDelta {
		final Object oldValue;
		final Object newValue;
		public ValueDelta(Object oldValue, Object newValue) {
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
	}
	
	/**
	 * The event type for this event.
	 */
	public final int type;
	
	/**
	 * The waypoint for this event.
	 */
	public final IWaypoint waypoint;
	
	/**
	 * A map of changes in values.
	 */
	private final TreeMap<String, ValueDelta> deltas;
	
	private final String[] oldTags;
	
	private final String[] newTags;
	
	private final String oldTagName;
	
	private final String newTagName;
	
	private WaypointChangeEvent(IWaypoint waypoint, int type, String[] keys, Object[] oldValues, Object[] newValues, String[] oldTags, String[] newTags, String oldTagName, String newTagName) {
		this.waypoint = waypoint;
		this.type = type;
		this.deltas = new TreeMap<String, ValueDelta>();
		for (int i = 0; i < keys.length; i++) {
			if (!oldValues[i].equals(newValues[i])) {
				deltas.put(keys[i], new ValueDelta(oldValues[i], newValues[i]));
			}
		}
		if (oldTags != null) {
			this.oldTags= oldTags;
		} else {
			this.oldTags = new String[0];
		}
		if (newTags != null) {
			this.newTags = newTags;
		} else {
			this.newTags = new String[0];
		}
		this.oldTagName = oldTagName;
		this.newTagName = newTagName;
	}
	/**
	 * Creates a new WaypointChangeEvent that indicates that the given waypoint is new to the model.
	 * @param waypoint the new waypoint.
	 * @return a new event.
	 */
	public static IWaypointChangeEvent createNewEvent(IWaypoint waypoint) {
		return new WaypointChangeEvent(waypoint, NEW, new String[0], new String[0], new String[0], new String[0], new String[0],null,null);
	}
	
	/**
	 * Creates a new WaypointChangeEvent that indicates that the given waypoint has been deleted
	 * from the model.
	 * @param waypoint the waypoint that has been deleted.
	 * @return the new event.
	 */
	public static IWaypointChangeEvent createDeleteEvent(IWaypoint waypoint) {
		return new WaypointChangeEvent(waypoint, DELETE, new String[0], new String[0], new String[0], new String[0], new String[0],null,null);
	}
	
	/**
	 * Creates a new change event for the given waypoint. This event is to be created after the change
	 * has already occurred in the model.
	 * @param waypoint the waypoint that has been changed.
	 * @param oldValues the old values for the waypoint.
	 * @return the new event
	 */
	public static WaypointChangeEvent createChangeEvent(IWaypoint waypoint, Map<String, Object> oldValues) {
		String[] keys = oldValues.keySet().toArray(new String[oldValues.keySet().size()]);
		Object[] oldArray = new Object[keys.length];
		Object[] newArray = new Object[keys.length];
		for (int i = 0; i < keys.length; i++) {
			oldArray[i] = oldValues.get(keys[i]);
			newArray[i] = waypoint.getValue(keys[i]);
		}
		return new WaypointChangeEvent(waypoint, CHANGE, keys, oldArray, newArray, new String[0], new String[0],null,null);
	}
	
	/**
	 * Creates a new event for when the list of tags on a waypoint has changed.
	 * @param waypoint
	 * @param oldTags
	 * @param newTags
	 * @return
	 */
	public static WaypointChangeEvent createTagChangeEvent(IWaypoint waypoint, String[] oldTags) {
		ITag[] tags = waypoint.getTags();
		String[] newTags = new String[tags.length];
		for (int i = 0; i < tags.length; i++) {
			newTags[i]=tags[i].getName();
		}
		return new WaypointChangeEvent(waypoint, TAGS_CHANGED, new String[0], new Object[0], new Object[0], oldTags, newTags,null,null);
	}
	
	public static WaypointChangeEvent createTagNameChangeEvent(IWaypoint waypoint, String oldName, String newName) {
		return new WaypointChangeEvent(waypoint, TAG_NAME_CHANGED, new String[0], new Object[0], new Object[0], new String[0], new String[0], oldName, newName);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointChangeEvent#getChangedAttributes()
	 */
	public String[] getChangedAttributes() {
		return this.deltas.keySet().toArray(new String[this.deltas.keySet().size()]);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointChangeEvent#getOldValue(java.lang.String)
	 */
	public Object getOldValue(String key) {
		ValueDelta delta = deltas.get(key);
		if (delta != null) {
			return delta.oldValue;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointChangeEvent#getNewValue(java.lang.String)
	 */
	public Object getNewValue(String key) {
		ValueDelta delta = deltas.get(key);
		if (delta != null) {
			return delta.newValue;
		}
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointChangeEvent#getType()
	 */
	public int getType() {
		return type;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointChangeEvent#getNewTags()
	 */
	public String[] getNewTags() {
		return newTags;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointChangeEvent#getOldTags()
	 */
	public String[] getOldTags() {
		return oldTags;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointChangeEvent#getWaypoint()
	 */
	public IWaypoint getWaypoint() {
		return waypoint;
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointChangeEvent#getNewTagName()
	 */
	public String getNewTagName() {
		return newTagName;
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointChangeEvent#getOldTagName()
	 */
	public String getOldTagName() {
		return oldTagName;
	}
}
