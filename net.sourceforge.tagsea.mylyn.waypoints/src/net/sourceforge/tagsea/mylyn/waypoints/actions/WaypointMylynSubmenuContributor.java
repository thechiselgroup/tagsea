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
import java.util.Set;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.mylyn.waypoints.WaypointsUtils;
import net.sourceforge.tagsea.mylyn.waypoints.WaypointMylynPlugin;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

public class WaypointMylynSubmenuContributor implements
		IDynamicSubMenuContributor {

	private static final String WAYPOINTS_LABEL = "Waypoints";

	// @tag tagsea.mylyn -author="John" -date="enCA:31/10/07" : borrowed from
	// org.eclipse.mylyn.internal.tasks.ui.ScheduleTaskMenuContributor
	public MenuManager getSubMenuManager(
			final List<AbstractTaskContainer> selectedElements) {
		
		final TaskListManager tasklistManager = TasksUiPlugin
				.getTaskListManager();

		final MenuManager subMenuManager = new MenuManager(WAYPOINTS_LABEL);
		System.err.println("Selected size: " + selectedElements.size());

		subMenuManager.setVisible(selectedElements.size() > 0);
		//System.err.println("Visibility: " + subMenuManager.isVisible());
		//System.err.println("Selected Type: " + selectedElements.get(0));

		for (AbstractTaskContainer selection : selectedElements) {
			if (selection instanceof AbstractTask) {
				final AbstractTask task = tasklistManager.getTaskForElement(
						selection, false);
				Set<IWaypoint> waypoints = WaypointMylynPlugin.getDefault()
						.getWaypoints(task);
				if (waypoints != null) {
					for (IWaypoint waypoint : waypoints) {
						Action action = new JumpToWaypoint(waypoint);
						action.setText(WaypointsUtils
								.getWaypointInformation(waypoint, false));
						action.setEnabled(true);
						subMenuManager.add(action);
					}
				} else {
					System.err.println("Couldn't find waypoints for task.");
				}
			}
		}
		return subMenuManager;
	}

	private class JumpToWaypoint extends Action {

		private final IWaypoint waypoint;

		JumpToWaypoint(IWaypoint waypoint) {
			this.waypoint = waypoint;
		}

		@Override
		public void run() {
			if (waypoint != null) {
				TagSEAPlugin.getDefault().navigate(waypoint);
			}
		}
	}

}
