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
package net.sourceforge.tagsea.java.waypoints;

import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.java.JavaTagsPlugin;
import net.sourceforge.tagsea.java.resources.internal.FileWaypointRefreshJob;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * Job for loading java waypoints.
 * @author Del Myers
 */

public class LoadWaypointsOperation extends JavaWaypointDelegate.InternalOperation {
	private class JavaFileFinder implements IResourceVisitor {
		
		private LinkedList<IFile> files;

		JavaFileFinder() {
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
				if ("java".equals(file.getFileExtension())) //$NON-NLS-1$
					files.add(file);
				break;
			}
			return false;
		}
		
		public List<IFile> getFiles() {
			return files;
		}
		
	}


	private IProject project;
	/**
	 * 
	 * @param name
	 */
	public LoadWaypointsOperation(IProject project) {
		super(Messages.getString("LoadWaypointsJob.loadingWaypoints" + " " + project.getName())); //$NON-NLS-1$
		this.project = project;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("LoadWaypointsJob.scanningJava"), 10100); //$NON-NLS-1$
		if (!project.isAccessible()) {
			monitor.done();
			return new Status(IStatus.OK, JavaTagsPlugin.PLUGIN_ID, project.getName() + " is not accessable.");
		}
		MultiStatus status = new MultiStatus(JavaTagsPlugin.PLUGIN_ID, IStatus.INFO, "Loading Java Waypoints", null);
		monitor.subTask(Messages.getString("LoadWaypointsJob.findJava")); //$NON-NLS-1$
		JavaFileFinder finder = new JavaFileFinder();
		try {
			project.accept(finder);
		} catch (CoreException e) {
			return e.getStatus();
		}
		monitor.worked(100);
		
		List<IFile> javaFiles = finder.getFiles();
		if (javaFiles.size() > 0) {
			status.merge(TagSEAPlugin.syncRun(new FileWaypointRefreshJob(javaFiles), new SubProgressMonitor(monitor,10000)));
		}
		monitor.done();
		return status;
	}
	
	
	@Override
	public ISchedulingRule getRule() {
		return ResourcesPlugin.getWorkspace().getRuleFactory().buildRule();
	}
}
