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

package com.ibm.research.tagging.ppt;

import java.io.File;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.cue.tourist.internal.win32.ppt.PowerpointApplication;
import com.ibm.research.tagging.resource.InvalidMarkerTypeException;
import com.ibm.research.tagging.resource.ResourceWaypoint;

public class PptWaypoint extends ResourceWaypoint
{
	public static final String TYPE = "com.ibm.research.tagging.ppt.PptWaypoint";
	
	public static final String MARKER_ID                    = "com.ibm.research.tagging.ppt.pptWaypointMarker";
	public static final String MARKER_ATTR_SLIDESTART 		= "com.ibm.research.tagging.ppt.slideStart";
	public static final String MARKER_ATTR_SLIDEEND 		= "com.ibm.research.tagging.ppt.slideEnd";

	private int fStartSlide;
	private int fEndSlide;
	
	public PptWaypoint(IResource resource, int startSlide, int endSlide, String description, String author, Date date) throws CoreException 
	{
		super(resource,description,author,date);
		fStartSlide = startSlide;
		fEndSlide = endSlide;
	}

	public PptWaypoint(IMarker marker) throws CoreException, InvalidMarkerTypeException
	{
		super(marker);
	}
	
	@Override
	public void load() throws CoreException 
	{
		super.load();
		loadSlidesFromMarker();
	}
	
	protected void loadSlidesFromMarker() 
	{
		try 
		{
			fStartSlide = Integer.parseInt(getMarker().getAttribute(MARKER_ATTR_SLIDESTART, null));
			fEndSlide = Integer.parseInt(getMarker().getAttribute(MARKER_ATTR_SLIDEEND, null));
		} 
		catch (NumberFormatException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void save() throws CoreException 
	{
		super.save();
		saveSlidesToMarker();
	}
	
	protected void saveSlidesToMarker() throws CoreException
	{
		getMarker().setAttribute(MARKER_ATTR_SLIDESTART, Integer.toString(getStartSlide()));
		getMarker().setAttribute(MARKER_ATTR_SLIDEEND, Integer.toString(getEndSlide()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypoint#getType()
	 */
	public String getType() 
	{
		return TYPE;
	}
	
	@Override
	protected String getMarkerType() 
	{
		return MARKER_ID;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypoint#navigate()
	 */
	public void navigate() 
	{
		try 
		{
			// show the file in package explorer for context 
			PackageExplorerPart view = PackageExplorerPart.openInActivePerspective();
			view.tryToReveal(getMarker().getResource());
			
			// for now, pop up powerpoint
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			PowerpointApplication ppt = new PowerpointApplication(shell,SWT.BAR);
			
			IFile file = (IFile) getMarker().getResource();
			
			ppt.open(file.getLocation().toString());
			
			int start = getStartSlide();
			int end   = getEndSlide();
			
			if ( start>=1 )
			{
				ppt.runSlideShow(start,end);
				ppt.gotoEditorSlide(start);   
			}
			else
			{
				ppt.runSlideShow();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the first slide of the show that is waypointed
	 * @return
	 */
	public int getStartSlide() 
	{
		return fStartSlide;
	}
	
	/**
	 * Set the first slide of the show that is waypointed
	 * @param startSlide
	 */
	public void setStartSlide(int startSlide) 
	{
		fStartSlide = startSlide;
	}
	
	/**
	 * Get the end slide of the show that is waypointed
	 * @return
	 */
	public int getEndSlide() 
	{
		return fEndSlide;
	}
	
	/**
	 * Set the end slide of the show that is waypointed
	 * @param endSlide
	 */
	public void setEndSlide(int endSlide) 
	{
		fEndSlide = endSlide;
	}
}
