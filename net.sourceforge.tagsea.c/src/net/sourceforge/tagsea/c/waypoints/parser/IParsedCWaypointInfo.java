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
package net.sourceforge.tagsea.c.waypoints.parser;

import org.eclipse.jface.text.IRegion;

/**
 * @author Del Myers
 */

public interface IParsedCWaypointInfo extends ICWaypointInfo {

	/**
	 * @return the waypointData
	 */
	String getWaypointData();

	/**
	 * Gets the text regions that the given tag exists at.
	 * @param tag
	 * @return
	 */
	IRegion[] getRegionsForTag(String tag);

	/**
	 * Returns the region in the text for the attribute of the given key.
	 * @param key the key of the attribute.
	 * @return the region in the text for the attribute of the given key.
	 */
	IRegion getRegionForAttribute(String key);
	
	/**
	 * Returns the region for the description (comment) portion of the waypoint.
	 * @return the region for the description (comment) portion of the waypoint.
	 */
	IRegion getRegionForDescription();

	/**
	 * Returns the problems that occurred while parsing this info. If this list is not empty,
	 * it should not be assumed that any waypoint information could be correctly obtained from
	 * the parser.
	 * @return
	 */
	WaypointParseProblem[] getProblems();
	
	/**
	 * Returns the offset into the text data that this waypoint begins at.
	 * @return the offset into the text data that this waypoint begins at.
	 */
	int getOffset();

}