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
package net.sourceforge.tagsea.parsed.core.internal.persistence;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.IWaypointChangeListener;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.ParsedWaypointUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.ui.IMemento;

/**
 * A class for persisting Parsed waypoints as mementos. The class backs up files to attempt to perform crash
 * recovery. Waypoint changes are monitored in order to flag the waypoint state as needing to be saved on the
 * next workbench save. Marker jobs are sheduled at the same time to refresh the markers for a file. This way
 * markers don't have to be updated from within the UI thread, causing the possibility of inconsistent states
 * and deadlock.
 * @author Del Myers
 *
 */

//@tag start : make this a document change listener to listen for when dirty documents are disposed.
public class WaypointXMLPersistence implements ISaveParticipant, IWaypointChangeListener {
	/**
	 * Job that is run to update the markers after waypoint changes.
	 * @author Del Myers
	 *
	 */
	private final class MarkerUpdateJob extends Job {
		
		private MarkerRunnable runnable;

		/**
		 * @param name
		 * @param markerFiles
		 */
		private MarkerUpdateJob(String name, Set<IResource> markerFiles) {
			super(name);
			this.runnable = new MarkerRunnable(markerFiles);
		}

		@Override
		public IStatus run(IProgressMonitor monitor) {
			try {
				ResourcesPlugin.getWorkspace().run(runnable, getRule(), IWorkspace.AVOID_UPDATE, monitor);
			} catch (CoreException e) {
				return e.getStatus();
			}
			return Status.OK_STATUS;
		}
	}
	
	/**
	 * Runnable that actually performs the marker update operations. Used to avoid workspace updates.
	 * @author Del Myers
	 *
	 */
	private class MarkerRunnable implements IWorkspaceRunnable {

		private Set<IResource> markerFiles;

		public MarkerRunnable(Set<IResource> markerFiles) {
			this.markerFiles = markerFiles;
		}
		
		public void run(IProgressMonitor monitor) throws CoreException {
			monitor.beginTask("Updating Waypoint Markers", markerFiles.size());
			MultiStatus status = new MultiStatus(ParsedWaypointPlugin.PLUGIN_ID, 0, "Updating Waypoint Markers", null);
			for (IResource resource : markerFiles) {
				if (resource != null && resource.exists()) {
					try {
						resource.deleteMarkers(ParsedWaypointPlugin.MARKER_TYPE, false, IResource.DEPTH_ZERO);
						List<IWaypoint> wps = getWaypointsForFile(resource.getFullPath().makeAbsolute().toPortableString());
						for (IWaypoint wp : wps) {
							IMarker marker = resource.createMarker(ParsedWaypointPlugin.MARKER_TYPE);
							marker.setAttribute(IMarker.CHAR_START, ParsedWaypointUtils.getCharStart(wp));
							marker.setAttribute(IMarker.CHAR_END, ParsedWaypointUtils.getCharEnd(wp));
							marker.setAttribute(IMarker.MESSAGE, wp.getText());
						}
					} catch (CoreException e) {
						status.merge(e.getStatus());
					}
				}
			}
			if (!status.isOK()) {
				throw new CoreException(status);
			}
		}
		
	}
	
	private Set<String> dirtyFiles;
	private WaypointSerializer serializer;
	/**
	 * A serializer used to backup files to restore dirty editors.
	 */
	private WaypointSerializer backupSerializer;
	public WaypointXMLPersistence() throws IOException {
		IPath stateLocation = ParsedWaypointPlugin.getDefault().getStateLocation();
		this.dirtyFiles = new HashSet<String>();
		serializer = new WaypointSerializer(stateLocation.append("waypoints.state"));
		backupSerializer = new WaypointSerializer(stateLocation.append("backup.state"));
	}
	
	
	public void doneSaving(ISaveContext context) {
	}

	public void prepareToSave(ISaveContext context) throws CoreException {
	}

	public void rollback(ISaveContext context) {
	}

	public void saving(ISaveContext context) throws CoreException {
		save();
	}
	
