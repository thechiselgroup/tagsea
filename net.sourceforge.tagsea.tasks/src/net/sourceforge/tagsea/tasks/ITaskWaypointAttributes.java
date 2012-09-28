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
package net.sourceforge.tagsea.tasks;

import net.sourceforge.tagsea.core.ITextWaypointAttributes;
import net.sourceforge.tagsea.resources.IResourceInterfaceAttributes;

/**
 * Lists attributes for the java task waypoint type.
 * @author Del Myers
 *
 */
public interface ITaskWaypointAttributes extends IResourceInterfaceAttributes,
		ITextWaypointAttributes {
	
	/**
	 * The marker id of the task that this waypoint references.
	 */
	public static final String ATTR_MARKER_ID = "markerId";
}
