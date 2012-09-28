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
package ca.uvic.cs.tagsea.tests;

import junit.framework.TestCase;

public class RouteTests extends TestCase {
	
	
	/*public void testRouteAddWaypoint() throws Exception {
		Tag tag = new Tag(null, "bug1");
		Route route = new Route("route1");
		Activator.getDefault().get
		Waypoint wp = new Waypoint(new Marker(), "bug1", "jiez", "susan")); 
		
		boolean result = route.addWaypoint(wp);
		assertTrue(result);
		
		//the following would result in consecutive duplicate waypoints,
		//which we are not allowing
		result = route.addWaypoint(wp);
		assertFalse(result);
		
		tag = new Tag(null, "bug2");
		wp = new Waypoint(tag); 
		result = route.addWaypoint(wp);
		assertTrue(result);
	}
	
	
	public void testRouteAddWaypointAtIndex() throws Exception {
		Tag tag1 = new Tag(null, "bug1");
		Tag tag2 = new Tag(null, "bug2");
		Tag tag3 = new Tag(null, "bug3");
		
		Route route = new Route("route1");
		
		Waypoint wp1 = new Waypoint(tag1);
		Waypoint wp2 = new Waypoint(tag2);
		Waypoint wp3 = new Waypoint(tag3);
		
		boolean result = route.addWaypoint(0, wp1);
		assertTrue(result);
		result = route.addWaypoint(1, wp2);
		assertTrue(result);
		result = route.addWaypoint(2, wp3);
		assertTrue(result);
		
		//the following would result in consecutive duplicate waypoints,
		//which we are not allowing
		result = route.addWaypoint(1, wp3);
		assertFalse(result);
		
		result = route.addWaypoint(1, wp2);
		assertTrue(result);
		
	}
	
	public void testRouteRemoveWaypoint() throws Exception {
		Tag tag1 = new Tag(null, "bug1");
		Tag tag2 = new Tag(null, "bug2");
		Tag tag3 = new Tag(null, "bug3");
		Tag tag4 = new Tag(null, "bug4");
		
		Route route = new Route("route1");
		
		Waypoint wp1 = new Waypoint(tag1);
		Waypoint wp2 = new Waypoint(tag2);
		Waypoint wp3 = new Waypoint(tag3);
		Waypoint wp4 = new Waypoint(tag4);
		
		route.addWaypoint(wp1);
		route.addWaypoint(wp2);
		route.addWaypoint(wp3);
		route.addWaypoint(wp2);
		route.addWaypoint(wp4);
		
		boolean result = route.removeWaypoint(0);
		assertTrue(result);
		
		result = route.removeWaypoint(1);
		assertTrue(result);
		
		result = route.removeWaypoint(5);
		assertFalse(result);
		
	}*/

	
}
