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
package net.sourceforge.tagsea.tasks;

import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.ParsedWaypointUtils;
import net.sourceforge.tagsea.resources.ResourceWaypointUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jdt.core.JavaCore;

/**
 * Utility class for tag waypoints.
 * @author Del Myers
 *
 */
public class TaskWaypointUtils {
	
	/**
	 * Returns true if the given marker should be waypointed on.
	 * @param marker
	 * @return
	 */
	public static boolean isInterestingMarker(IMarker marker) {
		return getTagString(marker) != null;
	}
	
	public static String getTagString(IMarker marker) {
		try {
			if (marker.isSubtypeOf(IMarker.TASK)) {
				IResource resource = marker.getResource();
				if (resource.exists() && "java".equals(marker.getResource().getFileExtension())) {
					int offset = marker.getAttribute(IMarker.CHAR_START, -1);
					int end = marker.getAttribute(IMarker.CHAR_END, -1);
					IWaypoint[] javaWaypoints = ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getWaypointsForFile((IFile)resource);

					if ((offset != -1) && (end != -1)) {
//						check to see if the marker is held inside a java waypoint.
						for (IWaypoint parsedWaypoint : javaWaypoints) {
							int pOff = ParsedWaypointUtils.getCharStart(parsedWaypoint);
							int pEnd = ParsedWaypointUtils.getCharEnd(parsedWaypoint);
							if (offset >= pOff && end <= pEnd) {
								//not interesting
								return null;
							}
						}
//						ITextFileBuffer buffer = 
//							FileBuffers.getTextFileBufferManager().getTextFileBuffer(resource.getLocation());
//						if (buffer == null) {
//							FileBuffers.getTextFileBufferManager().connect(resource.getLocation(), new NullProgressMonitor());
//							buffer = 
//								FileBuffers.getTextFileBufferManager().getTextFileBuffer(resource.getLocation());
//						}
						String text = marker.getAttribute(IMarker.MESSAGE, null);
//						if (buffer != null) {
//							String text = buffer.getDocument().get(offset, end-offset);
							String[] words = text.split("(\\s+|\\W+)");
							for (String tag : getJavaTaskTags()) {
								//String tagMatcher = "\\W*" + Pattern.quote(tag) + "\\W*";
								if (words[0].equals(tag)) {
									return tag;
								}
							}

						//}
					}
				}
			}

		//} catch (BadLocationException e) { 
		}catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String[] getJavaTaskTags() {
		Preferences prefs = JavaCore.getJavaCore().getPluginPreferences();
		String tags = prefs.getString(JavaCore.COMPILER_TASK_TAGS);
		String[] tagWords = tags.split("[,]");
		String[] result = new String[tagWords.length];
		for (int i = 0; i < tagWords.length; i++) {
			result[i] = tagWords[i].trim();
		}
		return result;
	}
	
	/**
	 * Returns the Task in the code to which the waypoint refers.
	 * @param wp
	 * @return the task in the code to which the waypoint refers.
	 */
	public static IMarker getTaskForWaypoint(IWaypoint wp) {
		String id = wp.getStringValue(ITaskWaypointAttributes.ATTR_MARKER_ID, null);
		if (id == null) return null;
		
		IResource resource = ResourceWaypointUtils.getResource(wp);
		if (resource == null) return null;
		return resource.getMarker(Long.parseLong(id));
	}
	
	/**
	 * Returns the waypoint that references the given task, if any exists.
	 * @param task
	 * @return the waypoint that references the given task, if any exists.
	 */
	public static IWaypoint getWaypointForTask(IMarker task) {
		IResource resource = task.getResource();
		IWaypoint[] waypoints = getWaypointsForResource(resource);
		for (IWaypoint wp :waypoints) {
			String idString = wp.getStringValue(ITaskWaypointAttributes.ATTR_MARKER_ID, null);
			long id = Long.parseLong(idString);
			if (id == task.getId()) {
				return wp;
			}
		}
		return null;
	}

	/**
	 * @param resource
	 * @return
	 */
	private static IWaypoint[] getWaypointsForResource(IResource resource) {
		IWaypoint[] all = TagSEAPlugin.getWaypointsModel().getWaypoints(TaskWaypointPlugin.WAYPOINT_ID);
		List<IWaypoint> result = new LinkedList<IWaypoint>();
		for (IWaypoint wp : all) {
			if (resource.getFullPath().equals(ResourceWaypointUtils.getResourcePath(wp))) {
				result.add(wp);
			}
		}
		return result.toArray(new IWaypoint[result.size()]);
	}
	
	
	public static int getOffset(IWaypoint waypoint) {
		return waypoint.getIntValue(ITaskWaypointAttributes.ATTR_CHAR_START, -1);
	}
	
	public static int getLength(IWaypoint waypoint) {
		int result = getEnd(waypoint) - getOffset(waypoint);
		return (result >= 0) ? result : -1;
	}
	
	public static int getEnd(IWaypoint waypoint) {
		return waypoint.getIntValue(ITaskWaypointAttributes.ATTR_CHAR_END, -1);
	}
	
 
	/**
	 * @param waypoint
	 * @return
	 */
	public static long getTaskID(IWaypoint waypoint) {
		String val = waypoint.getStringValue(ITaskWaypointAttributes.ATTR_MARKER_ID, null);
		if (val != null) {
			return Long.parseLong(val);
		}
		return -1;
	}
	
}
