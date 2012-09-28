/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

import net.sourceforge.tagsea.core.ITagChangeListener;
import net.sourceforge.tagsea.core.ITagSEAOperationStateListener;
import net.sourceforge.tagsea.core.ITagsModel;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeListener;
import net.sourceforge.tagsea.core.IWaypointsModel;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.core.WaypointMatch;
import net.sourceforge.tagsea.core.ui.BaseWaypointUI;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.core.ui.ITagSEAUI;
import net.sourceforge.tagsea.core.ui.IWaypointUIExtension;
import net.sourceforge.tagsea.core.ui.internal.TagSEAUI;
import net.sourceforge.tagsea.core.ui.internal.TagSEAUIEvent;
import net.sourceforge.tagsea.model.internal.TagSEAChangeSupport;
import net.sourceforge.tagsea.model.internal.TagsModel;
import net.sourceforge.tagsea.model.internal.WaypointsModel;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TagSEAPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sourceforge.tagsea"; //$NON-NLS-1$

	// The shared instance
	private static TagSEAPlugin plugin;
	
	private ITagSEAUI ui;
	
	private Map<String, AbstractWaypointDelegate> delegateMap;
	private Map<String, IWaypointUIExtension> uiMap;
	private Map<String, WaypointInterface> interfaceMap;
	
