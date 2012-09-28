/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.resources.synchronize.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.synchronize.WaypointSynchronizeObject;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.operations.WaypointProjectXMLWriteOperation;
import net.sourceforge.tagsea.resources.waypoints.xml.WaypointXMLUtilities;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.history.IFileHistory;
import org.eclipse.team.core.history.IFileHistoryProvider;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.ccvs.core.filehistory.CVSFileHistory;
import org.xml.sax.SAXException;

/**
 * Checks a given project for waypoints in the local system as well as the remote storage and creates
 * object that represent both the local and the remote waypoints.
 * @author Del Myers
 *
 */
public class ProjectWaypointSynchronizeFinder implements IRunnableWithProgress {

	List<WaypointSynchronizeObject> synchronizers;
	
	private static class DescriptorComparator implements Comparator<IResourceWaypointDescriptor> {
		public int compare(IResourceWaypointDescriptor o1, IResourceWaypointDescriptor o2) {
			return (o1.getResource()+o1.getStamp()).compareTo(o2.getResource()+o2.getStamp());
		}
	}
	
	private IProject project;
	ProjectWaypointSynchronizeFinder(IProject project) {
		this.project = project;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		//clear the list.
		synchronizers = new ArrayList<WaypointSynchronizeObject>();
		monitor.beginTask("Collecting synchronization data...", 300);
		SubProgressMonitor saveMonitor = 
			new SubProgressMonitor(monitor, 100, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
		monitor.subTask("Saving waypoints...");
		WaypointProjectXMLWriteOperation writer = new WaypointProjectXMLWriteOperation(project);
		writer.run(saveMonitor);
		if (monitor.isCanceled())
			throw new InterruptedException();
		IFile waypointFile = project.getFile(".resourceWaypoints");
		IResourceWaypointDescriptor[] localDescriptors = new IResourceWaypointDescriptor[0];
		if (waypointFile.exists()) {
			try {
				localDescriptors = WaypointXMLUtilities.loadFile(WaypointXMLUtilities.getWaypointFile(project));
			} catch (IOException e) {
			} catch (SAXException e) {
				ResourceWaypointPlugin.getDefault().log(e);
				return;
			}
		}
		//@tag tagsea.bug.50.enhance : consider using the subscriber to cache data instead.
		RepositoryProvider provider = RepositoryProvider.getProvider(project);
		SubProgressMonitor historyMonitor =
			new SubProgressMonitor(monitor, 100, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
		monitor.subTask("Getting remote history...");
		IFileRevision revision = provider.getFileHistoryProvider().getWorkspaceFileRevision(waypointFile);
		try {
			if (revision != null)
				revision = revision.withAllProperties(new NullProgressMonitor());
		} catch (CoreException e1) {
			throw new InvocationTargetException(e1);
		}

		IFileHistory history = provider.getFileHistoryProvider().getFileHistoryFor(waypointFile,IFileHistoryProvider.SINGLE_REVISION, historyMonitor);
		if (history instanceof CVSFileHistory) {
			//@tag tagsea.bug.50.hack -author="Del Myers" -date="enCA:21/02/07" : CVSFileHistory doesn't refresh itself.
			//I don't know why the CVSFileHistory doesn't refresh itself. This seems silly because you
			//can never be sure using the interface methods whether or not the revision history has been refreshed.
			try {
				((CVSFileHistory)history).refresh(new NullProgressMonitor());
			} catch (TeamException e) {
			}
		}
		IResourceWaypointDescriptor[] remoteDescriptors = new IResourceWaypointDescriptor[0];
		
		if (history != null) {
			IFileRevision[] revisions = history.getFileRevisions();

			SubProgressMonitor downloadMonitor =
				new SubProgressMonitor(monitor, 50, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
			monitor.subTask("Getting remote data...");
			if (revisions.length > 0) {
				try {
					IStorage storage = revisions[0].getStorage(downloadMonitor);
					remoteDescriptors = WaypointXMLUtilities.load(storage.getContents());
				} catch (CoreException e) {
					ResourceWaypointPlugin.getDefault().log(e);
					return;
				} catch (SAXException e) {
					ResourceWaypointPlugin.getDefault().log(e);
					return;
				} catch (IOException e) {
				}
			}
		} else {
			monitor.worked(50);
		}
		if (monitor.isCanceled())
			throw new InterruptedException();
		monitor.subTask("Organizing changes...");
		long time = (revision != null) ? revision.getTimestamp() : -1;
		organizeDescriptors(time, localDescriptors, remoteDescriptors);
		monitor.done();
	}
	/**
	 * Sorts and organizes the local and remote descriptors to group waypoint changes.
	 * 
	 * @param localDescriptors
	 * @param remoteDescriptors
	 */
	private void organizeDescriptors(long workspaceRevision, IResourceWaypointDescriptor[] localDescriptors, IResourceWaypointDescriptor[] remoteDescriptors) {
		Arrays.sort(localDescriptors, new DescriptorComparator());
		Arrays.sort(remoteDescriptors, new DescriptorComparator());
		
		int li = 0;
		int ri = 0;
		boolean done = false;
		List<WaypointSynchronizeObject> synchronizeObjects = new ArrayList<WaypointSynchronizeObject>();
		while (!done) {
			if (li >= localDescriptors.length) {
				done = true;
			}
			if (ri >= remoteDescriptors.length) {
				done = true;
			}
			if (!done) {
				String lstamp = localDescriptors[li].getStamp();
				String rstamp = remoteDescriptors[ri].getStamp();
				int diff = lstamp.compareTo(rstamp);
				if (diff == 0) {
					//same waypoint create a synchronize object that contains both.
					WaypointSynchronizeObject so =
						new WaypointSynchronizeObject(project, workspaceRevision, localDescriptors[li], localDescriptors[ri]);
					synchronizeObjects.add(so);
					li++;
					ri++;
				} else if (diff < 0) {
					//the local waypoint does not exist in the remote repository... add it.
					synchronizeObjects.add(
						new WaypointSynchronizeObject(project, workspaceRevision, localDescriptors[li], null)	
					);
					li++;
				} else if (diff > 0) {
//					the remote waypoint does not exist locally... add it.
					synchronizeObjects.add(
						new WaypointSynchronizeObject(project, workspaceRevision, null, remoteDescriptors[ri])	
					);
					ri++;
				}
			}
		}
		while (li < localDescriptors.length) {
			synchronizeObjects.add(
				new WaypointSynchronizeObject(project, workspaceRevision, localDescriptors[li], null)	
			);
			li++;
		}
		while (ri < remoteDescriptors.length) {
			synchronizeObjects.add(
				new WaypointSynchronizeObject(project, workspaceRevision, null, remoteDescriptors[ri])	
			);
			ri++;
		}
		synchronizers = synchronizeObjects;
	}
	
	/**
	 * @return the synchronizers
	 */
	public List<WaypointSynchronizeObject> getSynchronizers() {
		return synchronizers;
	}

}
