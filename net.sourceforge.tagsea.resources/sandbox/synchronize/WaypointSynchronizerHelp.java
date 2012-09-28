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
package net.sourceforge.tagsea.resources.synchronize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITagSEAOperationStateListener;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ResourceWaypointPreferences;
import net.sourceforge.tagsea.resources.ResourceWaypointUtils;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.ResourceWaypointProxyDescriptor;
import net.sourceforge.tagsea.resources.waypoints.operations.LoadProjectWaypointsOperation;
import net.sourceforge.tagsea.resources.waypoints.operations.UpdateStampOperation;
import net.sourceforge.tagsea.resources.waypoints.operations.WaypointProjectXMLWriteOperation;
import net.sourceforge.tagsea.resources.waypoints.xml.WaypointXMLUtilities;

/**
 * An object that loads, reads, and watches for changes in the shared resource waypoints, and publishes change
 * events.
 * @author Del Myers
 *
 */
@Deprecated
public class WaypointSynchronizerHelp {
	public static final WaypointSynchronizerHelp INSTANCE = new WaypointSynchronizerHelp();
	private ListenerList listeners;
	/**
	 * project being saved by the internal project saver.
	 */
	IProject projectBeingSaved;
	private class ProjectSaveListener implements ITagSEAOperationStateListener {
		/* (non-Javadoc)
		 * @see net.sourceforge.tagsea.core.ITagSEAOperationStateListener#stateChanged(net.sourceforge.tagsea.core.TagSEAOperation)
		 */
		public void stateChanged(TagSEAOperation operation) {
			if (operation instanceof WaypointProjectXMLWriteOperation) {
				switch (operation.getState()) {
				case RUNNING: 
					projectBeingSaved = ((WaypointProjectXMLWriteOperation)operation).getProject();
					break;
				case DONE: projectBeingSaved = null;
				}
			}
		}
	}
	
