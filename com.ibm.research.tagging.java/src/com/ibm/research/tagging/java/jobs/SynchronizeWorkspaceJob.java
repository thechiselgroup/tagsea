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

package com.ibm.research.tagging.java.jobs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.ibm.research.tagging.java.JavaWaypoint;
import com.ibm.research.tagging.java.JavaWaypointPlugin;
import com.ibm.research.tagging.java.WaypointFileScanner;
import com.ibm.research.tagging.java.WaypointImporter;
import com.ibm.research.tagging.java.resources.JavaFileResourceVisitor;

public class SynchronizeWorkspaceJob extends WorkspaceJob 
{
	private IProject[] fProjects;

	public SynchronizeWorkspaceJob(String title,IProject[] projects) 
	{
		super(title);
		fProjects = projects;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) 
	{
		process(fProjects,monitor);
		return Status.OK_STATUS;
	}

	private void process(IProject[] projects,IProgressMonitor monitor) 
	{
		List<IFile> javaFiles = new ArrayList<IFile>();

		for(IProject p : fProjects)
		{
			JavaFileResourceVisitor visitor = new JavaFileResourceVisitor();

			try 
			{
				p.accept(visitor);
			} 
			catch (CoreException e) 
			{
				e.printStackTrace();
				continue;
			}

			javaFiles.addAll(visitor.getFiles());
		}

		processFiles(javaFiles,monitor);
		monitor.done();
	}

	private void processFiles(List<IFile> files,IProgressMonitor monitor)
	{
		monitor.beginTask("Scanning java files...", files.size());
		
		for(IFile file : files)
		{	
			monitor.setTaskName(file.getName());
			
			try 
			{
				file.deleteMarkers(JavaWaypoint.MARKER_ID, false, IResource.DEPTH_ONE);
			} 
			catch (CoreException e) 
			{
				e.printStackTrace();
			}
			
			JavaWaypointPlugin.getDefault().clear(file);
		
			WaypointFileScanner scanner = new WaypointFileScanner(file);
			IMarker[] markers = scanner.scan();
			WaypointImporter importer = new WaypointImporter();
			importer.importWaypoints(markers);
			monitor.worked(1);
		}
	}
}
