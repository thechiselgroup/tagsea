package net.sourceforge.tagsea.c;

import net.sourceforge.tagsea.core.ITextWaypointAttributes;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.BaseWaypointUI;
import net.sourceforge.tagsea.resources.IResourceInterfaceAttributes;

import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.Image;

public class CWaypointUI extends BaseWaypointUI {
	
	public static final String IMAGE_C_WAYPOINT_QUICKFIX = "c.quickfix";
	/**
	 * The descriptor key for a java waypoint image.
	 */
	public static String IMAGE_C_WAYPOINT = "c.waypoint";
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#canUIChange(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	@Override
	public boolean canUIChange(IWaypoint waypoint, String attribute) {
		return (
			ITextWaypointAttributes.ATTR_AUTHOR.equals(attribute) ||
			ITextWaypointAttributes.ATTR_MESSAGE.equals(attribute) ||
			ITextWaypointAttributes.ATTR_DATE.equals(attribute)
		);
	}
	
	@Override
	public String[] getVisibleAttributes() {
		return new String[] {
			ICWaypointAttributes.ATTR_MESSAGE,
			ICWaypointAttributes.ATTR_AUTHOR,
			ICWaypointAttributes.ATTR_DATE,
			ICWaypointAttributes.ATTR_RESOURCE,
			ICWaypointAttributes.ATTR_CHAR_START,
			ICWaypointAttributes.ATTR_CHAR_END
		};
	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#getValueLabel(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	@Override
	public String getValueLabel(IWaypoint waypoint, String attribute) {
		return super.getValueLabel(waypoint, attribute);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#getValueImage(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	@Override
	public Image getValueImage(IWaypoint waypoint, String attribute) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#getImage(net.sourceforge.tagsea.core.IWaypoint)
	 */
	@Override
	public Image getImage(IWaypoint waypoint) {
		return CWaypointsPlugin.getDefault().getImageRegistry().get(IMAGE_C_WAYPOINT);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#getLabel(net.sourceforge.tagsea.core.IWaypoint)
	 */
	@Override
	public String getLabel(IWaypoint waypoint) {
		Path resource = new Path(getValueLabel(waypoint, IResourceInterfaceAttributes.ATTR_RESOURCE));
		return  waypoint.getText() +  " (" +resource.lastSegment() + ")";
	}
	
	public String getLocationString(IWaypoint waypoint) {
		Path resource = new Path(getValueLabel(waypoint, IResourceInterfaceAttributes.ATTR_RESOURCE));
		return resource.lastSegment();
	}
	
}
