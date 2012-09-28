package net.sourceforge.tagsea.parsed;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.parsed.core.IDocumentRegistry;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointRegistry;
import net.sourceforge.tagsea.parsed.core.internal.ParsedWaypointDelegate;
import net.sourceforge.tagsea.parsed.core.internal.resources.DocumentRegistry;
import net.sourceforge.tagsea.parsed.parser.IWaypointParseProblemCollector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ParsedWaypointPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sourceforge.tagsea.parsed";

	public static final String WAYPOINT_TYPE = "net.sourceforge.tagsea.parsed.parsedWaypoint";
	
	public static final String MARKER_TYPE = "net.sourceforge.tagsea.parsed.marker";

	// The shared instance
	private static ParsedWaypointPlugin plugin;
	

	private ParsedWaypointDelegate delegate;
	
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
		//to ensure that crashes are logged.
		this.delegate.stop();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ParsedWaypointPlugin getDefault() {
		return plugin;
	}
	
	public IParsedWaypointRegistry getParsedWaypointRegistry() {
		if (this.delegate == null) {
			this.delegate = ((ParsedWaypointDelegate)TagSEAPlugin.getDefault().getWaypointDelegate(WAYPOINT_TYPE));
		}
		return this.delegate.getParsedWaypointRegistry();
	}

	/**
	 * @param e
	 */
	public void log(Exception e) {
		getLog().log(createErrorStatus(e));
	}
	
	public IStatus createErrorStatus(Exception e) {
		String message = e.getMessage();
		if (message == null) {
			message = "";
		}
		return new Status(Status.ERROR, PLUGIN_ID, Status.ERROR, message, e);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(IParsedWaypointImageConstants.PARSE_OVERLAY, imageDescriptorFromPlugin(PLUGIN_ID, "icons/parseoverlay.png"));
		
		//create an image for default parsed waypoints.
		Image base = getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		ImageDescriptor overlay = TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_WAYPOINT_OVERLAY);
		ImageDescriptor overlay2 = ParsedWaypointPlugin.getDefault().getImageRegistry().getDescriptor(IParsedWaypointImageConstants.PARSE_OVERLAY);
		DecorationOverlayIcon icon = new DecorationOverlayIcon(base, new ImageDescriptor[]{overlay,  null, null, overlay2, null});
		reg.put(IParsedWaypointImageConstants.PARSED_WAYPOINT, icon);
		
	}
	
	/**
	 * Returns the problem collector used by the platform to manage problems that arise while parsing files.
	 * @return the problem collector used by the platform to manage problems that arise while parsing files.
	 */
	public IWaypointParseProblemCollector getPlatformProblemCollector() {
		return DocumentRegistry.INSTANCE;
	}
	
	/**
	 * Returns the registry used by the platform to manage documents and files.
	 * @return the registry used by the platform to manage documents and files.
	 */
	public IDocumentRegistry getPlatformDocumentRegistry() {
		return DocumentRegistry.INSTANCE;
	}
	
}
