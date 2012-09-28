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

public class DefaultJavaWaypointParser implements IJavaWaypointParser
{
	private static final String KEYWORD            = "@tag";
	
	/**
	 * Very simple waypoint definition parser
	 * Can recover already stored information form eroneous waypoint definitions
	 * 
	 * @author mdesmond
	 * @author amarkus
	 */
	public JavaWaypointInfo parse(String waypointDefinition) throws Exception 
	{
		if(waypointDefinition == null || waypointDefinition.length() == 0)
			throw new Exception();
		
		String keyword = waypointDefinition.substring(0,KEYWORD.length());
		
		if(!keyword.equalsIgnoreCase(KEYWORD))
			throw new Exception();
		
		String strippedWaypointDefinition = waypointDefinition.substring(KEYWORD.length());
		
		JavaWaypointInfo info = new JavaWaypointInfo();
		
		// split on colon
		String[] splitOnColon = strippedWaypointDefinition.split("\\:");
		
		if(splitOnColon.length == 1)
		{
			// Check if nothing exists after the keyword
			if(strippedWaypointDefinition.trim().length() == 0)
				return info;
		}
		
		String tagPart = splitOnColon[0];
	
		// split the tags on whitespace
		String[] tags = tagPart.trim().split("\\s+");
		
		// add the tags
		for(String tagName : tags)
			if(tagName.trim().length() > 0)
				info.getTagList().add(tagName);
		
		// the comment/meta part after the colon
		StringBuffer afterColonBuffer = new StringBuffer();
		
		// Get the region after the first colon
		for(int i = 1 ; i<splitOnColon.length; i++)
		{
			afterColonBuffer.append(splitOnColon[i]);
			
			if(i != splitOnColon.length -1)
				afterColonBuffer.append(":");
		}
		
		
		String afterColon = afterColonBuffer.toString();
		
		// ?something? exists after the colon
		if(afterColon.trim().length() > 0)
		{	
			// split on the first meta open
			String[] splitOnMetaOpen = afterColon.split("\\[");
			
			// check if a description exists after the colon and before the meta open
			if(splitOnMetaOpen[0].trim().length() > 0)
				info.setDescription(splitOnMetaOpen[0].trim());

			// a meta data part exists		
			if(splitOnMetaOpen.length == 2)
			{
				// index of meta close
				int indexOfClose = splitOnMetaOpen[1].lastIndexOf(']');
				
				// close bracket does not exist return what we have
				if(indexOfClose == -1)
					return info;
				
				// get the metadata part
				String metaData = splitOnMetaOpen[1].substring(0,indexOfClose);
				
				// split the name value pairs
				String[] nameValues = metaData.split("\\;");
				
				// for each pair of 'name = value'
				for(String nameValue : nameValues)
				{
					// index of the first equals
					int firstEqualIndex = nameValue.indexOf("=");
				
					// no equals exists in this 'name = value' part so skip it
					if(firstEqualIndex == -1)
							continue;
					
					// Get the name part
					String name = nameValue.substring(0,firstEqualIndex);
					String value;
					
					// see if the 'value' part exists
					if(firstEqualIndex + 1 == nameValue.length())
						value = "";
					else
						value = nameValue.substring(firstEqualIndex + 1);
					
					info.getAttributes().put(name, value);
				}
			}
		}
		
		return info;
	}


}
