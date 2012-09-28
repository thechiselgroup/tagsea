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
package com.ibm.research.tagging.url.wizards;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.ui.wizards.WaypointWizard;
import com.ibm.research.tagging.url.UrlWaypoint;

/**
 * 
 * @author mdesmond
 *
 */
public class NewUrlWaypointWizard extends WaypointWizard 
{
	private final static String WINDOW_TITLE = "New Url Waypoint";

	public NewUrlWaypointWizard() 
	{
		super(WINDOW_TITLE, new NewUrlWaypointPage());
	}

	protected IWaypoint getWaypoint()
	{	
		UrlWaypoint waypoint = null;
		
        try 
        {
			URL url  = new URL(((NewUrlWaypointPage) getPage()).getUrlText().trim());
			String description = getPage().getDescriptionText().trim();
			String author = getPage().getAuthorText().trim();
			
			waypoint = new UrlWaypoint(url.toExternalForm(),description,author,new Date());
			
			TagCorePlugin.getDefault().getTagCore().getWaypointModel().addWaypoint(waypoint);
		} 
        catch (MalformedURLException e) 
        {
        	String errorMessage = "The given url " + (((NewUrlWaypointPage) getPage()).getUrlText().length()>0?("'" + ((NewUrlWaypointPage) getPage()).getUrlText() + "'"):"") + " is invalid. The Url cannot be empty and must begin with a protocol preamble such as http://.";
        	MessageDialog.openError(getShell(),"Error", errorMessage);
			return null;
		}
        
        return waypoint;
	}
}
