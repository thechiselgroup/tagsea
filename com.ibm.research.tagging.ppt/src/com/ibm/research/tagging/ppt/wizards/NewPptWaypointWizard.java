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

package com.ibm.research.tagging.ppt.wizards;

import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.ui.wizards.WaypointWizard;
import com.ibm.research.tagging.ppt.PptWaypoint;

public class NewPptWaypointWizard extends WaypointWizard 
{
	private final static String WINDOW_TITLE = "New Powerpoint Waypoint";
	private IResource fResource;

	public NewPptWaypointWizard(IResource resource, int numberOfSlides) 
	{
		super(WINDOW_TITLE,new NewPptWaypointPage(numberOfSlides));
		assert(numberOfSlides>0);
		fResource = resource;
	}

	protected IWaypoint getWaypoint() {
		
		PptWaypoint waypoint = null;
		
		if(fResource.exists())
		{
			int startSlide = ((NewPptWaypointPage) getPage()).getStartSlide();
			int endSlide = ((NewPptWaypointPage) getPage()).getEndSlide();
			String description = getPage().getDescriptionText();
			String author = getPage().getAuthorText();
			
			try 
			{
				waypoint = new PptWaypoint(fResource,startSlide,endSlide,description,author,new Date());
				
				TagCorePlugin.getDefault().getTagCore().getWaypointModel().addWaypoint(waypoint);
				
				// flush the waypoint to the marker
				waypoint.save();
			}
			catch (CoreException e) 
			{
				e.printStackTrace();
				MessageDialog.openError(getShell(),WINDOW_TITLE,"An error was encountered when creating the powerpoint slide waypoint.");
			}
		}
		else
			MessageDialog.openError(getShell(),WINDOW_TITLE,"An error was encountered when creating the powerpoint slide waypoint. The slide show no longer exists in your workspace.");
		
		return waypoint;
	}
}
