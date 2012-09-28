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
 * Represents a change in a tag.
 * @author Del Myers
 */

public interface ITagChangeEvent {
	/**
	 * Change type indicating that the name of this tag has changed.
	 */
	public static final int NAME = 0;
	
	/**
	 * Change type indicating that the waypoints referenced by this tag have changed.
	 */
	public static final int WAYPOINTS = 1;
	
	/**
	 * Change type indicating that this is a new tag to the model.
	 */
	public static final int NEW = 2;
	
	/**
	 * Change type indicating that this tag has been deleted from the model.
	 */
	public static final int DELETED = 3;
	
	/**
	 * Returns the change type for this event. One of NAME, WAYPOINTS, NEW, or DELETED.
	 * @return the change type for this event.
	 */
	int getType();
	
	/**
	 * Returns the old name for the tag that this change is on. If getType() does not return NAME, then this will
	 * simply return the current name of the tag.
	 * @return the old name of the tag.
	 */
	String getOldName();
	
	/**
	 * Returns the new name for the tag that this change is on.  
	 * If getType() does not return NAME, then this will
	 * simply return the current name of the tag.
	 * @return the new name of the tag.
	 */
	String getNewName();
	
	/**
	 * Returns the old waypoints associated with the tag that this change is on. 
	 * If getType() does not return
	 * WAYPOINTS, then these will just be the current waypoints on the tag. The waypoints
	 * are not guaranteed to exist in the model. Check IWaypoint.exists() to be sure.
	 * @return the old waypoints on the tag.
	 */
	IWaypoint[] getOldWaypoints();
	
	/**
	 * Returns the new waypoints associated with the tag that this change is on. 
	 * If getType() does not return
	 * WAYPOINTS, then these will just be the current waypoints on the tag. The waypoints
	 * are not guaranteed to exist in the model. Check IWaypoint.exists() to be sure.
	 * @return the new waypoints on the tag.
	 */
	IWaypoint[] getNewWaypoints();
	
	/**
	 * Returns the tag that this change is for.
	 * @return
	 */
	ITag getTag();

}
