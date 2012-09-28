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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.ui.IWaypointUIExtension;

/**
 * 
 * @author mdesmond
 *
 */
public class UrlWaypointUIExtension implements IWaypointUIExtension
{	
	private static final int MAX_VISIBLE_URL_LEN = 60;

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagUIExtension#getImage(com.ibm.research.tagging.core.IWaypoint)
	 */
	public Image getImage(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(UrlWaypoint.TYPE))
			return UrlWaypointPlugin.getDefault().getImageRegistry().get(UrlWaypointPlugin.IMG_WEB);
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagUIExtension#getLabel(com.ibm.research.tagging.core.IWaypoint)
	 */
	public String getLabel(IWaypoint waypoint)
	{
		if(waypoint.getType().equals(UrlWaypoint.TYPE))
		{
			UrlWaypoint webWaypoint = (UrlWaypoint)waypoint;
			
			// would like to see something more descriptive than just the url...
			String label = webWaypoint.getURL();
			
			// some urls can be very long (e.g. forms, Google search queries, Google Maps, wikis, Notes, etc) - truncate for readability
			if ( label.length()>MAX_VISIBLE_URL_LEN )
				label = label.substring(0,MAX_VISIBLE_URL_LEN-3) + "...";
			
			String desc  = webWaypoint.getDescription();
			if ( desc!=null && !desc.trim().equals("") )
				label += " (" + desc +")";
			
			return label;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagUIExtension#getSmallImage(com.ibm.research.tagging.core.IWaypoint)
	 */
	public Image getSmallImage(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(UrlWaypoint.TYPE))
			return UrlWaypointPlugin.getDefault().getImageRegistry().get(UrlWaypointPlugin.IMG_WEB);
		
		return null;
	}

	public boolean editProperties(IWaypoint waypoint) {
		// TODO Auto-generated method stub
		return false;
	}

	public FormText getProperties(Composite parent, IWaypoint waypoint) {
		// TODO Auto-generated method stub
		return null;
	}

	public FormText getToolTipText(IWaypoint waypoint) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ui.IWaypointUIExtension#getLabel(com.ibm.research.tagging.core.IWaypoint, int)
	 */
	public String getLabel(IWaypoint waypoint, int column) {
		if(waypoint.getType().equals(UrlWaypoint.TYPE))
		{
			UrlWaypoint webWaypoint = (UrlWaypoint)waypoint;
			
			if ( column==0 )
			{
				return webWaypoint.getURL();
			}
			else if ( column==1 )
			{
				return webWaypoint.getDescription();
			}
		}
		return null;
	}
}
