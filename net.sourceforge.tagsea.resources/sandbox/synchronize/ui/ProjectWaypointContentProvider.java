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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.synchronize.WaypointSynchronizeObject;
import net.sourceforge.tagsea.resources.synchronize.WaypointSynchronizerHelp;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

class ProjectWaypointContentProvider implements ITreeContentProvider {
	private HashMap<IPath, List<WaypointSynchronizeObject>> synchronizers;
	private class SynchronizeLoadRunnable implements IRunnableWithProgress {

		private List<WaypointSynchronizeObject> synchronizers;
		private IProject project;
		public SynchronizeLoadRunnable(IProject project) {
			this.project = project;
		}
		/* (non-Javadoc)
		 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			this.synchronizers = WaypointSynchronizerHelp.INSTANCE.getSynchronizeInfo(project, monitor);			
		}
		
		/**
		 * @return the synchronizers
		 */
		public List<WaypointSynchronizeObject> getSynchronizers() {
			return synchronizers;
		}
		
	}
	
	
	ProjectWaypointContentProvider() {
		synchronizers = new HashMap<IPath, List<WaypointSynchronizeObject>>();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IProject) {
			final IProject project = (IProject) parentElement;
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
			try {
				SynchronizeLoadRunnable synchronizer = new SynchronizeLoadRunnable(project);
				dialog.run(true, true, synchronizer);
				List<WaypointSynchronizeObject> unsorted = synchronizer.getSynchronizers();
				List<IPath> keysToRemove = new LinkedList<IPath>();
				HashSet<Object> result = new HashSet<Object>();
				for (IPath path : synchronizers.keySet()) {
					if (path.segment(0).equals(project.getName())) {
						keysToRemove.add(path);
					}
				}
				synchronizers.keySet().removeAll(keysToRemove);
				for (WaypointSynchronizeObject synch : unsorted) {
					String resourceString = null;
					if (synch.getLocal() != null) {
						resourceString = synch.getLocal().getResource();
						
					}
					if (resourceString == null && synch.getRemote() != null) {
						resourceString = synch.getRemote().getResource();
					}
					IPath path = new Path(resourceString);
					List<WaypointSynchronizeObject> synchs = synchronizers.get(path);
					if (synchs == null) {
						synchs = new LinkedList<WaypointSynchronizeObject>();
						synchronizers.put(path, synchs);
						
					}
					if (path.equals(project.getFullPath())) {
						if (synch.getKind() != WaypointSynchronizeObject.EQUAL)
							result.add(synch);
					} else {
						if (synch.getKind() != WaypointSynchronizeObject.EQUAL) {
							result.add(path);
							synchs.add(synch);
						}
					}
				}
				return result.toArray();
			} catch (InvocationTargetException e) {
				ResourceWaypointPlugin.getDefault().log(e);
			} catch (InterruptedException e) {
			}
		} else if (parentElement instanceof IPath) {
			return (synchronizers.get(parentElement).toArray());
		}
		return new Object[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof WaypointSynchronizeObject) {
			return ((WaypointSynchronizeObject)element).getProject();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return (element instanceof IProject || element instanceof IPath);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return ResourcesPlugin.getWorkspace().getRoot().getProjects();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		synchronizers.clear();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		synchronizers.clear();
	}
	
}