	/**
	 * 
	 * Listens for when resources are opened/closed, deleted, etc. This will gather information about resources
	 * that are no longer available so that the waypoints can be removed. It will also look for when the 
	 * .resourceWaypoints file is added, so that the waypoints can be loaded again.
	 * @author Del Myers
	 *
	 */
	private class ResourceGainLostListener implements IResourceChangeListener {
		private class ResourceDeltaVisitor implements IResourceDeltaVisitor {
			private List<IProject> newProjects = new LinkedList<IProject>();
			private List<IWaypoint> removedWaypoints = new LinkedList<IWaypoint>();
			private List<IProject> changedProjects = new LinkedList<IProject>();
			/* (non-Javadoc)
			 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
			 */
			public boolean visit(IResourceDelta delta) throws CoreException {
				if (delta.getKind() == IResourceDelta.ADDED) {
					if (delta.getResource() instanceof IFile) {
						IFile file = (IFile) delta.getResource();
						if (file.getProjectRelativePath().equals(new Path(WaypointXMLUtilities.fileName))) {
							if (checkProjectForSynchronization(file.getProject())) {
								newProjects.add(file.getProject());
							}
							return false;
						}
					}
				} else if (delta.getKind() == IResourceDelta.REMOVED) {
					if (delta.getResource() instanceof IFile) {
						if (((IFile)delta.getResource()).getProjectRelativePath().equals(new Path(WaypointXMLUtilities.fileName)))
							newProjects.remove(delta.getResource().getProject());
					}
					IWaypoint[] wps = ResourceWaypointUtils.getWaypointsForResource(delta.getResource(), false);
					if (wps.length > 0) {
						removedWaypoints.addAll(Arrays.asList(wps));
					}
				} else if (delta.getKind() == IResourceDelta.CHANGED) {
					if (delta.getResource() instanceof IFile) {
						IFile file = (IFile) delta.getResource();
						if (file.getProjectRelativePath().equals(new Path(WaypointXMLUtilities.fileName))) {
							if (checkProjectForSynchronization(file.getProject())) {
								changedProjects.add(file.getProject());
							}
							return false;
						}
					}
				}
				return true;
			}
			/**
			 * Checks to see if the given project is sharing waypoints. If it is, a check is done to see 
			 * whether the the project is currently saving waypoints (i.e. the resource change was caused 
			 * internally). If the first condition is true, and the second is false, the waypoints have to
			 * be synchronized, and this method will return true.
			 * @param project the project to check.
			 * @return true iff the project needs to be synchronized.
			 */
			private boolean checkProjectForSynchronization(IProject project) {
				return (!project.equals(projectBeingSaved) && ResourceWaypointPreferences.doesShareWaypoints(project));
			}

		}
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
		 */
		public void resourceChanged(IResourceChangeEvent event) {
			final ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();
			try {
				if (event.getDelta() == null) return;
				event.getDelta().accept(visitor);
			} catch (CoreException e) {
				return;
			}
			if (visitor.newProjects.size() > 0) {
				for (IProject project : visitor.newProjects) {
					TagSEAPlugin.run(new LoadProjectWaypointsOperation(project), false);
				}
			}
			if (visitor.removedWaypoints.size() > 0) {
				TagSEAPlugin.run(new TagSEAOperation("Deleting Resource Waypoints..."){
					@Override
					public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
						monitor.beginTask("Deleting waypoints...", visitor.removedWaypoints.size());
						for (IWaypoint waypoint : visitor.removedWaypoints) {
							waypoint.delete();
							monitor.worked(1);
						}
						monitor.done();
						return Status.OK_STATUS;
					}
				}, false);
			}
			if (visitor.changedProjects.size() > 0) {
				//copy all of the project waypoint files to .synchronize files in the
				//workspace data area so that we can do synchronization.
				for (IProject project : visitor.changedProjects) {
					IFile projectFile = project.getFile(WaypointXMLUtilities.fileName);
					if (projectFile != null && projectFile.exists()) {
						//copy the contents.
						try {
							File synchronizeFile = getSynchronizeFile(project);
							InputStream in = projectFile.getContents();
							FileOutputStream out = new FileOutputStream(synchronizeFile);
							byte[] buff = new byte[1024];
							int i = 0;
							while ((i = in.read(buff)) != -1) {
								out.write(buff, 0, i);
							}
							in.close();
							out.close();
						} catch (IOException e) {
							ResourceWaypointPlugin.getDefault().log(e);
							continue;
						} catch (CoreException e) {
							ResourceWaypointPlugin.getDefault().getLog().log(e.getStatus());
						}
					}
					fireSynchronizeChanged(project);
				}
			}
		}

	}
	
	private static class DescriptorComparator implements Comparator<IResourceWaypointDescriptor> {
		public int compare(IResourceWaypointDescriptor o1, IResourceWaypointDescriptor o2) {
			return (o1.getResource()+o1.getStamp()).compareTo(o2.getResource()+o2.getStamp());
		}
	}
	
	
	//make it so that only the static instance can be created;
	private WaypointSynchronizerHelp() {
		listeners = new ListenerList();
	}
	
	
	/**
	 * Installs all hooks and starts listening to the workspace.
	 *
	 */
	public void start() {
		TagSEAPlugin.addOperationStateListener(new ProjectSaveListener());
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new ResourceGainLostListener());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List<WaypointSynchronizeObject> getSynchronizeInfo(IProject project, IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		List<WaypointSynchronizeObject> synchronizers = new ArrayList<WaypointSynchronizeObject>(0);
		monitor.beginTask("Collecting synchronization data...", 300);
		monitor.subTask("Updating synchronization information...");
		long lastRevision = ResourceWaypointPreferences.getRevision(project);
		TagSEAPlugin.run(new UpdateStampOperation(), true);
		monitor.worked(100);
		IPath stateLocation = ResourceWaypointPlugin.getDefault().getStateLocation();
		IPath synchronizePath = stateLocation.append(project.getName() + "/.synchronize");
		File synchronizeFile = synchronizePath.toFile();
		if (!synchronizeFile.exists()) {
			monitor.done();
			return synchronizers;
		}
		monitor.subTask("Getting difference data...");
		IWaypoint[] localWaypoints = ResourceWaypointUtils.getWaypointsForProject(project);
		IResourceWaypointDescriptor[] localDescriptors = new IResourceWaypointDescriptor[localWaypoints.length];
		for (int i = 0; i < localWaypoints.length; i++) {
			localDescriptors[i] = new ResourceWaypointProxyDescriptor(localWaypoints[i]);
		}
		monitor.worked(50);
		IResourceWaypointDescriptor[] remoteDescriptors = null;
		try {
			remoteDescriptors = WaypointXMLUtilities.loadFile(synchronizeFile);
		} catch (IOException e) {
			ResourceWaypointPlugin.getDefault().log(e);
			monitor.done();
			return synchronizers;
		} catch (SAXException e) {
			ResourceWaypointPlugin.getDefault().log(e);
			monitor.done();
			return synchronizers;
		}
		monitor.worked(50);
		monitor.subTask("Organizing differences...");
		synchronizers = organizeDescriptors(project, lastRevision, localDescriptors, remoteDescriptors);
		monitor.done();
		return synchronizers;
	}
	
	/**
	 * Saves the given descriptors to the synchronization file for the given project.
	 * @param project
	 * @param descriptors
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void saveSynchronizeInfo(IProject project, List<IResourceWaypointDescriptor> descriptors, IProgressMonitor monitor) throws IOException, SAXException {
		File file = getSynchronizeFile(project);
		if (file == null) return;
		//make sure that all of the descriptors are actually for the project.
		List<IResourceWaypointDescriptor> writeDescriptors = new ArrayList<IResourceWaypointDescriptor>(descriptors.size());
		for (IResourceWaypointDescriptor d : descriptors) {
			if (project.getFullPath().isPrefixOf(new Path(d.getResource()))) {
				writeDescriptors.add(d);
			}
		}
		WaypointXMLUtilities.writeDescriptors(writeDescriptors, new FileOutputStream(file), monitor);
		fireSynchronizeChanged(project);
	}
	
	/**
	 * Updates the current set of descriptors that represent the synchronization data for the updated
	 * set of descriptors. If a descriptor exists with the same stamp as one of the descriptors in the
	 * given list, the current one is replaced. If there doesn't exist a descriptor with the given stamp,
	 * than the new descriptor is added. If the resources for the two descriptors don't match, the new one
	 * is assumed to be in error.
	 * @param project
	 * @param descriptors
	 * @param monitor
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public void updateSynchronizeInfo(IProject project, List<IResourceWaypointDescriptor> descriptors, IProgressMonitor monitor) throws IOException, SAXException {
		monitor.beginTask("Updating Synchronization Info", 10);
		File file = getSynchronizeFile(project);
		if (file == null) return;
		IResourceWaypointDescriptor[] oldArray = WaypointXMLUtilities.loadFile(file);
		List<IResourceWaypointDescriptor> writeDescriptors = new ArrayList<IResourceWaypointDescriptor>();
		Comparator<IResourceWaypointDescriptor> sorter = new Comparator<IResourceWaypointDescriptor>() {
			public int compare(IResourceWaypointDescriptor o1, IResourceWaypointDescriptor o2) {
				return o1.getStamp().compareTo(o2.getStamp());
			}
		};
		IResourceWaypointDescriptor[] newArray = descriptors.toArray(new IResourceWaypointDescriptor[descriptors.size()]);
		Arrays.sort(oldArray, sorter);
		Arrays.sort(newArray, sorter);
		int j = 0;
		int i = 0;
		for (i = 0; i < newArray.length; i++) {
			for (;j < oldArray.length; j++) {
				if (i >= newArray.length) {
					writeDescriptors.add(oldArray[j]);
					continue;
				}
				int diff = sorter.compare(newArray[i], oldArray[j]);
				if (diff < 0) {
					writeDescriptors.add(newArray[i]);
					break;
				} else if (diff == 0) {
					if (newArray[i].getResource().equals(oldArray[j].getResource())) {
						writeDescriptors.add(newArray[i]);
					} else {
						writeDescriptors.add(oldArray[j]);
					}
					j++;
					break;
				} else {
					writeDescriptors.add(oldArray[j]);
					
				}
			}
			if (i >= oldArray.length) {
				if (writeDescriptors.add(newArray[i]));
			}
		}
		for (; i < newArray.length; i++) {
			writeDescriptors.add(newArray[i]);
		}
		for (; j < oldArray.length; j++) {
			writeDescriptors.add(oldArray[i]);
		}
		saveSynchronizeInfo(project, writeDescriptors, new SubProgressMonitor(monitor, 5));
	}
	
	/**
	 * Sorts and organizes the local and remote descriptors to group waypoint changes.
	 * 
	 * @param localDescriptors
	 * @param remoteDescriptors
	 */
	private List<WaypointSynchronizeObject> organizeDescriptors(IProject project, long workspaceRevision, IResourceWaypointDescriptor[] localDescriptors, IResourceWaypointDescriptor[] remoteDescriptors) {
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
						new WaypointSynchronizeObject(project, workspaceRevision, localDescriptors[li], remoteDescriptors[ri]);
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
		return synchronizeObjects;
	}
	
	/**
	 * Returns the file used to synchronize changes to the resource waypoints, or null if the project is
	 * not being shared. If the file does not exist, it is created.
	 * @param project the project to get the file for.
	 * @return the file used to synchronize changes to the resource waypoints.
	 * @throws IOException 
	 */
	public File getSynchronizeFile(IProject project) throws IOException {
		if (!ResourceWaypointPreferences.doesShareWaypoints(project)) return null;
		IPath state = ResourceWaypointPlugin.getDefault().getStateLocation();
		File stateFile = state.toFile();
		File synchronizeProjectLocation = new File(stateFile, project.getName());
		if (!synchronizeProjectLocation.exists()) {
			synchronizeProjectLocation.mkdir();
		}
		File synchronizeFile = new File(synchronizeProjectLocation, ".synchronize");
		if (!synchronizeFile.exists()) {
			synchronizeFile.createNewFile();
		}
		return synchronizeFile;
	}
	
	/**
	 * Adds the given listener to the list of listeners if it doesn't already exist.
	 * @param listener the listener to add.
	 */
	public void addSynchronizeListener(IWaypointSynchronizeListener listener) {
		listeners.add(listener);
	}
	
	public void removeSynchronizeListener(IWaypointSynchronizeListener listener) {
		listeners.remove(listener);
	}
	
	private void fireSynchronizeChanged(IProject project) {
		for (Object listener : listeners.getListeners()) {
			((IWaypointSynchronizeListener)listener).synchronizationChanged(project);
		}
	}

}
