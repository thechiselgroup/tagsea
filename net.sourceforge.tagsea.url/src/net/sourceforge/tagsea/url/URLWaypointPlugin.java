package net.sourceforge.tagsea.url;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class URLWaypointPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sourceforge.tagsea.url";
	public static final String WAYPOINT_ID = PLUGIN_ID + ".urlwaypoint";
	
	public static final String PATH_WEB 	     = "icons/internal_browser.gif";
	public static final String IMG_WEB 	     = "IMG_WEB";
	
	// The shared instance
	private static URLWaypointPlugin plugin;
	
	/**
	 * The constructor
	 */
	public URLWaypointPlugin() {
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
	public static URLWaypointPlugin getDefault() {
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
		loadImageDescriptorIntoRegistry(registry, IMG_WEB, PATH_WEB);
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
}
