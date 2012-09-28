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

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.tasks.ITaskWaypointAttributes;
import net.sourceforge.tagsea.tasks.TaskWaypointPlugin;
import net.sourceforge.tagsea.tasks.TaskWaypointUtils;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Loads the java tasks in the workspace and creates task waypoints for them.
 * @author Del Myers
 *
 */
public class TaskLoadOperation extends TagSEAOperation implements IInternalWaypointOperation{

	public TaskLoadOperation() {
		super("Loading Java Task Waypoints...");
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.TagSEAOperation#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus run(IProgressMonitor monitor)
			throws InvocationTargetException {
		try {
			IMarker[] markers = ResourcesPlugin.getWorkspace().getRoot().findMarkers(IMarker.TASK, true, IResource.DEPTH_INFINITE);
			monitor.beginTask("Loading task waypoints.", markers.length);
			for (IMarker marker : markers) {
				if (!marker.exists()) continue;
				String tag = TaskWaypointUtils.getTagString(marker);
				if (tag != null) {
					IWaypoint wp = TaskWaypointUtils.getWaypointForTask(marker);
					if (wp != null) 
						continue;
					wp = TagSEAPlugin.getWaypointsModel().createWaypoint(TaskWaypointPlugin.WAYPOINT_ID, new String[]{tag});
					if (wp != null) {
						wp.setStringValue(ITaskWaypointAttributes.ATTR_RESOURCE, marker.getResource().getFullPath().toPortableString());
						int offset = marker.getAttribute(IMarker.CHAR_START, -1);
						int end = marker.getAttribute(IMarker.CHAR_END, -1);
						wp.setIntValue(ITaskWaypointAttributes.ATTR_CHAR_START, offset);
						wp.setIntValue(ITaskWaypointAttributes.ATTR_CHAR_END, end);
						wp.setStringValue(ITaskWaypointAttributes.ATTR_MARKER_ID, ""+marker.getId());
						String author = marker.getAttribute(ITaskWaypointAttributes.ATTR_AUTHOR, null);
						if (author != null) {
							wp.setStringValue(ITaskWaypointAttributes.ATTR_AUTHOR, author);
						}
						String message = marker.getAttribute(IMarker.MESSAGE, "");
						wp.setText(message);
					}
				}
			}
		} catch (CoreException e) {
			return e.getStatus();
		}
		
		return Status.OK_STATUS;
	}

}
