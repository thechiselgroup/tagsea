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
package net.sourceforge.tagsea.resources.waypoints.operations;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.ResourceWaypointDelegate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

/**
 * Updates the stamps for a group of waypoints.
 * @author Del Myers
 *
 */
//@tag tagsea.enhancement : stamp updater made to work on fewer waypoints in order to increase effeciency.
public class WaypointsStampUpdater extends TagSEAOperation implements
		IInternalUpdateOperation {

	private IWaypoint[]  waypoints;

	public WaypointsStampUpdater(IWaypoint[] waypoints) {
		super("Updating resource waypoint stamps...");
		this.waypoints = waypoints;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.TagSEAOperation#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus run(IProgressMonitor monitor)
			throws InvocationTargetException {
		monitor.beginTask("Updating resource waypoint stamps...", waypoints.length);
		MultiStatus s = new MultiStatus(ResourceWaypointPlugin.PLUGIN_ID, 0, "Updating resource waypoint stamps", null);
		ResourceWaypointDelegate delegate = 
			(ResourceWaypointDelegate) TagSEAPlugin.getDefault().getWaypointDelegate(ResourceWaypointPlugin.WAYPOINT_ID);
		for (IWaypoint wp : waypoints) {
			if (wp.getType().equals(delegate.getType()) || wp.isSubtypeOf(delegate.getType())) {
				String oldStamp = wp.getStringValue(IResourceWaypointAttributes.ATTR_STAMP, "");
				String newStamp = delegate.calculateStampForWaypoint(wp);
				if (!oldStamp.equals(newStamp)) {
					wp.setStringValue(IResourceWaypointAttributes.ATTR_STAMP, newStamp);
				}
			}
		}
		return s;
	}

}
