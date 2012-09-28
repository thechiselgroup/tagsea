package net.sourceforge.tagsea.delicious.ui;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.BaseWaypointUI;
import net.sourceforge.tagsea.core.ui.IWaypointUIExtension;
import net.sourceforge.tagsea.delicious.DeliciousWaypointPlugin;
import net.sourceforge.tagsea.delicious.waypoints.DeliciousWaypointUtil;

import org.eclipse.swt.graphics.Image;

public class DeliciousWaypointUI extends BaseWaypointUI implements IWaypointUIExtension 
{
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#canUIChange(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	@Override
	public boolean canUIChange(IWaypoint waypoint, String attribute) 
	{
		return true;		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#getAttributeLabel(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	@Override
	public String getAttributeLabel(IWaypoint waypoint, String attribute) 
	{
		return super.getAttributeLabel(waypoint, attribute);
	}

	@Override
	public Image getImage(IWaypoint waypoint) 
	{
		return DeliciousWaypointPlugin.getDefault().getImageRegistry().get(DeliciousWaypointPlugin.IMG_DELICIOUS);

	}

	@Override
	public String getLabel(IWaypoint waypoint) 
	{
		return waypoint.getStringValue(DeliciousWaypointUtil.URL_ATTR, "");
	}
}
