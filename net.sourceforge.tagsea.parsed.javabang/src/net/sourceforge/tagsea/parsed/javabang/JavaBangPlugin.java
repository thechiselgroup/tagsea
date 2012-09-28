package net.sourceforge.tagsea.parsed.javabang;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JavaBangPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sourceforge.tagsea.parsed.javabang";

	// The shared instance
	private static JavaBangPlugin plugin;

	public static String WAYPOINT_KIND = "net.sourceforge.tagsea.parsed.javabang";
	
	/**
	 * The constructor
	 */
	public JavaBangPlugin() {
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
	public static JavaBangPlugin getDefault() {
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
			Status s = new Status(
					Status.ERROR,
					PLUGIN_ID,
					message,
					e
			);
			getLog().log(s);
		}
	}

}
