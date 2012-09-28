/*******************************************************************************
 * 
 *   Copyright 2007, 2008, CHISEL Group, University of Victoria, Victoria, BC, Canada. 
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *       The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.mylyn.waypoints.actions;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.mylyn.core.actions.MylynTaskGoto;
import net.sourceforge.tagsea.mylyn.waypoints.WaypointMylynPlugin;
import net.sourceforge.tagsea.mylyn.waypoints.WaypointsUtils;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.tasks.core.AbstractTask;

public class WaypointMylynTask extends MylynTaskGoto {

	@Override
	protected Set<AbstractTask> getTasks(StructuredSelection selection) {
		List<IWaypoint> waypoints = WaypointsUtils
		.getWaypoints(selection);
		Set<AbstractTask> tasks = new HashSet<AbstractTask>();
		for (IWaypoint waypoint : waypoints) {
			Set<AbstractTask> t = WaypointMylynPlugin.getDefault().getTasks(
					waypoint);
			if (t != null) {
				tasks.addAll(t);
			}
		}
		return tasks;
	}

}
