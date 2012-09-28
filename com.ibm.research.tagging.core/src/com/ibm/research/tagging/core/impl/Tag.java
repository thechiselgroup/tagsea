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
import com.ibm.research.tagging.core.ITagListener;
import com.ibm.research.tagging.core.IWaypoint;


/**
 * @author mdesmond
 */
public class Tag implements ITag
{
	private String fName;
	private List<IWaypoint> fWaypoints;
	private List<ITagListener> fListeners;

	/**
	 * Generic support for Tags
	 * @param parent
	 * @param name
	 */
	public Tag(String name)
	{
		setName(name);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITag#getName()
	 */
	public String getName()
	{
		return fName;
	}

	/**
	 * Set the name
	 * @param name
	 */
	public void setName(String name)
	{
		fName = name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITag#getWaypoints()
	 */
	public IWaypoint[] getWaypoints()
	{
		IWaypoint[] waypointArray = new IWaypoint[0];
		waypointArray = getWaypointList().toArray(waypointArray);
		return waypointArray;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITag#getWaypointCount()
	 */
	public int getWaypointCount()
	{
		return getWaypointList().size();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITag#addWaypoint(com.ibm.research.tagging.core.IWaypoint)
	 */
	protected void addWaypoint(final IWaypoint waypoint) 
	{
		if(!getWaypointList().contains(waypoint))
		{
			getWaypointList().add(waypoint);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITag#removeWaypoint(com.ibm.research.tagging.core.IWaypoint)
	 */
	protected void removeWaypoint(final IWaypoint waypoint) 
	{
		getWaypointList().remove(waypoint);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITag#addTagListener(com.ibm.research.tagging.core.ITagListener)
	 */
	public void addTagListener(ITagListener tagListener)
	{
		if(!getListeners().contains(tagListener))
			getListeners().add(tagListener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITag#removeTagListener(com.ibm.research.tagging.core.ITagListener)
	 */
	public void removeTagListener(ITagListener tagListener) 
	{
		getListeners().remove(tagListener);
	}
	
	/**
	 * Gte the listeners
	 * @return the listeners
	 */
	protected List<ITagListener> getListeners()
	{
		if(fListeners == null)
			fListeners = new ArrayList<ITagListener>();
		
		return fListeners;
	}
	
	/**
	 * Gte the listeners
	 * @return the listeners
	 */
	protected List<IWaypoint> getWaypointList()
	{
		if(fWaypoints == null)
			fWaypoints = new ArrayList<IWaypoint>();
		
		return fWaypoints;
	}
	
	@Override
	public String toString()
	{
		return "Tag [name = " + getName() + "]";
	}
}
