package net.sourceforge.tagsea.url.waypoints;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.core.ITagChangeEvent;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.url.URLWaypointPlugin;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class URLWaypointDelegate extends AbstractWaypointDelegate 
{
	private boolean fStoreOutOfSync = false;
	private DiskFlushThread fDiskFlushThread;

	private class DiskFlushThread extends Thread
	{
		@Override
		public void run() 
		{
			// Suspend until the disk store becomes invalid
			while (true)
			{
				try
				{
					synchronized(this) 
					{
						while (!fStoreOutOfSync)
							wait();
					}
				} 
				catch (InterruptedException e)
				{
				}

				save();
				// Disk is no longer invalid
				fStoreOutOfSync = false;
			}
		}
	}

	private DiskFlushThread getDiskFlushThread()
	{
		if(fDiskFlushThread == null)
			fDiskFlushThread  = new DiskFlushThread();

		return fDiskFlushThread;
	}

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

			save();
		}
	}

	public URLWaypointDelegate() 
	{

	}


	@Override
	protected void load() 
	{
		// Load the waypoints from disk
		URLSerializer.deSerialize();

		// Start the disk flush thread
		getDiskFlushThread().start();
	}

	protected void save() 
	{
		URLSerializer.serialize();
	}

	@Override
	public void navigate(IWaypoint waypoint) 
	{
		try 
		{
			IWorkbenchBrowserSupport browserSupport = URLWaypointPlugin.getDefault().getWorkbench().getBrowserSupport();
			IWebBrowser browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.NAVIGATION_BAR, null, null, null);

			String urlString = waypoint.getStringValue(URLWaypointUtil.URL_ATTR,"");

			// Check for protocol preamble
			int index = urlString.indexOf("://");

			// No preamble, use default
			if(index < 0)
				urlString = "http://" + urlString;

			URL url = new URL(urlString);
			browser.openURL(url);
		} 
		catch (PartInitException e) 
		{
			e.printStackTrace();
		} 
		catch (MalformedURLException e) 
		{
			String message =  "An unexpected error occured when attempting to open the selected URL waypoint.";
			String reason =  "The URL " + waypoint.getStringValue(URLWaypointUtil.URL_ATTR,"") + " is malformed.";

			ErrorDialog.openError(Display.getDefault().getActiveShell(), 
					"Error", 
					message, 
					new Status(IStatus.ERROR, URLWaypointPlugin.PLUGIN_ID, IStatus.ERROR,reason, null));
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
				if(waypoint.getType().equals(URLWaypointPlugin.WAYPOINT_ID))
					scheduleSave();
			}

			waypoints = event.getOldWaypoints();

			for(IWaypoint waypoint:waypoints)
			{
				if(waypoint.getType().equals(URLWaypointPlugin.WAYPOINT_ID))
					scheduleSave();
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

				if(waypoint.getType().equals(URLWaypointPlugin.WAYPOINT_ID))
					scheduleSave();
			}
		}
	}

	// Schedules a save event
	private void scheduleSave()
	{
		if(!fStoreOutOfSync)
		{
			fStoreOutOfSync = true;

			// Run a disk flush in 10 seconds
		    new Timer().schedule(new TimerTask() {
			
				@Override
				public void run() 
				{
					synchronized (getDiskFlushThread()) 
					{
						getDiskFlushThread().notify();
					}
				}
			
			}, 10000);
		}
	}
}
