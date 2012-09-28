/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tours.fx;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ibm.research.tours.fx.preferences.FXPreferencesPage;
import com.ibm.research.tours.fx.preferences.IToursPreferences;

/**
 * The activator class controls the plug-in life cycle
 */
public class ToursFxPlugin extends AbstractUIPlugin implements IToursPreferences{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.ibm.research.tours.fx";

	public static final String IMG_EFFECT    = "IMG_EFFECT";
	private static final String PATH_EFFECT   = "icons/effect.gif";
	public static final String IMG_ECLIPSE    = "IMG_ECLIPSE";
	private static final String PATH_ECLIPSE   = "icons/sample.gif";
	public static final String IMG_TEXT    = "IMG_TEXT";
	private static final String PATH_TEXT   = "icons/text.gif";
	public static final String IMG_WIZ_TEXT    = "IMG_WIZ_TEXT";
	private static final String PATH_WIZ_TEXT   = "icons/wiz_text.gif";

	public static final String IMG_PERSPECTIVE    = "IMG_PERSPECTIVE";
	private static final String PATH_PERSPECTIVE   = "icons/perspective.gif";
	
	public static final String IMG_VIEW   = "IMG_VIEW";
	private static final String PATH_VIEW   = "icons/view.gif";	
	
	public static final String IMG_RESET   = "IMG_RESET";
	private static final String PATH_RESET   = "icons/reset.gif";	
	
	public static final String IMG_FONT   = "IMG_FONT";
	private static final String PATH_FONT   = "icons/font.gif";	
	
	public static final String IMG_CONSOLE   = "IMG_CONSOLE";
	private static final String PATH_CONSOLE   = "icons/console.gif";	
	
	public static final String IMG_JAVA_EDITOR   = "IMG_JAVA_EDITOR";
	private static final String PATH_JAVA_EDITOR    = "icons/java_editor.gif";	
	
	public static final String IMG_PROPERTIES   = "IMG_PROPERTIES";
	private static final String PATH_PROPERTIES    = "icons/properties.gif";
	
	public static final String IMG_BIG_ICON = "IMG_BIG_ICON";
	private static final String PATH_BIG_ICON = " icons/bigicon.gif";
	
	public static final String IMG_BIG_CURSOR = "IMG_BIG_CURSOR";
	private static final String PATH_BIG_CURSOR = "icons/bigcursor.ico";
	
	// The shared instance
	private static ToursFxPlugin plugin;
	private ColorRegistry fColorRegistry;
	
	/**
	 * The constructor
	 */
	public ToursFxPlugin() 
	{
		plugin = this;
		fColorRegistry= new ColorRegistry();
	}
	
	public ColorRegistry getColorRegistry()
	{
		if(fColorRegistry == null)
			fColorRegistry= new ColorRegistry();
		
		return fColorRegistry;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		getPreferenceStore().setDefault(FX_DELAY, DEFAULT_FX_DELAY);
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
	public static ToursFxPlugin getDefault() {
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
		loadImageDescriptorIntoRegistry(registry, IMG_EFFECT, PATH_EFFECT);
		loadImageDescriptorIntoRegistry(registry, IMG_ECLIPSE, PATH_ECLIPSE);
		loadImageDescriptorIntoRegistry(registry, IMG_TEXT, PATH_TEXT);
		loadImageDescriptorIntoRegistry(registry, IMG_WIZ_TEXT, PATH_WIZ_TEXT);
		loadImageDescriptorIntoRegistry(registry, IMG_PERSPECTIVE, PATH_PERSPECTIVE);
		loadImageDescriptorIntoRegistry(registry, IMG_VIEW, PATH_VIEW);
		loadImageDescriptorIntoRegistry(registry, IMG_RESET, PATH_RESET);
		loadImageDescriptorIntoRegistry(registry, IMG_FONT, PATH_FONT);
		loadImageDescriptorIntoRegistry(registry, IMG_CONSOLE, PATH_CONSOLE);
		loadImageDescriptorIntoRegistry(registry, IMG_JAVA_EDITOR, PATH_JAVA_EDITOR);
		loadImageDescriptorIntoRegistry(registry, IMG_PROPERTIES, PATH_PROPERTIES);
		loadImageDescriptorIntoRegistry(registry, IMG_BIG_ICON, PATH_BIG_ICON);
		loadImageDescriptorIntoRegistry(registry, IMG_BIG_CURSOR, PATH_BIG_CURSOR);
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
