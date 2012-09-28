/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.c.resources.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.tagsea.TagSEAModelException;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.c.CWaypointsPlugin;
import net.sourceforge.tagsea.c.ICWaypointsConstants;
import net.sourceforge.tagsea.c.documents.internal.FileWaypointExtractor;
import net.sourceforge.tagsea.c.waypoints.parser.IParsedCWaypointInfo;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * Refreshes the waypoints on a given file. The file will have all waypoints removed, and then
 * added again.
 * @author Del Myers
 */

public class FileWaypointRefreshJob extends TagSEAOperation {
	
	private List<IFile> files;
	


	public FileWaypointRefreshJob(List<IFile> files) {
		super(Messages.getString("FileWaypointRefreshJob.jobName")); //$NON-NLS-1$
		this.files = files;
	}


	
	public IStatus runForFile(IProgressMonitor monitor, IFile file)  {
		monitor.beginTask(Messages.getString("FileWaypointRefreshJob.refreshWaypoints") + file.getFullPath(), 300); //$NON-NLS-1$
		if (!file.exists()) {
			//remove the waypoints from the file.
			return removeWaypointsForFile(file);
		}
		IMarker[] problems;
		MultiStatus status = new MultiStatus(CWaypointsPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
		try {
			problems = file.findMarkers(ICWaypointsConstants.WAYPOINT_PROBLEM_MARKER, true, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			status.merge(e.getStatus());
			problems = new IMarker[0];
		}
		for (IMarker problem : problems) {
			try {
				problem.delete();
			} catch (CoreException e) {
				status.merge(e.getStatus());
			}
		}
		IMarker[] markers;
		try {
			markers = file.findMarkers(ICWaypointsConstants.WAYPOINT_MARKER, true, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			status.merge(e.getStatus());
			markers = new IMarker[0];
		}
		// This process is started by a resource change event, so we have to delete the markers later; otherwise the process will hang.
		for (IMarker marker : markers) {
			try {
				marker.delete();
			} catch (CoreException e) {
				status.merge(e.getStatus());
			}
		}
		monitor.subTask(Messages.getString("FileWaypointRefreshJob.findWaypoints")); //$NON-NLS-1$
		
		IParsedCWaypointInfo[] toCreate = FileWaypointExtractor.findWaypoints(file);
		
		monitor.worked(300);
		monitor.subTask(Messages.getString("FileWaypointRefreshJob.refreshWaypoints")); //$NON-NLS-1$
		status.merge(removeWaypointsForFile(file));
		status.merge(createWaypoints(toCreate, file));
		monitor.done();
		return status;
		
	}
	
	/**
	 * @param file2
	 * @throws TagSEAModelException 
	 */
	private IStatus removeWaypointsForFile(IFile file) {
		IWaypoint[] toDelete = CWaypointsPlugin.getCWaypointsForFile(file);
		MultiStatus status = new MultiStatus(CWaypointsPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
		for (IWaypoint wp : toDelete) {
			status.merge(TagSEAPlugin.getWaypointsModel().removeWaypoint(wp).getStatus());
		}
		return status;
	}


	private IStatus createWaypoints(IParsedCWaypointInfo[] toCreate, IFile file) {
		MultiStatus s = new MultiStatus(CWaypointsPlugin.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$
		for (IParsedCWaypointInfo info : toCreate) {
			IWaypoint wp = WaypointResourceUtil.createWaypointForInfo(info, file);
			if (wp == null) {
				s.add(
					new Status(
						IStatus.WARNING, 
						CWaypointsPlugin.PLUGIN_ID, 
						IStatus.WARNING, 
						Messages.getString("FileWaypointRefreshJob.error.noWaypointCreate") + ": " + file.getName(), //$NON-NLS-1$ 
						null
					)
				);
			}
		}
		return s;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.TagSEAOperation#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
		monitor.beginTask(Messages.getString("FileWaypointRefreshJob.refreshFiles"), 100*files.size()); //$NON-NLS-1$
		MultiStatus status = new MultiStatus(CWaypointsPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
		for (IFile file : files) {
			status.merge(runForFile(new SubProgressMonitor(monitor, 100), file));
		}
		monitor.done();
		return status;
	}
}



