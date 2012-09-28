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
 * The basic interface to an instance of a tag on the platform. Tags are meant to be a
 * shareable way of annotating elements in the Eclipse workspace. They are not created
 * directly, but rather by using "waypoints." Waypoints represent "places" that have
 * been "tagged" as interesting. When a digital artifact (source code, file, url, etc.)
 * has been deemed as interesting, the user can define a waypoint on that artifact, and
 * supply a tag name for the waypoint. The artifact will then be associated with that tag
 * name. Tags cannot exist without waypoints.
 * 
 * All waypoints are automatically associated with the default tag name "default".
 * 
 * Instances of ITag are comparable with one another by the simple comparason of
 * names.
 * 
 * This interface should never be implemented by clients.
 * 
 * @author Del Myers
 * @see ITag.DEFAULT.
 * @see IWaypoint
 * @see ITagsModel
 */

public interface ITag extends Comparable<ITag> {
	
	/**
	 * Platform reserved name for a tag. All waypoints are automatically associated with this
	 * tag.
	 */
	static final String DEFAULT = "default";
	
	/**
	 * Gets the name of this tag. The name "default" is reserved by the TagSEA platform.
	 * In order to support structured viewing of tags, a standard naming convension is followed.
	 * '.' symbols are understood to separate names of tags that can be viewed as a hierarchy.
	 * For example, if a tag is named "bug.12345", "12345" can be viewed as a "sub-tag"
	 * of "bug". This is for view purposes only. Only one tag actually exists in the platform:
	 * "bug.12345".
	 * @return the name of this tag.
	 */
	String getName();
	
	/**
	 * Sets the name of this tag to the new name. If an ITagSEAOperation is currently running,
	 * this method will block until it is finished, at which point the request may become 
	 * obsolete. The request will be obsolete if the ITagSEAOperation caused this tag to 
	 * be removed from the model. If this is the case, then the method will return false,
	 * and the operation will not be completed. The operation will also not be completed if
	 * a tag of the given name already exists. Otherwise, true us returned.
	 * @return true iff the operation could be completed.
	 */
	boolean setName(String name);
	
	/**
	 * Returns all the waypoints associated with this tag. Waypoints are understood as interesting
	 * "places" in digital artifacts. If the list is empty, the reference to this tag is deemed as
	 * stale, and slated for removal, or has been removed from the model, and it should no longer
	 * be used.
	 * @return the waypoints associated with this tag.
	 */
	IWaypoint[] getWaypoints();
	
	/**
	 * Returns the number of waypoints associated with this tag. If the number is zero, 
	 * the reference to this tag is deemed as
	 * stale, and slated for removal, or has been removed from the model, and it should no longer
	 * be used.
	 * @return the number of waypoints associated with this tag.
	 */
	int getWaypointCount();
	
	/**
	 * Returns true iff this tag exists in the model. If false is returned, then this tag was
	 * previously removed from the model.
	 * @return 
	 */
	boolean exists();
	
	
}
