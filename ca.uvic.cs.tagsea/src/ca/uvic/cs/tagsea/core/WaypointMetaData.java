/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.core;

import java.util.HashMap;

/**
 * Contains all the metadata for a waypoint
 * 
 * @author mdesmond
 */
public class WaypointMetaData 
{
	public static final String AUTHOR   = "author";
	public static final String DATE 	= "date";
	public static final String COMMENT  = "comment";
	
	private HashMap<String,String> fAttributes;
	
	public WaypointMetaData()
	{
	}
	
	public WaypointMetaData(String[] names, String[] values)
	{
		for (int i = 0; i < names.length; i++) 
		{
			String name = names[i].toLowerCase();
			getAttributes().put(name, values[i]);
		}
	}
	
	/**
	 * @param key
	 * @return the value or null if none exists
	 */
	public String getAttribute(String key)
	{
		return getAttributes().get(key);
	}
	
	public String getAuthor() 
	{
		return getAttribute(AUTHOR);
	}
	
	public String getComment()
	{
		return getAttribute(COMMENT);
	}
	
	public String getDate() 
	{
		return getAttribute(DATE);
	}
	
	public void setAuthor(String value) 
	{
		getAttributes().put(AUTHOR, value);
	}
	
	public void setComment(String value)
	{
		getAttributes().put(COMMENT, value);
	}
	
	public void setDate(String value) 
	{
		getAttributes().put(DATE, value);
	}

	/**
	 * @return The attribute map
	 */
	private HashMap<String,String> getAttributes()
	{
		if(fAttributes == null)
			fAttributes = new HashMap<String, String>();
		
		return fAttributes;
	}
}
