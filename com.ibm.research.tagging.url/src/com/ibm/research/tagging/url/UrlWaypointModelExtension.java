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
package com.ibm.research.tagging.url;

import com.ibm.research.tagging.core.ITagCore;
import com.ibm.research.tagging.core.IWaypointModelExtension;
import com.ibm.research.tagging.core.TagCorePlugin;

/**
 * 
 * @author mdesmond
 *
 */
public class UrlWaypointModelExtension implements IWaypointModelExtension
{
	private UrlSerializer fSerializer;
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.ITagModelExtension#initialize(com.ibm.research.tagging.core.ITagModel)
	 */
	public void initialize(ITagCore model) 
	{
		fSerializer = new UrlSerializer();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModelExtension#loadWaypoints()
	 */
	public void loadWaypoints() 
	{
		fSerializer.deSerialize(TagCorePlugin.getDefault().getTagCore());
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModelExtension#saveWaypoints()
	 */
	public void saveWaypoints() 
	{
		fSerializer.serialize(TagCorePlugin.getDefault().getTagCore());
	}
}

