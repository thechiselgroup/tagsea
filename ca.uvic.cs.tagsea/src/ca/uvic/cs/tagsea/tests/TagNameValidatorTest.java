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
import ca.uvic.cs.tagsea.editing.TagNameValidator;


/**
 * Tests valid names for tags.
 * 
 * @author Chris Callendar
 */
public class TagNameValidatorTest extends TestCase {

	public void testValidNames() throws Exception {
		TagNameValidator validator = new TagNameValidator();
		
		String testName = "TagName";
		// validator.isValid(String) returns null if the name is valid
		// otherwise an error message is returned
		assertNull(validator.isValid(testName));

		testName = "asdfasdf";
		assertNull(validator.isValid(testName));		

		// apostrophe's are allowed?
		testName = "It's";
		assertNull(validator.isValid(testName));
		
	}
	
	public void testInvalidNames() throws Exception {
		TagNameValidator validator = new TagNameValidator();

		// spaces in names are not allowed
		String testName = "Tag name";
		assertNotNull(validator.isValid(testName));

		// special characters like #, @, ! are not allowed
		testName = "Tag#";
		assertNotNull(validator.isValid(testName));
		testName = "@tag";
		assertNotNull(validator.isValid(testName));
		testName = "Help!";
		assertNotNull(validator.isValid(testName));
				
		// quotes not allowed
		testName = "It\"s";
		assertNotNull(validator.isValid(testName));

		// colons not allowed
		testName = "tagname : test";
		assertNotNull(validator.isValid(testName));
	}
	
}