	public synchronized void save() throws CoreException {
		if (dirtyFiles.size() == 0) {
			//nothing is dirty, just quit.
			return;
		}
		List<IWaypoint> waypointsToSave = new LinkedList<IWaypoint>();
		for (String file : dirtyFiles) {
			waypointsToSave.addAll(getWaypointsForFile(file));
		}
		try {
			serializer.write(waypointsToSave.toArray(new IWaypoint[waypointsToSave.size()]), true);
		} catch (IOException e) { 
			throw new CoreException(ParsedWaypointPlugin.getDefault().createErrorStatus(e));
		}
		dirtyFiles.clear();
	}



	/**
	 * @param fileName
	 * @return
	 */
	private List<IWaypoint> getWaypointsForFile(String fileName) {
		IWaypoint[] allWaypoints = TagSEAPlugin.getWaypointsModel().getWaypoints(ParsedWaypointPlugin.WAYPOINT_TYPE);
		List<IWaypoint> waypointList = new LinkedList<IWaypoint>();
		Path filePath = new Path(fileName);
		if (!filePath.isAbsolute()) {
			fileName = filePath.makeAbsolute().toPortableString();
		}
		for (IWaypoint wp : allWaypoints) {
			if (fileName.equals(ParsedWaypointUtils.getResourceName(wp))) {
				waypointList.add(wp);
			}
		}
		return waypointList;
	}


	/**
	 * This implementation of the waypoint state listener is expected to be registered only for
	 * parsed waypoint changes.
	 */
	public synchronized void waypointsChanged(WaypointDelta delta) {
		//the waypoint state file will now be deleted and ready for rewriting. Cache all the dirty waypoints
		//so that they can be saved in the state file.
		Set<IResource> markerFiles = new HashSet<IResource>();
		for (IWaypointChangeEvent event : delta.getChanges()) {
			IWaypoint wp = event.getWaypoint();
			String fileName = ParsedWaypointUtils.getResourceName(wp);
			IPath filePath = new Path(fileName);
			filePath.makeAbsolute();
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(filePath);
			if (resource != null && resource.exists()) {
				markerFiles.add(resource);
			}
			dirtyFiles.add(filePath.toPortableString());
		}
		Job job = new MarkerUpdateJob("Updating Waypont Markers", markerFiles);
		job.setRule(new MultiRule(markerFiles.toArray(new IResource[markerFiles.size()])));
		job.schedule();
	}


	/**
	 * @return the mementos saved with this persistance. Excludes mementos for files that have been
	 * marked as dirty.
	 * @throws IOException 
	 */
	public IMemento[] loadMementos() throws IOException {
		try {
			LinkedList<IMemento> unfiltered = new LinkedList<IMemento>(Arrays.asList(serializer.loadFileMementos()));
			for (Iterator<IMemento> i = unfiltered.iterator(); i.hasNext();) {
				IMemento m = i.next();
				String fileName = m.getString("fileName");
				if (dirtyFiles.contains(fileName)) {
					i.remove();
				}
			}
			return unfiltered.toArray(new IMemento[unfiltered.size()]);
		} catch (IOException e) {
			//delete the file so that it doesn't cause any more problems.
			serializer.getFile().delete();
			throw e;
		}
	}


	/**
	 * Marks the given file as dirty and in need of a save.
	 * @param file
	 */
	public synchronized void markDirty(IFile file) {
		dirtyFiles.add(file.getFullPath().makeAbsolute().toPortableString());
	}


	/**
	 * @param waypoints
	 * @throws IOException 
	 */
	public void backup(IWaypoint[] waypoints) throws IOException {
		backupSerializer.write(waypoints);		
	}


	/**
	 * Undoes the changes done to the given resource by reloading the backup mementos and applying
	 * them to the given file.
	 * @param file the file to undo changes on
	 * @throws IOException 
	 */
	public synchronized IMemento removeBackup(IFile file) throws IOException {
		return backupSerializer.removeFileInfo(file.getFullPath().makeRelative().toPortableString());
	}

}
