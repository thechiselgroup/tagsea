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
package net.sourceforge.tagsea.tours.waypoint.dropadapter;

import net.sourceforge.tagsea.core.IWaypoint;

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
		String resourceString = waypoint.getStringValue(IResourceWaypointAttributes.ATTR_RESOURCE, null);
		if (resourceString == null) return null;
		return new Path(resourceString);
	}
	
	public static IResource getResource(IWaypoint waypoint) {
		IPath path = getResourcePath(waypoint);
		if (path == null) return null;
		return ResourcesPlugin.getWorkspace().getRoot().findMember(path);
	}
	
	public static int getLine(IWaypoint waypoint) {
		if (!isText(waypoint)) return -2;
		return waypoint.getIntValue(IResourceWaypointAttributes.ATTR_LINE, -2);
	}
	
	private static boolean isText(IWaypoint waypoint) {
		return waypoint.isSubtypeOf(IWaypoint.TEXT_WAYPOINT);
	}
	
}
