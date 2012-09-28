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
package net.sourceforge.tagsea.core.ui;

import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author Del Myers
 */

public interface ITagSEAUI {

	/*
	 * Returns the main tags view if it is available. Null otherwise.
	 * @return the main tags view if it is available. Null otherwise.
	 *
	public abstract TagsView getTagsView();

	/**
	 * Shows the main tags view if it is available, and returns it.
	 * @return the main tags view if it is available.
	 *
	public abstract TagsView showTagsView();

	/*
	 * Returns the main waypoints view if it is available. Null otherwise.
	 * @return the main waypoints view if it is available.
	 *
	public abstract WaypointView getWaypointView();

	/*
	 * Shows the main waypoints view if it is available, and returns it.
	 * @return the main waypoints view if it is available.
	 *
	public abstract WaypointView showWaypointView();
    */
	
	
	
	/**
	 * Returns an image for the given waypoint.
	 * @param waypoint the waypoint in question.
	 * @return an image for the given waypoint.
	 * @see IWaypointUIExtension
	 */
	public abstract Image getImage(IWaypoint waypoint);

	/**
	 * Returns a descriptive label that is used in the TagSEA views.
	 * @param waypoint the waypoint in question.
	 * @return a descriptive label that is used in the TagSEA views.
	 */
	public abstract String getLabel(IWaypoint waypoint);

	/**
	 * Returns tool-tip information for the given waypoint.
	 * @param waypoint the wayoint in question.
	 * @return IWaypointUIExtension
	 */
	public abstract String getTooltipText(IWaypoint waypoint);

	/**
	 * Returns true iff TagSEA ui components are allowed to change the given attribute for the given waypoint.
	 * Used as a hint to determine what kinds of editors should be enabled in views.
	 * @param waypoint the waypoint in question.
	 * @param attribute the attribute in question.
	 * @return true iff TagSEA ui components are allowed to change the given attribute for the given waypoint.
	 * @see IWaypointUIExtension
	 */
	public abstract boolean canUIChange(IWaypoint waypoint, String attribute);

	/**
	 * Returns true iff TagSEA ui components are allowed to change the tags on the given waypoint.
	 * @param waypoint the waypoint in question. Used as a hint
	 * to determine what actions should be enabled in views.
	 * @return true iff TagSEA ui components are allowed to change the tags on the given waypoint.
	 * @see IWaypointUIExtension
	 */
	public abstract boolean canUIMove(IWaypoint waypoint);

	/**
	 * Returns true iff TagSEA ui components are allowed to delete the given waypoint. Used as a hint
	 * to determine what actions should be enabled in views.
	 * @param waypoint the waypoint in question.
	 * @return true iff TagSEA ui components are allowed to delete the given waypoint.
	 * @see IWaypointUIExtension
	 */
	public abstract boolean canUIDelete(IWaypoint waypoint);

	/**
	 * Gets the waypoint ui for the given type.
	 * @param waypointType
	 * @return
	 */
	public abstract IWaypointUIExtension getWaypointUI(String waypointType);

	/**
	 * Shows and returns the TagSEA view.
	 * @return the TagSEA view
	 */
	public abstract TagSEAView showTagSEAView();
	
	/**
	 * Returns the TagSEA view if it is currently open. Null otherwise.
	 * @return the TagSEA view.
	 */
	public abstract TagSEAView getTagSEAView();

}