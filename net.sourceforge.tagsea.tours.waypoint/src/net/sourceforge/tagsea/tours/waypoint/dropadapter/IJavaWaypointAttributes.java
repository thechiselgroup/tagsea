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

import net.sourceforge.tagsea.core.ITextWaypointAttributes;

/**
 * Attributes for java waypoints.
 * @author Del Myers
 *
 */
public interface IJavaWaypointAttributes extends ITextWaypointAttributes,
		IResourceInterfaceAttributes {

	/**
	 * The java element.
	 */
	public static final String ATTR_JAVA_ELEMENT = "javaElement";
}
