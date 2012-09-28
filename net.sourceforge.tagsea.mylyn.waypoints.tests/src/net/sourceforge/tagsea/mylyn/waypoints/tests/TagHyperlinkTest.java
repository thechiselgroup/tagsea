package net.sourceforge.tagsea.mylyn.waypoints.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointsModel;
import net.sourceforge.tagsea.mylyn.waypoints.TagHyperlinkDetector;
import net.sourceforge.tagsea.mylyn.waypoints.TagsHyperlink;
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
import org.omg.IOP.TAG_MULTIPLE_COMPONENTS;

public class TagHyperlinkTest {

	private TextViewer viewer;
	private static IWaypoint testWaypoint;
	private static String tagText;
	private TagHyperlinkDetector tagDetector;
	private static ITag[] tags;

	@BeforeClass
	public static void fixture() {
		IWaypointsModel waypointModel = TagSEAPlugin.getWaypointsModel();
		assertNotNull(waypointModel);

		IWaypoint[] waypoints = waypointModel
				.getWaypoints(ParsedWaypointPlugin.WAYPOINT_TYPE);
		for (IWaypoint waypoint : waypoints) {
			if (waypoint.getText().equals("This is a complex tag")) {
				testWaypoint = waypoint;
			}

		}

		assertNotNull(testWaypoint);
		tagText = WaypointsUtils.getTags(testWaypoint);
		tags = testWaypoint.getTags();
		assertEquals(2, tags.length);
	}

	@Before
	public void setup() {
		viewer = new TextViewer(new Shell(), SWT.NONE);
		tagDetector = new TagHyperlinkDetector();
	}

	@Test
	public void tagPattern() {
		assertFalse(tagText.isEmpty());

		Pattern pattern = TagHyperlinkDetector.PATTERN;
		Matcher m = pattern.matcher(tagText);
		assertTrue(m.find());

		m.reset();
		int numTags = 0;
		while (m.find()) {
			numTags++;
		}
		assertEquals(tags.length, numTags);
	}

	@Test
	public void tagInfo() {
		Pattern pattern = TagHyperlinkDetector.PATTERN;
		Matcher m = pattern.matcher(tagText);
		assertTrue(m.find());
		String[] tagInfo = tagDetector.getTagInfo(m);
		assertNotNull(tagInfo);

		assertEquals(tags.length, tagInfo.length);
		List<String> tagStrs = new ArrayList<String>();
		for (ITag tag : tags) {
			tagStrs.add(tag.getName());
		}
		for (String tag : tagInfo) {
			assertTrue(tagStrs.contains(tag));
		}
	}

	@Test
	public void detectTagHyperlink() {
		viewer.setDocument(new Document(tagText));
		Region region = new Region(0, tagText.length());
		IHyperlink[] links = tagDetector
				.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(tags.length, links.length);

		int tagIndex = 0;
		for (IHyperlink link : links) {
			
			assertEquals(tagIndex + WaypointsUtils.TAG_MARKER.length()+1, link
					.getHyperlinkRegion().getOffset());
			tagIndex = tagText.indexOf(WaypointsUtils.TAG_MARKER, tagIndex + 1);
			for (ITag tag : tags) {
				if (((TagsHyperlink) link).getTag().contains(tag.getName())) {
					assertEquals(tag.getName().length(), link
							.getHyperlinkRegion().getLength());
				}
			}
		}
	}

}
