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

/**
 * The interface to a tag instance
 * @author mdesmond
 */
public interface ITag
{
	/**
	 * Get the tag name
	 * @return the tag name
	 */
	public String getName();
	
	/**
	 * Get the waypoints associated with this tag, modifications
	 * to this array will not affect the tag, tag modification is
	 * only possible using ITag modification api
	 * @return the waypoints associated with this tag
	 */
	public IWaypoint[] getWaypoints();
	
	/**
	 * Get the number of waypoints associated with this tag
	 * @return the number of associated waypoints
	 */
	public int getWaypointCount();
	
	/**
	 * Attach a tag listener to this tag, duplicate listeners have no effect
	 * @param tagListener the listener to add
	 */
	public void addTagListener(ITagListener tagListener);
	
	/**
	 * Removes the given listener from this tag
	 * @param tagListener the listener to remove
	 */
	public void removeTagListener(ITagListener tagListener);
}
