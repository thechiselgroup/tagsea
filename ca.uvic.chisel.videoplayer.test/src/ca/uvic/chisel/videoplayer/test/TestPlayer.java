package ca.uvic.chisel.videoplayer.test;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TestPlayer extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ca.uvic.chisel.videoplayer.test";

	public static final String ICON_PLAY = PLUGIN_ID + ".icon.play";

	public static final String ICON_PAUSE = PLUGIN_ID + ".icon.pause";

	public static final String ICON_STOP = PLUGIN_ID + ".icon.stop";

	// The shared instance
	private static TestPlayer plugin;
	
	/**
	 * The constructor
	 */
	public TestPlayer() {
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
		//delete cached files.
		IPath stateLocation = getStateLocation();
		File stateFile = stateLocation.toFile();
		for (File cache : stateFile.listFiles()) {
			if (cache.isFile() && cache.getName().toLowerCase().endsWith(".flv")) {
				cache.delete();
			}
		}
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TestPlayer getDefault() {
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
	protected void initializeImageRegistry(ImageRegistry reg) {
		reg.put(ICON_PLAY, getImageDescriptor("icons/play.png"));
		reg.put(ICON_PAUSE, getImageDescriptor("icons/pause.png"));
		reg.put(ICON_STOP, getImageDescriptor("icons/stop.png"));
	}
}
