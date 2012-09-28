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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.ui.wizards.WaypointPage;
import com.ibm.research.tagging.url.UrlWaypoint;
import com.ibm.research.tagging.url.UrlWaypointPlugin;

/**
 * 
 * @author mdesmond
 *
 */
public class NewUrlWaypointPage extends WaypointPage 
{	
	private final static String WIZARD_ICON = "icons/WebWizardIcon.png";
	
	private final static String PAGE_TITLE = "Url Waypoint";
	private final static String PAGE_DESCRIPTION = "Fill in the provided fields to create a new url waypoint.";
	
	private final static String URL_LABEL_TEXT = "Url:";
	private Text fUrlText;
	
	protected NewUrlWaypointPage() 
	{
		super(PAGE_TITLE,PAGE_DESCRIPTION,UrlWaypointPlugin.getImageDescriptor(WIZARD_ICON));
	}


	public String getUrlText() {
		return fUrlText.getText();
	}

	protected void createPageContents(Composite parent) {
		
		createLabel(parent, SWT.LEFT, URL_LABEL_TEXT);
		fUrlText = createCustomText(parent);
	    fUrlText.addModifyListener(new ModifyListener() 
	    {
			public void modifyText(ModifyEvent e) 
			{
				urlChanged();
			}
		});

	    super.createPageContents(parent);
	}
	
	protected void urlChanged() 
	{
		IWaypoint[] waypoints = TagCorePlugin.getDefault().getTagCore().getWaypointModel().getWaypoints();
		
		for(IWaypoint waypoint : waypoints)
		{
			if(waypoint.getType().equals(UrlWaypoint.TYPE))
			{
				String url = ((UrlWaypoint)waypoint).getURL();
				
				if(url.equalsIgnoreCase(getUrlText()))
				{
					setErrorMessage("A Url waypoint with the given Url already exists.");
					setPageComplete(false);
					return;
				}
			}
		}
		setErrorMessage(null);
		setPageComplete(true);
	}
}