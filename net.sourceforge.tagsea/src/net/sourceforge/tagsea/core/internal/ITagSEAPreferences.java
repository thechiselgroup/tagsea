/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core.internal;

/**
 * Preference values for the TagSEA plugin.
 * @author Del Myers
 */

public interface ITagSEAPreferences {
	/**
	 * Preference constant for linking the filtering to the tags view.
	 */
	public static final String WAYPOINT_VIEW_LINK_TO_TAGS_VIEW = "linktotagsview";
	/**
	 * Preference constant for making the tags view list tags in a tree.
	 */
	public static final String TAGS_VIEW_TREE = "tagsastree";
	/**
	 * Preference constant for keeping tags in a hierarchy even when the tree view is flat.
	 */
	public static final String TAGS_VIEW_TREE_NAMING = "tagskeephierarchy";
	
	/**
	 * Preference constant for a comma-separated list of waypoint types that will be filtered
	 * from the waypoints view.
	 */
	public static final String FILTERED_WAYPOINT_TYPES = "filteredWaypoints";
	/**
	 * Preference constant for filtering tags according to whether or not their waypoints have been filtered.
	 */
	public static final String FILTER_TAGS_TO_WAYPOINTS = "filterToWaypoints";
	
}
