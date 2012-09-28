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
package net.sourceforge.tagsea.mylyn.waypoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.IWaypointChangeListener;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.mylyn.core.BuildMapsJob;
import net.sourceforge.tagsea.mylyn.core.LocationDescriptor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

public class BuildWaypointMaps extends BuildMapsJob implements IWaypointChangeListener {

	private Map<String, Set<AbstractTask>> taskWaypointLeft = new HashMap<String, Set<AbstractTask>>();
	
	public BuildWaypointMaps() {
		this("Building waypoint map");
	}

	public BuildWaypointMaps(String name) {
		super(name);
	}

	// @tag tagsea.mylyn.task.example : Example for building Mylyn task/Eclipse
	// task map
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		super.run(monitor);
		
		TaskList taskList = taskManager.getTaskList();
		if(taskList == null){
			System.err.println("Tasklist null");
		}

		TagSEAPlugin.addWaypointChangeListener(this);

		synchronized (taskWaypointLeft) {
			IWaypoint[] waypoints = TagSEAPlugin.getWaypointsModel()
					.getAllWaypoints();
			for (AbstractTask task : taskList.getAllTasks()) {
				String notes = task.getNotes();
				String[] lines = notes.split("\n");
				for (String line : lines) {
					// Handle case where there is no information
					if (line.isEmpty())
						continue;

					WaypointsUtils.waypointFromText(line);
					IWaypoint waypoint = waypointFromText(line, waypoints);
					if (waypoint != null) {
						WaypointMylynPlugin.getDefault().addWaypoint(waypoint,
								task);
					} else {

						// Add to list of lines that we haven't found a waypoint
						// for
						Set<AbstractTask> tasks = this.taskWaypointLeft
								.get(line);
						if (tasks == null) {
							tasks = new HashSet<AbstractTask>();
							this.taskWaypointLeft.put(line, tasks);
						}
						tasks.add(task);
					}
				}
			}

		}

		return Status.OK_STATUS;
	}

	private IWaypoint waypointFromText(String line, IWaypoint[] waypoints) {
		LocationDescriptor twp = LocationDescriptor.createFromText(
				WaypointHyperlink.LINK_TAG, WaypointsUtils.TAG_MARKER, line);
		for (IWaypoint waypoint : waypoints) {
			if (waypoint.getText().equals(twp.getDescription())
					&& WaypointsUtils.getLocation(waypoint).equals(
							twp.getLocation())) {
				return waypoint;
			}
		}
		return null;
	}

	public void waypointsChanged(WaypointDelta delta) {

		IWaypointChangeEvent[] changes = delta.getChanges();
		for (IWaypointChangeEvent change : changes) {
			IWaypoint waypoint = change.getWaypoint();

			synchronized (taskWaypointLeft) {
				List<String> canRemove = new ArrayList<String>();
				for (String line : taskWaypointLeft.keySet()) {
					LocationDescriptor twp = LocationDescriptor.createFromText(
							WaypointHyperlink.LINK_TAG, WaypointsUtils.TAG_MARKER, line);
					boolean sameDesc = waypoint.getText().equals(
							twp.getDescription());
					if (sameDesc) {
						canRemove.add(line);
						Set<AbstractTask> tasks = taskWaypointLeft.get(line);
						for (AbstractTask task : tasks) {
							WaypointMylynPlugin.getDefault().addWaypoint(
									waypoint, task);
						}
					}
				}
				for (String line : canRemove)
					taskWaypointLeft.remove(line);

			}
		}

	}

}
