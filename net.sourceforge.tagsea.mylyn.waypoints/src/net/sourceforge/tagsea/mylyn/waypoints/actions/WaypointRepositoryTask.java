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

import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.tasks.core.AbstractTask;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.mylyn.core.actions.MylynRepositoryTask;
import net.sourceforge.tagsea.mylyn.waypoints.WaypointsUtils;

public class WaypointRepositoryTask extends MylynRepositoryTask {

	private List<IWaypoint> waypoints;
	
	@Override
	protected String getTaskInfo() {
		return WaypointsUtils.getWaypointInformation(this.waypoints);
	}

	@Override
	protected void gatherData(StructuredSelection selection) {
		this.waypoints = WaypointsUtils.getWaypoints(selection);
	}

	@Override
	protected void recordTask(AbstractTask task) {
		WaypointsUtils.recordTask(this.waypoints, task);
	}
	
	@Override
	protected List<String> getHandles(){
		return WaypointsUtils.getHandles(this.waypoints);
	}

	@Override
	protected void postOps(AbstractTask task) {
		WaypointsUtils.addTaskTag(task, waypoints);
	}

}
