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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ITextWaypointAttributes;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.TagSEAView;
import net.sourceforge.tagsea.core.ui.internal.tags.TagTreeItem;
import net.sourceforge.tagsea.mylyn.core.LocationDescriptor;
import net.sourceforge.tagsea.parsed.IParsedWaypointAttributes;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.tasks.ITaskWaypointAttributes;
import net.sourceforge.tagsea.tasks.TaskWaypointPlugin;
import net.sourceforge.tagsea.tasks.TaskWaypointUtils;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

public class WaypointsUtils {

	public static final String REPORT_TAG = "bug";
	public static final String TAG_MARKER = "tag>";

	private static List<IWaypoint> convertTagsToWaypoints(
			TagTreeItem[] tagTreeItems) {
		List<IWaypoint> wps = new ArrayList<IWaypoint>();
		boolean rootSelected = false;
		TagTreeItem root = null;
		for (TagTreeItem tagTreeItem : tagTreeItems) {
			if (null == tagTreeItem.getParent()) { // @tag info -author="John
				// Anvik"
				// -date="enCA:24/10/07" :
				// check for root which has
				// no name
				rootSelected = true;
				root = tagTreeItem;
			}
		}
		if (rootSelected) {
			tagTreeItems = root.getChildren();
		}
		IWaypoint[] waypoints;
		for (TagTreeItem tagTreeItem : tagTreeItems) {
			List<ITag> tags = tagTreeItem.getChildTags();
			for (ITag tag : tags) {
				waypoints = tag.getWaypoints();
				for (IWaypoint waypoint : waypoints) {
					wps.add(waypoint);
				}
			}
		}
		return wps;
	}

	public static IWaypoint waypointFromText(String text) {

		IWaypoint waypoint = null;
		WaypointMylynPlugin plugin = WaypointMylynPlugin.getDefault();
		if (plugin == null) {
			return null;
		}

		LocationDescriptor taskWaypoint = LocationDescriptor.createFromText(
				WaypointHyperlink.LINK_TAG, TAG_MARKER, text);

		List<IWaypoint> waypoints = new ArrayList<IWaypoint>(plugin
				.getAllWaypoints());
		waypoint = searchWaypoints(taskWaypoint, waypoints);

		// If the cache doesn't have the waypoint, check all known waypoints
		IWaypoint[] allWaypoints = TagSEAPlugin.getWaypointsModel()
				.getAllWaypoints();
		waypoints = Arrays.asList(allWaypoints);
		waypoint = searchWaypoints(taskWaypoint, waypoints);

		return waypoint;
	}

	private static IWaypoint searchWaypoints(LocationDescriptor taskWaypoint,
			List<IWaypoint> waypoints) {
		for (IWaypoint waypoint : waypoints) {
			String waypointDesc = waypoint.getText();
			if (waypointDesc.equals(taskWaypoint.getDescription())) {
				if (taskWaypoint.getLocation() != null) {
					String location = getLocation(waypoint);
					if (location.equals(taskWaypoint.getLocation()))
						return waypoint;
				}
			}
		}
		return null;
	}

