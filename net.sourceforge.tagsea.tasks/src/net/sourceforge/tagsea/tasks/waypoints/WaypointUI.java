package net.sourceforge.tagsea.tasks.waypoints;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.BaseWaypointUI;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.core.ui.IWaypointUIExtension;
import net.sourceforge.tagsea.tasks.ITaskWaypointAttributes;
import net.sourceforge.tagsea.tasks.TaskWaypointPlugin;
import net.sourceforge.tagsea.tasks.TaskWaypointUtils;

import org.eclipse.core.resources.IMarker;
import org.eclipse.swt.graphics.Image;

public class WaypointUI extends BaseWaypointUI implements IWaypointUIExtension {
	private final String[] VISIBLE_ATTRIBUTES =
	{
		ITaskWaypointAttributes.ATTR_MESSAGE,
		ITaskWaypointAttributes.ATTR_RESOURCE,
		ITaskWaypointAttributes.ATTR_CHAR_START,
		ITaskWaypointAttributes.ATTR_CHAR_END
	};
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#getImage(net.sourceforge.tagsea.core.IWaypoint)
	 */
	@Override
	public Image getImage(IWaypoint waypoint) {
		return TaskWaypointPlugin.getDefault().getImageRegistry().get(ITagSEAImageConstants.IMG_WAYPOINT);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#getLabel(net.sourceforge.tagsea.core.IWaypoint)
	 */
	@Override
	public String getLabel(IWaypoint waypoint) {
		IMarker marker = TaskWaypointUtils.getTaskForWaypoint(waypoint);
		String result = "";
		if (marker != null) {
			result = marker.getAttribute(IMarker.MESSAGE, "");
			String location = getLocationString(waypoint);
			if (!"".equals(location)) {
				if (!"".equals(result)) {
					result = result + " (" + location + ")";
				} else {
					result = location;
				}
			}
			
		}
		return result;
	}
	
	public String getLocationString(IWaypoint waypoint) {
		IMarker marker = TaskWaypointUtils.getTaskForWaypoint(waypoint);
		if (marker == null) return "";
		String result = marker.getResource().getName();
		int line = marker.getAttribute(IMarker.LINE_NUMBER, -1);
		if (line != -1) {
			result = result + " line " + line;
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#getVisibleAttributes()
	 */
	@Override
	public String[] getVisibleAttributes() {
		return VISIBLE_ATTRIBUTES;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#getAttributeLabel(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	@Override
	public String getAttributeLabel(IWaypoint waypoint, String attribute) {
		if (attribute != null) {
			if (attribute.startsWith("char")) {
				attribute = attribute.substring("char".length());
			}
			return Character.toUpperCase(attribute.charAt(0)) + attribute.substring(1);
		}
		return "";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#canUIChange(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	@Override
	public boolean canUIChange(IWaypoint waypoint, String attribute) {
		return IWaypoint.ATTR_AUTHOR.equals(attribute) || IWaypoint.ATTR_DATE.equals(attribute);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#canUIDelete(net.sourceforge.tagsea.core.IWaypoint)
	 */
	@Override
	public boolean canUIDelete(IWaypoint waypoint) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#canUIMove(net.sourceforge.tagsea.core.IWaypoint)
	 */
	@Override
	public boolean canUIMove(IWaypoint waypoint) {
		return false;
	}
	
	@Override
	public boolean canUIAddTag(IWaypoint waypoint) {
		return false;
	}
	
	@Override
	public boolean canUIDeleteTag(IWaypoint waypoint) {
		return false;
	}
}
