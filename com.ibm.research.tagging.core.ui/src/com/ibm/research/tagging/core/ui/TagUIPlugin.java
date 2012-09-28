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
package com.ibm.research.tagging.core.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.ui.impl.TagUI;
import com.ibm.research.tagging.core.ui.tags.TagView;
import com.ibm.research.tagging.core.ui.waypoints.IconPadder;
import com.ibm.research.tagging.core.ui.waypoints.WaypointView;

/**
 * The activator class controls the plug-in life cycle
 */
public class TagUIPlugin extends AbstractUIPlugin 
{
	// The plug-in ID
	public static final String PLUGIN_ID = "com.ibm.research.tagging.core.ui";
	public static final String UI_EXTENSION_ID = PLUGIN_ID + ".waypointUI";

	// Shared images from the workbench 
	public static final String IMG_DELETE 			   = "IMG_DELETE";
	public static final String IMG_DELETE_DISABLED     = "IMG_DELETE_DISABLED";
	public static final String IMG_BACK 			   = "IMG_BACK";
	public static final String IMG_BACK_DISABLED 	   = "IMG_BACK_DISABLED";
	public static final String IMG_FORWARD 		 	   = "IMG_FORWARD";
	public static final String IMG_FORWARD_DISABLED    = "IMG_FORWARD_DISABLED";
	// Our images
	public static final String IMG_RENAME 			   = "IMG_RENAME";
	public static final String IMG_SYNCHRONIZE		   = "IMG_SYNCHRONIZE";
	public static final String IMG_LINKWITH 		   = "IMG_LINKWITH";
	public static final String IMG_ADD 				   = "IMG_ADD";
	public static final String IMG_WAYPOINT			   = "IMG_WAYPOINT";
	public static final String IMG_TAG			       = "IMG_TAG";
	public static final String IMG_ROUTE			   = "IMG_ROUTE";
	public static final String IMG_DOWN_ARROW          = "IMG_DOWN_ARROW";
	public static final String IMG_UP_ARROW_DISABLED   = "IMG_UP_ARROW_DISABLED";
	public static final String IMG_UP_ARROW            = "IMG_UP_ARROW";
	public static final String IMG_DOWN_ARROW_DISABLED = "IMG_DOWN_ARROW_DISABLED";
	public static final String IMG_FILTER 			   = "IMG_FILTER";
	public static final String IMG_DELETE_UNUSED 	   = "IMG_DELETE_UNUSED";
	
	// the paths
	private static final String PATH_RENAME 		     = "icons/rename.gif";
	private static final String PATH_SYNCHRONIZE 	     = "icons/refresh.gif";
	private static final String PATH_LINKWITH 		     = "icons/link.gif";
	private static final String PATH_ADD 			     = "icons/add.gif";
	private static final String PATH_WAYPOINT 		     = "icons/waypoint.gif";
	private static final String PATH_TAG 		         = "icons/tag.gif";
	private static final String PATH_ROUTE 			     = "icons/route.gif";
	private static final String PATH_UP_ARROW            = "icons/up.gif";
	private static final String PATH_UP_ARROW_DISABLED   = "icons/up_disabled.gif";
	private static final String PATH_DOWN_ARROW          = "icons/down.gif";
	private static final String PATH_DOWN_ARROW_DISABLED = "icons/down_disabled.gif";
	private static final String PATH_FILTER              = "icons/filter.gif";
	private static final String PATH_DELETE_UNUSED 	     = "icons/delete_unused.gif";
	
	// The shared instance
	private static TagUIPlugin plugin;
	
	private	ITagUI fTagUI;	
	private IconPadder fIconPadder;
	
