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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.xml.WaypointXMLUtilities;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.xml.sax.SAXException;

/**
 * Loads resource waypoints for the given project.
 * @author Del Myers
 */

public class LoadProjectWaypointsOperation extends TagSEAOperation {

	private IProject project;


	/**
	 * 
	 */
	public LoadProjectWaypointsOperation(IProject project) {
		super("Loading Resource Waypoints...");
		this.project = project;
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.TagSEAOperation#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
		monitor.beginTask("Project " + project.getName(), 1);
		if (!project.isAccessible()) {
			monitor.done();
			return Status.OK_STATUS;
		}
		File waypointFile = WaypointXMLUtilities.getWaypointFile(project);
		if (!waypointFile.exists())
			return Status.OK_STATUS;
		MultiStatus status = new MultiStatus(ResourceWaypointPlugin.PLUGIN_ID, 0, "", null);
		Exception ex = null;
		if (waypointFile != null) {
				IResourceWaypointDescriptor[] descriptors;
				try {
					descriptors = WaypointXMLUtilities.loadFile(waypointFile);
					for (IResourceWaypointDescriptor descriptor : descriptors) {
						//previously, the descriptors didn't carry the project name with them. For backward
						//compatability, make sure that the resource in the descriptor begins with 
						//the project name.
						IPath p = new Path(descriptor.getResource());
						if (!project.getFullPath().isPrefixOf(p)) {
							p = project.getFullPath().append(p);
							((IMutableResourceWaypointDescriptor)descriptor).setResource(p.toPortableString());
						}
						CreateWaypointOperation subOp = new CreateWaypointOperation(descriptor);
						TagSEAPlugin.run(subOp, true);
						status.merge(subOp.getStatus());
					}
				} catch (IOException e) {
					ex = e;
				} catch (SAXException e) {
					ex = e;
				}
		}
		if (ex != null) {
			return new Status(
				Status.WARNING,
				ResourceWaypointPlugin.PLUGIN_ID,
				Status.WARNING,
				"Could not load waypoints",
				ex
			);
		}
		monitor.done();
		return status;
	}

	@Override
	public ISchedulingRule getRule() {
		return project;
	}

}