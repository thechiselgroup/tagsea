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
package net.sourceforge.tagsea.c.waypoints;

import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.c.CWaypointUtils;
import net.sourceforge.tagsea.c.CWaypointsPlugin;
import net.sourceforge.tagsea.c.resources.internal.FileWaypointRefreshJob;
import net.sourceforge.tagsea.c.resources.internal.MarkerWaypointLoadOperation;
import net.sourceforge.tagsea.core.TagSEAOperation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Job for loading java waypoints.
 * @author Del Myers
 */

public class LoadWaypointsOperation extends TagSEAOperation {
	private class CFileFinder implements IResourceVisitor {
		
		private LinkedList<IFile> files;

		CFileFinder() {
			this.files = new LinkedList<IFile>();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
		 */
		public boolean visit(IResource resource) throws CoreException {
			switch (resource.getType()) {
			case IResource.ROOT:
			case IResource.PROJECT:
			case IResource.FOLDER:
				return true;
			case IResource.FILE:
				IFile file = (IFile)resource;
				if (CWaypointUtils.isCFile(file)) //$NON-NLS-1$
					files.add(file);
				break;
			}
			return false;
		}
		
		public List<IFile> getFiles() {
			return files;
		}
		
	}
	/**
	 * 
	 * @param name
	 */
	public LoadWaypointsOperation() {
		super(Messages.getString("LoadWaypointsJob.loadingWaypoints")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus run(IProgressMonitor monitor) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		CWaypointDelegate delegate = (CWaypointDelegate)CWaypointsPlugin.getCWaypointDelegate();
		monitor.beginTask(Messages.getString("LoadWaypointsJob.scanningJava"), 10100); //$NON-NLS-1$
		monitor.subTask(Messages.getString("LoadWaypointsJob.createTemporary")); //$NON-NLS-1$
		delegate.internalRun(new MarkerWaypointLoadOperation(), true);
		
		monitor.subTask(Messages.getString("LoadWaypointsJob.findJava")); //$NON-NLS-1$
		CFileFinder finder = new CFileFinder();
		try {
			root.accept(finder);
		} catch (CoreException e) {
			return e.getStatus();
		}
		monitor.worked(100);
		
		List<IFile> javaFiles = finder.getFiles();
		if (javaFiles.size() > 0)
			delegate.internalRun(new FileWaypointRefreshJob(javaFiles), true);
		monitor.done();
		return Status.OK_STATUS;
	}
	
}
