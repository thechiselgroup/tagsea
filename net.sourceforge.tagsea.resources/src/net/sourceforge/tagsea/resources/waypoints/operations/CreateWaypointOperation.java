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
package net.sourceforge.tagsea.resources.waypoints.operations;

import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;

/**
 * Creates a waypoint for the IResourceWaypointDescriptor given in the constructor.
 * @author Del Myers
 *
 */
public class CreateWaypointOperation extends TagSEAOperation implements IInternalUpdateOperation {

	private IResourceWaypointDescriptor descriptor;
	private IWaypoint waypoint;
	private IStatus status;
	public CreateWaypointOperation(IResourceWaypointDescriptor descriptor) {
		super("Creating Resource Waypoint");
		this.descriptor = descriptor;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.TagSEAOperation#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus run(IProgressMonitor monitor)
			throws InvocationTargetException {
		monitor.beginTask("Creating Waypoint...", 1);
		SortedSet<String> tags = descriptor.getTags();
		IWaypoint waypoint = TagSEAPlugin.getWaypointsModel().createWaypoint(ResourceWaypointPlugin.WAYPOINT_ID, tags.toArray(new String[tags.size()]));
		MultiStatus status = new MultiStatus(ResourceWaypointPlugin.PLUGIN_ID, 0, "", null);

		if (descriptor.getDate() != null) {
			status.merge(waypoint.setDate(descriptor.getDate()).getStatus());
		}
		if (descriptor.getAuthor() != null) {
			status.merge(waypoint.setAuthor(descriptor.getAuthor()).getStatus());
		}
		if (descriptor.getText() != null) {
			status.merge(waypoint.setText(descriptor.getText()).getStatus());
		}
		if (descriptor.getStamp() != null) {
			status.merge(waypoint.setStringValue(IResourceWaypointAttributes.ATTR_STAMP, descriptor.getStamp()).getStatus());
		}
		if (descriptor.getRevision() != null) {
			status.merge(waypoint.setStringValue(IResourceWaypointAttributes.ATTR_REVISION, descriptor.getRevision()).getStatus());
		}
		status.merge(waypoint.setIntValue(IResourceWaypointAttributes.ATTR_CHAR_START,descriptor.getCharStart()).getStatus());
		status.merge(waypoint.setIntValue(IResourceWaypointAttributes.ATTR_CHAR_END,descriptor.getCharEnd()).getStatus());
		
		status.merge(waypoint.setIntValue(IResourceWaypointAttributes.ATTR_LINE, descriptor.getLine()).getStatus());

		Path resourcePath = new Path(descriptor.getResource());
		status.merge(waypoint.setStringValue(IResourceWaypointAttributes.ATTR_RESOURCE, resourcePath.toPortableString()).getStatus());
		monitor.worked(1);
		monitor.done();
		this.status = status;
		this.waypoint = waypoint;
		return status;
	}
	
	/**
	 * If the thread was told to wait until the operation was complete, this method returns the status of 
	 * running the operation. May be null otherwise.
	 * @return the status
	 */
	public IStatus getStatus() {
		return status;
	}
	
	/**
	 * If the thread was told to wait until the operation was complete, this method returns the waypoint created.
	 * May be null otherwise.
	 * @return the waypoint.
	 */
	public IWaypoint getWaypoint() {
		return waypoint;
	}

}
