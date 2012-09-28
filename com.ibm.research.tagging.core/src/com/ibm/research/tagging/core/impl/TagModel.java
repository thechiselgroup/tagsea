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
import com.ibm.research.tagging.core.ITagModel;
import com.ibm.research.tagging.core.ITagModelListener;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.TagCorePlugin;

/**
 * @author mdesmond
 */
public class TagModel implements ITagModel
{
	private List<ITag> fTags; 
	private List<ITagModelListener> fListeners; 

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModel#addTag(java.lang.String)
	 */
	public ITag addTag(String name) 
	{
		ITag tag = getTag(name);

		if(tag == null)
		{
			tag = new Tag(name);
			getTagList().add(tag);

			final ITag _tag = tag;
			TagCorePlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
			{
				public void run() 
				{
					for(ITagModelListener listener : getListeners())
						listener.tagAdded(_tag);
				}
			});
		}
		
		return tag;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModel#removeTag(com.ibm.research.tagging.core.ITag)
	 */
	public void removeTag(final ITag tag) 
	{
		if(getTagList().remove(tag))
		{
			final IWaypoint[] waypoints = tag.getWaypoints();

			for(IWaypoint waypoint : waypoints)
				waypoint.removeTag(tag);

			TagCorePlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
			{
				public void run() 
				{
					for(ITagModelListener listener : getListeners())
						listener.tagRemoved(tag,waypoints);
				
				}
			});
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModel#renameTag(com.ibm.research.tagging.core.ITag, java.lang.String)
	 */
	public void renameTag(final ITag tag, String newName) 
	{
		if(getTagList().contains(tag))
		{
			if(newName!=null && newName.length() > 0)
			{
				final String oldName = tag.getName();
				
				// See if we have an existing tag with the new name
				ITag existingTag = getTag(newName);
				
				if(existingTag == null)
				{
					// No name clash so execute the rename
					Tag concreteTag = (Tag)tag;
					concreteTag.setName(newName);
				}
				else
				{
					//There was a name clash so add the waypoints to the clashee
					// @tag todo critical : allow batching in the core model
					IWaypoint[] waypoints = tag.getWaypoints();
					
					// transfer waypoints to the existing tag
					for(IWaypoint waypoint : waypoints)
						waypoint.addTag(existingTag);
					
					// remove the old tag
					removeTag(tag);	
					
					// rename operation is not executed
					return;
				}
				
				TagCorePlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
				{
					
					public void run() 
					{
						for(ITagModelListener listener : getListeners())
							listener.tagRenamed(tag,oldName);
					}
				});
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModel#getTags()
	 */
	public ITag[] getTags() 
	{
		ITag[] array = new ITag[0];
		array = getTagList().toArray(array);
		return array;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModel#addTagModelListener(com.ibm.research.tagging.core.ITagModelListener)
	 */
	public synchronized void addTagModelListener(ITagModelListener listener) 
	{
		if(!getListeners().contains(listener))
			getListeners().add(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModel#removeTagModelListener(com.ibm.research.tagging.core.ITagModelListener)
	 */
	public synchronized void removeTagModelListener(ITagModelListener listener) 
	{
		getListeners().remove(listener);
	}
	
	/**
	 * Get the listeners
	 * @return the listeners
	 */
	protected List<ITagModelListener> getListeners()
	{
		if(fListeners == null)
			fListeners = new ArrayList<ITagModelListener>();
		
		return fListeners;
	}
	
	/**
	 * Get the internal routes list
	 * @return the listeners
	 */
	protected List<ITag> getTagList()
	{
		if(fTags == null)
			fTags = new ArrayList<ITag>();
		
		return fTags;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModel#getTag(java.lang.String)
	 */
	public ITag getTag(String name) 
	{
		String searchName = name.trim();
		
		for(ITag tag : getTagList())
			if(tag.getName().equals(searchName))
				return tag;
		
		return null;
	}
}
