package net.sourceforge.tagsea.powerpoint.ui;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.BaseWaypointUI;
import net.sourceforge.tagsea.core.ui.IWaypointUIExtension;
import net.sourceforge.tagsea.powerpoint.PowerpointWaypointPlugin;
import net.sourceforge.tagsea.powerpoint.waypoints.PowerpointWaypointDelegate;
import net.sourceforge.tagsea.powerpoint.waypoints.PowerpointWaypointUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

public class PowerpointWaypointUI extends BaseWaypointUI implements IWaypointUIExtension 
{
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#canUIChange(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	
	public boolean canUIChange(IWaypoint waypoint, String attribute) 
	{
		if(attribute.equals(PowerpointWaypointDelegate.MARKER_ID_ATTR))
			return false;
		
		return true;
			
	} 
	
	
	public String[] getVisibleAttributes() 
	{
		String[] visible =  super.getVisibleAttributes();
		List<String> list = new ArrayList<String>();
		
		for(String attr : visible)
			list.add(attr);
		
		list.remove(PowerpointWaypointDelegate.MARKER_ID_ATTR);
		
		return list.toArray(new String[0]);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#getAttributeLabel(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	
	public String getAttributeLabel(IWaypoint waypoint, String attribute) 
	{		
		return super.getAttributeLabel(waypoint, attribute);
	}

	
	public Image getImage(IWaypoint waypoint) 
	{
		// different images for different types of slide waypoints
		return PowerpointWaypointPlugin.getDefault().getImageRegistry().get(PowerpointWaypointPlugin.IMG_PPT);
	}

	
	public String getLabel(IWaypoint waypoint) 
	{
		return waypoint.getText();
	}
	
	public String getLocationString(IWaypoint waypoint) 
	{
		String markerIdString = waypoint.getStringValue(PowerpointWaypointDelegate.MARKER_ID_ATTR, "");

		if(markerIdString.trim().length()>0)
		{
			try 
			{
				Long markerId = Long.parseLong(markerIdString.trim()); // throws NFE

				IResource root = ResourcesPlugin.getWorkspace().getRoot();
				IMarker[] markers = root.findMarkers(PowerpointWaypointPlugin.MARKER_ID ,false,IResource.DEPTH_INFINITE);
				IMarker marker = null;

				for(IMarker m : markers)
					if(m!=null && m.exists())	
						if(m.getId() == markerId)
							marker = m;
				
				if(marker != null && marker.exists())
				{
					IFile file = (IFile)marker.getResource();
					StringBuffer buffer = new StringBuffer(file.getName());
					
					int slide = waypoint.getIntValue(PowerpointWaypointDelegate.SLIDE_ATTR, 0);
					String slideRange = waypoint.getStringValue(PowerpointWaypointDelegate.SLIDE_RANGE_ATTR, null);
					
					if(slide > 0)
						buffer.append(" (Slide #" + slide + ")");
					else
						if(slideRange != null && slideRange.trim().length()>0)
						{
							int[] range = PowerpointWaypointUtil.getSlideRange(slideRange);
						
							if(range!=null)
								buffer.append(" (Slides " + range[0] + " to " + range[1] + ")");
						}
						else
							buffer.append(" (All slides)");
					
					return buffer.toString();
				}
			} 
			catch (NumberFormatException e) 
			{
				//e.printStackTrace();
			} 
			catch (CoreException e) 
			{
				//e.printStackTrace();
			}	
		}
		
		return "Powerpoint";
	}
	
	
	public boolean canUIAddTag(IWaypoint waypoint) {
		return true;
	}
	
	
	public boolean canUIMove(IWaypoint waypoint) {
		return false;
	}
	
	
	public boolean canUIDeleteTag(IWaypoint waypoint) {
		return true;
	}
}
