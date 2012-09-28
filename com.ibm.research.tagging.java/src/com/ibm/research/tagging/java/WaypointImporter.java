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
package com.ibm.research.tagging.java;

import java.util.Date;

import org.eclipse.core.resources.IMarker;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.java.markers.WaypointMarkerParser;
import com.ibm.research.tagging.java.parser.IJavaWaypointInfo;
import com.ibm.research.tagging.java.parser.JavaWaypointParserUtilities;

public class WaypointImporter 
{
	public WaypointImporter()
	{
	}

	public void importWaypoints(IMarker[] markers) 
	{	
		WaypointMarkerParser parser = new WaypointMarkerParser();
		
		for(IMarker marker : markers)
		{
			IJavaWaypointInfo info;
			
			try 
			{
				info = parser.parse(marker);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				continue;
			}
			
			String author = JavaWaypointParserUtilities.getAuthor(info);
			Date date = JavaWaypointParserUtilities.getDate(info);
			JavaWaypoint waypoint = new JavaWaypoint(marker,info.getDescription(),author,date);
			
			// Always add java waypoints via the proxy
			JavaWaypointPlugin.getDefault().addWaypoint(waypoint);
			
			for(String tagName : info.getTags())
			{
				ITag tag = TagCorePlugin.getDefault().getTagCore().getTagModel().addTag(tagName);
				waypoint.addTag(tag);
			}
		}
	}
}
