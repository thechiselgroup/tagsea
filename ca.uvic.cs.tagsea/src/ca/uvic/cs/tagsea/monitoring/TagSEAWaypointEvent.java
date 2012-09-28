/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.monitoring;

import ca.uvic.cs.tagsea.core.Waypoint;

/**
 * Event for monitoring waypoints.
 * @author Del Myers
 *
 */
public class TagSEAWaypointEvent implements ITagSEAMonitoringEvent {
	public enum Waypointing {
		New,
		Removed,
		Edited
	}
	
	private Waypointing waypointing;
	private Waypoint waypoint;
	
	/**
	 * 
	 */
	public TagSEAWaypointEvent(Waypointing waypointing, Waypoint waypoint) {
		this.waypointing = waypointing;
		this.waypoint = waypoint;
	}
	
	/**
	 * @return the waypoint
	 */
	public Waypoint getWaypoint() {
		return waypoint;
	}
	
	/**
	 * @return the waypointing
	 */
	public Waypointing getWaypointing() {
		return waypointing;
	}

	/* (non-Javadoc)
	 * @see ca.uvic.cs.tagsea.monitoring.ITagSEAMonitoringEvent#getKind()
	 */
	public EventKind getKind() {
		return EventKind.Waypointing;
	}
	
}
