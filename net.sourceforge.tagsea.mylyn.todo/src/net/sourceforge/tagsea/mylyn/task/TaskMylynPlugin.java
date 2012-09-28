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
package net.sourceforge.tagsea.mylyn.task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TaskMylynPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "Tasks2Mylyn";

	// The shared instance
	private static TaskMylynPlugin plugin;

	
	private final Map<IMarker, Set<AbstractTask>> markerMap;
	private final Map<AbstractTask, Set<IMarker>> taskMap;
	
	/**
	 * The constructor
	 */
	public TaskMylynPlugin() {
		this.markerMap = new HashMap<IMarker, Set<AbstractTask>>();
		this.taskMap = new HashMap<AbstractTask, Set<IMarker>>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		Job buildMaps = new BuildTaskMaps();
		buildMaps.schedule();
	}

	/*
	 * (non-Javadoc)
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
	public static TaskMylynPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public void addMarker(IMarker marker, AbstractTask task) {
		System.out.println("Adding " + TaskUtils.getDescription(marker));
		Set<AbstractTask> taskSet = this.markerMap.get(marker);
		if (taskSet == null) {
			taskSet = new HashSet<AbstractTask>();
			this.markerMap.put(marker, taskSet);
		}
		taskSet.add(task);

		Set<IMarker> markerSet = this.taskMap.get(task);
		if (markerSet == null) {
			markerSet = new HashSet<IMarker>();
			this.taskMap.put(task, markerSet);
		}
		markerSet.add(marker);
	}

	public Set<IMarker> getMarkers(AbstractTask task) {
		return this.taskMap.get(task);
	}

	public Set<AbstractTask> getTasks(IMarker todo){
		return this.markerMap.get(todo);
	}
	
	public Set<IMarker> getAllMarkers(){
		return this.markerMap.keySet();
	}
}
