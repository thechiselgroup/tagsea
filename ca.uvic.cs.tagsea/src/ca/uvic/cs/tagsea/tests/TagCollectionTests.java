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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import ca.uvic.cs.tagsea.core.Tag;
import ca.uvic.cs.tagsea.core.TagCollection;


/**
 * Tests the TagCollection.
 * 
 * @author Chris Callendar
 */
public class TagCollectionTests extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testAddRoot() throws Exception {
		TagCollection tags = new TagCollection();
		tags.addRootTag(new Tag(null, "MakeMeARoot"));
		Tag[] roots = tags.getRootTags();
		assertEquals(1, roots.length);
		Tag root = roots[0];
		assertEquals("MakeMeARoot", root.getName());
	}
	
	public void testLabelProvider() throws Exception {
		ILabelProvider provider = new TagCollection();
		Tag tag = new Tag(null, "tagName");
		assertEquals("tagName", provider.getText(tag));
	}
	
	public void testSingleAdd() throws Exception {		
		TagCollection tagCollection = new TagCollection();
		tagCollection.add("peter(bug(45)):this []", TestPlatformFactory.instance().getMarker(), null );
		Tag[] rootTags = tagCollection.getRootTags();
		assertEquals("peter", rootTags[0].getName());
	}
	
	public void testMultiAdd() throws Exception {		
		TagCollection tagCollection = new TagCollection();
		tagCollection.add("peter(bugzilla(22)):this []", TestPlatformFactory.instance().getMarker(), null );
		tagCollection.add("peter(bug(45)):this []", TestPlatformFactory.instance().getMarker(), null );
		Tag[] rootTags = tagCollection.getRootTags();
		assertEquals("peter", rootTags[0].getName());
		assertEquals(1,rootTags.length);
	}
	
	public void testContentProvider() throws Exception {
		TagCollection tagCollection = new TagCollection();
		tagCollection.add("chris(child1)", TestPlatformFactory.instance().getMarker(), null);
		tagCollection.add("chris(child2)", TestPlatformFactory.instance().getMarker(), null );
		tagCollection.add("chris2", TestPlatformFactory.instance().getMarker(), null );
		
		ITreeContentProvider provider = tagCollection;
		Tag[] roots = (Tag[]) provider.getElements(new Object());
		assertEquals(2, roots.length);
		assertTrue(provider.hasChildren(roots[0]));
		assertEquals(2, provider.getChildren(roots[0]).length);
		assertTrue(!provider.hasChildren(roots[1]));
	}
	
	public void testTree() throws Exception {
		TagCollection tagCollection = new TagCollection();
		tagCollection.add("peter(bugzilla(22)):this []", TestPlatformFactory.instance().getMarker(), null );
		tagCollection.add("peter(bug(45)):this []", TestPlatformFactory.instance().getMarker(), null );
		Tag[] rootTags = tagCollection.getRootTags();
		Tag[] level1 = rootTags[0].getChildren();
		assertEquals(2,level1.length);
		assertEquals(true, ( "bugzilla".equals(level1[0].getName()) && "bug".equals(level1[1].getName())) ||
						   ( "bug".equals(level1[0].getName()) && "bugzilla".equals(level1[1].getName())) );
	}
	

}