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
import ca.uvic.cs.tagsea.editing.TagTreeItemListener;


/**
 * 
 * 
 * @author Chris Callendar
 */
public class TagTreeItemListenerTest extends TestCase {

	private TagTreeItemListener listener;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		listener = new TagTreeItemListener(null);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testRename() throws Exception {
		Tag root = new Tag();
		Tag tag1 = new Tag(root, "tag1");
		listener.renameTag(tag1, "tag2");
		assertTrue("tag2".equals(tag1.getName()));		
	}
	
	public void testDelete() throws Exception {
		Tag root = new Tag();
		Tag tag1 = new Tag(root, "tag1");
		listener.deleteTag(tag1);
		assertEquals(0, root.getChildrenCount());		
	}
	
	public void testMove() throws Exception {
		Tag hiddenRoot = new Tag();
		Tag root = new Tag(hiddenRoot, "root");
		Tag tag1 = new Tag(root, "tag1");
		Tag anotherRoot = new Tag(hiddenRoot, "anotherRoot");

		assertEquals(2, hiddenRoot.getChildrenCount());		
		
		listener.moveTag(tag1, anotherRoot);
		assertEquals(0, root.getChildrenCount());
		assertEquals("/anotherRoot/tag1", tag1.getId());
		
		listener.moveTag(tag1, null);
		// the parent will the the hiddenRoot - it has an empty name
		assertEquals(0, tag1.getParent().getName().length());
		
		// make sure the children get copied over
		tag1.addChild("child");
		Tag thirdParent = new Tag(hiddenRoot, "thirdParent");
		thirdParent.addChild("tag1");
		
		listener.moveTag(tag1, thirdParent);
		assertEquals(1, thirdParent.getChildrenCount());
		Tag tag1Check = thirdParent.getChildren()[0];
		assertEquals("tag1", tag1Check.getName());
		assertEquals(1, tag1Check.getChildrenCount());
		assertEquals("child", tag1Check.getChildren()[0].getName());
		
	}
	
}
