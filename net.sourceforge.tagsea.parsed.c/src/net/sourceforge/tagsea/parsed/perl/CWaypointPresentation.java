package net.sourceforge.tagsea.parsed.perl;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.parsed.IParsedWaypointImageConstants;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointPresentation;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.swt.graphics.Image;

public class CWaypointPresentation implements IParsedWaypointPresentation {

	public CWaypointPresentation() {
	}

	public Image getDomainImage(IWaypoint waypoint, String domainObjectString) {
		return null;
	}

	public String getDomainLabel(IWaypoint waypoint, String domainObjectString) {
		return domainObjectString;
	}

	public String getDomainObjectName(IWaypoint waypoint) {
		return null;
	}

	public Image getImage(IWaypoint waypoint) {
		return getImage();
	}

	public Image getImage() {
		ImageRegistry registry = CWaypointPlugin.getDefault().getImageRegistry();
		Image image = registry.get("C_WAYPOINT_IMAGE");
		if (image == null) {
			//construct the image from various adornments.
			Image base = registry.get("C_ICON");
			ImageDescriptor overlay = TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_WAYPOINT_OVERLAY);
			ImageDescriptor overlay2 = ParsedWaypointPlugin.getDefault().getImageRegistry().getDescriptor(IParsedWaypointImageConstants.PARSE_OVERLAY);
			DecorationOverlayIcon icon = new DecorationOverlayIcon(base, new ImageDescriptor[]{overlay,  null, null, overlay2, null});
			registry.put("C_WAYPOINT_IMAGE", icon);
			image = registry.get("C_WAYPOINT_IMAGE");
		}
		return image;
	}

	public String getLabel(IWaypoint waypoint) {
		return null;
	}

	public String getLocationString(IWaypoint waypoint) {
		return null;
	}

}
