package net.sourceforge.tagsea.breakpoint.waypoints;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.breakpoint.BreakpointWaypointPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;

public class BreakpointUtil 
{
	public static String MARKER_ID_ATTR = "MarkerID";
	public static String ENABLMENT_ATTR  = "Enabled";
	public static String TAGS_ATTR      = "tags";
	public static String WAYPOINT_MESSAGE_ATTR   = "waypointMessage";
	public static String TAG_DELIM      = ",";

	public static IBreakpoint[] getBreakpoints()
	{
		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
		return manager.getBreakpoints();
	}

	public static IBreakpoint findBreakpoint(Long markerID)
	{
		IBreakpoint[] breakpoints = getBreakpoints();

		for(IBreakpoint breakpoint : breakpoints)
		{
			IMarker marker =  breakpoint.getMarker();
			if(marker!=null && marker.exists())
			{
				if(marker.getId() == markerID)
					return breakpoint;
			}

		}
		return null;
	}

	public static IBreakpoint findBreakpoint(IWaypoint waypoint) 
	{
		String markerID = waypoint.getStringValue(MARKER_ID_ATTR, null);

		if(markerID!=null)
		{
			try 
			{
				long id = Long.parseLong(markerID);
				return BreakpointUtil.findBreakpoint(id);
			} 
			catch (NumberFormatException e) 
			{
			}
		}
		return null;
	}

	public static void createWaypointFromBreakpoint(final IBreakpoint breakpoint, final boolean newWaypoint) 
	{
		final IMarker marker =  breakpoint.getMarker();

		if(marker!=null && marker.exists())
		{
			TagSEAPlugin.run(new TagSEAOperation("Creating Breakpoint Waypoint...")
			{	
				@Override
				public IStatus run(IProgressMonitor monitor)throws InvocationTargetException 
				{
					// Create the waypoint
					IWaypoint waypoint = TagSEAPlugin.getWaypointsModel().createWaypoint(BreakpointWaypointPlugin.WAYPOINT_ID,new String[0]);
					monitor.beginTask("Creating Breakpoint Waypoint...", 1);
					if(waypoint!=null)
					{
						try 
						{
							// Fill out the waypoint from the marker
							waypoint.setStringValue(MARKER_ID_ATTR, Long.toString(marker.getId()));
							waypoint.setBooleanValue(ENABLMENT_ATTR, breakpoint.isEnabled());

							// Process the tags
							String tags = (String)marker.getAttribute(TAGS_ATTR);

							if(tags == null)
							{
								// We have not processed this waypoint before, add the default tag
								waypoint.addTag("breakpoint");
							}
							else
							{
								String[] tagStrings = tags.split(TAG_DELIM);

								List<String> tagsToAdd = new ArrayList<String>();

								for(String tagString : tagStrings)
									tagsToAdd.add(tagString);

								for(String tagToAdd : tagsToAdd)
									waypoint.addTag(tagToAdd);
							}

							// Author
							String author = (String)marker.getAttribute(IWaypoint.ATTR_AUTHOR);

							if(author == null)
								waypoint.setAuthor(System.getProperty("user.name"));
							else
								waypoint.setAuthor(author);

							String message = breakpoint.getMarker().getAttribute(IMarker.MESSAGE, "");

							if(message != null && message.trim().length()>0)
								waypoint.setText(message);
							else
							{
								message = (String)marker.getAttribute(WAYPOINT_MESSAGE_ATTR);

								if(message != null)
									waypoint.setText(message);
							}

							if(newWaypoint)
							{
								waypoint.setDate(new Date());
							}
							else
							{
								// Look for an existing date
								String dateString = (String)marker.getAttribute(IWaypoint.ATTR_DATE);
								Date date = null;

								if(dateString!=null)
								{
									try {
										date = SimpleDateFormat.getInstance().parse(dateString);
									} 
									catch (ParseException e) {}
								}

								if(date!=null)
									waypoint.setDate(date);
							}
						} 
						catch (CoreException e)
						{
							waypoint.delete();
						}
					}
					monitor.done();
					return Status.OK_STATUS;
				}

			}, true);
		}
	}

	public static void removeWaypoint(IBreakpoint breakpoint) 
	{
		IWaypoint waypoint = findWaypointForBreakpoint(breakpoint);

		if(waypoint!=null)
			waypoint.delete();
	}

	public static IWaypoint findWaypointForBreakpoint(IBreakpoint breakpoint)
	{
		IMarker marker =  breakpoint.getMarker();

		if(marker!=null)
		{
			long breakpointMarkerID = marker.getId();
			IWaypoint waypoint = findWaypointForMarkerId(breakpointMarkerID);

			if(waypoint!=null)
				return waypoint;
		}

		return null;
	}

