/*******************************************************************************
 * 
 *   Copyright 2007, 2008, CHISEL Group, University of Victoria, Victoria, BC, Canada. 
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *       The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.mylyn.waypoints.tests;

import net.sourceforge.tagsea.mylyn.waypoints.WaypointDescriptor;
import net.sourceforge.tagsea.mylyn.waypoints.WaypointHyperlink;

import org.junit.Assert;
import org.junit.Test;


public class TaskWaypointTest {

	private String desc = "Add actions";
	private String location = "TagSEAView.java ItemEditListener[8595-8625]";
	private String waypointText = WaypointHyperlink.LINK_TAG + " " + desc + ": " + location;
	
	@Test
	public void creation() throws Exception {
		WaypointDescriptor twp = WaypointDescriptor.createFromText(waypointText);
		Assert.assertEquals(desc, twp.getDescription());
		Assert.assertEquals(location, twp.getLocation());
	}
	
}
