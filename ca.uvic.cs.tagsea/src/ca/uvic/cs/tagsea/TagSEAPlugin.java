/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ca.uvic.cs.tagsea.core.RouteCollection;
import ca.uvic.cs.tagsea.core.TagCollection;
import ca.uvic.cs.tagsea.monitoring.ITagSEAMonitor;
import ca.uvic.cs.tagsea.monitoring.internal.Monitoring;
import ca.uvic.cs.tagsea.preferences.TagSEAPreferences;
import ca.uvic.cs.tagsea.research.UserIDDialog;
import ca.uvic.cs.tagsea.ui.views.RoutesView;
import ca.uvic.cs.tagsea.ui.views.TagsView;

/**
 * The activator class controls the plug-in life cycle
 */
public class TagSEAPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ca.uvic.cs.tagsea";
	public static final String MARKER_ID = "ca.uvic.cs.tagsea.waypointmarker";
	public static final String ANNOTATION_ID = "ca.uvic.cs.tagsea.waypointannotation";
	
	// Color keys
	public static final String COLOR_TEXT_KEY = "ca.uvic.cs.tagsea.TEXT_COLOR";
	public static final RGB RGB_TEXT = new RGB(0, 147, 192);
	public static final String COLOR_ERROR_KEY = "ca.uvic.cs.tagsea.ERROR_COLOR";
	public static final RGB RGB_ERROR = new RGB(255, 0, 0);
	
	// Font keys
	//public static final String FONT_HEADER_KEY = "ca.uvic.cs.tagsea.TEXT_FONT";
	
	// The shared instance
	private static TagSEAPlugin plugin;
	
	private boolean startupCalled = false;
	
	private	TagCollection tagCollection;	
	private	RouteCollection routeCollection;
	private ResourceWaypointMap resourceWaypointMap;
	
	private TagseaImages images;
	private ColorRegistry colors = null;

	/**
	 * The constructor
	 */
	public TagSEAPlugin() {
		plugin = this;
		images = new TagseaImages();
		tagCollection = new TagCollection();
		routeCollection = new RouteCollection();
		resourceWaypointMap = new ResourceWaypointMap();
	}
	
	/**
	 * Sets startup called
	 * @param startup
	 */
	public void setStartupCalled(boolean startup) {
		this.startupCalled = startup;
	}
	
	/**
	 * Gets if startup was called.
	 * @return
	 */
	public boolean getStartupCalled() {
		return this.startupCalled;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		//TagRestoringJob job = new TagRestoringJob();
		//job.setUser(false);
		//job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception 
	{
		// Save the routes to disk
		getRouteCollection().save();
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance
	 * @return the shared instance
	 */
	public static TagSEAPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Gets the tag collection.
	 * @return
	 */
	public TagCollection getTagCollection() {
		return tagCollection;
	}
	
	/**
	 * Gets resource to waypoint map
	 * @return
	 */
	public ResourceWaypointMap getResourceWaypointMap() {
		return resourceWaypointMap;
	}
	
	/**
	 * Gets the route collection.
	 * @return
	 */
	public RouteCollection getRouteCollection() {
		return routeCollection;
	}
	
	
	/**
	 * Returns the fonts registry
	 * @return FontRegistry
	 */
//	public FontRegistry getFontRegistry() {
//		if ( fonts == null )  {
//			fonts = new FontRegistry();
//			initializeFontRegistry();
//		}
//		
//		return fonts;
//	}
//		
//	protected void initializeFontRegistry() {
//		// create the header font data
//		FontData[] fontData = fonts.getFontData(FONT_HEADER_KEY);
//		if (fontData.length >= 1) {
//			for (FontData data : fontData) {
//				data.setHeight(11);
//				data.setStyle(SWT.BOLD);
//			}
//		}
//		fonts.put(FONT_HEADER_KEY, fontData);
//
//		// initialize any other fonts here
//	}
	
	/**
	 * Returns the colors registry
	 * @return ColorRegistry
	 */
	public ColorRegistry getColorRegistry() {
		if (colors == null)  {
			colors = new ColorRegistry();
			initializeColorRegistry();
		}
		return colors;
	}
	
	protected void initializeColorRegistry() {
		colors.put(COLOR_TEXT_KEY, RGB_TEXT);
		colors.put(COLOR_ERROR_KEY, RGB_ERROR);
		
		// add any other colors used in TagSEA here
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		images.loadImages();
	}

	/**
	 * Gets the TagseaImages object which is used to retrieve images used in tagsea.
	 * @return TagseaImages
	 */
	public ITagseaImages getTagseaImages() {
		return images;
	}
	
	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * @param path the path relative to the plugin directory.
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
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
			msg = "";
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

	/**
	 * @author mdesmond
	 * @return
	 */
	public RoutesView getRoutesView()
	{
		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		if (activeWindow != null)
		{
			IWorkbenchPage activePage = activeWindow.getActivePage();
			
			if (activePage != null)
			{
				RoutesView view = (RoutesView)activePage.findView(RoutesView.ID);
				
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
	public TagsView getTagsView()
	{
		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		if (activeWindow != null)
		{
			IWorkbenchPage activePage = activeWindow.getActivePage();
			
			if (activePage != null)
			{
				TagsView view = (TagsView)activePage.findView(TagsView.ID);
				
				if (view != null) 
					return view;	
			}
		}
		return null;
	}
	
	/**
	 * Stops monitoring on the given monitor. Note, if the monitor has not been plugged-in, this
	 * method does nothing. Monitors must be registered via plugins.
	 * @param monitor stops monitoring.
	 */
	public void stopMonitoring(ITagSEAMonitor monitor) {
		String value = getPreferenceStore().getString(Monitoring.getPreferenceKey(monitor));
		if ("".equals(value)) return;
		if (Boolean.parseBoolean(value)) {
			getPreferenceStore().setValue(Monitoring.getPreferenceKey(monitor), Boolean.toString(false));
		}
	}
	/**
	 * Starts monitoring on the given monitor. Monitors must be registered via plugins. If the
	 * given monitor was not registered in such a way, this method does nothing.
	 * @param monitor starts monitoring.
	 */
	public void startMonitoring(ITagSEAMonitor monitor) {
		String value = getPreferenceStore().getString(Monitoring.getPreferenceKey(monitor));
		if ("".equals(value)) return;
		if (!Boolean.parseBoolean(value)) {
			getPreferenceStore().setValue(Monitoring.getPreferenceKey(monitor), Boolean.toString(true));
		}
	}
	
	public int getUserID() {
		int uid = TagSEAPreferences.getUserID();
		return uid;
	}
	
	/**
	 * 
	 * @return the new user id if successful, -1 if the user cancelled the operation, -2 if there
	 * was an error in recieving the id.
	 */
	public int askForUserID() {
		if (getUserID() > 0 ) return getUserID();
		class IDRunnable implements Runnable {
			int result;
			public void run() {
				UserIDDialog d = new UserIDDialog(
						getWorkbench().
						getActiveWorkbenchWindow().
						getShell()
				);
				result = d.open();
				if (result == UserIDDialog.FINISH) {
					TagSEAPreferences.setUserID(d.getID());
				} 
				if (result != UserIDDialog.ERROR) {
					TagSEAPreferences.setAskedForID(true);
				}
			}
		}
		IDRunnable r = new IDRunnable();
		getWorkbench().getDisplay().syncExec(r);
		if (r.result == UserIDDialog.ERROR) {
			return -2;
		}
		int uid = TagSEAPreferences.getUserID();
		return uid;
	}
	
}