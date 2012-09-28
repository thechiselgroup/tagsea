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
package ca.uvic.cs.tagsea;

import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.core.resources.IResource;

import ca.uvic.cs.tagsea.core.Waypoint;

public class ResourceWaypointMap {

	private HashMap<IResource, LinkedList<Waypoint>> map = null;

	public ResourceWaypointMap() {
		map = new HashMap<IResource, LinkedList<Waypoint>>();
	}

	public LinkedList<Waypoint> remove(IResource resource) {
		LinkedList<Waypoint> waypoints = getWaypoints(resource);
		map.remove(resource);
		return waypoints;
	}

	public void add(IResource resource, Waypoint waypoint) {
		if (map.get(resource) == null) {
			LinkedList<Waypoint> listOfWaypoints = new LinkedList<Waypoint>();
			listOfWaypoints.add(waypoint);
			map.put(resource, listOfWaypoints);

		} else {
			LinkedList<Waypoint> listOfWaypoints = map.get(resource);
			listOfWaypoints.add(waypoint);
		}
	}

	public LinkedList<Waypoint> getWaypoints(IResource resource) {
		if (map.get(resource) == null) {
			return new LinkedList<Waypoint>();
		} else {
			return map.get(resource);
		}
	}

}
