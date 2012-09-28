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

package com.ibm.research.tagging.resource;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.ITagModelListener;
import com.ibm.research.tagging.core.ITagCore;
import com.ibm.research.tagging.core.IWaypointModelExtension;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.IWaypointModelListener;
import com.ibm.research.tagging.core.TagCorePlugin;

public class ResourceWaypointModelExtension implements IWaypointModelExtension, ITagModelListener, IWaypointModelListener
{
	private ResourceWaypointResourceChangeListener resourceWaypointResourceChangeListener = new ResourceWaypointResourceChangeListener();
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModelExtension#initialize(com.ibm.research.tagging.core.ITagModel)
	 */
	public void initialize(ITagCore model) 
	{
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceWaypointResourceChangeListener);
		model.getTagModel().addTagModelListener(this);
		model.getWaypointModel().addWaypointModelListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModelExtension#loadWaypoints()
	 */
	public void loadWaypoints() 
	{
		// Load the resource waypoints
		try 
		{
			IResource root = ResourcesPlugin.getWorkspace().getRoot();
			IMarker[] markers = root.findMarkers(ResourceWaypoint.MARKER_ID,false,IResource.DEPTH_INFINITE);

			for(IMarker marker : markers)
			{
				ResourceWaypoint waypoint = new ResourceWaypoint(marker);
				waypoint.load();
				TagCorePlugin.getDefault().getTagCore().getWaypointModel().addWaypoint(waypoint);
			}
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
		catch (InvalidMarkerTypeException e) 
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModelExtension#saveWaypoints()
	 */
	public void saveWaypoints() 
	{
		IWaypoint[] waypoints = TagCorePlugin.getDefault().getTagCore().getWaypointModel().getWaypoints();
		
		for(IWaypoint waypoint : waypoints)
		{
			if(waypoint.getType().equals(ResourceWaypoint.TYPE))
			{
				try
				{
					((ResourceWaypoint)waypoint).save();
				} 
				catch (CoreException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagCollectionListener#tagAdded(com.ibm.research.tagging.core.ITag)
	 */
	public void tagAdded(ITag tag) 
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagCollectionListener#tagRemoved(com.ibm.research.tagging.core.ITag, com.ibm.research.tagging.core.IWaypoint[])
	 */
	public void tagRemoved(ITag tag, IWaypoint[] waypoints) 
	{
		for(IWaypoint waypoint : waypoints)
		{
			if(waypoint.getType().equals(ResourceWaypoint.TYPE))
			{
				ITag[] tags = waypoint.getTags();
				
				if(tags.length == 0)
				{
					TagCorePlugin.getDefault().getTagCore().getWaypointModel().removeWaypoint(waypoint);
					((ResourceWaypoint)waypoint).delete();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagCollectionListener#tagRenamed(com.ibm.research.tagging.core.ITag, java.lang.String)
	 */
	public void tagRenamed(ITag tag, String oldName) 
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointCollectionListener#waypointAdded(com.ibm.research.tagging.core.IWaypoint)
	 */
	public void waypointAdded(IWaypoint waypoint) 
	{
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointCollectionListener#waypointRemoved(com.ibm.research.tagging.core.IWaypoint)
	 */
	public void waypointRemoved(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(ResourceWaypoint.TYPE))
			((ResourceWaypoint)waypoint).delete();
	}
}
