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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.ibm.research.tagging.java.ModifiedFileSynchronizer;
import com.ibm.research.tagging.java.JavaWaypointPlugin;
import com.ibm.research.tagging.java.WaypointFileScanner;
import com.ibm.research.tagging.java.WaypointImporter;

public class ResourceChangeJob extends Job 
{
	private List<IResourceDelta> fAddedResourceDeltas;
	private List<IResourceDelta> fRemovedResourceDeltas;
	private List<IResourceDelta> fChangedResourceDeltas;

	public ResourceChangeJob(List<IResourceDelta> addedResourceDeltas,
			List<IResourceDelta> removedResourceDeltas,
			List<IResourceDelta> changedResourceDeltas) 
	{
		super("ResourceChangedJob");

		fAddedResourceDeltas = addedResourceDeltas;
		fRemovedResourceDeltas = removedResourceDeltas;
		fChangedResourceDeltas = changedResourceDeltas;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) 
	{
		processAddedResourceDeltas(fAddedResourceDeltas);
		processRemovedResourceDeltas(fRemovedResourceDeltas);
		processChangedResourceDeltas(fChangedResourceDeltas);
		return Status.OK_STATUS;
	}

	private void processAddedResourceDeltas(List<IResourceDelta> addedResourceDeltas) 
	{
		for(IResourceDelta delta : addedResourceDeltas)
		{	
			// Scan the file and import the new waypoints
			IFile file = (IFile)delta.getResource();

			WaypointFileScanner scanner = new WaypointFileScanner(file);
			IMarker[] markers = scanner.scan();

			WaypointImporter importer = new WaypointImporter();
			importer.importWaypoints(markers);
		}
	}

	private void processRemovedResourceDeltas(List<IResourceDelta> removedResourceDeltas)
	{
		for(IResourceDelta delta : removedResourceDeltas)
		{
			// Clear all waypoints
			IFile file = (IFile)delta.getResource();
			JavaWaypointPlugin.getDefault().clear(file);
		}
	}

	/**
	 * We attempt create and delete waypoints only when absolutly necessary
	 * Invalidating all waypoints as was done before causes havok with refactoring
	 * @param resourceDeltas
	 */
	private void processChangedResourceDeltas(List<IResourceDelta> resourceDeltas)
	{
		for(IResourceDelta delta : resourceDeltas)
		{
			IFile file = (IFile)delta.getResource();	
			
			// Attempt to synchronize the file with the tag model
			ModifiedFileSynchronizer synchronizer = new ModifiedFileSynchronizer();
			synchronizer.lockAndSynchronizeWithModel(file);
		}
	}
}
