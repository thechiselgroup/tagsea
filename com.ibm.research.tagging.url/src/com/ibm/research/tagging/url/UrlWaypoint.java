/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tagging.url;

import java.net.URL;
import java.util.Date;

import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WebBrowserUIPlugin;

import com.ibm.research.tagging.core.impl.AbstractWaypoint;

public class UrlWaypoint extends AbstractWaypoint 
{
	public static final String TYPE = "com.ibm.research.tagging.web.WebWayPoint";
	private String fUrl;
	
	public UrlWaypoint(String url,String description, String author,Date date) 
	{
		super(description,author,date);
		fUrl = url;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypoint#getId()
	 */
	public String getId() 
	{
		return fUrl;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypoint#getType()
	 */
	public String getType() 
	{
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypoint#navigate()
	 */
	public void navigate() 
	{
		try 
		{
			IWorkbenchBrowserSupport browserSupport = WebBrowserUIPlugin.getInstance().getWorkbench().getBrowserSupport();
			IWebBrowser browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.NAVIGATION_BAR, null, null, null);
			URL url = new URL(getURL());
			browser.openURL(url);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the url
	 * @return
	 */
	public String getURL() 
	{
		return fUrl;
	}
}
