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
package net.sourceforge.tagsea.resources.synchronize;

import org.eclipse.core.resources.IProject;

/**
 * Indicates when the synchronization of resource waypoints in a particular project has changed.
 * @author Del Myers
 *
 */
public interface IWaypointSynchronizeListener {
	/**
	 * The synchronization state for the given project has changed.
	 * @param project
	 */
	void synchronizationChanged(IProject project);

}
