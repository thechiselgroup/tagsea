/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Utilitly methods for resource waypoints.
 * @author Del Myers
 */

public class ResourceWaypointUtils {
	
	/**
	 * Returns the full path for the resource of the given waypoint.
	 * @param waypoint
	 * @return
	 */
	public static IPath getResourcePath(IWaypoint waypoint) {
		if (!isResource(waypoint)) return null;
		String resourceString = waypoint.getStringValue(IResourceWaypointAttributes.ATTR_RESOURCE, null);
		if (resourceString == null) return null;
		return new Path(resourceString);
	}
	
	/**
	 * Returns the resource that the waypoint points to, or null if it does not exist.
	 * @param waypoint
	 * @return
	 */
	public static IResource getResource(IWaypoint waypoint) {
		IPath path = getResourcePath(waypoint);
		if (path == null) return null;
		return ResourcesPlugin.getWorkspace().getRoot().findMember(path);
	}
	
	public static int getLine(IWaypoint waypoint) {
		if (!isText(waypoint)) return -2;
		return waypoint.getIntValue(IResourceWaypointAttributes.ATTR_LINE, -2);
	}
	
	/**
	 * Returnst all the resource waypoints for the given resource.
	 * @param resource the resource to check
	 * @param includeSubtypes this method should include waypoints that are subtypes of the resource waypoints.
	 * @return a list of waypoints.
	 */
	public static IWaypoint[] getWaypointsForResource(IResource resource, boolean includeSubtypes) {
		HashSet<AbstractWaypointDelegate> delegatesToCheck = new HashSet<AbstractWaypointDelegate>();
		delegatesToCheck.add(TagSEAPlugin.getDefault().getWaypointDelegate(ResourceWaypointPlugin.WAYPOINT_ID));
		List<IWaypoint> result = new LinkedList<IWaypoint>();
		if (includeSubtypes) {
			for (AbstractWaypointDelegate delegate : TagSEAPlugin.getDefault().getWaypointDelegates()) {
				if (delegate.isSubtypeOf(ResourceWaypointPlugin.INTERFACE_ID)) {
					delegatesToCheck.add(delegate);
				}
			}
		}
		for (AbstractWaypointDelegate delegate : delegatesToCheck) {
			IWaypoint[] waypoints = TagSEAPlugin.getWaypointsModel().getWaypoints(delegate.getType());
			for (IWaypoint waypoint : waypoints) {
				if (resource.getFullPath().equals(getResourcePath(waypoint))) {
					result.add(waypoint);
				}
			}
		}
		return result.toArray(new IWaypoint[result.size()]);
	}


	private static boolean isText(IWaypoint waypoint) {
		return waypoint.isSubtypeOf(IWaypoint.TEXT_WAYPOINT);
	}
	
	private static boolean isResource(IWaypoint waypoint) {
		return waypoint.isSubtypeOf(ResourceWaypointPlugin.INTERFACE_ID);
	}

	/**
	 * Returns all of the waypoints for a given project. Note that the waypoint's need not actually point to
	 * an existant resource. The resource may not exist in the project.
	 * @param project
	 * @return
	 */
	public static IWaypoint[] getWaypointsForProject(IProject project) {
		IWaypoint[] waypoints = TagSEAPlugin.getWaypointsModel().getWaypoints(ResourceWaypointPlugin.WAYPOINT_ID);
		List<IWaypoint> result = new ArrayList<IWaypoint>();
		for (IWaypoint waypoint : waypoints) {
			IPath path = getResourcePath(waypoint);
			if (project.getFullPath().isPrefixOf(path)) {
				result.add(waypoint);
			}
		}
		return result.toArray(new IWaypoint[result.size()]);
	}
	
}