	public static String getLocation(IWaypoint waypoint) {

		String location = null;
		int lineNumber = waypoint.getIntValue(
				ITextWaypointAttributes.ATTR_LINE, -1);

		String resourceName = waypoint.getStringValue(
				IParsedWaypointAttributes.ATTR_RESOURCE, null);
		if (resourceName != null) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(new Path(resourceName));
			if (resource != null && resource.exists()) {
				if (lineNumber == -1) {
					// Try getting the line number via a marker
					String markerIdStr = waypoint.getStringValue(
							ITaskWaypointAttributes.ATTR_MARKER_ID, "-1");
					long markerId = Long.parseLong(markerIdStr);
					if (markerId != -1) {
						IMarker marker = resource.getMarker(markerId);
						if (marker != null) {
							lineNumber = marker.getAttribute(
									IMarker.LINE_NUMBER, -1);
						}
					}

				}

				IPath path = resource.getLocation();
				location = path.lastSegment();
				if (lineNumber != -1) {
					location += " line " + lineNumber;
				}

			}
		}
		/*
		 * IWaypointUIExtension ui = TagSEAPlugin.getDefault().getWaypointUI(
		 * waypoint.getType()); if (ui != null) { return
		 * ui.getLocationString(waypoint); } System.err.println("No Location: " +
		 * waypoint.getType());
		 */
		return location;
	}

	public static String getWaypointInformation(IWaypoint waypoint,
			boolean marker) {
		String waypointInfo = waypoint.getText();
		String location = getLocation(waypoint);
		if (location != null)
			waypointInfo += LocationDescriptor.LOCATON_DELIMINTER + " "
					+ location + " ";

		if (marker)
			return WaypointHyperlink.LINK_TAG + " " + waypointInfo + WaypointHyperlink.LINK_TAG;
		else
			return waypointInfo;
	}

	public static String getWaypointInformation(List<IWaypoint> waypoints) {
		StringBuffer waypointInfo = new StringBuffer();
		for (IWaypoint waypoint : waypoints) {
			waypointInfo.append(getWaypointInformation(waypoint, true) + " "
					+ getTags(waypoint) + "\n");
		}
		return waypointInfo.toString();
	}

	public static String getTags(IWaypoint waypoint) {
		// Add tag information
		ITag[] tags = waypoint.getTags();
		
		String tagInfo = "";
		for (ITag tag : tags) {
			tagInfo += TAG_MARKER + " " + tag + " ";
		}
		return tagInfo.trim();
	}

	public static List<IWaypoint> getWaypoints(
			StructuredSelection structuredSelection) {
		IStructuredSelection ss = (IStructuredSelection) ((IStructuredSelection) structuredSelection);
		Object o = ss.getFirstElement();
		if (o instanceof TagTreeItem) {

			TagTreeItem[] items = new TagTreeItem[ss.size()];
			int index = 0;
			for (Object selection : ss.toList()) {
				items[index] = (TagTreeItem) selection;
				index++;
			}

			return WaypointsUtils.convertTagsToWaypoints(items);
		}

		if (o instanceof IWaypoint) {
			IWaypoint waypoint = (IWaypoint) o;
			List<IWaypoint> waypointList = new ArrayList<IWaypoint>();
			waypointList.add(waypoint);
			return waypointList;

		}
		return Collections.EMPTY_LIST;
	}

	public static void recordTask(List<IWaypoint> waypoints, AbstractTask task) {
		WaypointMylynPlugin plugin = WaypointMylynPlugin.getDefault();
		for (IWaypoint waypoint : waypoints) {
			plugin.addWaypoint(waypoint, task);
		}
	}

	public static String getHandle(IWaypoint waypoint) {
		// @tag tagsea.mylyn.redesign -author=John -date="enCA:20/03/08" :
		// Ideally this should all be delegated to the specific waypoint plugin
		// @tag tagsea.mylyn.clone -author=John -date="enCA:20/03/08" :
		// Synchronize changes with TaskUtils.getHandle(IMarker)
		if (waypoint.getType().equals(ParsedWaypointPlugin.WAYPOINT_TYPE)) {
			String domain = waypoint.getStringValue(
					IParsedWaypointAttributes.ATTR_DOMAIN, "");
			return domain;
		}
		if (waypoint.getType().equals(TaskWaypointPlugin.WAYPOINT_ID)) {
			try {
				IMarker marker = TaskWaypointUtils.getTaskForWaypoint(waypoint);
				IResource resource = marker.getResource();
				IJavaElement element = JavaCore.create(resource);
				if (element instanceof ICompilationUnit) {
					ICompilationUnit unit = (ICompilationUnit) element;
					int charStart = Integer.parseInt(marker.getAttribute(
							IMarker.CHAR_START).toString());
					IJavaElement unitElement = unit.getElementAt(charStart);
					if (unitElement != null) {
						// unitElement is a method or field
						return unitElement.getHandleIdentifier();
					} else { // use the enclosing class
						return unit.getHandleIdentifier();
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JavaModelException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static List<String> getHandles(List<IWaypoint> waypoints) {
		List<String> handles = new ArrayList<String>();
		for (IWaypoint waypoint : waypoints) {
			handles.add(getHandle(waypoint));
		}
		return handles;
	}

	public static String addTaskTag(AbstractTask task, List<IWaypoint> waypoints) {
		String tag = task.getConnectorKind() + "." + REPORT_TAG
				+ task.getTaskId();
		for (IWaypoint waypoint : waypoints) {
			waypoint.addTag(tag);
		}
		return tag;
	}

	public static List<IWaypoint> getNewWaypoints(AbstractTask task,
			List<IWaypoint> waypoints) {
		List<IWaypoint> newWaypoints = new ArrayList<IWaypoint>(waypoints);
		Set<IWaypoint> existingWaypoints = WaypointMylynPlugin.getDefault()
				.getWaypoints(task);
		if (existingWaypoints == null) {
			return newWaypoints;
		}

		for (IWaypoint waypoint : waypoints) {
			if (existingWaypoints.contains(waypoint)) {
				newWaypoints.remove(waypoint);
			}
		}
		return newWaypoints;
	}

	public static TagSEAView getTagSEAView() {
		IViewReference[] views = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (IViewReference viewReference : views) {
			if (viewReference.getId().equals(TagSEAView.ID))
				return (TagSEAView) viewReference.getView(false);
		}

		return null;
	}
}