//	 the paths
	private static final String PATH_RENAME 		     = "icons/rename.gif"; //$NON-NLS-1$
	private static final String PATH_SYNCHRONIZE 	     = "icons/refresh.gif"; //$NON-NLS-1$
	private static final String PATH_LINKWITH 		     = "icons/link.gif"; //$NON-NLS-1$
	private static final String PATH_ADD 			     = "icons/add.gif"; //$NON-NLS-1$
	private static final String PATH_WAYPOINT 		     = "icons/waypoint.gif"; //$NON-NLS-1$
	private static final String PATH_TAG 		         = "icons/tag.gif"; //$NON-NLS-1$
	private static final String PATH_ROUTE 			     = "icons/route.gif"; //$NON-NLS-1$
	private static final String PATH_UP_ARROW            = "icons/up.gif"; //$NON-NLS-1$
	private static final String PATH_UP_ARROW_DISABLED   = "icons/up_disabled.gif"; //$NON-NLS-1$
	private static final String PATH_DOWN_ARROW          = "icons/down.gif"; //$NON-NLS-1$
	private static final String PATH_DOWN_ARROW_DISABLED = "icons/down_disabled.gif"; //$NON-NLS-1$
	private static final String PATH_FILTER              = "icons/filter.gif"; //$NON-NLS-1$
	private static final String PATH_DELETE_UNUSED 	     = "icons/delete_unused.gif"; //$NON-NLS-1$
	private static final String PATH_FILTER_WAYPOINT	 = "icons/filtertowaypoint.gif"; //$NON-NLS-1$

	/**
	 * ID for waypoint markers in the workspace. Any marker that is a subtype of this marker
	 * will automatically cause decorations to occurr in the platform ui.
	 */
	public static final String MARKER_ID = PLUGIN_ID + ".waypointmarker"; //$NON-NLS-1$

	private static final String PATH_WAYPOINT_OVERLAY = "icons/waypointoverlay.gif";

	/**
	 * The constructor
	 */
	public TagSEAPlugin() {
		plugin = this;
		delegateMap = new TreeMap<String, AbstractWaypointDelegate>();
		uiMap = new TreeMap<String, IWaypointUIExtension>();
		interfaceMap = new TreeMap<String, WaypointInterface>();
		ui = new TagSEAUI();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		loadTypes();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		((TagSEAUI)ui).stop();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TagSEAPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Returns the waypoint delegate for the given waypoint type. If the given type has not
	 * been contributed, then null is returned. The waypoint type is defined as the plugin id
	 * of the defining plugin, followed by a '.' and the simple type name given by the plugin
	 * extension point. 
	 * @param waypointType
	 * @return the waypoint delegate, or null if none exists.
	 */
	public AbstractWaypointDelegate getWaypointDelegate(String waypointType) {
		return delegateMap.get(waypointType);
	}
	
	/**
	 * Returns the interface or delegate for the given waypoint type. If the given type
	 * has not been contributed via the <code>net.sourceforge.tagsea.waypoint</code> extension,
	 * then null is returned.
	 * 
	 * This method is prefered to calling getWaypointDelegate(String)
	 * @param waypointType the type to find.
	 * @return the waypoint type, or null if none exists.
	 */
	public IWaypointType getWaypointType(String waypointType) {
		IWaypointType type = delegateMap.get(waypointType);
		if (type == null) {
			type = interfaceMap.get(waypointType);
		}
		return type;
	}
	
	/**
	 * Returns all of the delegates registered with the platform.
	 * @return all of the delegates registered with the platform.
	 */
	public AbstractWaypointDelegate[] getWaypointDelegates() {
		Collection<AbstractWaypointDelegate> delegates = delegateMap.values();
		return delegates.toArray(new AbstractWaypointDelegate[delegates.size()]);
	}
	
	/**
	 * Returns all interfaces declared in the <code>net.sourceforge.tagsea.waypoint</code>
	 * extension.
	 * @return all interfaces declared in the <code>net.sourceforge.tagsea.waypoint</code>
	 * extension.
	 */
	public WaypointInterface[] getWaypointInterfaces() {
		Collection<WaypointInterface> interfaces = interfaceMap.values();
		return interfaces.toArray(new WaypointInterface[interfaces.size()]);
	}
	
	/**
	 * Returns the waypoint ui extension declared by the plugin that defined the given waypoint type.
	 * If none was declared, then an instance of BaseWaypointUI is returned.
	 * @param waypointType the waypoint type.
	 * @return the waypoint ui extension.
	 */
	public IWaypointUIExtension getWaypointUI(String waypointType) {
		return uiMap.get(waypointType);
	}
	
	/**
	 * Returns a list of waypoint types known to this plugin.
	 * @return
	 */
	public String[] getWaypointTypes() {
		ArrayList<String> types = new ArrayList<String>();
		types.addAll(interfaceMap.keySet());
		types.addAll(delegateMap.keySet());
		return types.toArray(new String[types.size()]);
	}
	
	private void loadTypes() {
		IConfigurationElement[] elements = 
			Platform.getExtensionRegistry().getConfigurationElementsFor("net.sourceforge.tagsea.waypoint"); //$NON-NLS-1$
		for (IConfigurationElement e : elements) {
			if ("interface".equals(e.getName())) { //$NON-NLS-1$
				loadInterface(e);
			} else if ("waypoint".equals(e.getName())) { //$NON-NLS-1$
				loadWaypoint(e);
			}
		}
	}
	
	/**
	 * @param e
	 */
	private void loadInterface(IConfigurationElement e) {
		WaypointInterface wpinterface = new WaypointInterface();
		if (wpinterface.configure(e)) {
			if (getWaypointType(wpinterface.getType()) == null) {
				interfaceMap.put(wpinterface.getType(), wpinterface);
			} else {
				//already defined: post an error.
				Status status = new Status(Status.ERROR, PLUGIN_ID, Status.ERROR, Messages.getString("TagSEAPlugin.error.duplicateWaypoint") + wpinterface.getType(), null); //$NON-NLS-1$
				getLog().log(status);
			}
		} else {
			Status status = new Status(Status.ERROR, PLUGIN_ID, Status.ERROR, Messages.getString("TagSEAPlugin.error.waypointConfiguration"), null); //$NON-NLS-1$
			getLog().log(status);
		}
	}

	/**
	 * @param e
	 */
	private void loadWaypoint(IConfigurationElement e) {
		try {
			String type =
				e.getContributor().getName() + "." + e.getAttribute("type"); //$NON-NLS-1$ //$NON-NLS-2$
			AbstractWaypointDelegate delegate = 
				(AbstractWaypointDelegate) e.createExecutableExtension("class"); //$NON-NLS-1$

			String ui = e.getAttribute("ui"); //$NON-NLS-1$
			IWaypointUIExtension uiExtension = new BaseWaypointUI();
			if (ui != null) {
				uiExtension = (IWaypointUIExtension) e.createExecutableExtension("ui"); //$NON-NLS-1$
			}
			uiMap.put(type, uiExtension);
			if (uiExtension instanceof BaseWaypointUI) {
				((BaseWaypointUI)uiExtension).setType(type);
			}
			if (delegate.configure(e)) {
				if (getWaypointType(type) == null)
					delegateMap.put(type, delegate);
				else {
					getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.ERROR, Messages.getString("TagSEAPlugin.error.duplicateWaypoint") + type, null)); //$NON-NLS-1$
				}
			}
		} catch (CoreException ex) {
			log(ex);
		}
	}

	public void log(Exception e) {
		if (e instanceof CoreException) {
			getLog().log(((CoreException)e).getStatus());
		} else {
			String message = e.getMessage();
			if (message == null) {
				message = "";
			}
			Status status = new Status(
				Status.ERROR,
				PLUGIN_ID,
				Status.ERROR,
				message,
				e
			);
			getLog().log(status);
		}
	}

	/**
	 * Returns a reference to the model in which all the tags are stored in memory.
	 * @return a reference to the model in which all the tags are stored in memory.
	 */
	public static ITagsModel getTagsModel() {
		return TagsModel.INSTANCE;
	}
	
	/**
	 * Gets a refernce to the model in which all the waypoints are stored in memory.
	 * @return a refernce to the model in which all the waypoints are stored in memory.
	 */
	public static IWaypointsModel getWaypointsModel() {
		return WaypointsModel.INSTANCE;
	}
	
	/**
	 * Adds the given listener to the list of waypoint listeners, if it has not already been added.
	 * @param listener the listener to add.
	 */
	public static void addWaypointChangeListener(IWaypointChangeListener listener) {
		TagSEAChangeSupport.INSTANCE.addWaypointChangeListener(listener);
	}
	
	/**
	 * Add the given listener to the list of waypoint listeners for the given type only. The listener
	 * will be notified only of changes to that particular type. 
	 * @param listener the listener to add.
	 * @param type the type to listen to.
	 */
	public static void addWaypointChangeListener(IWaypointChangeListener listener, String type) {
		TagSEAChangeSupport.INSTANCE.addWaypointChangeListener(listener, type);
	}
	
	/**
	 * Removes the given listener from the list of waypoint listeners, if it has previously been added.
	 * @param listener the listener to remove.
	 */
	public static void removeWaypointChangeListener(IWaypointChangeListener listener) {
		TagSEAChangeSupport.INSTANCE.removeWaypointChangeListener(listener);
	}
	
	/**
	 * Adds the given listener to the list of tag listeners, if it has not already been added.
	 * @param listener the listener to add.
	 */
	public static void addTagChangeListener(ITagChangeListener listener) {
		TagSEAChangeSupport.INSTANCE.addTagChangeListener(listener);
	}
	
	/**
	 * Runs the given operation this thread will block if wait is set to true. Normally, that will
	 * be the preferred way of calling this method. Setting wait to false is useful for long running
	 * operations that begin in the UI thread. Note: all other operations on the model will block
	 * until this operation is complete. Even single calls to the model to change fine-grained elements
	 * will block if another operation is currently running.
	 * 
	 * Views that set their progress service to show busy for the TagSEAPlugin.getDefault() object
	 * will show busy indication while TagSEAOperations are running.
	 * @param op the operation to run
	 * @param wait suspend this thread until the operation is complete.
	 */
	public static void run(TagSEAOperation op, boolean wait) {
		TagSEAChangeSupport.INSTANCE.run(op, wait);
	}
	
	/**
	 * Runs the given operation in the current thread. This differs from {@link #run(TagSEAOperation, boolean)}
	 * in that {@link #run(TagSEAOperation, boolean)} will always spawn a separate job to run the code found
	 * in the given operation. This method will run the code within this thread, using the progress monitor
	 * provided. This method is still thread-safe in that it will suspend the current thread until other
	 * running TagSEAOperations have been completed, or any current model changes have finished. It does
	 * not guarantee ordering of TagSEAOperations either. If several {@link TagSEAOperation} have been
	 * scheduled using {@link #run(TagSEAOperation, boolean)}, this method may insert itself between 
	 * operations waiting to run, depending on scheduling. Changes to the TagSEA model will not
	 * be posted until after the operation (and possible nested operations) has completed. 
	 * @param op the operation to run.
	 * @param monitor the monitor to use while running the operation.
	 * @return the status of the changes 
	 */
	public static IStatus syncRun(TagSEAOperation op, IProgressMonitor monitor) {
		return TagSEAChangeSupport.INSTANCE.syncRun(op, monitor);
	}
	
	/**
	 * Returns true if there is a concurrent running TagSEAOperation in this thread or another
	 * thread due to a {@link #run(TagSEAOperation, boolean)} or {@link #syncRun(TagSEAOperation, IProgressMonitor)}
	 * call. Note that two consecutive calls to this method may return opposite values unless it is called
	 * from within a currently running TagSEAOperation. This is meant to be a hint only.
	 * @return true if there is a concurrent running TagSEAOperation in this or another thread.
	 */
	public static boolean isBlocked() {
		return TagSEAChangeSupport.INSTANCE.isBlocked();
	}
	/**
	 * Adds the given {@link ITagSEAOperationStateListener} to the list of listeners in the platform.
	 * Has no effect if the listener has already been registered. 
	 * @param listener the listener to register.
	 */
	public static void addOperationStateListener(ITagSEAOperationStateListener listener) {
		TagSEAChangeSupport.INSTANCE.addOperationStateListener(listener);
	}
	
	/**
	 * Removes the given {@link ITagSEAOperationStateListener} from the list of listeners in the 
	 * platform. Has no effect if the listener has not been registered.
	 * @param listener the listener to deregister
	 */
	public static void removeOperationStateListener(ITagSEAOperationStateListener listener) {
		TagSEAChangeSupport.INSTANCE.removeOperationStateListener(listener);
	}
	
	/**
	 * Removes the given listener from the list of tag listeners, if it has previously been added.
	 * @param listener the listener to remove.
	 */
	public static void removeTagChangeListener(ITagChangeListener listener) {
		TagSEAChangeSupport.INSTANCE.removeTagChangeListener(listener);
	}
	
	/**
	 * Navigates to the given waypoint.
	 * @param waypoint the waypoin to navigate to.
	 */
	public void navigate(IWaypoint waypoint) {
		AbstractWaypointDelegate delegate = getWaypointDelegate(waypoint.getType());
		if (delegate != null) {
			((TagSEAUI)getUI()).getUIEventModel().fireEvent(TagSEAUIEvent.createNavigationEvent(waypoint));
			delegate.navigate(waypoint);
		}
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
	protected void initializeImageRegistry(ImageRegistry registry) {
		
		super.initializeImageRegistry(registry);
				
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_RENAME, PATH_RENAME);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_SYNCHRONIZE, PATH_SYNCHRONIZE);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_LINKWITH, PATH_LINKWITH);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_ADD, PATH_ADD);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_WAYPOINT, PATH_WAYPOINT);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_TAG, PATH_TAG);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_ROUTE, PATH_ROUTE);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_FILTER, PATH_FILTER);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_DELETE_UNUSED, PATH_DELETE_UNUSED);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_UP_ARROW, PATH_UP_ARROW);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_UP_ARROW_DISABLED, PATH_UP_ARROW_DISABLED);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_DOWN_ARROW, PATH_DOWN_ARROW);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_DOWN_ARROW_DISABLED, PATH_DOWN_ARROW_DISABLED);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_FILTER_WAYPOINT, PATH_FILTER_WAYPOINT);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_WAYPOINT_OVERLAY, PATH_WAYPOINT_OVERLAY);
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_TAG_HIERARCHY, "icons/taghierarchy.gif");
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_WAYPOINT_WARNING, "icons/waypointwarning.gif");
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_WAYPOINT_ERROR, "icons/waypointerror.gif");
		loadImageDescriptorIntoRegistry(registry, ITagSEAImageConstants.IMG_WAYPOINT_FIX, "icons/waypointfix.gif");
		// load the shared workbench images
		final ISharedImages workbenchImages = PlatformUI.getWorkbench().getSharedImages();
		
		registry.put(ITagSEAImageConstants.IMG_DELETE, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		registry.put(ITagSEAImageConstants.IMG_DELETE_DISABLED, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		registry.put(ITagSEAImageConstants.IMG_BACK, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
		registry.put(ITagSEAImageConstants.IMG_BACK_DISABLED, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED));
		registry.put(ITagSEAImageConstants.IMG_FORWARD, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
		registry.put(ITagSEAImageConstants.IMG_FORWARD_DISABLED, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD_DISABLED));
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
	 * Returns a convenient access point for ui elements in TagSEA.
	 * @return a convenient access point for ui elements in TagSEA.
	 */
	public ITagSEAUI getUI() {
		return ui;
	}

	/**
	 * @param string
	 * @param e
	 */
	public void log(String string, Exception e) {
		Status status = new Status(
				Status.ERROR,
				PLUGIN_ID,
				Status.ERROR,
				string,
				e
			);
			getLog().log(status);
	}
	
	/**
	 * Checks for equality between two waypoints. Waypoint equality is determined first
	 * by checking the equality of the waypoint types, then by querying the defining
	 * plugin for the waypoint type. This method does not replace the equals() method
	 * of a waypoint. It may be used by UI elements to update selections or check for
	 * duplicates, but should be used sparingly when a normal equals() would do better.
	 * @param wp0 the first waypoint.
	 * @param wp1 the second waypoint.
	 * @return whether or not the waypoint's defining plugin determines the two waypoints
	 * to be equal.
	 */
	public boolean waypointsEqual(IWaypoint wp0, IWaypoint wp1) {
		if (!wp0.getType().equals(wp1.getType())) return false;
		return getWaypointDelegate(wp1.getType()).waypointsEqual(wp0, wp1);
	}
	
	/**
	 * Attempts to find waypoints of the given type that match the given attributes and tag names. The
	 * returned matches are ordered by certainty.
	 * @param type the waypoint type to check.
	 * @param attributes the attributes to search for.
	 * @param tagNames the tag names to match.
	 * @return the matches.
	 */
	public WaypointMatch[] locate(String type, Map<String, Object> attributes, SortedSet<String> tagNames) {
		AbstractWaypointDelegate delegate = getWaypointDelegate(type);
		if (delegate != null) {
			return delegate.getLocator().findMatches(attributes, tagNames);
		}
		return new WaypointMatch[0];
	}
		
}
