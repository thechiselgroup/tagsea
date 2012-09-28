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

package com.ibm.research.tagging.java.markers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.texteditor.MarkerUtilities;

import com.ibm.research.tagging.java.parser.IJavaWaypointInfo;
import com.ibm.research.tagging.java.parser.IJavaWaypointParser;
import com.ibm.research.tagging.java.parser.JavaWaypointParserFactory;

public class WaypointMarkerParser 
{
	public WaypointMarkerParser()
	{
		
	}
	
	public IJavaWaypointInfo parse(IMarker marker) throws Exception
	{
		String waypointDefinition = MarkerUtilities.getMessage(marker);
		IJavaWaypointParser parser = JavaWaypointParserFactory.createParser();
		IJavaWaypointInfo info = parser.parse(waypointDefinition);
		return info;
	}
	
	public IJavaWaypointInfo[] parse(List<IMarker> markers)
	{
		List<IJavaWaypointInfo> javaWaypointInfoList = new ArrayList<IJavaWaypointInfo>();
		
		for(IMarker marker : markers)
		{
			IJavaWaypointInfo info;
			
			try 
			{
				info = parse(marker);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				continue;
			}
			
			javaWaypointInfoList.add(info);
		}
		
		return javaWaypointInfoList.toArray(new IJavaWaypointInfo[0]);
	}
}
