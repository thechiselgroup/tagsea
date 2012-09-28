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
 * Event for monitoring navigation of waypoints.
 * @author Del Myers
 *
 */
public class TagSEANavigationEvent implements ITagSEAMonitoringEvent {
	private Waypoint to;
	
	/**
	 * Constructor.
	 * @param from the waypoint that we are navigating from; null if none.
	 * @param to the waypoint that we are navigating to; null if none.
	 */
	public TagSEANavigationEvent(Waypoint to) {
		this.to = to;
	}

	/* (non-Javadoc)
	 * @see ca.uvic.cs.tagsea.monitoring.ITagSEAMonitoringEvent#getKind()
	 */
	public EventKind getKind() {
		return EventKind.Navigation;
	}
	

	/**
	 * @return the to
	 */
	public Waypoint getTo() {
		return to;
	}
	
	

}
