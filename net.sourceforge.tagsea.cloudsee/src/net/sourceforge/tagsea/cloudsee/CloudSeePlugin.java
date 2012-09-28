package net.sourceforge.tagsea.cloudsee;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CloudSeePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sourceforge.tagsea.cloudsee";

	// The shared instance
	private static CloudSeePlugin plugin;

	private TagseaImages images;
	
	/**
	 * The constructor
	 */
	public CloudSeePlugin() {
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
	public static CloudSeePlugin getDefault() {
		return plugin;
	}
	
	public ITagseaImages getImages() {
		if (this.images == null) {
			this.images = new TagseaImages();
			this.images.loadImages();
		}
		return images;
	}
	
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
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

}
