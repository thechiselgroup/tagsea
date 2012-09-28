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
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.ui.TagUIPlugin;
import com.ibm.research.tagging.core.ui.adapters.StructuredContentProviderAdapter;
import com.ibm.research.tagging.core.ui.tags.TagView;

/**
 * 
 * @author mdesmond
 *
 */
public class WaypointTableContentProvider extends StructuredContentProviderAdapter
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) 
	{	
		TagView view = TagUIPlugin.getDefault().getTagView();
		
		if(view != null)
		{
			TableViewer viewer = view.getTagTableViewer();
			IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
			Set<IWaypoint> result = new HashSet<IWaypoint>();
			List tagSelection = selection.toList(); 
			
			if(tagSelection.size() == 0)
				return getAllWaypoints();
			
			for(Object o : tagSelection)
			{
				ITag tag = (ITag)o;
				IWaypoint[] waypoints = tag.getWaypoints();

				for(IWaypoint waypoint : waypoints)
					result.add(waypoint);
			}
			
			return result.toArray();
		}
		else
			return getAllWaypoints();
	}

	
	/**
	 * Get all waypoints
	 * @return
	 */
	private Object[] getAllWaypoints()
	{
		return TagCorePlugin.getDefault().getTagCore().getWaypointModel().getWaypoints();
	}
}
