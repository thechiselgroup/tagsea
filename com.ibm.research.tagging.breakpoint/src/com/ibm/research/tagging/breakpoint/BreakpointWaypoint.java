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
package com.ibm.research.tagging.breakpoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.impl.AbstractWaypoint;

public class BreakpointWaypoint extends AbstractWaypoint
{
	public static final String TYPE = "com.ibm.research.tagging.breakpoint.BreakPointWaypoint";
	
	public static final String BREAKPOINT_TAG           = "breakpoint";
	public static final String DEBUG_TAG                = "debug";
	
	public static final String MARKER_ATTR_AUTHOR       = "com.ibm.research.tagging.breakpoint.author";
	public static final String MARKER_ATTR_DESCRIPTION  = "com.ibm.research.tagging.breakpoint.description";
	public static final String MARKER_ATTR_DATE 		= "com.ibm.research.tagging.breakpoint.date";	public static final String MARKER_ATTR_TAGS 		= "com.ibm.research.tagging.breakpoint.tags";

	public static final String MARKER_TAG_DELIMETER 	= " ";
	
	private IBreakpoint fBreakpoint;
	
	public BreakpointWaypoint(IBreakpoint breakpoint)
	{
		super(null,null,null);
		fBreakpoint = breakpoint;		
	}
	
	public void load()
	{
		loadAuthorFromMarker();
		loadDescriptionFromMarker();
		loadDateFromMarker();
		loadTagsFromMarker();
	}
	
	private void loadTagsFromMarker() 
	{
		String tagString = getBreakpoint().getMarker().getAttribute(MARKER_ATTR_TAGS, null);
		String[] tags = new String[0];
		
		if(tagString!=null && tagString.trim().length() > 0)
			tags = tagString.split(MARKER_TAG_DELIMETER);
		
		for(String tagName : tags)
		{
			ITag tag = TagCorePlugin.getDefault().getTagCore().getTagModel().addTag(tagName);
			addTag(tag);
		}
	}

	private void loadDateFromMarker() 
	{
		String dateString = getBreakpoint().getMarker().getAttribute(MARKER_ATTR_DATE, null);
		SimpleDateFormat format = new SimpleDateFormat();

		if(dateString != null && dateString.length() > 0)
		{
			try 
			{
				fDate = format.parse(dateString);
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	private void loadDescriptionFromMarker() 
	{
		fDescription = getBreakpoint().getMarker().getAttribute(MARKER_ATTR_DESCRIPTION, null);
	}

	private void loadAuthorFromMarker() 
	{
		fAuthor = getBreakpoint().getMarker().getAttribute(MARKER_ATTR_AUTHOR, null);
	}
	
	/**
	 * Save this waypoint to persistent storage
	 * @throws CoreException
	 */
	public void save() throws CoreException
	{
		saveAuthorToMarker();
		saveDescriptionToMarker();
		saveDateToMarker();
		saveTagsToMarker();
	}

	
	protected void saveAuthorToMarker() throws CoreException
	{
		getBreakpoint().getMarker().setAttribute(MARKER_ATTR_AUTHOR, getAuthor());
	}
	
	protected void saveDescriptionToMarker() throws CoreException
	{
		getBreakpoint().getMarker().setAttribute(MARKER_ATTR_DESCRIPTION, getDescription());
	}
	
	protected void saveDateToMarker() throws CoreException
	{
		if(getDate()!=null)
		{
			String dateString = SimpleDateFormat.getInstance().format(getDate());
			getBreakpoint().getMarker().setAttribute(MARKER_ATTR_DATE, dateString);
		}
	}
	
	protected void saveTagsToMarker() throws CoreException
	{
		ITag[] tags = getTags();
		
		if(tags.length >0)
		{
			StringBuffer buffer = new StringBuffer();
			
			for(ITag tag : tags)
			{
				buffer.append(tag.getName() + MARKER_TAG_DELIMETER);
			}
			
			String tagString = buffer.toString().trim();
			
			if(tagString.length() > 0)
				getBreakpoint().getMarker().setAttribute(MARKER_ATTR_TAGS, buffer.toString().trim());
		}
	}
	
	public String getId() 
	{
		return Long.toString(getBreakpoint().getMarker().getId());
	}

	public String getType() 
	{
		return TYPE;
	}

	public void navigate() 
	{
		try 
		{
			IDE.openEditor(BreakpointPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage(), getBreakpoint().getMarker());
		} 
		catch (PartInitException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the associated breakpoint
	 * @return
	 */
	public IBreakpoint getBreakpoint()
	{
		return fBreakpoint;
	}
	
	/**
	 * Delete the underlying breakpoint
	 */
	public void delete() throws CoreException
	{
		getBreakpoint().delete();
	}
	
	public void setEnabled(boolean state) throws CoreException
	{
		getBreakpoint().setEnabled(state);
		fireWaypointChanged();
	}
	
	public boolean isEnabled() throws CoreException
	{
		return getBreakpoint().isEnabled();
	}
	
	public void setDate(Date date)
	{
		fDate = date;
	}
	
	public void setDescription(String description)
	{
		fDescription = description;
	}
	
	public void setAuthor(String author)
	{
		fAuthor = author;
	}
}
