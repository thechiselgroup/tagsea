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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.impl.AbstractWaypoint;

public class ResourceWaypoint extends AbstractWaypoint 
{
	public final static String TYPE = "com.ibm.research.tagging.ResourceWaypoint";
	public static final String MARKER_ID                = "com.ibm.research.tagging.resource.resourceWaypointMarker";
	public static final String MARKER_ATTR_AUTHOR       = "com.ibm.research.tagging.resource.author";
	public static final String MARKER_ATTR_DESCRIPTION  = "com.ibm.research.tagging.resource.description";
	public static final String MARKER_ATTR_DATE 		= "com.ibm.research.tagging.resource.date";
	public static final String MARKER_ATTR_TAGS 		= "com.ibm.research.tagging.resource.tags";
	
	public static final String MARKER_TAG_DELIMETER 	= " ";
	
	private IMarker fMarker;
	private boolean fSelectResource = true;
	
	/**
	 * Create a new resource waypoint on the given resource with the specified fields
	 * @param resource
	 * @param description
	 * @param author
	 * @param date
	 * @throws CoreException
	 */
	public ResourceWaypoint(IResource resource, String description, String author, Date date) throws CoreException
	{
		super(description, author, date);
		fMarker = createMarker(resource);
	}

	/**
	 * Create a new Resource Waypoint based on the given marker
	 * @param marker
	 * @throws CoreException
	 */
	public ResourceWaypoint(IMarker marker) throws InvalidMarkerTypeException, CoreException
	{
		super();

		if(!marker.getType().equals(getMarkerType()))
			throw new InvalidMarkerTypeException("Incorrect marker type");

		fMarker = marker;
	}
	
	public void load() throws CoreException
	{
		loadAuthorFromMarker();
		loadDescriptionFromMarker();
		loadDateFromMarker();
		loadTagsFromMarker();
	}
	
	private void loadTagsFromMarker() 
	{
		String tagString = getMarker().getAttribute(MARKER_ATTR_TAGS, null);
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
		String dateString = getMarker().getAttribute(MARKER_ATTR_DATE, null);
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
		fDescription = getMarker().getAttribute(MARKER_ATTR_DESCRIPTION, null);
	}

	private void loadAuthorFromMarker() 
	{
		fAuthor = getMarker().getAttribute(MARKER_ATTR_AUTHOR, null);
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
		getMarker().setAttribute(MARKER_ATTR_AUTHOR, getAuthor());
	}
	
	protected void saveDescriptionToMarker() throws CoreException
	{
		getMarker().setAttribute(MARKER_ATTR_DESCRIPTION, getDescription());
	}
	
	protected void saveDateToMarker() throws CoreException
	{
		if(getDate()!=null)
		{
			String dateString = SimpleDateFormat.getInstance().format(getDate());
			getMarker().setAttribute(MARKER_ATTR_DATE, dateString);
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
				getMarker().setAttribute(MARKER_ATTR_TAGS, buffer.toString().trim());
		}
	}

	protected IMarker createMarker(IResource resource) throws CoreException
	{
		return resource.createMarker(getMarkerType());
	}

	protected String getMarkerType() 
	{
		return ResourceWaypoint.MARKER_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypoint#getId()
	 */
	public String getId() 
	{
		return Long.toString(getMarker().getId());
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypoint#getType()
	 */
	public String getType() 
	{
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypoint#navigate()
	 */
	public void navigate() 
	{

		// always show the resource - whether it is a file or whatever, in the package explorer for context
		PackageExplorerPart view = PackageExplorerPart.openInActivePerspective();
		view.tryToReveal(getMarker().getResource());
		
		// deselect the resource, a bit hacky
		if(!fSelectResource)
			view.getTreeViewer().setSelection(new StructuredSelection());
		
		if(getMarker().getResource() instanceof IFile)
		{
			try 
			{
				IWorkbenchPage page = ResourceWaypointPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IDE.openEditor(page, getMarker());
			}
			catch (PartInitException e) 
			{
				e.printStackTrace();
			}
		}
		else // everything else should open in the navigator? 
		{
//			IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
//			ShowInNavigatorAction action = new ShowInNavigatorAction(page,new SelectionProviderAdapter() 
//			{
//				@Override
//				public ISelection getSelection() 
//				{
//					return new StructuredSelection(getMarker().getResource());
//				}
//			});
//			action.run();
			
//			PackageExplorerPart view = PackageExplorerPart.openInActivePerspective();
//			view.tryToReveal(getMarker().getResource());
		}
	}
	
	public void selectResource(boolean value)
	{
		fSelectResource = value;
	}
	
	public boolean opensInEditor()
	{
		if(getMarker()!=null && getMarker().exists())
			if(getMarker().getResource() instanceof IFile)
				return true;
		
		return false;
	}
	
	/**
	 * Get the marker
	 * @return the marker
	 */
	public IMarker getMarker() 
	{
		return fMarker;
	}
	
	/**
	 * Delete this waypoint, this will delete the underlying data structures
	 * It will not remove the waypoint from the waypoint model
	 */
	public void delete()
	{
		try 
		{
			if(getMarker().exists())
				getMarker().delete();
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}
}