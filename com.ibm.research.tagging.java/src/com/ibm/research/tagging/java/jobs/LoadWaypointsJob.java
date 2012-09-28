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

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.ibm.research.tagging.java.JavaWaypoint;
import com.ibm.research.tagging.java.WaypointFileScanner;
import com.ibm.research.tagging.java.WaypointImporter;
import com.ibm.research.tagging.java.markers.WaypointMarkerValidator;
import com.ibm.research.tagging.java.util.SortingUtilities;

public class LoadWaypointsJob extends Job
{
	public LoadWaypointsJob(String name) 
	{
		super(name);
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		try 
		{
			// Existing waypoints are cached as markers
			IResource root = ResourcesPlugin.getWorkspace().getRoot();

			// Get markers
			IMarker[] markers = root.findMarkers(JavaWaypoint.MARKER_ID,false,IResource.DEPTH_INFINITE);
			
			// arrange the markers by (resource -> Marker list)
			Map<IResource, List<IMarker>> map = SortingUtilities.sortMarkersByResource(markers);

			monitor.beginTask("Importing waypoints", map.keySet().size());
			
			// Process each resource seperatly, if we have invalid markers then roll back the entire resource
			// This is a pessimistic approach but also realistic because one invalid marker will mean all markers
			// later in the file will also be invalid
			for(IResource resource : map.keySet())
			{
				List<IMarker> markerList = map.get(resource);
				
				if(resource.exists())
				{
					// get the file
					IFile file = (IFile)resource;
				
					monitor.setTaskName(file.getName());
					
					// The validator will compare cached waypoint info with the fresh version from the file
					WaypointMarkerValidator validator = new WaypointMarkerValidator(file);

					// All the markers are in a consistent state
					if(validator.isValid(markerList))
					{
						importWaypoints(markerList.toArray(new IMarker[0]));
					}
					// Invalid markers were found so we need to rescan the file
					else
					{
						file.deleteMarkers(JavaWaypoint.MARKER_ID,false,IResource.DEPTH_INFINITE);
						WaypointFileScanner scanner = new WaypointFileScanner(file);
						IMarker[] newMarkers = scanner.scan();
						importWaypoints(newMarkers);
					}
					
					monitor.worked(1);
				}
			}
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
		
		return Status.OK_STATUS;
	}

	
	private void importWaypoints(IMarker[] markers) 
	{
		WaypointImporter importer = new WaypointImporter();
		importer.importWaypoints(markers);
	}
}
