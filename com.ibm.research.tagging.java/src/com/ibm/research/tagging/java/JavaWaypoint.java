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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.MarkerUtilities;

import com.ibm.research.tagging.core.impl.AbstractWaypoint;
import com.ibm.research.tagging.java.parser.IJavaWaypointInfo;


/**
 * @author mdesmond
 */
public class JavaWaypoint extends AbstractWaypoint 
{
	public static final String TYPE = "com.ibm.research.tagging.java.JavaFileWaypoint";
	public static final String MARKER_ID = "com.ibm.research.tagging.java.waypointmarker";
	private IMarker fMarker;
	
	/**
	 * @param name
	 * @param attributes
	 */
	public JavaWaypoint(IMarker marker,String description, String author,Date date) 
	{
		super(description,author,date);
		fMarker = marker;
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
	 * @see com.ibm.research.tagging.core.impl.AbstractWaypoint#getId()
	 */
	public String getId() 
	{
		// Use the marker id as our waypoint id
		return Long.toString(fMarker.getId());
	}

	/**
	 * Get the line number associated with this waypoint
	 * @return
	 */
	public int getLineNumber()
	{
		return MarkerUtilities.getLineNumber(fMarker);
	}

	/*
	 * (non-Javadoc)
	 * @see ca.uvic.cs.tagsea.core.model.IWaypoint#navigate()
	 */
	public void navigate() 
	{
		if (getMarker().exists()) 
		{
			try 
			{
				IDE.openEditor(JavaWaypointPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage(), getMarker(), OpenStrategy.activateOnOpen());
			} 
			catch (PartInitException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Get the marker associated with this waypoint
	 * @return
	 */
	public IMarker getMarker()
	{
		return fMarker;
	}

	/**
	 * Get the marker associated with this waypoint
	 * @return
	 */
	public IFile getFile()
	{
		return (IFile)fMarker.getResource();
	}

	/**
	 * Get the java element associated with this waypoint
	 * @return
	 */
	public IJavaElement getJavaElement() throws JavaModelException
	{
		ICompilationUnit cu = (ICompilationUnit) JavaCore.create(fMarker.getResource());
		return cu.getElementAt(MarkerUtilities.getCharStart(fMarker));
	}

	/**
	 * Check if this waypoint still exists
	 * @return true if the waypoint exists
	 */
	public boolean exists()
	{
		return fMarker.exists();
	}

	@Override
	public String toString() 
	{
		return "Waypoint [id = " + getId() + "; resource = " + getMarker().getResource().getName() + "]";
	}

	public void delete()
	{
		if(getMarker().exists())
		{
			try 
			{
				getMarker().delete();
			} 
			catch (CoreException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
