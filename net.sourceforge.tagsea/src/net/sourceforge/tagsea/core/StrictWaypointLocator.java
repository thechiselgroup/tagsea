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
package net.sourceforge.tagsea.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;



/**
 * A locator that searches through waypoints and only returns those that have exact matches to
 * the map of attributes.
 * @author Del Myers
 *
 */


public class StrictWaypointLocator implements IWaypointLocator {
	
	/**
	 * The type for the waypoint to search.
	 */
	private final String type;
	
	/**
	 * Creates a locator for the given waypoint type.
	 * @param type the type to search through.
	 */
	public StrictWaypointLocator(String type) {
		this.type = type;
	}

	public WaypointMatch[] findMatches(Map<String, Object> attributes, SortedSet<String> tagNames) {
		IWaypoint[] waypoints = TagSEAPlugin.getWaypointsModel().getWaypoints(getType());
		ArrayList<WaypointMatch> matchings = new ArrayList<WaypointMatch>();
		AbstractWaypointDelegate delegate = TagSEAPlugin.getDefault().getWaypointDelegate(getType());
		if (delegate == null) {
			return matchings.toArray(new WaypointMatch[0]);
		}
		float certainty = ((float)(attributes.keySet().size()))/delegate.getDeclaredAttributes().length;
		if (certainty > 1.0) {
			certainty = 1/certainty;
		}
		for (IWaypoint waypoint : waypoints) {
			boolean match = false;
			TreeSet<String> waypointTags = new TreeSet<String>();
			for (ITag tag : waypoint.getTags()) {
				waypointTags.add(tag.getName());
			}
			if (waypointTags.equals(tagNames)) {
				match = true;
				for (String key : attributes.keySet()) {
					if (!attributes.get(key).equals(waypoint.getValue(key))) {
						match = false;
						break;
					}
				}
			}
			if (match) {
				matchings.add(new WaypointMatch(attributes, tagNames, waypoint, certainty));
			}
		}
		Collections.sort(matchings);
		return matchings.toArray(new WaypointMatch[matchings.size()]);
	}

	/**
	 * @return the type that this locator searches through.
	 */
	public final String getType() {
		return type;
	}

}