	public static IWaypoint findWaypointForMarkerId(Long markerId)
	{
		IWaypoint[] waypoints = TagSEAPlugin.getWaypointsModel().getWaypoints(BreakpointWaypointPlugin.WAYPOINT_ID);

		for(IWaypoint waypoint : waypoints)
		{
			String waypointMarkerID = waypoint.getStringValue(MARKER_ID_ATTR, null);

			if(waypointMarkerID!=null)
			{
				long waypointID = Long.parseLong(waypointMarkerID);

				if(waypointID == markerId)
					return waypoint;
			}
		}

		return null;
	}

	public static void removeWaypoint(long markerId) 
	{
		IWaypoint waypoint = findWaypointForMarkerId(markerId);

		if(waypoint!=null)
			waypoint.delete();
	}

	public static void updateWaypoints(final IBreakpoint[] breakpoints) 
	{

		// see https://secure.cs.uvic.ca/bugzilla/show_bug.cgi?id=32
		// see https://secure.cs.uvic.ca/bugzilla/show_bug.cgi?id=36
		TagSEAPlugin.run(new TagSEAOperation("Updating Breakpoint Waypoints...") 
		{

			@Override
			public IStatus run(IProgressMonitor monitor)throws InvocationTargetException 
			{
				monitor.beginTask("Updating breakpoint waypoints", breakpoints.length);
				MultiStatus status = new MultiStatus(BreakpointWaypointPlugin.PLUGIN_ID, 0, "Breakpoint waypoint status", null);
				for(IBreakpoint breakpoint : breakpoints)
				{
					IWaypoint waypoint = findWaypointForBreakpoint(breakpoint);

					if(waypoint!=null)
					{
						try 
						{	
							status.merge(waypoint.setBooleanValue(ENABLMENT_ATTR, breakpoint.isEnabled()).getStatus());

							// Message
							String message = breakpoint.getMarker().getAttribute(IMarker.MESSAGE, "");
							
							if(message != null && message.trim().length()>0)
								waypoint.setText(message);
						} 
						catch (CoreException e) 
						{
						}
					}
					// This is a hack to force a ui update, should have a better way soon.. 
					// If i call into the viewer the update system locks??
					String author = waypoint.getAuthor();
					waypoint.setAuthor("");
					waypoint.setAuthor(author);
				}
				return status;
			}

		}, false);
	}


	public static IMarker getMarkerForWaypoint(IWaypoint waypoint) 
	{
		IBreakpoint breakpoint = findBreakpoint(waypoint);

		if(breakpoint != null)
			return breakpoint.getMarker();

		return null;
	}

	/**
	 * Stores the waypoint data into the breakpoint markers
	 */
	public static void save(IWaypoint waypoint) 
	{
		IMarker marker = BreakpointUtil.getMarkerForWaypoint(waypoint);
		IBreakpoint breakpoint = BreakpointUtil.findBreakpoint(waypoint);

		if(marker !=null && marker.exists())
		{
			try 
			{
				ITag[] tags = waypoint.getTags();
				StringBuffer tagBuffer = new StringBuffer();

				for(int i = 0 ; i < tags.length; i++)
				{
					tagBuffer.append(tags[i].getName());

					if( i != tags.length - 1)
						tagBuffer.append(TAG_DELIM);
				}

				String tagsFromModel = tagBuffer.toString();					
				String authorFromModel = waypoint.getAuthor();					

				String tagsFromMarker = marker.getAttribute(TAGS_ATTR, "");
				String authorFromMarker = marker.getAttribute(IWaypoint.ATTR_AUTHOR, "");
				String dateFromMarker = marker.getAttribute(IWaypoint.ATTR_DATE, "");

				String message = breakpoint.getMarker().getAttribute(IMarker.MESSAGE, "");

				if(message != null && message.trim().length()>0)
					marker.setAttribute(WAYPOINT_MESSAGE_ATTR, message);
				else
					marker.setAttribute(WAYPOINT_MESSAGE_ATTR, waypoint.getText());

				if(!tagsFromMarker.equals(tagsFromModel))
					marker.setAttribute(TAGS_ATTR, tagsFromModel);

				if(!authorFromMarker.equals(authorFromModel))
					marker.setAttribute(IWaypoint.ATTR_AUTHOR, authorFromModel);

				Date date = waypoint.getDate();
				if(date!=null)
				{
					SimpleDateFormat format = new SimpleDateFormat();
					String dateString = format.format(date);

					if(!dateFromMarker.equals(dateString))
						marker.setAttribute(IWaypoint.ATTR_DATE, dateString);	
				}
			} 
			catch (CoreException e) 
			{
				e.printStackTrace();
			}
		}
	}

	public static void saveAll() 
	{
		IWaypoint[] waypoints = TagSEAPlugin.getWaypointsModel().getWaypoints(BreakpointWaypointPlugin.WAYPOINT_ID);

		for(IWaypoint waypoint : waypoints)
			save(waypoint);
	}

	public static void delete(IBreakpoint breakpoint) 
	{
		try 
		{
			breakpoint.delete();
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}	
	}
}
