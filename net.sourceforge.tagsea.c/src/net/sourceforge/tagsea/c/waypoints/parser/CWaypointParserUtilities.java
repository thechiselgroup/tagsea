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
package net.sourceforge.tagsea.c.waypoints.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CWaypointParserUtilities 
{
	public static final String AUTHOR_KEY = "author";
	public static final String DATE_KEY = "date";
	
	public static String getAuthor(ICWaypointInfo info)
	{
		String author = info.getAttributes().get(AUTHOR_KEY);
		
		if(author != null)
			return author;
			
		return new String();
	}
	
	public static Date getDate(ICWaypointInfo info)
	{
		String dateString = info.getAttributes().get(DATE_KEY);
		
		if(dateString != null)
		{
			Date date = null;
			
			try 
			{
				date = SimpleDateFormat.getInstance().parse(dateString);
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
			}
			
			return date;
		}
		
		return null;
	}
}
