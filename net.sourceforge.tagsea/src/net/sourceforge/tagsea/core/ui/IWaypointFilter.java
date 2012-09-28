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
package net.sourceforge.tagsea.core.ui;

import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.jface.viewers.ISelection;

/**
 * Filters waypoints in the standard Waypoints View and and other UI components.
 * There can only be one IWaypointFilter per waypoint type, and it is always associated
 * with an IWaypointFilterUI which is used to apply waypoint-specific settings to the
 * waypoint filter. Waypoint filters must be contributed in the same plugin that defines
 * the waypoint type.
 * 
 * Contributed using the <code>net.sourceforge.tagsea.filters</code> extension.
 * @author Del Myers
 */
public interface IWaypointFilter {
	
	/**
	 * Called exactly once when the waypoint filter is first loaded in the platform. Used
	 * to initialize the filter according to user preferences, or what ever data is needed
	 * in order to do the filtering.
	 *
	 */
	void initialize();
	
	/**
	 * Returns whether the given waypoint makes it through the filter. This is independant of the viewer.
	 * @param waypoint the waypoint to filter.
	 * @return true if the waypoint makes it through the filter.
	 */
	boolean select(IWaypoint waypoint);

	
	/**
	 * Some filters may need to refresh views based on selections in the workbench. This method
	 * is used to indicate whether or not the given selection should cause a refresh in the
	 * viewer that uses this filter.
	 * @param selection the selection in the workbench.
	 * @return true if the selection should cause an update.
	 */
	boolean isImportantSelection(ISelection selection);
	
	
}
