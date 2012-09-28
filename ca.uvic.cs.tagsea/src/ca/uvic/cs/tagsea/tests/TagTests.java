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


/**
 * 
 * 
 * @author Chris Callendar
 */
public class TagTests extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testChildren() throws Exception {
		Tag tag = new Tag(null, "Parent");
		assertEquals(0, tag.getChildrenCount());
		
		Tag child = new Tag(tag, "Child");
		assertEquals(1, tag.getChildrenCount());
		assertTrue("Child".equals(child.getName()));
	}
	
	public void testChildrenRemove() throws Exception {
		Tag tag = new Tag(null, "Parent");
		Tag child1 = new Tag(tag, "Child1");
		Tag child2 = new Tag(child1, "Child2");
		Tag child3 = new Tag(child2, "Child3");
		Waypoint waypoint = new Waypoint(child3);
		child3.addWaypoint(waypoint);
		child3.removeWaypoint(waypoint);
		assertEquals(0, tag.getChildrenCount());
		
	}
		
	public void testTagName() throws Exception {
		Tag tag = new Tag(null, "TagName");
		assertEquals("TagName", tag.getName());
	}	
	
	public void testFindWaypoint() throws Exception {
		Tag tag = new Tag(null, "root");
		Tag tag1 = new Tag(tag, "tag1");
		Tag child = new Tag(tag1, "child");
		Waypoint wp = new Waypoint(child);
		child.addWaypoint(wp);
		String keyword = wp.getKeyword();
		
		// test searching from root tag
		Waypoint found = tag.findWaypoint(keyword);
		assertNotNull(found);
		
		found = child.findWaypoint(keyword);
		assertNotNull(found);
		
	}
	

	public void testKeywordGeneration() throws Exception {
		Tag tag = new Tag(null, "root");
		Tag tag1 = new Tag(tag, "tag1");
		Tag child = new Tag(tag1, "child");

		String keyword = Tag.generateKeyword(child);
		assertEquals("root(tag1(child))", keyword);
	}	
	
}
