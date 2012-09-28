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
package com.ibm.research.tagging.java.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IRegion;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.java.JavaWaypoint;

public class WaypointUtil
{
	public static void sortByAscendingOffset(List<IRegion> regions)
	{
		Collections.sort(regions,new Comparator<IRegion>() 
		{
			public int compare(IRegion a, IRegion b) 
			{
				return b.getOffset() - a.getOffset();
			}
		});
	}
	
	public static Map<IResource, List<JavaWaypoint>> sortByResource(IWaypoint[] waypoints)
	{
		Map<IResource, List<JavaWaypoint>> map = new HashMap<IResource, List<JavaWaypoint>>();
		
		for(IWaypoint waypoint : waypoints)
		{
			if(waypoint instanceof JavaWaypoint)
			{
				JavaWaypoint javaFileWaypoint = (JavaWaypoint)waypoint;
				IResource resource = javaFileWaypoint.getMarker().getResource();
				List<JavaWaypoint> list = map.get(resource);
				
				if(list == null)
				{
					list = new ArrayList<JavaWaypoint>();
					map.put(resource, list);
				}
				
				list.add(javaFileWaypoint);
			}
		}
		return map;
	}
	
	public static Map<IResource, List<IMarker>> sortMarkersByResource(IMarker[] markers)
	{
		Map<IResource,List<IMarker>> map = new HashMap<IResource,List<IMarker>>(); 

		for(IMarker marker : markers)
		{
			IResource resource = marker.getResource();
			List<IMarker> markerList = map.get(resource);

			if(markerList == null)
			{
				markerList = new ArrayList<IMarker>();
				map.put(resource, markerList);
			}

			markerList.add(marker);
		}
		
		return map;
	}
}
