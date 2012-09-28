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
package com.ibm.research.tagging.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.IWaypointModel;
import com.ibm.research.tagging.core.IWaypointModelListener;
import com.ibm.research.tagging.core.TagCorePlugin;

/**
 * 
 * @author mdesmond
 *
 */
public class WaypointModel implements IWaypointModel 
{
	private List<IWaypoint> fWaypoints;
	private List<IWaypointModelListener> fListeners;
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModel#addWaypoint(com.ibm.research.tagging.core.IWaypoint)
	 */
	public void addWaypoint(final IWaypoint waypoint) 
	{
		if(!getWaypointList().contains(waypoint))
		{
			fWaypoints.add(waypoint);

			TagCorePlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 	
			{
				public void run() 
				{
					synchronized (getListeners()) 
					{
						for(IWaypointModelListener listener : getListeners())
							listener.waypointAdded(waypoint);
					}
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModel#removeWaypoint(com.ibm.research.tagging.core.IWaypoint)
	 */
	public void removeWaypoint(final IWaypoint waypoint) 
	{
		if(getWaypointList().remove(waypoint))
		{
			ITag[] tags = waypoint.getTags();

			for(ITag tag: tags)
				((Tag)tag).removeWaypoint(waypoint);

			TagCorePlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
			{
				public void run() 
				{
					synchronized (getListeners()) 
					{
						for(IWaypointModelListener listener : getListeners())
							listener.waypointRemoved(waypoint);
					}
				}
			});
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModel#getWaypoints()
	 */
	public IWaypoint[] getWaypoints() 
	{
		IWaypoint[] array = new IWaypoint[0];
		array = getWaypointList().toArray(array);
		return array;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModel#getWaypoint(java.lang.String)
	 */
	public IWaypoint getWaypoint(String id)
	{
		for(IWaypoint waypoint : getWaypointList())
			if(waypoint.getId().equals(id))
				return waypoint;
		
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModel#addWaypointModelListener(com.ibm.research.tagging.core.IWaypointModelListener)
	 */
	public void addWaypointModelListener(IWaypointModelListener listener) 
	{
		synchronized (getListeners()) 
		{
			if(!getListeners().contains(listener))
				getListeners().add(listener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModel#removeWaypointModelListener(com.ibm.research.tagging.core.IWaypointModelListener)
	 */
	public void removeWaypointModelListener(IWaypointModelListener listener) 
	{
		synchronized (getListeners()) 
		{
			getListeners().remove(listener);
		}
	}

	/**
	 * Get the list of waypoints
	 * @return
	 */
	protected List<IWaypoint> getWaypointList() 
	{
		if(fWaypoints == null)
			fWaypoints = new ArrayList<IWaypoint>();
		
		return fWaypoints;
	}
	
	/**
	 * Get the list of waypoints
	 * @return
	 */
	protected List<IWaypointModelListener> getListeners() 
	{
		if(fListeners == null)
			fListeners = new ArrayList<IWaypointModelListener>();
		
		return fListeners;
	}
}
