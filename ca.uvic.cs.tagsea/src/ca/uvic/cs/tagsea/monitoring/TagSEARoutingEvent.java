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

import ca.uvic.cs.tagsea.core.Route;
import ca.uvic.cs.tagsea.core.Waypoint;

/**
 * Class for monitoring events to do with routing waypoings.
 * @author Del Myers
 *
 */
public class TagSEARoutingEvent implements ITagSEAMonitoringEvent {
	public enum Routing {
		New,
		Delete,
		NewWaypoint,
		DeleteWaypoint,
		MoveWaypoint,
		Rename
	}
	private Route route;
	private Waypoint waypoint;
	private Routing routing;
	private String oldName;

	/**
	 * Creates a new TagSEARoutingEvent
	 */
	public TagSEARoutingEvent(Routing routing, Route route, Waypoint waypoint) {
		this.routing = routing;
		this.route = route;
		this.waypoint = waypoint;
	}
	
	public TagSEARoutingEvent(Route route, String oldName) {
		this.oldName = oldName;
		this.route = route;
		this.routing = Routing.Rename;
	}
	
	/**
	 * @return the route
	 */
	public Route getRoute() {
		return route;
	}
	
	/**
	 * @return the routing
	 */
	public Routing getRouting() {
		return routing;
	}
	
	/**
	 * @return the waypoint
	 */
	public Waypoint getWaypoint() {
		return waypoint;
	}

	/* (non-Javadoc)
	 * @see ca.uvic.cs.tagsea.monitoring.ITagSEAMonitoringEvent#getKind()
	 */
	public EventKind getKind() {
		return EventKind.Routing;
	}
	
	/**
	 * @return the oldName
	 */
	public String getOldName() {
		return oldName;
	}
}
