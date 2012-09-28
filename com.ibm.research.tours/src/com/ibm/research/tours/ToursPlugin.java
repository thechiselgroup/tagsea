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
package com.ibm.research.tours;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.ibm.research.tours.runtime.DefaultTourRuntime;
import com.ibm.research.tours.runtime.ITourRuntime;
import com.ibm.research.tours.system.IEventDispatcher;
import com.ibm.research.tours.system.impl.DefaultEventDispatcher;

/**
 * The activator class controls the plug-in life cycle
 */
public class ToursPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.ibm.research.tours";
	
	public static final String PALETTE_EXTENSION_ID = PLUGIN_ID + ".paletteEntry";
	public static final String DROP_EXTENSION_ID = PLUGIN_ID + ".tourElementDropAdapter";
	public static final String DOUBLE_CLICK_EXTENSION_ID = PLUGIN_ID + ".tourElementDoubleClickDelegate";
	
	private static final String PATH_TOUR		     = "icons/tour.gif";
	private static final String PATH_TOUR_WIZ  	     = "icons/tour_wiz.gif";
	private static final String PATH_PALETTE  	     = "icons/palette.gif";
	private static final String PATH_TITLE  	     = "icons/title.gif";
	private static final String PATH_DESCRIPTION  	 = "icons/description.gif";
	private static final String PATH_ELEMENTS  	 	 = "icons/elements.gif";
	private static final String PATH_TIME  	 		 = "icons/time.gif";
	private static final String PATH_TRANSITION  	 = "icons/transition.gif";
	private static final String PATH_DELETE  	 	 = "icons/delete.gif";
	private static final String PATH_AUTHOR  	 	 = "icons/author.gif";
	private static final String PATH_RUN  	 	     = "icons/run.gif";
	private static final String PATH_PREVIEW  	 	 = "icons/preview.gif";
	
	private static final String PATH_PREVIOUS  	 	 = "icons/previous.gif";
	private static final String PATH_NEXT  	 	     = "icons/next.gif";
	private static final String PATH_STOP  	 	     = "icons/stop.gif";
	private static final String PATH_BLANK 	 	     = "icons/blank.gif";
	
	public static final String IMG_TIME		         = "IMG_TIME";
	public static final String IMG_TRANSITION  	     = "IMG_TRANSITION";
	public static final String IMG_DELETE   	     = "IMG_DELETE";
	public static final String IMG_TOUR		         = "IMG_TOUR";
	public static final String IMG_TOUR_WIZ  	     = "IMG_TOUR_WIZ";
	public static final String IMG_PALETTE  	     = "IMG_PALETTE";
	public static final String IMG_TITLE  	         = "IMG_TITLE";
	public static final String IMG_DESCRIPTION  	 = "IMG_DESCRIPTION";
	public static final String IMG_ELEMENTS  	     = "IMG_ELEMENTS";
	public static final String IMG_AUTHOR  	     	 = "IMG_AUTHOR";
	public static final String IMG_RUN   	     	 = "IMG_RUN";
	public static final String IMG_PREVIEW   	     = "IMG_PREVIEW";
	
	public static final String IMG_PREVIOUS   	     = "IMG_PREVIOUS";
	public static final String IMG_NEXT   	         = "IMG_NEXT";
	public static final String IMG_STOP   	         = "IMG_STOP";
	public static final String IMG_BLANK             = "IMG_BLANK";

	private ITourElementClipBoard fClipBoard;
	private IEventDispatcher fEventDispatcher;
	private PaletteModel fPaletteModel;
	private FontRegistry fFontRegistry;
	private List<ITourElementDropAdapter> fDropAdapters;
	private List<IDoubleClickActionContribution> fDoubleClickActionContributions;
	private ITourRuntime fRuntime;
	private static ToursPlugin plugin;
	
	/**
	 * The constructor
	 */
	public ToursPlugin() {
		plugin = this;
	}

	public ITourRuntime getTourRuntime() 
	{
		if(fRuntime == null)
			fRuntime = new DefaultTourRuntime();
		
		return fRuntime;
	}

	public IEventDispatcher getEventDispatcher()
	{
		if(fEventDispatcher == null)
			fEventDispatcher = new DefaultEventDispatcher();
		
		return fEventDispatcher;
	}
	
	public FontRegistry getFontRegistry()
	{
		if(fFontRegistry == null)
			fFontRegistry = new FontRegistry(Display.getDefault());
		
		return fFontRegistry;
	}
	
	public IPaletteModel getPaletteModel()
	{
		if(fPaletteModel == null)
		{
			fPaletteModel = new PaletteModel();		
			loadPaletteExtensions();
		}
		
		return fPaletteModel;
	}
	
	private List<ITourElementDropAdapter> getDropAdapterList()
	{
		if(fDropAdapters == null)
		{
			fDropAdapters = new ArrayList<ITourElementDropAdapter>();
			loadDropExtensions();
		}
		
		return fDropAdapters;
	}
	
	private List<IDoubleClickActionContribution> getDoubleClickActionContributionList()
	{
		if(fDoubleClickActionContributions == null)
		{
			fDoubleClickActionContributions = new ArrayList<IDoubleClickActionContribution>();
			loadDoubleClickActionContributions();
		}
		
		return fDoubleClickActionContributions;
	}
	
	private void loadDoubleClickActionContributions() 
	{
		IExtensionRegistry registry 	       = Platform.getExtensionRegistry();
		IExtensionPoint    extensionPt = registry.getExtensionPoint(DOUBLE_CLICK_EXTENSION_ID);
		IExtension[]	   extensions  = extensionPt.getExtensions();
		
		if ( extensions!=null )
		{
			for (int i=0; i<extensions.length; i++)
			{
				IExtension extension = extensions[i];
				IConfigurationElement[] config = extension.getConfigurationElements();
				
				for (int j=0; j<config.length; j++)
				{
					try
					{
						IDoubleClickActionDelegate delegate = (IDoubleClickActionDelegate) config[j].createExecutableExtension("class");
						String objectContribution = config[j].getAttribute("objectContribution");
						getDoubleClickActionContributionList().add(new DoubleClickActionContribution(delegate,objectContribution));
					}
					catch (CoreException e) 
					{
						log("Error loading double click extensions", e);
					}
				}
			}
		}
	}

	public IDoubleClickActionContribution[] getDoubleClickActionContributions()
	{
		return getDoubleClickActionContributionList().toArray(new IDoubleClickActionContribution[0]);
	}

	public ITourElementClipBoard getClipBoard()
	{
		if(fClipBoard == null)
			fClipBoard = new TourElementClipBoard();
		
		return fClipBoard;
	}
	
	public ITourElementDropAdapter[] getDropAdapters()
	{
		return getDropAdapterList().toArray(new ITourElementDropAdapter[0]);
	}
	
	protected void loadPaletteExtensions()
	{
		IExtensionRegistry registry 	       = Platform.getExtensionRegistry();
		IExtensionPoint    paletteExtensionPt = registry.getExtensionPoint(PALETTE_EXTENSION_ID);
		IExtension[]	   paletteExtensions  = paletteExtensionPt.getExtensions();
		
		if ( paletteExtensions!=null )
		{
			for (int i=0; i<paletteExtensions.length; i++)
			{
				IExtension extension = paletteExtensions[i];
				IConfigurationElement[] config = extension.getConfigurationElements();
				
				for (int j=0; j<config.length; j++)
				{
					try
					{
						ITourElementProvider provider = (ITourElementProvider) config[j].createExecutableExtension("class");
						
						String id = config[j].getAttribute("id");
						
						if(id == null)
							continue;
						
						String label = config[j].getAttribute("text"); 
						String description = config[j].getAttribute("description");
						String icon = config[j].getAttribute("icon");
						
						URL iconUrl = null;
						
						try 
						{
							String namespace = extension.getNamespaceIdentifier();
							Bundle bundle = Platform.getBundle(namespace);
							iconUrl = new URL(bundle.getEntry("/"), icon);
							//iconUrl = new URL(extension.getDeclaringPluginDescriptor().getInstallURL(),icon);
						} 
						catch (MalformedURLException e) 
						{
							log("Malformed url", e);
						}
						
						Image iconImage = ImageDescriptor.createFromURL(iconUrl).createImage();
						
						if(id!=null)
						{
							IPaletteEntry entry = new PaletteEntry(id,provider,iconImage,label,description);
							getImageRegistry().put(id, iconImage);
							fPaletteModel.addPaletteEntry(entry);
						}
					}
					catch (CoreException e) 
					{
						log("Error loading palette extensions", e);
					}
				}
			}
		}
	}
	
	protected void loadDropExtensions()
	{
		IExtensionRegistry registry 	       = Platform.getExtensionRegistry();
		IExtensionPoint    dropExtensionPt = registry.getExtensionPoint(DROP_EXTENSION_ID);
		IExtension[]	   dropExtensions  = dropExtensionPt.getExtensions();
		if ( dropExtensions!=null )
		{
			for (int i=0; i<dropExtensions.length; i++)
			{
				IExtension extension = dropExtensions[i];
				IConfigurationElement[] config = extension.getConfigurationElements();
				for (int j=0; j<config.length; j++)
				{
					try
					{
						ITourElementDropAdapter adapter = (ITourElementDropAdapter) config[j].createExecutableExtension("class");
						getDropAdapterList().add(adapter);
					}
					catch (CoreException e) 
					{
						log("Error loading drop extensions", e);
					}
				}
			}
		}	
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
	public static ToursPlugin getDefault() {
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
		loadImageDescriptorIntoRegistry(registry, IMG_TOUR, PATH_TOUR);
		loadImageDescriptorIntoRegistry(registry, IMG_TOUR_WIZ, PATH_TOUR_WIZ);
		loadImageDescriptorIntoRegistry(registry, IMG_PALETTE, PATH_PALETTE);
		loadImageDescriptorIntoRegistry(registry, IMG_DESCRIPTION, PATH_DESCRIPTION);
		loadImageDescriptorIntoRegistry(registry, IMG_TITLE, PATH_TITLE);
		loadImageDescriptorIntoRegistry(registry, IMG_ELEMENTS, PATH_ELEMENTS);
		loadImageDescriptorIntoRegistry(registry, IMG_TIME, PATH_TIME);
		loadImageDescriptorIntoRegistry(registry, IMG_TRANSITION, PATH_TRANSITION);
		loadImageDescriptorIntoRegistry(registry, IMG_DELETE, PATH_DELETE);
		loadImageDescriptorIntoRegistry(registry, IMG_AUTHOR, PATH_AUTHOR);
		loadImageDescriptorIntoRegistry(registry, IMG_RUN, PATH_RUN);
		loadImageDescriptorIntoRegistry(registry, IMG_PREVIEW, PATH_PREVIEW);
		loadImageDescriptorIntoRegistry(registry, IMG_PREVIOUS, PATH_PREVIOUS);
		loadImageDescriptorIntoRegistry(registry, IMG_NEXT, PATH_NEXT);
		loadImageDescriptorIntoRegistry(registry, IMG_STOP, PATH_STOP);
		loadImageDescriptorIntoRegistry(registry, IMG_BLANK, PATH_BLANK);
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
		
		// write the error out to standard error
		String errMsg = "LOG [severity=" + severity + "]: " + msg;
		System.err.println(errMsg);
		
		if (getDefault() == null) {
			if (ex != null) {
				ex.printStackTrace(System.err);
			}
		} else {
			getDefault().getLog().log(new Status(severity, PLUGIN_ID, severity, msg, ex));
		}
	}	
}
