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

package com.ibm.research.tagging.resource;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ResourceWaypointPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.ibm.research.tagging.resource";
	
	// The shared instance
	private static ResourceWaypointPlugin plugin;
	
	/**
	 * The constructor
	 */
	public ResourceWaypointPlugin() {
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
	public static ResourceWaypointPlugin getDefault() {
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
