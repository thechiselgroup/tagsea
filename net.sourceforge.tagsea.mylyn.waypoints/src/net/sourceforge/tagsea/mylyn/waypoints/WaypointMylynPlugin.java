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
package net.sourceforge.tagsea.mylyn.waypoints;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class WaypointMylynPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sourceforge.tagsea.mylyn";

	// The shared instance
	private static WaypointMylynPlugin plugin;

	private final Map<IWaypoint, Set<AbstractTask>> waypointMap;
	private final Map<AbstractTask, Set<IWaypoint>> taskMap;

	/**
	 * The constructor
	 */
	public WaypointMylynPlugin() {
		this.waypointMap = new HashMap<IWaypoint, Set<AbstractTask>>();
		this.taskMap = new HashMap<AbstractTask, Set<IWaypoint>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	
		Job buildMaps = new BuildWaypointMaps();
		buildMaps.schedule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static WaypointMylynPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public void addWaypoint(IWaypoint waypoint, AbstractTask task) {
		System.out.println("Adding: " + waypoint.getText());
		Set<AbstractTask> taskSet = this.waypointMap.get(waypoint);
		if (taskSet == null) {
			taskSet = new HashSet<AbstractTask>();
			this.waypointMap.put(waypoint, taskSet);
		}
		taskSet.add(task);

		Set<IWaypoint> waypointSet = this.taskMap.get(task);
		if (waypointSet == null) {
			waypointSet = new HashSet<IWaypoint>();
			this.taskMap.put(task, waypointSet);
		}
		waypointSet.add(waypoint);
	}

	public Set<IWaypoint> getWaypoints(AbstractTask task) {
		return this.taskMap.get(task);
	}

	public Set<AbstractTask> getTasks(IWaypoint waypoint){
		return this.waypointMap.get(waypoint);
	}

	public Set<IWaypoint> getAllWaypoints() {
		return this.waypointMap.keySet();
		
	}
}
