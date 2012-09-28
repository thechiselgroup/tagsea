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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.texteditor.MarkerUtilities;

import com.ibm.research.tagging.java.JavaWaypoint;
import com.ibm.research.tagging.java.parser.IJavaWaypointInfo;
import com.ibm.research.tagging.java.parser.IJavaWaypointParser;
import com.ibm.research.tagging.java.parser.JavaWaypointParserFactory;

public class WaypointUtilities 
{
	public static String getWaypointDefinition(IMarker marker)
	{
		return MarkerUtilities.getMessage(marker);
	}
	
	public static IJavaWaypointInfo parse(String waypointDefinition)
	{
		try 
		{
			IJavaWaypointParser parser = JavaWaypointParserFactory.createParser();
			IJavaWaypointInfo info = parser.parse(waypointDefinition);
			return info;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static JavaWaypoint createWaypoint(IMarker marker, IJavaWaypointInfo info)
	{
		Map<String,String> attributes = info.getAttributes();
		
		String description = info.getDescription();
		String author = attributes.get("author");
		String dateString = attributes.get("date");
		
		Date date = null;

		if(dateString != null)
		{
			SimpleDateFormat format = new SimpleDateFormat();
			try 
			{
				date = format.parse(dateString);
			}
			catch (ParseException e) 
			{
				e.printStackTrace();
			}
		}

		return new JavaWaypoint(marker,description,author,date);
	}
}
