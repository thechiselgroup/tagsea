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

import java.util.regex.PatternSyntaxException;

import junit.framework.TestCase;
import ca.uvic.cs.tagsea.core.Tag;
import ca.uvic.cs.tagsea.navigation.TagsTreeFilter;
import ca.uvic.cs.tagsea.navigation.TreeFilter;


/**
 * Tests the TreeFilter and TagsTreeFilter classes.
 * 
 * @author Chris Callendar
 */
public class TagsTreeFilterTest extends TestCase {

	public void testPatternAndMatchTag() throws Exception {
		TagsTreeFilter filter = new TagsTreeFilter();
		filter.setPattern("T");

		// Simple pattern match
		Tag tag = new Tag(null, "Tag1(Child)");
		assertTrue(filter.match(tag));

		// Child match
		filter.setPattern("C");
		assertTrue(filter.match(tag));

		// Does not exist
		filter.setPattern("A");
		assertTrue(!filter.match(tag));

		// Should exist (ignores case)
		filter.setPattern("child");
		assertTrue(filter.match(tag));
		
		// Should throw exception
		try {
  		  filter.setPattern("Tag1(Ch");
  		  assertTrue(false); // should not get here!
		} catch (PatternSyntaxException pse) {
		  assertTrue(true);	
		}
	}
	
	public void testComplexPatternMatchTag() throws Exception {
		TagsTreeFilter filter = new TagsTreeFilter();
		Tag tag = new Tag(null, "Tag1(Child)");

		// Search for anything that contains a number
		filter.setPattern(".*\\d");
		assertTrue(filter.match(tag));
		
		tag = new Tag(null, "Tag1(Child(1234))");
		// Search in a deeper nested tag
		filter.setPattern("1");
		assertTrue(filter.match(tag));
	}	
	
	public void testFilter() throws Exception {
		TreeFilter filter = new TreeFilter();
		filter.setPattern("B");
		
		String[] tests = { "A[B]", "A{B}", "A B", "A(B)", "A_B" };
		for (String test : tests) {
			assertTrue(filter.match(test));
		}
	}
}
