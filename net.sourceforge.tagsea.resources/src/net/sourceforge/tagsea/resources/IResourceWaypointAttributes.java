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
package net.sourceforge.tagsea.resources;

import net.sourceforge.tagsea.core.ITextWaypointAttributes;

/**
 * Lists the attributes found on waypoints that extend the resource waypoint.
 * @author Del Myers
 *
 */
public interface IResourceWaypointAttributes extends
		IResourceInterfaceAttributes, ITextWaypointAttributes {
	public static final String ATTR_HANDLE_IDENTIFIER = "handleIdentifier";
	
	/**
	 * An identification "stamp" that is placed on a waypoint at creation time. It is used
	 * later to identify the waypoint in a unique way for synchronization and saving.
	 * Stamps on resource waypoints cannot be changed or initialized outside of the resource
	 * waypoint platform. An attempt to create a resource waypoint with a stamp that is
	 * not the default value will fail. Any attempt to set this value outside of the platform
	 * will also fail. This behaviour, however, cannot be expected for waypoint types that
	 * are children of resource waypoints. Check the documentation for those waypoints for
	 * information.
	 * 
	 */
	public static final String ATTR_STAMP = "stamp";
	
	/**
	 * The revision of the last change on a waypoint, if it can be found. For internal use only.
	 */
	public static final String ATTR_REVISION = "revisionStamp";
}
