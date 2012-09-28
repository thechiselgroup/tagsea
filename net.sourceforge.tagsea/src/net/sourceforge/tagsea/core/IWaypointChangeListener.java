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
 * Basic listener for waypoint deltas.
 * @author Del Myers
 */

public interface IWaypointChangeListener {
	/**
	 * Indicates a changes to a waypoint or a list of waypoints.
	 * @param delta the delta containing the change events.
	 */
	void waypointsChanged(WaypointDelta delta);
}
