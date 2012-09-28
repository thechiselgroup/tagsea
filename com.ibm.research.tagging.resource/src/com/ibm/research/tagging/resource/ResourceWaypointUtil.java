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
import org.eclipse.core.runtime.CoreException;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.TagCorePlugin;

public class ResourceWaypointUtil 
{	
	/**
	 * Apply the given tags to the given waypoint 
	 * @param waypoint
	 * @param tags
	 */
	public static void tag(IWaypoint waypoint, String[] tags)
	{
		if(tags == null)
			return;
		
		for(String tagName : tags)
		{
			if(tagName.trim().length() > 0)
			{
				ITag tag = TagCorePlugin.getDefault().getTagCore().getTagModel().addTag(tagName);
				waypoint.addTag(tag);
			}
		}
	}

	/**
	 * Get the waypoint in the model with the given string
	 * @param id
	 * @return
	 */
	public static IWaypoint getWaypointFromModel(String id)
	{
		IWaypoint waypoint = TagCorePlugin.getDefault().getTagCore().getWaypointModel().getWaypoint(id);
		return waypoint;
	}
	
	/**
	 * Get the first marker of the given type on the given resource
	 * @param markerID
	 * @param resource
	 * @return
	 */
	public static IMarker getFirstMarker(String markerID, IResource resource) 
	{
		try
		{
			IMarker[] markers = resource.findMarkers(markerID, false, IResource.DEPTH_ZERO);
		
			if(markers.length > 0)
				return markers[0];
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
}
