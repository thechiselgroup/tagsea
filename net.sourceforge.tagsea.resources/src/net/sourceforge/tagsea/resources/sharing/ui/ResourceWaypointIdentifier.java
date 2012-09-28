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
package net.sourceforge.tagsea.resources.sharing.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;

/**
 * An identifier for resource waypoints.
 * @author Del Myers
 *
 */
public class ResourceWaypointIdentifier implements IWaypointIdentifier {
	
	private HashMap<String, Object> attributes;
	private SortedSet<String> tagNames;
	
	public ResourceWaypointIdentifier(IWaypoint waypoint) {
		attributes = new HashMap<String, Object>();
		tagNames = new TreeSet<String>();
		for (String a : waypoint.getAttributes()) {
			Object v = waypoint.getValue(a);
			if (v != null) {
				attributes.put(a, v);
			}
		}
		for (ITag tag : waypoint.getTags()) {
			tagNames.add(tag.getName());
		}
	}
	
	public ResourceWaypointIdentifier(IResourceWaypointDescriptor descriptor) {
		attributes = new HashMap<String, Object>();
		tagNames = new TreeSet<String>();
		for (String a : descriptor.getAttributes()) {
			Object v = descriptor.getValue(a);
			if (v != null) {
				attributes.put(a, v);
			}
		}
		tagNames.addAll(descriptor.getTags());
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointIdentifier#getWaypointType()
	 */
	public String getWaypointType() {
		return ResourceWaypointPlugin.WAYPOINT_ID;
	}

	/**
	 * Returns the stamp id for the waypoint.
	 * @return the stamp id for the waypoint.
	 */
	public String getStamp() {
		return (String) attributes.get(IResourceWaypointAttributes.ATTR_STAMP);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!obj.getClass().isAssignableFrom(getClass())) return false;
		return ((ResourceWaypointIdentifier)obj).getStamp().equals(getStamp());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getStamp().hashCode();
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public SortedSet<String> getTagNames() {
		return tagNames;
	}
	
}
