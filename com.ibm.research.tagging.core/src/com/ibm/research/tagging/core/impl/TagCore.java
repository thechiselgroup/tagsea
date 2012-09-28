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

import com.ibm.research.tagging.core.ITagCore;
import com.ibm.research.tagging.core.ITagModel;
import com.ibm.research.tagging.core.IWaypointModel;
import com.ibm.research.tagging.core.IWaypointModelExtension;

/**
 * 
 * @author mdesmond
 *
 */
public class TagCore implements ITagCore
{
	private TagModel fTagCollection;
	private WaypointModel fWaypointCollection;
	private List<IWaypointModelExtension> fExtensions;
	private TagSerializer fTagSerializer;
	
	public TagCore()
	{
		fTagCollection = new TagModel();
		fWaypointCollection = new WaypointModel();
	}

	/**
	 * Start the tag Model, this should be called in a non ui thread
	 * as to not hang the workspace ui
	 */
	public void start()
	{
		getTagSerializer().deSerialize(getTagModel());
		
		long startTime = System.currentTimeMillis();
		
		for(IWaypointModelExtension extension : getExtensions())
		{
			extension.initialize(this);
			System.out.println("Initialized extension " + extension.getClass() + " in roughly " + (System.currentTimeMillis() - startTime) + " ms");
			startTime = System.currentTimeMillis();
		}
		
		startTime = System.currentTimeMillis();
		
		for(IWaypointModelExtension extension : getExtensions())
		{
			extension.loadWaypoints();
			System.out.println("Loaded waypoints for " + extension.getClass() + " in roughly " + (System.currentTimeMillis() - startTime) + " ms");
			startTime = System.currentTimeMillis();
		}
	}
	
	/**
	 * Shut down the tag Model
	 */
	public void stop()
	{
		getTagSerializer().serialize(getTagModel());
		
		for(IWaypointModelExtension extension : getExtensions())
			extension.saveWaypoints();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModel#getTagCollection()
	 */
	public ITagModel getTagModel() 
	{
		return fTagCollection;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModel#getWaypointCollection()
	 */
	public IWaypointModel getWaypointModel() 
	{
		return fWaypointCollection;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModel#addTagModelExtension(com.ibm.research.tagging.core.ITagModelExtension)
	 */
	public void addWaypointModelExtension(IWaypointModelExtension extension) 
	{
		if(!getExtensions().contains(extension))
			getExtensions().add(extension);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModel#removeTagModelExtension(com.ibm.research.tagging.core.ITagModelExtension)
	 */
	public void removeWaypointModelExtension(IWaypointModelExtension extension) 
	{
		getExtensions().remove(extension);
	}

	/**
	 * Get the extension list
	 * @return
	 */
	protected List<IWaypointModelExtension> getExtensions() 
	{
		if(fExtensions == null)
			fExtensions = new ArrayList<IWaypointModelExtension>();
		return fExtensions;
	}

	
	/**
	 * Get the tag serializer
	 * @return
	 */
	protected TagSerializer getTagSerializer() 
	{
		if(fTagSerializer == null)
			fTagSerializer = new TagSerializer();
		
		return fTagSerializer;
	}
}
