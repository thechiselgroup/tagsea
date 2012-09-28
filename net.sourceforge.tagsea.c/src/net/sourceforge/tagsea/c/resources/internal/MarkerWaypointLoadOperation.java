/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.c.resources.internal;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.tagsea.c.CWaypointsPlugin;
import net.sourceforge.tagsea.c.ICWaypointsConstants;
import net.sourceforge.tagsea.c.waypoints.parser.MarkerCWaypointInfo;
import net.sourceforge.tagsea.core.TagSEAOperation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

/**
 * @author Del Myers
 */

public class MarkerWaypointLoadOperation extends TagSEAOperation {
	
	public MarkerWaypointLoadOperation() {
		super("Refreshing C/C++ Waypoints from Markers...");
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ITagSEAOperation#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
		IMarker[] markers  = new IMarker[0];
		MultiStatus status = new MultiStatus(CWaypointsPlugin.PLUGIN_ID, 0, "", null);
		try {
			markers = ResourcesPlugin.getWorkspace().getRoot().findMarkers(ICWaypointsConstants.WAYPOINT_MARKER, false, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			status.add(e.getStatus());
		}
		for (IMarker marker : markers) {
			MarkerCWaypointInfo info = new MarkerCWaypointInfo(marker);
//			have to delete the marker in order to not get duplicates.
			try {
				marker.delete();
			} catch (CoreException e) {
				status.merge(e.getStatus());
			}
			WaypointResourceUtil.createWaypointForInfo(info, ((IFile)marker.getResource()));
			
		}
		return status;
	}

}