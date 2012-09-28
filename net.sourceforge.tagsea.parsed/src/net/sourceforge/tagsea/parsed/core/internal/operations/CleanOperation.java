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
package net.sourceforge.tagsea.parsed.core.internal.operations;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.internal.WaypointParsingTools;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * Cleans a resource and its children for parsed waypoints. NOT MEANT TO BE CALLED OUTSIDE OF ParsedWaypointRegistry.
 * @author Del Myers
 *
 */
public class CleanOperation extends AbstractWaypointUpdateOperation {

	private IResource resource;
	private int depth;
	/**
	 * Gathers immediate children
	 * @author Del Myers
	 *
	 */
	private class ChildGatherer implements IResourceVisitor {
		boolean root = true;
		private List<IResource> children = new LinkedList<IResource>();
		public boolean visit(IResource resource) throws CoreException {
			boolean oldRoot = root;
			if (root) {
				root = false;
			} else {
				//check to see if it is a text file
				if (resource instanceof IFile) {
					IContentType textType = Platform.getContentTypeManager().getContentType("org.eclipse.core.runtime.text");
					IContentType[] types = 
						Platform.getContentTypeManager().findContentTypesFor(resource.getName());
					for (IContentType type : types) {
						if (type.isKindOf(textType)) {
							children.add(resource);
							break;
						}
					}
				} else {
					children.add(resource);
				}
				
			}
			return oldRoot;
		}
		/**
		 * @return the children
		 */
		public List<IResource> getChildren() {
			return children;
		}
	}

	/**
	 * @param name
	 */
	public CleanOperation(String name, IResource resource, int depth) {
		super(name);
		this.resource = resource;
		this.depth = depth;
	}

	@Override
	public IStatus run(IProgressMonitor monitor)
			throws InvocationTargetException {
		if (this.resource instanceof IFile) {
			monitor.beginTask("Parsing file " + resource.getFullPath(), 100);
			IStatus s = TagSEAPlugin.syncRun(new ParseFileOperation((IFile)resource), new NullProgressMonitor());
			monitor.done();
			return s;
		} else if (this.resource instanceof IContainer) {
			if (!this.resource.isAccessible()) {
				monitor.done();
				return Status.OK_STATUS;
			}
			if (this.depth != IResource.DEPTH_ZERO) {
				//clean up dead waypoints.
				IWaypoint[] allWaypoints = 
					ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().findWaypoints(resource, depth);
				for (IWaypoint wp : allWaypoints) {
					IResource wpResource =
						ParsedWaypointPlugin.
							getDefault().
							getParsedWaypointRegistry().
							getFileForWaypoint(wp);
					if (wpResource == null || !wpResource.isAccessible()) {
						WaypointParsingTools.deleteWaypoint(wp);
					}
				}
				//get all of the files.
				ChildGatherer gatherer = new ChildGatherer();
				MultiStatus status = new MultiStatus(ParsedWaypointPlugin.PLUGIN_ID, 0, "Parsing children", null);
				try {
					resource.accept(gatherer);
					monitor.beginTask("Cleaning resource " + this.resource.getFullPath(), gatherer.getChildren().size());
					for (IResource child : gatherer.getChildren()) {
						status.merge(recursiveParseChildren(child, depth, monitor));
						monitor.worked(1);
						if (monitor.isCanceled()) {
							return new Status(Status.WARNING, ParsedWaypointPlugin.PLUGIN_ID, "Unfinished parsing of files");
						}
					}
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				}
				return status;
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * @param child
	 * @param depth2
	 * @throws CoreException 
	 */
	private IStatus recursiveParseChildren(IResource resource, int depth, IProgressMonitor monitor) throws CoreException {
		monitor.subTask("processing resource " + resource.getFullPath());
		if (resource instanceof IFile) {
			return TagSEAPlugin.syncRun(new ParseFileOperation((IFile)resource), new NullProgressMonitor()); 
		} else if (resource instanceof IContainer && depth == IResource.DEPTH_INFINITE) {
			if (resource.isAccessible()){
				MultiStatus status = new MultiStatus(ParsedWaypointPlugin.PLUGIN_ID, 0, "Parsing children", null);
				ChildGatherer gatherer = new ChildGatherer();
				resource.accept(gatherer);
				for (IResource child : gatherer.getChildren()) {
					status.merge(recursiveParseChildren(child, depth, monitor));
					monitor.worked(1);
				}
				return status;
			}
		}
		return Status.OK_STATUS;
	}
	
	@Override
	public ISchedulingRule getRule() {
		return resource.getWorkspace().getRuleFactory().modifyRule(resource);
	}

}
