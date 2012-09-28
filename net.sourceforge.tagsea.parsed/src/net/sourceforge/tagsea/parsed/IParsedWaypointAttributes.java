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
package net.sourceforge.tagsea.parsed;

import net.sourceforge.tagsea.core.ITextWaypointAttributes;
import net.sourceforge.tagsea.resources.IResourceInterfaceAttributes;

/**
 * Attributes for parsed waypoints.
 * @author Del Myers
 *
 */
public interface IParsedWaypointAttributes extends ITextWaypointAttributes,
		IResourceInterfaceAttributes {
	
	/**
	 * Distinguishes the "sub-type" for this waypoint.
	 */
	public static final String ATTR_KIND = "waypointKind";
	
	/**
	 * Information for domain-specific data used in the waypoint.
	 */
	public static final String ATTR_DOMAIN = "domainObject";

}
