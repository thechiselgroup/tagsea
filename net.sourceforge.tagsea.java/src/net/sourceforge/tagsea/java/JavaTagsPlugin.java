package net.sourceforge.tagsea.java;

import java.util.LinkedList;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAModelException;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JavaTagsPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sourceforge.tagsea.java"; //$NON-NLS-1$
	
	/**
	 * The Java waypoint type.
	 */
	public static final String JAVA_WAYPOINT = PLUGIN_ID + ".javaWaypoint"; //$NON-NLS-1$
	/**
	 * TagSEAChangeStatus error code for when a Java waypoint can't be changed due to some
	 * concurrent changes occurring within the Java waypoints platform.
	 */
	public static final int CONCURRENT_CHANGE_ERROR = 2;

	/**
	 * TagSEAChangeStatus error code for when an attempt was made to change a waypoint
	 * attribute that cannot be changed outside of the waypoints platform.
	 */
	public static final int ILLEGAL_CHANGE_ERROR = 3;

	// The shared instance
	private static JavaTagsPlugin plugin;
	
	/**
	 * The constructor
	 */
	public JavaTagsPlugin() {
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
	public static JavaTagsPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Gets all the java waypoints for a particular file.
	 * @param file
	 * @return
	 * @throws TagSEAModelException
	 */
	public static IWaypoint[] getJavaWaypointsForFile(IFile file) {
		//@tag tagsea.todo.performance : it might be better to cache the waypoints for the files.
		IWaypoint[] waypoints = TagSEAPlugin.getWaypointsModel().getWaypoints(IJavaWaypointsConstants.JAVA_WAYPOINT);
		if (!"java".equals(file.getFileExtension())) //$NON-NLS-1$
				return new IWaypoint[0];
		IPath filePath = file.getFullPath();
		LinkedList<IWaypoint> fileWaypoints = new LinkedList<IWaypoint>();
		for (IWaypoint waypoint : waypoints) {
			if (!waypoint.exists()) continue;
			String waypointPath = waypoint.getStringValue(IJavaWaypointAttributes.ATTR_RESOURCE, ""); //$NON-NLS-1$
			if (waypointPath.length() > 0) {
				IPath path = new Path(waypointPath);
				if (path.equals(filePath))
					fileWaypoints.add(waypoint);
			}
		}
		return fileWaypoints.toArray(new IWaypoint[fileWaypoints.size()]);
	}
	
	public static AbstractWaypointDelegate getJavaWaypointDelegate() {
		return TagSEAPlugin.getDefault().getWaypointDelegate(IJavaWaypointsConstants.JAVA_WAYPOINT);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		ImageDescriptor descriptor = imageDescriptorFromPlugin(PLUGIN_ID, "/icons/waypoint.gif");
		reg.put(JavaWaypointUI.IMAGE_JAVA_WAYPOINT, descriptor);
		descriptor = imageDescriptorFromPlugin(PLUGIN_ID, "/icons/waypointfix.gif");
		reg.put(JavaWaypointUI.IMAGE_JAVA_WAYPOINT_QUICKFIX, descriptor);
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
