package net.sourceforge.tagsea.mylyn.waypoints.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointsModel;
import net.sourceforge.tagsea.mylyn.waypoints.WaypointMylynPlugin;
import net.sourceforge.tagsea.mylyn.waypoints.WaypointsUtils;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;

import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.junit.Before;
import org.junit.Test;

/**
 * There are a number of other tests that should be included in here but require
 * setting up a runtime workspace containing waypoints (e.g. for testing
 * getLocation() a waypoint needs a location) Didn't have time for set this up.
 * 
 * @author John
 * 
 */

public class WaypointUtilsTest {

	private static final int EXISTING_WAYPOINTS = 2;

	private AbstractTask task;

	private static String[] TAGS = {"tag1, tag2"};

	@Test
	public void validateTestEnvironment() {
		IWaypointsModel waypointModel = TagSEAPlugin.getWaypointsModel();
		assertNotNull(waypointModel);

		IWaypoint[] waypoints = waypointModel.getAllWaypoints();
		assertNotNull(waypoints);
		assertEquals(EXISTING_WAYPOINTS, waypoints.length);
	}

	@Before
	public void setup() {
		task = new LocalTask("1", "Test Task");
	}

	@Test
	public void waypointMap() {

		WaypointMylynPlugin plugin = WaypointMylynPlugin.getDefault();
		Set<IWaypoint> knownWaypoints = plugin.getAllWaypoints();
		assertNotNull(knownWaypoints);
		assertEquals(EXISTING_WAYPOINTS, knownWaypoints.size());

		IWaypoint waypoint = TagSEAPlugin.getWaypointsModel().createWaypoint(
				ParsedWaypointPlugin.WAYPOINT_TYPE, TAGS);
		assertNotNull(waypoint);
		
		plugin.addWaypoint(waypoint, task);

		assertEquals(1 + EXISTING_WAYPOINTS, knownWaypoints.size());

		Set<AbstractTask> tasks = plugin.getTasks(waypoint);
		assertNotNull(tasks);
		assertEquals(1, tasks.size());

		Set<IWaypoint> waypoints = plugin.getWaypoints(task);
		assertNotNull(waypoints);
		assertEquals(1, waypoints.size());
	}

	@Test
	public void getNewWaypoints() throws Exception {

		WaypointMylynPlugin plugin = WaypointMylynPlugin.getDefault();
		Set<IWaypoint> knownWaypoints = plugin.getAllWaypoints();
		for (IWaypoint waypoint : knownWaypoints) {
			plugin.addWaypoint(waypoint, task);	
		}
		
		IWaypoint newWaypoint = TagSEAPlugin.getWaypointsModel().createWaypoint(
				ParsedWaypointPlugin.WAYPOINT_TYPE, TAGS);
		List<IWaypoint> waypoints = new ArrayList<IWaypoint>(knownWaypoints);
		waypoints.add(newWaypoint);
		
		List<IWaypoint> newWaypoints = WaypointsUtils.getNewWaypoints(task,
				waypoints);
		assertEquals(1, newWaypoints.size());
		IWaypoint waypoint = newWaypoints.get(0);
		assertEquals(newWaypoint, waypoint);
	}

	@Test
	public void addTaskTag() throws Exception {
		String tag = task.getConnectorKind() + "." + WaypointsUtils.REPORT_TAG
				+ "1";
		assertEquals(tag, WaypointsUtils.addTaskTag(task,
				Collections.EMPTY_LIST));
	}

}
