package net.sourceforge.tagsea.breakpoint.ui;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tagsea.breakpoint.waypoints.BreakpointUtil;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.BaseWaypointUI;
import net.sourceforge.tagsea.core.ui.IWaypointUIExtension;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.swt.graphics.Image;

public class BreakpointWaypointUI extends BaseWaypointUI implements IWaypointUIExtension 
{
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#canUIChange(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	@Override
	public boolean canUIChange(IWaypoint waypoint, String attribute) 
	{
		return false;		
	}
	
	@Override
	public String[] getVisibleAttributes() 
	{
		String[] visible =  super.getVisibleAttributes();
		List<String> list = new ArrayList<String>();
		
		for(String attr : visible)
			list.add(attr);
		
		list.remove(BreakpointUtil.MARKER_ID_ATTR);
		
		return list.toArray(new String[0]);
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
		IBreakpoint breakpoint = BreakpointUtil.findBreakpoint(waypoint);

		if(breakpoint!=null)
			return DebugUITools.newDebugModelPresentation().getImage(breakpoint);

		return super.getImage(waypoint);
	}

	@Override
	public String getLabel(IWaypoint waypoint) 
	{
		IBreakpoint breakpoint = BreakpointUtil.findBreakpoint(waypoint);

		if(breakpoint!=null)
			return DebugUITools.newDebugModelPresentation().getText(breakpoint);

		return super.getLabel(waypoint);
	}
	
	public String getLocationString(IWaypoint waypoint) 
	{
		IBreakpoint breakpoint = BreakpointUtil.findBreakpoint(waypoint);
		
		if (breakpoint != null) 
		{
			IMarker marker = breakpoint.getMarker();
			IResource resource = marker.getResource();
			int line = marker.getAttribute(IMarker.LINE_NUMBER, -1);
			String result = resource.getName();
			if (line != -1) {
				result = result + " line " + line;
			}
			return result;
		}
		return super.getLocationString(waypoint);
	}
	
	@Override
	public boolean canUIAddTag(IWaypoint waypoint) {
		return true;
	}
	
	@Override
	public boolean canUIMove(IWaypoint waypoint) {
		return false;
	}
	
	@Override
	public boolean canUIDeleteTag(IWaypoint waypoint) {
		return true;
	}
}
