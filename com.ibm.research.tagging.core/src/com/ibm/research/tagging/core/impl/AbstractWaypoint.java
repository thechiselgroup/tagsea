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
import java.util.Date;
import java.util.List;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.IWaypointListener;
import com.ibm.research.tagging.core.TagCorePlugin;


/**
 * @author mdesmond
 * @tag waypoint.core :  [Author = mdesmond;Date = 6/30/06 2:09 PM;]
 */
public abstract class AbstractWaypoint implements IWaypoint
{
	protected String fAuthor;
	protected Date fDate;
	protected String fDescription;
	private List<ITag> fTags;
	private List<IWaypointListener> fListeners;
	
	/**
	 *
	 */
	public AbstractWaypoint()
	{

	}
	
	/**
	 * Generic waypoint
	 * @param author
	 * @param comment
	 */
	public AbstractWaypoint(String description,String author,Date date)
	{
		fDescription = description;
		fAuthor = author;
		fDate = date;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypoint#getDescription()
	 */
	public String getDescription() 
	{
		if(fDescription == null)
			fDescription = new String();
		
		return fDescription;
	}
	
	public void setDescription(String description) 
	{
		fDescription = description;
	}
	
	public void setAuthor(String author) 
	{
		fAuthor= author;
	}
	
	public void setDate(Date date) 
	{
		fDate = date;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypoint#getAuthor()
	 */
	public String getAuthor()
	{
		if(fAuthor == null)
			fAuthor = new String();
		
		return fAuthor;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypoint#getDate()
	 */
	public Date getDate() 
	{	
		return fDate;
	}
	
	@Override
	public String toString() 
	{
		return "Waypoint [" + getId() + "]";
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypoint#addTag(com.ibm.research.tagging.core.ITag)
	 */
	public void addTag(final ITag tag) 
	{
		if(!getTagList().contains(tag))
		{
			getTagList().add(tag);
			
			((Tag)tag).addWaypoint(this);
			
			TagCorePlugin.getDefault().getEventDispatcher().dispatch(new Runnable() {
			
				public void run() 
				{
					for(IWaypointListener listener : getListeners())
						listener.tagAdded(AbstractWaypoint.this,tag);

				}
			});
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypoint#removeTag(com.ibm.research.tagging.core.ITag)
	 */
	public void removeTag(final ITag tag) 
	{
		boolean removed = getTagList().remove(tag);
		
		if(removed)
		{
			((Tag)tag).removeWaypoint(this);
			
			TagCorePlugin.getDefault().getEventDispatcher().dispatch(new Runnable() {
				
				public void run() 
				{
					for(IWaypointListener listener : getListeners())
						listener.tagRemoved(AbstractWaypoint.this,tag);
				}
			
			});
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypoint#getTags()
	 */
	public ITag[] getTags() 
	{
		ITag[] array = new ITag[0];
		array = getTagList().toArray(array);
		return array;
	}
	
	public void addWaypointListener(IWaypointListener listener)
	{
		if(!getListeners().contains(listener))
			getListeners().add(listener);
	}
	
	public synchronized void removeWaypointListener(IWaypointListener listener)
	{
		getListeners().remove(listener);
	}
	
	
	/**
	 * Get the tag list
	 * @return
	 */
	protected List<IWaypointListener> getListeners()
	{
		if(fListeners == null)
			fListeners = new ArrayList<IWaypointListener>();
		
		return fListeners;
	}
	
	/**
	 * Get the tag list
	 * @return
	 */
	protected List<ITag> getTagList()
	{
		if(fTags == null)
			fTags = new ArrayList<ITag>();
		
		return fTags;
	}
	
	/**
	 * Fire a waypoint changed event, use wisely, the UI and others are listening
	 */
	protected void fireWaypointChanged()
	{
		TagCorePlugin.getDefault().getEventDispatcher().dispatch(new Runnable() {
			
			public void run() 
			{
				for(IWaypointListener listener : getListeners())
					listener.waypointChanged(AbstractWaypoint.this);
			}
		});
	}
}
