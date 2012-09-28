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

import static junit.framework.Assert.*;
import net.sourceforge.tagsea.mylyn.waypoints.WaypointHyperlink;
import net.sourceforge.tagsea.mylyn.waypoints.WaypointHyperlinkDetector;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;


public class WaypointHyperlinkDetectorTest {

	
	private static String WAYPOINT_FORMAT_1 = WaypointHyperlink.LINK_TAG + "Waypoint #1";
	private static String WAYPOINT_FORMAT_2 = WaypointHyperlink.LINK_TAG + " Waypoint #1 ";
	private static final String WAYPOINT_FORMAT_1_2 = WaypointHyperlink.LINK_TAG + "Waypoint #2";
	
	private WaypointHyperlinkDetector detector = new WaypointHyperlinkDetector();
	private TextViewer viewer = new TextViewer(new Shell(), SWT.NONE);
	
	private String[] formats = {WAYPOINT_FORMAT_1, WAYPOINT_FORMAT_2};

	// Test cases taken from org.eclipse.mylyn.bugzilla.tests.BugzillaTaskHyperlinkDetectorTest
	@Test
	public void testBeginning() {
		for (String format : formats) {
			String testString = format + " is at the beginning";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertEquals(testString.indexOf(format)+WaypointHyperlink.LINK_TAG.length(), links[0].getHyperlinkRegion().getOffset());
		}
	}
	
	@Test
	public void testEnd() {
		for (String format : formats) {
			String testString = "is ends with " + format;
			viewer.setDocument(new Document(testString));
			Region region = new Region(testString.indexOf(format), testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertEquals(testString.indexOf(format)+WaypointHyperlink.LINK_TAG.length(), links[0].getHyperlinkRegion().getOffset());
		}
	}

	@Test
	public void testMiddle() {
		for (String format : formats) {
			String testString = "is a " + format + " in the middle";
			viewer.setDocument(new Document(testString));
			Region region = new Region(testString.indexOf(format), testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertEquals(testString.indexOf(format)+WaypointHyperlink.LINK_TAG.length(), links[0].getHyperlinkRegion().getOffset());
		}
	}
}
