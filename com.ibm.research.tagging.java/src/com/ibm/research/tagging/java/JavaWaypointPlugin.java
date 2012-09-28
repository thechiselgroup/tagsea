/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tagging.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import com.ibm.research.tagging.core.TagCorePlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class JavaWaypointPlugin extends AbstractUIPlugin {

	public static final String IMG_WAYPOINT 	 = "IMG_WAYPOINT";
	private static final String PATH_WAYPOINT	 = "icons/waypoint.gif";
	
	// The plug-in ID
	public static final String PLUGIN_ID = "com.ibm.research.tagging.java";

	// The shared instance
	private static JavaWaypointPlugin plugin;

	private Map<IFile,List<JavaWaypoint>> fWaypointRegistry;
	
	/**
	 * The constructor
	 */
	public JavaWaypointPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
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
	public static JavaWaypointPlugin getDefault() {
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

	@Override
	protected ImageRegistry createImageRegistry() 
	{
		ImageRegistry registry = super.createImageRegistry();
		loadImageDescriptorIntoRegistry(registry, IMG_WAYPOINT, PATH_WAYPOINT);
		return registry;
	}
	
	/**
	 * Loads an image descriptor into the registry if it doesn't already exist.
	 * @param reg the image registry
	 * @param key the key for the image in the registry
	 * @param path the path to the image (relative to the plugin directory (e.g. /icons/icon.gif) 
	 */
	private void loadImageDescriptorIntoRegistry(ImageRegistry reg, String key, String path) {
		ImageDescriptor descriptor = reg.getDescriptor(key);
		if (descriptor == null) {
			descriptor = getImageDescriptor(path);
			if(descriptor != null) {
				reg.put(key, descriptor);
			}
		}
	}
	
	/**
	 * Get the waypoint registry
	 * @return the registry map
	 */
	private Map<IFile,List<JavaWaypoint>> getWaypointRegistry() 
	{
		if(fWaypointRegistry == null)
			fWaypointRegistry = new HashMap<IFile, List<JavaWaypoint>>();
		return fWaypointRegistry;
	}

	public List<JavaWaypoint> get(IFile file) 
	{
		return getWaypointRegistry().get(file);
	}
	
	public void addWaypoint(JavaWaypoint waypoint) 
	{
		synchronized (getWaypointRegistry())
		{
			IFile file = (IFile)waypoint.getMarker().getResource();
			List<JavaWaypoint> waypoints = getWaypointRegistry().get(file);
		
			if(waypoints == null)
			{
				waypoints = new ArrayList<JavaWaypoint>();
				getWaypointRegistry().put(file,waypoints);
			}
		
			waypoints.add(waypoint);
		
			TagCorePlugin.getDefault().getTagCore().getWaypointModel().addWaypoint(waypoint);
		}
	}
	
	public void removeWaypoint(JavaWaypoint waypoint) 
	{
		synchronized (getWaypointRegistry())
		{
			IFile file = (IFile)waypoint.getMarker().getResource();
		
			List<JavaWaypoint> waypoints = getWaypointRegistry().get(file);
			waypoints.remove(waypoint);
			
			if(waypoints.size() == 0)
				getWaypointRegistry().remove(file);
		
			TagCorePlugin.getDefault().getTagCore().getWaypointModel().removeWaypoint(waypoint);
		}
	}

	public void clear(IFile file) 
	{
		synchronized (getWaypointRegistry())
		{
		
			List<JavaWaypoint> waypoints = getWaypointRegistry().get(file);
		
			if(waypoints != null && waypoints.size() > 0)
			{
				for(JavaWaypoint waypoint : waypoints)
				{
					TagCorePlugin.getDefault().getTagCore().getWaypointModel().removeWaypoint(waypoint);
				}
			}
		
			getWaypointRegistry().remove(file);
		}
	}
	
	/**
	 * Logs an information message.
	 * @param msg the message to log to the eclipse error log
	 */
	public static void log(String msg) {
		log(IStatus.INFO, msg, null);
	}

	/**
	 * Logs a message with the given severity.
	 * @see IStatus
	 * @param severity the severity of the log message
	 * @param msg the message to log
	 */
	public static void log(int severity, String msg) {
		log(severity, msg, null);
	}
	
	/**
	 * Logs a message and an exception to the workspace error log.
	 * @param msg the message to log
	 * @param ex the exception to log (can be null)
	 */
	public static void log(String msg, Throwable ex) {
		log(IStatus.ERROR, msg, ex);
	}
	
		
	/**
	 * Logs a message to the eclipse error log.  
	 * @param severity  the importance - from INFO to ERROR
	 * @param msg the message to log
	 * @param ex the optional exception (can be null)
	 * 
	 */
	@SuppressWarnings("restriction")
	private static void log(int severity, String msg, Throwable ex) {
		if (msg == null)
			return;
		if (getDefault() == null) {
			// write the error out to standard error
			String errMsg = "LOG [severity=" + severity + "]: " + msg;
			System.err.println(errMsg);
			if (ex != null) {
				ex.printStackTrace(System.err);
			}
		} else {
			getDefault().getLog().log(new Status(severity, PLUGIN_ID, severity, msg, ex));
		}
	}	
}
