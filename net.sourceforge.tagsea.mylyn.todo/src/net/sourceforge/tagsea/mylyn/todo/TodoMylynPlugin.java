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
package net.sourceforge.tagsea.mylyn.todo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TodoMylynPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "Tasks2Mylyn";

	// The shared instance
	private static TodoMylynPlugin plugin;

	
	private final Map<IMarker, List<AbstractTask>> todoMap;
	private final Map<AbstractTask, List<IMarker>> taskMap;
	
	/**
	 * The constructor
	 */
	public TodoMylynPlugin() {
		this.todoMap = new HashMap<IMarker, List<AbstractTask>>();
		this.taskMap = new HashMap<AbstractTask, List<IMarker>>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
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
	public static TodoMylynPlugin getDefault() {
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

	public void addTodo(IMarker todo, AbstractTask task) {
		List<AbstractTask> taskList = this.todoMap.get(todo);
		if (taskList == null) {
			taskList = new ArrayList<AbstractTask>();
			this.todoMap.put(todo, taskList);
		}
		taskList.add(task);

		List<IMarker> todoList = this.taskMap.get(task);
		if (todoList == null) {
			todoList = new ArrayList<IMarker>();
			this.taskMap.put(task, todoList);
		}
		todoList.add(todo);
	}

	public List<IMarker> getTodos(AbstractTask task) {
		return this.taskMap.get(task);
	}

	public List<AbstractTask> getTasks(IMarker todo){
		return this.todoMap.get(todo);
	}
}
