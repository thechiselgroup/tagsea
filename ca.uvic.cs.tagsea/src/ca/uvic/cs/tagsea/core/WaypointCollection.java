/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.core;

import java.util.Hashtable;

public class WaypointCollection {

	private Hashtable<String, Waypoint> waypoints;

	public WaypointCollection() {
		super();
		waypoints = new Hashtable<String, Waypoint>();
	}
	
	public void addWaypoint(String wpId, Waypoint waypoint) {
		waypoints.put(wpId, waypoint);
	}
	
	public Waypoint getWaypoint(String wpId) {
		return waypoints.get(wpId);
	}
}