	/**
	 * The constructor
	 */
	public TagUIPlugin() 
	{
		plugin = this;
		fTagUI = new TagUI();
		
		IExtensionRegistry registry 	    = Platform.getExtensionRegistry();
		IExtensionPoint    tagUIextensionPt = registry.getExtensionPoint(UI_EXTENSION_ID);
		IExtension[]	   tagUIextensions  = tagUIextensionPt.getExtensions();
		
		if ( tagUIextensions!=null )
		{
			for (int i=0; i<tagUIextensions.length; i++)
			{
				IExtension extension = tagUIextensions[i];
				IConfigurationElement[] config = extension.getConfigurationElements();
				
				for (int j=0; j<config.length; j++)
				{
					try
					{
						IWaypointUIExtension ui = (IWaypointUIExtension) config[j].createExecutableExtension("class");
						
						// @tag debug tagsea extension-point ui : comment out when no longer need to verify extensions are loading correctly
						System.out.println("adding ui extension : name=" + config[j].getName() + " id=" + config[j].getAttribute("id"));
						
						fTagUI.addWaypointUIExtension(ui);
					}
					catch (RuntimeException e)
					{
						System.out.println("unable to instantiate tag ui extension : " + config[j].getName() + " ... skipping");
						e.printStackTrace();
					} 
					catch (CoreException e) 
					{
						System.out.println("unable to instantiate tag ui extension : " + config[j].getName() + " ... skipping");
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Gets the tag UI model.
	 * @return
	 */
	public ITagUI getTagUI()
	{
		return fTagUI;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		fIconPadder = new IconPadder();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		fIconPadder.dispose();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TagUIPlugin getDefault() {
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
		ImageRegistry registry =  super.createImageRegistry();
		
		loadImageDescriptorIntoRegistry(registry, IMG_RENAME, PATH_RENAME);
		loadImageDescriptorIntoRegistry(registry, IMG_SYNCHRONIZE, PATH_SYNCHRONIZE);
		loadImageDescriptorIntoRegistry(registry, IMG_LINKWITH, PATH_LINKWITH);
		loadImageDescriptorIntoRegistry(registry, IMG_ADD, PATH_ADD);
		loadImageDescriptorIntoRegistry(registry, IMG_WAYPOINT, PATH_WAYPOINT);
		loadImageDescriptorIntoRegistry(registry, IMG_TAG, PATH_TAG);
		loadImageDescriptorIntoRegistry(registry, IMG_ROUTE, PATH_ROUTE);
		loadImageDescriptorIntoRegistry(registry, IMG_FILTER, PATH_FILTER);
		loadImageDescriptorIntoRegistry(registry, IMG_DELETE_UNUSED, PATH_DELETE_UNUSED);
		loadImageDescriptorIntoRegistry(registry, IMG_UP_ARROW, PATH_UP_ARROW);
		loadImageDescriptorIntoRegistry(registry, IMG_UP_ARROW_DISABLED, PATH_UP_ARROW_DISABLED);
		loadImageDescriptorIntoRegistry(registry, IMG_DOWN_ARROW, PATH_DOWN_ARROW);
		loadImageDescriptorIntoRegistry(registry, IMG_DOWN_ARROW_DISABLED, PATH_DOWN_ARROW_DISABLED);
		
		// load the shared workbench images
		final ISharedImages workbenchImages = PlatformUI.getWorkbench().getSharedImages();
		
		registry.put(IMG_DELETE, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		registry.put(IMG_DELETE_DISABLED, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		registry.put(IMG_BACK, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
		registry.put(IMG_BACK_DISABLED, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED));
		registry.put(IMG_FORWARD, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
		registry.put(IMG_FORWARD_DISABLED, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD_DISABLED));

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
	 * @author mdesmond
	 * @return
	 */
	public TagView getTagView()
	{
		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		if (activeWindow != null)
		{
			IWorkbenchPage activePage = activeWindow.getActivePage();
			
			if (activePage != null)
			{
				TagView view = (TagView)activePage.findView(TagView.ID);
				
				if (view != null) 
					return view;	
			}
		}
		return null;
	}
	
	/**
	 * @author mdesmond
	 * @return
	 */
	public WaypointView getWaypointView()
	{
		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		if (activeWindow != null)
		{
			IWorkbenchPage activePage = activeWindow.getActivePage();
			
			if (activePage != null)
			{
				WaypointView view = (WaypointView)activePage.findView(WaypointView.ID);
				
				if (view != null) 
					return view;	
			}
		}
		return null;
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
	
	public Image getPaddedIcon(String key, Image image)
	{
		return fIconPadder.getPadded(key, image);
	}
}
