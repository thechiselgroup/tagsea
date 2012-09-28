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

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Runs all the tests that <b>don't</b> depend on the plugin or workspace.
 * 
 * @author Chris Callendar
 */
public class AllNonPluginTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Non-plugins test for ca.uvic.cs.tagsea.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TagNameValidatorTest.class);
		suite.addTestSuite(ParserTests.class);
		suite.addTestSuite(TagTests.class);
		//suite.addTestSuite(TagCollectionTests.class);
		suite.addTestSuite(WaypointContentProviderTest.class);
		//suite.addTestSuite(PluginTests.class);
		suite.addTestSuite(WaypointMetaDataTest.class);
		suite.addTestSuite(ExtractionTest.class);
		suite.addTestSuite(TagsTreeFilterTest.class);
		//$JUnit-END$
		return suite;
	}
	
}
