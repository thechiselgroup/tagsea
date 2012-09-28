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
package net.sourceforge.tagsea.resources.waypoints;

import java.util.Map;
import java.util.SortedSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointLocator;
import net.sourceforge.tagsea.core.WaypointMatch;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;

/**
 * Matches only waypoints that have the same stamp.
 * @author Del Myers
 *
 */
public class ResourceWaypointLocator implements IWaypointLocator {

	public WaypointMatch[] findMatches(Map<String, Object> attributes,
			SortedSet<String> tagNames) {
		Object s = attributes.get(IResourceWaypointAttributes.ATTR_STAMP);
		if (!(s instanceof String)) {
			return new WaypointMatch[0];
		}
		String stamp = s.toString();
		IWaypoint[] currentWaypoints = TagSEAPlugin.getWaypointsModel().getWaypoints(ResourceWaypointPlugin.WAYPOINT_ID);
		for (IWaypoint wp : currentWaypoints) {
			s = wp.getValue(IResourceWaypointAttributes.ATTR_STAMP);
			if (stamp.equals(s)) {
				return new WaypointMatch[] {new WaypointMatch(attributes, tagNames, wp, 1)};
			}
		}
		return new WaypointMatch[0];
	}

}
