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
package net.sourceforge.tagsea.resources.waypoints.operations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ResourceWaypointUtils;
import net.sourceforge.tagsea.resources.waypoints.xml.WaypointXMLUtilities;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.xml.sax.SAXException;

/**
 * Operation that gathers all of the resource waypoints and saves them to an XML file in the
 * workspace metadata area.
 * @author Del Myers
 */

public class WaypointProjectXMLWriteOperation extends TagSEAOperation implements IInternalUpdateOperation {
	private IProject project;

	/**
	 * @param name
	 */
	public WaypointProjectXMLWriteOperation(IProject project) {
		super("Writing resource waypoints");
		this.project = project;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus run(IProgressMonitor monitor) {
		MultiStatus status = new MultiStatus(ResourceWaypointPlugin.PLUGIN_ID, 0, "", null);
		//TagSEAPlugin.run(new UpdateStampOperation(), true);
		IWaypoint[] waypoints = ResourceWaypointUtils.getWaypointsForProject(project);
		monitor.beginTask("Saving resource waypoints", waypoints.length);
		
		WaypointsStampUpdater updater = new WaypointsStampUpdater(waypoints);
		TagSEAPlugin.run(updater, true);
		
		//gather the waypoints by project
//		if (waypoints.length == 0)
//			removeUnusedFile();
		

		Exception e = null;
		SubProgressMonitor subMonitor = 
			new SubProgressMonitor(
					monitor, waypoints.length, 
					SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK
			);
		try {
			writeWaypoints(project, Arrays.asList(waypoints), subMonitor);
//			if (ResourceWaypointPreferences.doesShareWaypoints(project))
//				updateRevision(project);
		} catch (IOException ioe) {
			e = ioe;
		} catch (SAXException se) {
			e = se;
		} finally {
			subMonitor.done();
		}
		if (e != null) {
			Status localStatus = new Status(
					IStatus.ERROR,
					ResourceWaypointPlugin.PLUGIN_ID,
					IStatus.ERROR,
					e.getMessage(),
					e
			);
			status.add(localStatus);
		}
		monitor.done();
		return status;
		
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.TagSEAOperation#operationStarted()
	 */
	@Override
	public void operationStarted() {
		super.operationStarted();
	}
			/**
	 * Updates the current revision for the project stored in the system. This makes it so that comparisons can
	 * be done between two revisions of a resource waypoints for synchronization.
	 * @param project the project to update.
	 *
	//
	@Deprecated
	private void updateRevision(IProject project) {
		IFile waypointFile = project.getFile(WaypointXMLUtilities.fileName);
		if (waypointFile != null && waypointFile.exists()) {
			RepositoryProvider provider = RepositoryProvider.getProvider(project);
			try {
				if (provider != null) {
					IFileRevision revision = provider.getFileHistoryProvider().getWorkspaceFileRevision(waypointFile);
					revision = revision.withAllProperties(new NullProgressMonitor());
					if (revision != null) {
						ResourceWaypointPreferences.setRevision(project, revision.getTimestamp());
					}
				}
			} catch (CoreException e) {
			}
		}
	}


	/**
	 * Scans the projects that have .resourceWaypoints files, but no longer use them because
	 * they don't have any more waypoints. Removes the files if they are no longer needed.
	 * @param projectsWithWaypoints the projects that have waypoints.
	 *
	@Deprecated
	private void removeUnusedFile() {
		//don't delete the file if we aren't sharing locally. Just igore it.
		if (!ResourceWaypointPreferences.doesShareWaypoints(project)) return;
		IFile waypointFile = project.getFile(WaypointXMLUtilities.fileName);
		if (waypointFile != null && waypointFile.exists()) {

			try {
				waypointFile.delete(true, new NullProgressMonitor());
			} catch (CoreException e) {
				ResourceWaypointPlugin.getDefault().log(e);
			}

		}
	}
		
*/


	

	/**
	 * Writes the waypoints for the given project to the waypoints file.
	 * @param project
	 * @param waypointList
	 * @throws IOException 
	 * @throws SAXException 
	 */
	private void writeWaypoints(IProject project, List<IWaypoint> waypointList, IProgressMonitor monitor) throws IOException, SAXException {
		if (!project.isAccessible()) {
			//can't get to the project... delete the waypoints. They will be loadable later when the
			//project becomes available again.
			for (IWaypoint waypoint : waypointList) {
				waypoint.delete();
			}
			return;
		}
		
		monitor.beginTask("Saving project " + project.getName(), waypointList.size() + 10);
		
		File file = WaypointXMLUtilities.getWaypointFile(project);
		if (!file.exists()) {
			if (!file.createNewFile()) throw new IOException("Could not create waypoints file in project " + project.getName());
		}
		FileOutputStream stream = new FileOutputStream(file);
		WaypointXMLUtilities.writeWaypoints(waypointList, stream, new SubProgressMonitor(monitor, waypointList.size()));
		stream.close();
//		try {
//			if (!PlatformUI.getWorkbench().isClosing() && !project.getWorkspace().isTreeLocked())
//				project.refreshLocal(IResource.DEPTH_ONE, new SubProgressMonitor(monitor, 10, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
//		} catch (CoreException e) {
			//don't worry about it.
//		}
		monitor.done();
	}
	
	/**
	 * @return the project
	 */
	public IProject getProject() {
		return project;
	}


}
