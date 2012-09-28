package net.sourceforge.tagsea.url.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.BaseWaypointUI;
import net.sourceforge.tagsea.core.ui.IWaypointUIExtension;
import net.sourceforge.tagsea.url.URLWaypointPlugin;
import net.sourceforge.tagsea.url.favicon.FaviconDownloader;
import net.sourceforge.tagsea.url.favicon.ImageConverter;
import net.sourceforge.tagsea.url.waypoints.URLWaypointUtil;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class URLWaypointUI extends BaseWaypointUI implements IWaypointUIExtension 
{
	/**
	 * The url host->favicon cache
	 */
	private Map<String,Image> fCache;

	/**
	 * Display disposer used to dispose any SWT image obects in the 
	 * favicon cache.
	 */
	private DisplayDisposeRunnable fDisplayDisposeRunnable;

	private class DisplayDisposeRunnable implements Runnable
	{

		public void run() 
		{
			Collection<Image> images = getCache().values();

			for(Image image: images)
				if(image != null && !image.isDisposed())
					image.dispose();
		}
	}


	public URLWaypointUI()
	{
		Display.getDefault().asyncExec(new Runnable() 
		{

			public void run() 
			{
				Display.getDefault().disposeExec(getDisplayDisposeRunnable());
			}
		});
		
		// Start up the favicon dispatcher
		getFaviconThread().start();
	}

	private DisplayDisposeRunnable getDisplayDisposeRunnable()
	{
		if(fDisplayDisposeRunnable == null)
			fDisplayDisposeRunnable  = new DisplayDisposeRunnable();
		
		return fDisplayDisposeRunnable;
	}
	
	private FaviconThread getFaviconThread()
	{
		if(fFaviconThread == null)
			fFaviconThread  = new FaviconThread();
		
		return fFaviconThread;
	}

	/**
	 * The favicon downloader thread
	 */
	private FaviconThread fFaviconThread;
	/**
	 * The queue of pending waypoints
	 */
	private Queue<IWaypoint> fPendingWaypoints;

	private class FaviconThread extends Thread
	{
		@Override
		public void run() 
		{
			// Suspend until waypoints are pending
			while (true)
			{
				try
				{
					synchronized(this) 
					{
						while (getPendingWaypoints().isEmpty())
							wait();
					}
				} 
				catch (InterruptedException e)
				{
				}

				// process the pending waypoints
				while(!getPendingWaypoints().isEmpty())
				{
					// Get the waypoint from the head of the queue
					final IWaypoint waypoint = getPendingWaypoints().poll();

					if(waypoint!= null)
					{
						try 
						{
							String urlString = getUrlString(waypoint);
							final URL url = new URL(urlString);
							final BufferedImage bufferedImage = FaviconDownloader.getInstance().downloadFavicon(url);

							if(bufferedImage!=null)
							{
								Display.getDefault().asyncExec(new Runnable() 
								{
									public void run() 
									{
										// Convert to swt land
										Image image = ImageConverter.convert(bufferedImage);

										if(image != null)
										{
											// Store in cache
											getCache().put(url.getHost(), image);
											
											// Force a ui refresh
											invalidate(waypoint);
										}
									}			
								} );
							}
						} 
						catch (MalformedURLException e){}
						catch (IOException e){}						
					}
				}
			}
		}
	}

	private Map<String,Image> getCache() 
	{
		if(fCache == null)
			fCache = new HashMap<String, Image>();
		return fCache;
	}

	public String getUrlString(IWaypoint waypoint) 
	{
		String urlString = waypoint.getStringValue(URLWaypointUtil.URL_ATTR, "");

		if(urlString.trim().length() <= 0)
			return null;

		// Check for protocol preamble
		int index = urlString.indexOf("://");

		// No preamble, use default
		if(index < 0)
			urlString = "http://" + urlString;

		return urlString;
	}
	
	protected void invalidate(IWaypoint waypoint) 
	{
		//@tag hax -author=Mike -date="enUS:12/27/07" : surely there is a better way of forcing a ui update???
		String author = waypoint.getAuthor();
		waypoint.setAuthor("");
		waypoint.setAuthor(author);
	}

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
		try 
		{
			String urlString = getUrlString(waypoint);
			URL url = new URL(urlString);
			
			// Look for cache hit
			if(getCache().containsKey(url.getHost()))
				return getCache().get(url.getHost());
			
			getPendingWaypoints().offer(waypoint);
			
			synchronized (getFaviconThread()) 
			{
				getFaviconThread().notify();
			}
						
		}
		catch (MalformedURLException e) 
		{
			//e.printStackTrace();
		}
		
		// Return the standard icon for now
		return URLWaypointPlugin.getDefault().getImageRegistry().get(URLWaypointPlugin.IMG_WEB);
	}

	private Queue<IWaypoint> getPendingWaypoints() 
	{
		if(fPendingWaypoints == null)
			fPendingWaypoints = new ConcurrentLinkedQueue<IWaypoint>();

		return fPendingWaypoints;
	}

	public String getLocationString(IWaypoint waypoint) {
		return waypoint.getStringValue(URLWaypointUtil.URL_ATTR, "");
	}
	
	@Override
	public boolean canUIAddTag(IWaypoint waypoint) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean canUIDeleteTag(IWaypoint waypoint) {
		// TODO Auto-generated method stub
		return true;
	}
}
