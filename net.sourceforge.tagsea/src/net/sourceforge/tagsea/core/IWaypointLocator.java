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

import java.util.Map;
import java.util.SortedSet;

/**
 * Implements a strategy to locate a waypoint based on a set of attribute values.
 * @author Del Myers
 *
 */
public interface IWaypointLocator {

	/**
	 * Returns a list of best-guesses to the waypoints that match the given attribute map, ordered by 
	 * confidence.
	 * @param attributes the attributes to match
	 * @param tagNames the tag names on the waypoint.
	 * @return the waypoint matches.
	 */
	public WaypointMatch[] findMatches(Map<String, Object> attributes, SortedSet<String> tagNames);
	
}
