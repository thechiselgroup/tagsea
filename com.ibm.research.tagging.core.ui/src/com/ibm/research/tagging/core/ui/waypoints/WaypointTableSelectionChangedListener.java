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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.ui.TagUIPlugin;

/**
 * 
 * @author mdesmond
 *
 */
public class WaypointTableSelectionChangedListener implements ISelectionChangedListener 
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) 
	{
		IStructuredSelection structuredSelection = (IStructuredSelection)event.getSelection();
		Object[] selection = structuredSelection.toArray();
		
		WaypointView view = TagUIPlugin.getDefault().getWaypointView();
		
		if(view != null)
		{
			// If a single item is selected then populate the collapsable sections
			if(selection.length > 0)
			{
				Set<ITag> tags = new HashSet<ITag>();
				
				for(Object selected : selection)
				{
					IWaypoint waypoint = (IWaypoint)selected;
					for(ITag tag : waypoint.getTags())
						tags.add(tag);					
				}
				
				view.setTags(tags.toArray(new ITag[0]));
				
				if(selection.length == 1)
					view.setSelectedWaypoint((IWaypoint)selection[0]);
				else
					view.clearSelectedWaypoint();
			}
			else
			{
				view.clearTags();
				view.clearSelectedWaypoint();
			}
			
			view.refreshSections();
		}
	}
}
