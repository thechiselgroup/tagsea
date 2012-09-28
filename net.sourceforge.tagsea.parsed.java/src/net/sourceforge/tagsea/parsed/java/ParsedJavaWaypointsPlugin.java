package net.sourceforge.tagsea.parsed.java;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ParsedJavaWaypointsPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sourceforge.tagsea.java";

	// The shared instance
	private static ParsedJavaWaypointsPlugin plugin;
	
	/**
	 * The constructor
	 */
	public ParsedJavaWaypointsPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
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
	public static ParsedJavaWaypointsPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * @param e
	 */
	public void log(Exception e) {
		if (e instanceof CoreException) {
			getLog().log(((CoreException)e).getStatus());
		} else {
			String message = e.getMessage();
			if (message == null) {
				message = "";
			}
			Status s = new Status(Status.ERROR, PLUGIN_ID, Status.ERROR, message, e);
			getLog().log(s);
		}
	}

}
