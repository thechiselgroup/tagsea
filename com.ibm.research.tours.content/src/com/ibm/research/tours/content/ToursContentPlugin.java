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
package com.ibm.research.tours.content;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ToursContentPlugin extends AbstractUIPlugin 
{
	// The plug-in ID
	public static final String PLUGIN_ID = "com.ibm.research.tours.content";

	private static final String PATH_ERROR	 	         = "icons/error.gif";
	public static final String IMG_ERROR		         = "IMG_ERROR";
	private static final String PATH_WEB	 	         = "icons/web.gif";
	public static final String IMG_WEB		             = "IMG_WEB";
	private static final String PATH_PPT	 	         = "icons/powerpoint.gif";
	public static final String IMG_PPT		             = "IMG_PPT";
	// The shared instance
	private static ToursContentPlugin plugin;

	/** Maps ImageDescriptors to Images */
	private static Map fgImages2= new Hashtable(10);
	private static List fgDisposeOnShutdownImages= new ArrayList();
	private ITextRegionClipBoard fTextRegionClipBoard;
	private IFileClipBoard fFileClipBoard;

	/**
	 * The constructor
	 */
	public ToursContentPlugin() 
	{
		plugin = this;
	}
	
	public Image getFavIcon(String url)
	{
		// favicon code removed - cannot be released 
		try {
			ImageDescriptor desc = getImageDescriptor(PATH_WEB);
			Image img = (Image) fgImages2.get(desc);
			if ( img==null )
			{
				fgImages2.put(desc,desc.createImage());
			}
			return img;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ITextRegionClipBoard getTextRegionClipBoard()
	{
		if(fTextRegionClipBoard == null)
			fTextRegionClipBoard = new TextRegionClipBoard();

		return fTextRegionClipBoard;
	}

	public IFileClipBoard getFileClipBoard()
	{
		if(fFileClipBoard == null)
			fFileClipBoard = new FileClipBoard();

		return fFileClipBoard;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception 
	{
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception 
	{
		plugin = null;
		super.stop(context);

		if (fgDisposeOnShutdownImages != null) {
			Iterator i= fgDisposeOnShutdownImages.iterator();
			while (i.hasNext()) {
				Image img= (Image) i.next();
				if (!img.isDisposed())
					img.dispose();
			}
		}
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ToursContentPlugin getDefault() 
	{
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) 
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Returns a shared image for the given adaptable.
	 * This convenience method queries the given adaptable
	 * for its <code>IWorkbenchAdapter.getImageDescriptor</code>, which it
	 * uses to create an image if it does not already have one.
	 * <p>
	 * Note: Images returned from this method will be automitically disposed
	 * of when this plug-in shuts down. Callers must not dispose of these
	 * images themselves.
	 * </p>
	 *
	 * @param adaptable the adaptable for which to find an image
	 * @return an image
	 */
	public static Image getImage(IAdaptable adaptable) {
		if (adaptable != null) {
			Object o= adaptable.getAdapter(IWorkbenchAdapter.class);
			if (o instanceof IWorkbenchAdapter) {
				ImageDescriptor id= ((IWorkbenchAdapter) o).getImageDescriptor(adaptable);
				if (id != null) {
					Image image= (Image)fgImages2.get(id);
					if (image == null) {
						image= id.createImage();
						try {
							fgImages2.put(id, image);
						} catch (NullPointerException ex) {
							// NeedWork
						}
						fgDisposeOnShutdownImages.add(image);

					}
					return image;
				}
			}
		}
		return null;
	}

	public static String getText(IAdaptable adaptable) 
	{
		if (adaptable != null) {
			Object o= adaptable.getAdapter(IWorkbenchAdapter.class);
			if (o instanceof IWorkbenchAdapter) {
				String text = ((IWorkbenchAdapter) o).getLabel(adaptable);
				if (text != null) 
					return text;
			}
		}
		return "";
	}

	@Override
	protected ImageRegistry createImageRegistry() 
	{
		ImageRegistry reg =  super.createImageRegistry();
		loadImageDescriptorIntoRegistry(reg,IMG_ERROR,PATH_ERROR);
		loadImageDescriptorIntoRegistry(reg,IMG_WEB,PATH_WEB);
		loadImageDescriptorIntoRegistry(reg,IMG_PPT,PATH_PPT);
		return reg;
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
