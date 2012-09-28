package net.sourceforge.tagsea.delicious.waypoints;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITagChangeEvent;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.delicious.DeliciousWaypointPlugin;
import net.sourceforge.tagsea.delicious.preferences.IDeliciousPreferences;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import del.icio.us.Delicious;
import del.icio.us.DeliciousException;
import del.icio.us.beans.Post;

public class DeliciousWaypointDelegate extends AbstractWaypointDelegate 
{
	DelciciousUpdateJob fDeliciousUpdateJob;

	private class DelciciousUpdateJob extends Job
	{
		//private Delicious fDelicious;

		public DelciciousUpdateJob(String name) 
		{
			super(name);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) 
		{
			try 
			{
				List<Post> posts = null;

				String tagFilter = DeliciousWaypointPlugin.getDefault().getPreferenceStore().getString(IDeliciousPreferences.TAG_FILTER);
				//String urlFilter = DeliciousWaypointPlugin.getDefault().getPreferenceStore().getString(IDeliciousPreferences.URL_FILTER);

				StringBuffer tagFilterBuffer = new StringBuffer();
				List<String> tagFilterList = new ArrayList<String>();
				// Clean up the tag string
				if(tagFilter!=null && tagFilter.trim().length()>0)
				{
					String[] tags = tagFilter.split(" ");

					for(String tag : tags)	
					{
						if(tag.trim().length()>0)
						{
							tagFilterBuffer.append(" " + tag.trim());
							tagFilterList.add(tag.trim());
						}
					}
				}

				Delicious delicious = DeliciousWaypointPlugin.getDefault().getDeliciousInstance();

				if(delicious!=null)
				{


					if(!tagFilterList.isEmpty())
						posts = delicious.getPostsForTags(tagFilterList.toArray(new String[0]));
					else
						posts = delicious.getAllPosts();

					for(Post p : posts)
					{
						IWaypoint waypoint = TagSEAPlugin.getWaypointsModel().createWaypoint(DeliciousWaypointPlugin.WAYPOINT_ID, new String[0]);

						waypoint.setStringValue(DeliciousWaypointUtil.URL_ATTR, p.getHref());

						Date date = p.getTimeAsDate();
						waypoint.setDate(date);

						waypoint.setText(p.getDescription());

						String[] tags = p.getTagsAsArray(" ");

						for(String tag : tags)
							if(tag.trim().length()>0)
								// The delicious null tag
								if(!tag.trim().equals("system:unfiled"))
									waypoint.addTag(tag.trim());
					}
				}
			} 
			catch (final DeliciousException e) 
			{
				Display.getDefault().asyncExec(new Runnable() 
				{
					public void run() 
					{
						String message =  "An unexpected error occured when attempting to download delicious bookmarks.";
						String reason =  e.getMessage();

						ErrorDialog.openError(Display.getDefault().getActiveShell(), 
								"Error", 
								message, 
								new Status(IStatus.ERROR, DeliciousWaypointPlugin.PLUGIN_ID, IStatus.ERROR,reason, null));
					}
				});

				return Status.CANCEL_STATUS;
			}

			return Status.OK_STATUS;
		}	

	}

	public DeliciousWaypointDelegate() 
	{
		DeliciousWaypointPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent event) 
			{
				clear();
				load();
			}
		});
	}

	private void clear() 
	{
		IWaypoint[] waypoints = TagSEAPlugin.getWaypointsModel().getWaypoints(DeliciousWaypointPlugin.WAYPOINT_ID);

		for(IWaypoint waypoint : waypoints)
			waypoint.delete();
	}

	@Override
	protected void load() 
	{	
		if(fDeliciousUpdateJob == null)
		{
			fDeliciousUpdateJob = new DelciciousUpdateJob("Downloading delicious bookmarks.");
			fDeliciousUpdateJob.setSystem(true);
		}

		fDeliciousUpdateJob.schedule();
	}

	@Override
	public void navigate(IWaypoint waypoint) 
	{
		try 
		{
			IWorkbenchBrowserSupport browserSupport = DeliciousWaypointPlugin.getDefault().getWorkbench().getBrowserSupport();
			IWebBrowser browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.NAVIGATION_BAR, null, null, null);

			URL url = new URL(waypoint.getStringValue(DeliciousWaypointUtil.URL_ATTR,""));
			browser.openURL(url);
		} 
		catch (PartInitException e) 
		{
			e.printStackTrace();
		} 
		catch (MalformedURLException e) 
		{
			String message =  "An unexpected error occured when attempting to open the selected delicious waypoint.";
			String reason =  "The URL " + waypoint.getStringValue(DeliciousWaypointUtil.URL_ATTR,"") + " is malformed.";

			ErrorDialog.openError(Display.getDefault().getActiveShell(), 
					"Error", 
					message, 
					new Status(IStatus.ERROR, DeliciousWaypointPlugin.PLUGIN_ID, IStatus.ERROR,reason, null));
		}
	}

	@Override
	protected void tagsChanged(TagDelta delta) 
	{
		ITagChangeEvent[] events = delta.getEvents();

		for(ITagChangeEvent event : events)
		{
			IWaypoint[] waypoints = event.getNewWaypoints();

			for(IWaypoint waypoint:waypoints)
			{
				if(waypoint.getType().equals(DeliciousWaypointPlugin.WAYPOINT_ID))
				{

				}
			}

			waypoints = event.getOldWaypoints();

			for(IWaypoint waypoint:waypoints)
			{
				if(waypoint.getType().equals(DeliciousWaypointPlugin.WAYPOINT_ID))
				{

				}
			}
		}
	}

	@Override
	protected void waypointsChanged(WaypointDelta delta)
	{
		IWaypointChangeEvent[] events = delta.getChanges();

		for(IWaypointChangeEvent event : events)
		{
			if(event.getType() == IWaypointChangeEvent.CHANGE)
			{
				IWaypoint waypoint = event.getWaypoint();

				if(waypoint.getType().equals(DeliciousWaypointPlugin.WAYPOINT_ID))
				{

				}
			}
		}
	}
}
