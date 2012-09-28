/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tagging.core;

import java.util.Date;

/**
 * @author mdesmond
 */
public interface IWaypoint
{	
	/**
	 * Get the waypoint id
	 * @return the waypoint id
	 */
	public String getId();
	
	/**
	 * Get the waypoint type
	 * @return the waypoint type
	 */
	public String getType();
	
	/**
	 * Get the waypoint description
	 * @return the Author
	 */
	public String getDescription();
	
	/**
	 * Get the waypoint Author
	 * @return the Author
	 */
	public String getAuthor();
	
	/**
	 * Get the Date
	 * @return the date
	 */
	public Date getDate();
	
	/**
	 * Attempt to Navigate to this waypoint
	 */
	public void navigate();
	
	/**
	 * Add a tag to this waypoint
	 * @param tag
	 */
	public void addTag(ITag tag);
	
	/**
	 * Remove a tag from this waypoint
	 * @param tag
	 */
	public void removeTag(ITag tag);
	
	/**
	 * Get the tags associated with this waypoint
	 */
	public ITag[] getTags();
	
	public void addWaypointListener(IWaypointListener listener);
	public void removeWaypointListener(IWaypointListener listener);
}
