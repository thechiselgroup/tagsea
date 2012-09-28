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
package com.ibm.research.tagging.core.ui.waypoints;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.TagCorePlugin;

/**
 * 
 * @author mdesmond
 *
 */
public class WaypointViewListener implements IWaypointViewListener {

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.ui.waypoints.IWaypointViewListener#deleteWaypoint(com.ibm.research.tagging.ui.waypoints.WaypointView)
	 */
	public void deleteWaypoint(WaypointView view) 
	{
		IStructuredSelection waypointSelection = (IStructuredSelection)view.getWaypointTableViewer().getSelection();
		
		boolean doIt = true;
		
	    if (!MessageDialog.openQuestion(view.getSite().getShell(), "Waypoint View", "Are you sure you want to delete the selected waypoints?")) 
	    	doIt = false;			

		if(doIt)
		{
			for(Object o : waypointSelection.toArray())
			{
				IWaypoint waypoint = (IWaypoint)o;
				TagCorePlugin.getDefault().getTagCore().getWaypointModel().removeWaypoint(waypoint);
			}
		}
	}
}
