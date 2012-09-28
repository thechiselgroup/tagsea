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
import ca.uvic.cs.tagsea.core.Tag;
import ca.uvic.cs.tagsea.core.Waypoint;
import ca.uvic.cs.tagsea.core.WaypointMetaData;
import ca.uvic.cs.tagsea.core.WaypointProvider;


/**
 * 
 * 
 * @author Chris Callendar
 */
public class WaypointContentProviderTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testColumnText() throws Exception {
		WaypointMetaData waypointMetaData = new WaypointMetaData();
		waypointMetaData.setAuthor("Chris");
		waypointMetaData.setComment("a comment");
		waypointMetaData.setDate("05/13/2006");
		
		WaypointProvider provider = new WaypointProvider(null);
		Tag tag = new Tag(new Tag(), "waypoint");
		Waypoint waypoint = new Waypoint(null, tag, null, waypointMetaData);
		String tagName = provider.getColumnText(waypoint, 0);
		assertEquals("waypoint", tagName);
		
		// TODO more tests on different columns?
	}

}
