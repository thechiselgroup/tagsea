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

package com.ibm.research.tagging.java.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaWaypointInfo implements IJavaWaypointInfo
{
	List<String> fTags;
	String fDescription;
	Map<String,String> fAttributes;
	
	public List<String> getTagList()
	{
		if(fTags == null)
			fTags = new ArrayList<String>();
		
		return fTags;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.java.parser.IJavaWaypointInfo#getAttributes()
	 */
	public Map<String,String> getAttributes()
	{
		if(fAttributes == null)
			fAttributes = new HashMap<String,String>();
		
		return fAttributes;
	}
	
	/**
	 * Set the description assocated with this waypoint
	 * @param description
	 */
	public void setDescription(String description)
	{
		fDescription = description;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.java.parser.IJavaWaypointInfo#getDescription()
	 */
	public String getDescription() 
	{
		if(fDescription == null)
			fDescription = new String();
		
		return fDescription;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.java.parser.IJavaWaypointInfo#getTags()
	 */
	public String[] getTags() 
	{
		String[] array = new String[0];
		return getTagList().toArray(array);
	}
	
	@Override
	public String toString() 
	{
		StringBuffer result = new StringBuffer();
		result.append("Tags [");
		for(String tag : getTags())
			result.append("<" + tag + ">");
		result.append("]\n");
		result.append("Description [" + (fDescription!=null?fDescription:"") + "]\n");
		result.append("Metadata [" + getAttributes().toString() + "]");
		return result.toString();
	}
}
