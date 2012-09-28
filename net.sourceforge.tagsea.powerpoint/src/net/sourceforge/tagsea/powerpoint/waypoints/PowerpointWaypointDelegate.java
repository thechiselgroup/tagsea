package net.sourceforge.tagsea.powerpoint.waypoints;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ITagChangeEvent;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.powerpoint.PowerpointWaypointPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.cue.tourist.internal.win32.ppt.PowerpointApplication;

public class PowerpointWaypointDelegate extends AbstractWaypointDelegate 
{
	public static final String TAGS_ATTR = "net.sourceforge.tagsea.powerpoint.tags";
	public static final String SLIDE_ATTR = "Slide";
	public static final String SLIDE_RANGE_ATTR = "SlideRange";
	public static final String AUTHOR_ATTR = "net.sourceforge.tagsea.powerpoint.author";
	public static final String DATE_ATTR = "net.sourceforge.tagsea.powerpoint.date";
	public static final String DESCRIPTION_ATTR = "net.sourceforge.tagsea.powerpoint.description";
	public static final String MARKER_ID_ATTR = "MarkerID";

	private class SaveParticipant implements ISaveParticipant {
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse.core.resources.ISaveContext)
		 */
		public void doneSaving(ISaveContext context) {
		}
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#prepareToSave(org.eclipse.core.resources.ISaveContext)
		 */
		public void prepareToSave(ISaveContext context) throws CoreException {

		}
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse.core.resources.ISaveContext)
		 */
		public void rollback(ISaveContext context) {
			//can't rollback saves

		}
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#saving(org.eclipse.core.resources.ISaveContext)
		 */
		public void saving(ISaveContext context) throws CoreException 
		{
			//only care about full saves.
			if (context.getKind() != ISaveContext.FULL_SAVE) 
				return;

			flushAll();
		}
	}


	public PowerpointWaypointDelegate() 
	{

	}

	
	protected void load() 
	{
		TagSEAPlugin.run(new TagSEAOperation("Loading Powerpoint Waypoints...")
		{
			
			public IStatus run(IProgressMonitor monitor) throws InvocationTargetException 
			{
				try 
				{
					IResource root = ResourcesPlugin.getWorkspace().getRoot();
					IMarker[] markers = root.findMarkers(PowerpointWaypointPlugin.MARKER_ID ,false,IResource.DEPTH_INFINITE);

					for(IMarker marker : markers)
					{
						String[] tags = loadTags(marker);
						String author = loadAuthor(marker);
						Date date = loadDate(marker);
						int slide = marker.getAttribute(SLIDE_ATTR, 0);
						String slideRange = marker.getAttribute(SLIDE_RANGE_ATTR, null);
						String description = loadDescription(marker);

						long markerId = marker.getId();

						IWaypoint waypoint = TagSEAPlugin.getWaypointsModel().createWaypoint(PowerpointWaypointPlugin.WAYPOINT_ID, tags);

						if(date!=null)
							waypoint.setDate(date);

						if(author!=null)
							waypoint.setAuthor(author);

						if(description!=null)
							waypoint.setText(description);

						if(slide > 0)
							waypoint.setIntValue(SLIDE_ATTR, slide);
						else if(slideRange!=null && slideRange.trim().length()>0)
							waypoint.setStringValue(SLIDE_RANGE_ATTR, slideRange.trim());
						
						waypoint.setStringValue(MARKER_ID_ATTR, Long.toString(markerId));
					}
				} 
				catch (CoreException e) 
				{
					e.printStackTrace();
				}

				return Status.OK_STATUS;
			}

		}, false);

		try 
		{
			ResourcesPlugin.getWorkspace().addSaveParticipant(PowerpointWaypointPlugin.getDefault(), new SaveParticipant());
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}

	protected String loadDescription(IMarker marker) 
	{
		String description = marker.getAttribute(DESCRIPTION_ATTR, "");

		if(description.trim().length()>0)
			return description;

		return null;
	}

	protected Date loadDate(IMarker marker) 
	{
		String dateString = marker.getAttribute(DATE_ATTR, "");

		if(dateString.trim().length()>0)
		{
			try 
			{
				SimpleDateFormat format = new SimpleDateFormat();
				Date date = format.parse(dateString);

				return date;
			} 
			catch (ParseException e) 
			{
			}
		}
		return null;
	}

	protected String loadAuthor(IMarker marker) 
	{
		String author = marker.getAttribute(AUTHOR_ATTR, "");

		if(author.trim().length()>0)
			return author;

		return null;
	}

	protected String[] loadTags(IMarker marker) 
	{
		String tagString = marker.getAttribute(TAGS_ATTR, "");

		if(tagString.trim().length()>0)
		{
			String[] tags = tagString.split(",");

			if(tags!= null && tags.length>0)
			{
				for(int i = 0; i <tags.length; i++)
					tags[i] = tags[i].trim();

				return tags;
			}
		}
		return new String[0];
	}

	@SuppressWarnings("restriction")
	
	public void navigate(IWaypoint waypoint) 
	{
		try 
		{
			String markerIdString = waypoint.getStringValue(MARKER_ID_ATTR, "");

			if(markerIdString.trim().length()>0)
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

					// show the file in package explorer for context 
					PackageExplorerPart view = PackageExplorerPart.openInActivePerspective();
					view.tryToReveal(marker.getResource());

					// for now, pop up powerpoint
					Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
					PowerpointApplication ppt = new PowerpointApplication(shell,SWT.BAR);

					IFile file = (IFile)marker.getResource();
					ppt.open(file.getLocation().toString());

					int slide = waypoint.getIntValue(SLIDE_ATTR, 0);
					String slideRange = waypoint.getStringValue(SLIDE_RANGE_ATTR, null);

					if(slide > 0)
					{
						// Single slide waypoint
						ppt.runSlideShow(slide,slide);
						ppt.gotoEditorSlide(slide);

					}
					else if(slideRange!=null && slideRange.trim().length()>0)
					{
						int[] range = PowerpointWaypointUtil.getSlideRange(slideRange.trim());

						if(range!=null && range.length == 2)
						{
							// Single slide waypoint
							ppt.runSlideShow(range[0],range[1]);
							ppt.gotoEditorSlide(range[0]);
						}
					}
					else
					{
						ppt.runSlideShow();
					}
					
					ppt.quit();
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	
	protected void tagsChanged(TagDelta delta) 
	{
		ITagChangeEvent[] events = delta.getEvents();

		for(ITagChangeEvent event : events)
		{
			IWaypoint[] waypoints = event.getNewWaypoints();

			for(IWaypoint waypoint:waypoints)
			{
				if(waypoint.getType().equals(PowerpointWaypointPlugin.WAYPOINT_ID))
					flush(waypoint);
			}

			waypoints = event.getOldWaypoints();

			for(IWaypoint waypoint:waypoints)
			{
				if(waypoint.getType().equals(PowerpointWaypointPlugin.WAYPOINT_ID))
					flush(waypoint);
			}
		}

	}


	
	protected void waypointsChanged(WaypointDelta delta)
	{
		IWaypointChangeEvent[] events = delta.getChanges();

		for(IWaypointChangeEvent event : events)
		{
			if(event.getType() == IWaypointChangeEvent.CHANGE)
			{
				IWaypoint waypoint = event.getWaypoint();

				if(waypoint.getType().equals(PowerpointWaypointPlugin.WAYPOINT_ID))
				{
					flush(waypoint);
				}
			}

			if(event.getType() == IWaypointChangeEvent.DELETE)
			{
				IWaypoint waypoint = event.getWaypoint();

				if(waypoint.getType().equals(PowerpointWaypointPlugin.WAYPOINT_ID))
				{
					try 
					{
						String markerIdString = waypoint.getStringValue(MARKER_ID_ATTR, "");

						if(markerIdString.trim().length()>0)
						{
							Long markerId = Long.parseLong(markerIdString.trim()); // throws NFE
							IResource root = ResourcesPlugin.getWorkspace().getRoot();
							IMarker[] markers = root.findMarkers(PowerpointWaypointPlugin.MARKER_ID ,false,IResource.DEPTH_INFINITE);

							for(IMarker marker : markers)
								if(marker!=null && marker.exists())	
									if(marker.getId() == markerId)
										marker.delete();	
						}
					} 
					catch (NumberFormatException e) 
					{
						e.printStackTrace();
					} 
					catch (CoreException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void flushAll() 
	{
		IWaypoint[] waypoints = TagSEAPlugin.getWaypointsModel().getWaypoints(PowerpointWaypointPlugin.WAYPOINT_ID);

		for(IWaypoint waypoint: waypoints)
			flush(waypoint);
	}

	public static void flush(IWaypoint waypoint) 
	{
		try 
		{
			String markerIdString = waypoint.getStringValue(MARKER_ID_ATTR, "");
			Long markerId = Long.parseLong(markerIdString.trim()); // throws NFE

			IResource root = ResourcesPlugin.getWorkspace().getRoot();
			IMarker[] markers = root.findMarkers(PowerpointWaypointPlugin.MARKER_ID ,false,IResource.DEPTH_INFINITE);

			for(IMarker marker : markers)
			{
				if(marker!=null && marker.exists())
				{
					if(marker.getId() == markerId)
					{

						int slide = waypoint.getIntValue(SLIDE_ATTR, 0);
						String slideRange = waypoint.getStringValue(SLIDE_RANGE_ATTR, null);
						String author = waypoint.getAuthor();
						Date date = waypoint.getDate();
						String description = waypoint.getText();
						ITag[] tags = waypoint.getTags();

						if(slide>0)
							marker.setAttribute(SLIDE_ATTR, slide);

						if(slideRange!=null)
							marker.setAttribute(SLIDE_RANGE_ATTR, slideRange);

						if(author!=null && author.trim().length()>0)
							marker.setAttribute(AUTHOR_ATTR, author);

						if(description!=null && description.trim().length()>0)
							marker.setAttribute(DESCRIPTION_ATTR, description);

						if(tags.length > 0)
						{
							StringBuffer buffer = new StringBuffer();

							for(ITag tag: tags)
							{
								if(tag.getName().trim().length()>0)
									buffer.append(tag.getName().trim() + ",");
							}

							if(buffer.toString().trim().length()>0)
								marker.setAttribute(TAGS_ATTR, buffer.toString());
						}

						if(date!=null)
						{
							SimpleDateFormat format = new SimpleDateFormat();
							String dateString = format.format(date);
							marker.setAttribute(DATE_ATTR, dateString);
						}
					}
				}
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
}
