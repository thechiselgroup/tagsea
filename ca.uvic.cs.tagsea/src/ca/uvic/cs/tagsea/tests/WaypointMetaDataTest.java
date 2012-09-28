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
import ca.uvic.cs.tagsea.core.WaypointMetaData;


/**
 * 
 * 
 * @author Chris Callendar
 */
public class WaypointMetaDataTest extends TestCase {
	
	public void testConstructor() throws Exception {
		String[] names = {"author", "date", "comment"};
		String[] values = {"Chris", "24/01/2004", "A comment here"};
		
        WaypointMetaData md = new WaypointMetaData(names, values);
        assertEquals(values[0], md.getAuthor());	
        assertEquals(values[1], md.getDate());	
        assertEquals(values[2], md.getComment());	

		String[] names2 = {"Author", "DATE", "coMMent"};
        md = new WaypointMetaData(names2, values);
        assertEquals(values[0], md.getAuthor());	
        assertEquals(values[1], md.getDate());	
        assertEquals(values[2], md.getComment());	
	}

}
