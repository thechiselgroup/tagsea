package net.sourceforge.tagsea.tasks;

import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TaskWaypointPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sourceforge.tagsea.tasks";

	public static final String WAYPOINT_ID = PLUGIN_ID + ".taskWaypoint";

	//public static final String MARKER_ID = PLUGIN_ID + ".waypointmarker";

	// The shared instance
	private static TaskWaypointPlugin plugin;
	
	/**
	 * The constructor
	 */
	public TaskWaypointPlugin() {
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
	public static TaskWaypointPlugin getDefault() {
		return plugin;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(ITagSEAImageConstants.IMG_WAYPOINT, imageDescriptorFromPlugin(PLUGIN_ID, "/icons/waypoint.gif"));
		reg.put("TOJAVA", imageDescriptorFromPlugin(PLUGIN_ID, "/icons/tojava.gif"));
	}

	/**
	 * @param e
	 */
	public void log(Exception e) {
		if (e instanceof CoreException) {
			getLog().log(((CoreException)e).getStatus());
		} else {
			Status s = new Status(
				Status.ERROR,
				PLUGIN_ID,
				Status.ERROR,
				e.getMessage(),
				e
			);
			getLog().log(s);
		}
	}

}
