/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.tasks.waypoints;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.tasks.ITaskWaypointAttributes;
import net.sourceforge.tagsea.tasks.TaskWaypointPlugin;
import net.sourceforge.tagsea.tasks.TaskWaypointUtils;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

/**
 * Update the given waypoint to match its corresponding task. This operation is different than many. It allows
 * for information to be added to it so that it can run continuously until all the data is flushed. This
 * allows us to have only one operation instead of many operations for very few changes to markers. The
 * operation expires after all of its data has been flushed.
 * @author Del Myers
 *
 */
public class UpdateWaypointOperation extends TagSEAOperation implements
		IInternalWaypointOperation {

	private List<IMarker> tasks;
	private boolean expired;
	/**
	 * @param interesting
	 */
	public UpdateWaypointOperation(List<IMarker> initialTasks) {
		super("Synchronizing Java Task Waypoints...");
		this.tasks = new LinkedList<IMarker>();
		this.tasks.addAll(initialTasks);
		expired = false;
	}
	
	/**
	 * Joins the given additional tasks to this task if the operation has not yet expired.
	 * @param additionalTasks
	 * @return true if the tasks were added to this operation, false if the operation has
	 * already expired.
	 */
	public boolean join(List<IMarker> additionalTasks) {
		synchronized (tasks) {
			if (expired) return false;
			tasks.addAll(additionalTasks);
			//System.out.println(tasks.size());
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.TagSEAOperation#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	private IStatus runForTask(IMarker task)
			throws InvocationTargetException {
		MultiStatus status = new MultiStatus(TaskWaypointPlugin.PLUGIN_ID, 0, "", null);
		IWaypoint waypoint = TaskWaypointUtils.getWaypointForTask(task);
		if (task.exists()) {
			String tagString = TaskWaypointUtils.getTagString(task);
			if (tagString == null) {
				if (waypoint != null)
					waypoint.delete();
				return Status.OK_STATUS;
			}
			if (waypoint == null) {
				waypoint = TagSEAPlugin.getWaypointsModel().createWaypoint(TaskWaypointPlugin.WAYPOINT_ID, new String[] {tagString});
				if (waypoint == null) return Status.OK_STATUS;
			}
			int offset = task.getAttribute(IMarker.CHAR_START, -1);
			int end = task.getAttribute(IMarker.CHAR_END, -1);
			waypoint.setIntValue(ITaskWaypointAttributes.ATTR_CHAR_START, offset);
			waypoint.setIntValue(ITaskWaypointAttributes.ATTR_CHAR_END, end);
			String message = task.getAttribute(IMarker.MESSAGE, "");
			waypoint.setStringValue(ITaskWaypointAttributes.ATTR_RESOURCE, task.getResource().getFullPath().toPortableString());
			waypoint.setStringValue(ITaskWaypointAttributes.ATTR_MARKER_ID, ""+task.getId());
			List<ITag> tags = Arrays.asList(waypoint.getTags());
			if (tagString != null) {

				ITag tag = TagSEAPlugin.getTagsModel().getTag(tagString);
				if (!tags.contains(tag)) {
					for (ITag currentTag : tags) {
						status.merge(waypoint.removeTag(currentTag).getStatus());
					}
				}
				status.merge(waypoint.setText(message).getStatus());
				
			}
		} else {
			if (waypoint != null) status.merge(waypoint.delete().getStatus());
		}
		return status;
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.TagSEAOperation#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
		monitor.beginTask("Checking Tasks", IProgressMonitor.UNKNOWN);
		MultiStatus status = new MultiStatus(TaskWaypointPlugin.PLUGIN_ID, 0, "", null);
		while (!expired) {
			synchronized (tasks) {
				if (tasks.size() > 0) {
					status.merge(runForTask(tasks.remove(0)));
				} else {
					expired = true;
				}
			}
		}
		return status;
	}

}
