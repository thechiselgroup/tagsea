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
package net.sourceforge.tagsea.tours.waypoint.dropadapter;

import net.sourceforge.tagsea.core.IBaseWaypointAttributes;

/**
 * Lists the attributes found on waypoints that extend the resource waypoint interface.
 * @author Del Myers
 *
 */
public interface IResourceInterfaceAttributes extends IBaseWaypointAttributes {
	/**
	 * The workspace location of the resource that the waypoint is on.
	 */
	public static final String ATTR_RESOURCE = "resource";
}
