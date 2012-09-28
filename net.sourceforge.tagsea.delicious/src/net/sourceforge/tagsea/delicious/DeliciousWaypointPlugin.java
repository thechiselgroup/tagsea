package net.sourceforge.tagsea.delicious;

import net.sourceforge.tagsea.delicious.preferences.IDeliciousPreferences;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import del.icio.us.Delicious;

/**
 * The activator class controls the plug-in life cycle
 */
public class DeliciousWaypointPlugin extends AbstractUIPlugin implements IDeliciousPreferences {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sourceforge.tagsea.delicious";
	public static final String WAYPOINT_ID = PLUGIN_ID + ".deliciouswaypoint";
	
	public static final String PATH_DELICIOUS 	     = "icons/delicious.gif";
	public static final String IMG_DELICIOUS 	     = "IMG_DELICIOUS";
	
	// The shared instance
	private static DeliciousWaypointPlugin plugin;
	//private static Delicious fDelicious;
	
	/**
	 * The constructor
	 */
	public DeliciousWaypointPlugin() 
	{
		plugin = this;
	}

	/**
	 * The constructor
	 */
	public Delicious getDeliciousInstance() 
	{
			return initDelicious();
	}
	
	
	private Delicious initDelicious() 
	{
		String username = DeliciousWaypointPlugin.getDefault().getPreferenceStore().getString(IDeliciousPreferences.USERNAME);
		String password = DeliciousWaypointPlugin.getDefault().getPreferenceStore().getString(IDeliciousPreferences.PASSWORD);

		if((username!=null && username.trim().length()>0) && (password!=null && password.trim().length()>0))
		{
			return new Delicious(username,password);
		}
		
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception 
	{
		super.start(context);
		getPreferenceStore().setDefault(USERNAME, DEFAULT_USERNAME);
		getPreferenceStore().setDefault(PASSWORD, DEFAULT_PASSWORD);
		getPreferenceStore().setDefault(TAG_FILTER, DEFAULT_TAG_FILTER);
		getPreferenceStore().setDefault(URL_FILTER, DEFAULT_URL_FILTER);
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
	public static DeliciousWaypointPlugin getDefault() {
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
		loadImageDescriptorIntoRegistry(registry, IMG_DELICIOUS, PATH_DELICIOUS);
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
