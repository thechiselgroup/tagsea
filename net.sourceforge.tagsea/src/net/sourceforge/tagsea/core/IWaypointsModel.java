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
 * The central access point for all waypoints that can be accessed from the workbench.
 * @author Del Myers
 */

public interface IWaypointsModel {
	/**
	 * Returns all the waypoints that can be accessed from the workbench.
	 * @return
	 */
	IWaypoint[] getAllWaypoints();
	
	/**
	 * Returns all waypoints of the given type
	 * @param type
	 * @return
	 */
	IWaypoint[] getWaypoints(String type);
	
	/**
	 * Creates an returns a waypoint of the given type with the given tag names associated with it.
	 * If the waypoint could not be created, null is returned.
	 * @param type
	 * @param tags
	 * @return
	 */
	IWaypoint createWaypoint(String type, String[] tags);
	
	/**
	 * Removes the given waypoint from the model, if it exists there and the defining plugin will allow the removal.
	 * @param waypoint the waypoint to be deleted.
	 * @return the status of the attempt to delete the waypoint.
	 */
	TagSEAChangeStatus removeWaypoint(IWaypoint waypoint);

}
