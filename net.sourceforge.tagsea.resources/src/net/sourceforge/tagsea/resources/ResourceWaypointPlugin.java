package net.sourceforge.tagsea.resources;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.resources.ui.ISharedImages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ResourceWaypointPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sourceforge.tagsea.resources";
	
	public static final String WAYPOINT_ID = PLUGIN_ID + ".resourceWaypoint";
	
	public static final String INTERFACE_ID = PLUGIN_ID + ".resourceInterface";
	
	public static final String MARKER_ID = PLUGIN_ID + ".waypointmarker";

	// The shared instance
	private static ResourceWaypointPlugin plugin;

	private AbstractWaypointDelegate delegate;

	
	
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
	
	public AbstractWaypointDelegate getDelgate() {
		if (this.delegate == null) {
			this.delegate = TagSEAPlugin.getDefault().getWaypointDelegate(WAYPOINT_ID);
		}
		return delegate;
	}
	
	public void log(Exception e) {
		if (e instanceof CoreException) {
			getLog().log(((CoreException)e).getStatus());
		} else {
			String message = e.getMessage();
			if (message == null) message = "";
			Status s = new Status(Status.ERROR, PLUGIN_ID, Status.ERROR, message, e);
			getLog().log(s);
		}
		
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(ITagSEAImageConstants.IMG_WAYPOINT, getImageDescriptor("icons/waypoint.gif"));
		reg.put(ISharedImages.IMG_TEXT, getImageDescriptor("icons/text.gif"));
		reg.put(ISharedImages.IMG_OVERLAY, getImageDescriptor("icons/waypointoverlay.gif"));
		reg.put(ISharedImages.IMG_SYNCH_OUT, getImageDescriptor("icons/outgo_synch.gif"));
		reg.put(ISharedImages.IMG_SYNCH_IN, getImageDescriptor("icons/incom_synch.gif"));
		reg.put(ISharedImages.IMG_NEW_OUT, getImageDescriptor("icons/outgo_new.gif"));
		reg.put(ISharedImages.IMG_NEW_IN, getImageDescriptor("icons/incom_new.gif"));
		reg.put(ISharedImages.IMG_REMOVE_OUT, getImageDescriptor("icons/outgo_remove.gif"));
		reg.put(ISharedImages.IMG_REMOVE_IN, getImageDescriptor("icons/incom_remove.gif"));
		reg.put(ISharedImages.IMG_CONFLICT, getImageDescriptor("icons/conflict_synch.gif"));
		
	}
}
