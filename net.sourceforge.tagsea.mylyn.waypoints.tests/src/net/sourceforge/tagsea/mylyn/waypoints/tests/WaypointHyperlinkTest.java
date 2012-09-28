package net.sourceforge.tagsea.mylyn.waypoints.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointsModel;
import net.sourceforge.tagsea.mylyn.waypoints.WaypointHyperlink;
import net.sourceforge.tagsea.mylyn.waypoints.WaypointHyperlinkDetector;
import net.sourceforge.tagsea.mylyn.waypoints.WaypointsUtils;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WaypointHyperlinkTest {

	private TextViewer viewer;
	private static IWaypoint testWaypoint;
	private static String waypointText;
	private WaypointHyperlinkDetector waypointDetector;

	@BeforeClass
	public static void fixture() {
		IWaypointsModel waypointModel = TagSEAPlugin.getWaypointsModel();
		assertNotNull(waypointModel);

		IWaypoint[] waypoints = waypointModel
				.getWaypoints(ParsedWaypointPlugin.WAYPOINT_TYPE);
		for (IWaypoint waypoint : waypoints) {
			if (waypoint.getText().equals("This is a simple test tag")) {
				testWaypoint = waypoint;
			}

		}

		assertNotNull(testWaypoint);
		waypointText = WaypointsUtils
				.getWaypointInformation(testWaypoint, true);
	}

	@Before
	public void setup() {
		viewer = new TextViewer(new Shell(), SWT.NONE);
		waypointDetector = new WaypointHyperlinkDetector();
	}

	@Test
	public void waypointText() {
		assertFalse(waypointText.isEmpty());
		assertTrue(waypointText.startsWith(WaypointHyperlink.LINK_TAG));
		assertTrue(waypointText.endsWith(WaypointHyperlink.LINK_TAG));
	}

	@Test
	public void waypointPattern() {

		Pattern pattern = WaypointHyperlinkDetector.PATTERN;
		Matcher m = pattern.matcher(waypointText);
		assertTrue(m.find());
	}

	@Test
	public void waypointInfo() {
		Pattern pattern = WaypointHyperlinkDetector.PATTERN;
		Matcher m = pattern.matcher(waypointText);
		assertTrue(m.find());
		String waypointInfo = waypointDetector.getWaypointInfo(m);
		assertNotNull(waypointInfo);
		assertEquals(waypointText.replace(WaypointHyperlink.LINK_TAG, "")
				.trim(), waypointInfo.trim());
	}

	@Test
	public void detectWaypointHyperlink() {
		viewer.setDocument(new Document(waypointText));
		Region region = new Region(0, waypointText.length());
		IHyperlink[] links = waypointDetector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(WaypointHyperlink.LINK_TAG.length() + 1, links[0]
				.getHyperlinkRegion().getOffset());
		assertEquals(waypointText.length()
				- (2 * (WaypointHyperlink.LINK_TAG.length() + 1)), links[0]
				.getHyperlinkRegion().getLength());
		
		List<IWaypoint> waypointList = new ArrayList<IWaypoint>();
		waypointList.add(testWaypoint);
		String waypointTagInfo = WaypointsUtils.getWaypointInformation(waypointList);
		viewer.setDocument(new Document(waypointTagInfo));
		region = new Region(0, waypointTagInfo.length());
		links = waypointDetector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(WaypointHyperlink.LINK_TAG.length() + 1, links[0]
				.getHyperlinkRegion().getOffset());
		
		int firstWaypointTagIndex = waypointTagInfo.indexOf(WaypointHyperlink.LINK_TAG);
		int lastWaypointTagIndex = waypointTagInfo.lastIndexOf(WaypointHyperlink.LINK_TAG);
		String linkText = waypointTagInfo.substring(firstWaypointTagIndex, lastWaypointTagIndex);
		linkText = linkText.replace(WaypointHyperlink.LINK_TAG, "").trim();
		assertEquals(linkText.length(), links[0]
				.getHyperlinkRegion().getLength());
		

	}
}
