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
 * 
 * @author mdesmond
 *
 */
public interface IWaypointModel 
{
	/**
	 * Get the current waypoints
	 * @return the waypoints
	 */
	public IWaypoint[] getWaypoints();
	
	/**
	 * Add a waypoint 
	 * @param waypoint
	 */
	public void addWaypoint(IWaypoint waypoint);
	
	/**
	 * Remove a waypoint 
	 * @param waypoint
	 */
	public void removeWaypoint(IWaypoint waypoint);
	
	/**
	 * Query the waypoint model for the given waypoint id
	 * @param id
	 * @return
	 */
	public IWaypoint getWaypoint(String id);
	
	/**
	 * Add a waypoint model listener
	 * @param listener
	 */
	public void addWaypointModelListener(IWaypointModelListener listener);
	
	/**
	 * Remove a waypoint model listener
	 * @param listener
	 */
	public void removeWaypointModelListener(IWaypointModelListener listener);
}
