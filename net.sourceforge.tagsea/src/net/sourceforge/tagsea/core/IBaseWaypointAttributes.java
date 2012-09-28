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

/**
 * Lists the attributes for the base waypoint, which is a parent of all waypoints.
 * @author Del Myers
 *
 */
public interface IBaseWaypointAttributes {
	/**
	 * Default author attribute. Found on all waypoints.
	 */
	public static final String ATTR_AUTHOR = "author"; //$NON-NLS-1$
	
	/**
	 * Default text attribute. Found on all waypoints.
	 */
	public static final String ATTR_MESSAGE = "message"; //$NON-NLS-1$
	
	/**
	 * Default date attribute. Found on all waypoints.
	 */
	public static final String ATTR_DATE = "date"; //$NON-NLS-1$

}
