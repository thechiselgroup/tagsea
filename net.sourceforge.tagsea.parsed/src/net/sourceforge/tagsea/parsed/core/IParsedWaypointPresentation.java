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
package net.sourceforge.tagsea.parsed.core;

import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.swt.graphics.Image;

/**
 * Presentation interface for parsed waypoints. Defines how labels and values should be shown in
 * the TagSEA ui. Also defines how a waypoint can be changed.
 * @author Del Myers
 *
 */
public interface IParsedWaypointPresentation {
	/**
	 * Get the image associated with this waypoint
	 * @param waypoint
	 * @return Image
	 */
	public Image getImage(IWaypoint waypoint);
	
	/**
	 * Get the label associated with this waypoint
	 * @param waypoint
	 * @return String
	 */
	public String getLabel(IWaypoint waypoint);

	
	/**
	 * @param waypoint
	 * @param domainObjectString
	 * @return
	 */
	public Image getDomainImage(IWaypoint waypoint, String domainObjectString);

	/**
	 * @param waypoint
	 * @param domainObjectString
	 * @return
	 */
	public String getDomainLabel(IWaypoint waypoint, String domainObjectString);

	/**
	 * Returns the human-readable label for the "type" of domain object that is represented in the
	 * given waypoint. For example, if the parsed waypoint is parsed on a 
	 * @param waypoint
	 * @return
	 */
	public String getDomainObjectName(IWaypoint waypoint);
	
	/**
	 * Returns the human-readable label for the "location" of the waypoint. This may be a file
	 * and a character location, or some sort of source-code element (related to the "domain
	 * object"). If the parsed waypoint definition doesn't supply a location string (this
	 * method returns null, or the parsed waypoint definition doesn't supply an IParsedWaypointPresentation),
	 * the platform will attempt to construct a suitable string.
	 * @param waypoint
	 * @return
	 */
	public String getLocationString(IWaypoint waypoint);

	/**
	 * Returns a small icon that represents this waypoint kind.
	 * @return a small icon that represents this waypoint kind.
	 */
	public Image getImage();
	
	
}
