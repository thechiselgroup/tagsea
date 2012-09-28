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
package net.sourceforge.tagsea.resources.waypoints.xml;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import net.sourceforge.tagsea.IWaypointType;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.ResourceWaypointDescriptor;

import org.eclipse.ui.IMemento;

/**
 * Class that shares waypoints using mementos
 * @author Del Myers
 *
 */
public class WaypointShareUtilities {
	
	/**
	 * Writes the given waypoint descriptor to the given memento.
	 * @param descriptor the descriptor to write.
	 * @param memento the memento to write it to.
	 */
	public static void writeWaypoint(IResourceWaypointDescriptor descriptor, IMemento memento) {
		String[] attributes = descriptor.getAttributes();
		for (String attr : attributes) {
			Object value = descriptor.getValue(attr);
			if (value instanceof String) {
				memento.putString(attr, (String)value);
			} else if (value instanceof Integer) {
				memento.putInteger(attr, (Integer)value);
			} else if (value instanceof Boolean) {
				memento.putString(attr, value.toString());
			} else if (value instanceof Date) {
				Date d = (Date) value;
				memento.putString(attr, WaypointXMLUtilities.getDateString(d));
			}
		}
		SortedSet<String> tags = descriptor.getTags();
		for (String tag : tags) {
			IMemento tagM = memento.createChild("tag");
			tagM.putTextData(tag);
		}
	}
	
	/**
	 * Reads the memento and creates a descriptor for it.
	 * @param memento the memento to read.
	 * @return the descriptor created, or null if the memento could not be read.
	 */
	public static IResourceWaypointDescriptor readMemento(IMemento memento) {
		ResourceWaypointDescriptor desc = new ResourceWaypointDescriptor();
		IWaypointType type = TagSEAPlugin.getDefault().getWaypointType(ResourceWaypointPlugin.WAYPOINT_ID);
		for (String attr : desc.getAttributes()) {
			Class aType = type.getAttributeType(attr);
			Object value = null;
			if (aType.equals(Date.class)) {
				String s = memento.getString(attr);
				if (s != null) {
					try {
						value = WaypointXMLUtilities.parseDate(s);
					} catch (ParseException e) {
						return null;
					}
				}
			} else if (aType.equals(Boolean.class)) {
				String s = memento.getString(attr);
				if (s != null) {
					value = Boolean.valueOf(s);
				}
			} else if (aType.equals(String.class)) {
				value = memento.getString(attr);
			} else if (aType.equals(Integer.class)) {
				value = memento.getInteger(attr);
			}
			if (value != null) {
				desc.setValue(attr, value);
			}
		}
		IMemento[] tagMs = memento.getChildren("tag");
		for (IMemento tagM : tagMs) {
			desc.addTag(tagM.getTextData());
		}
		return desc;
	}
	
	/**
	 * Writes the given waypoints out to an IMemento.
	 * @param descriptors the descriptors to write.
	 * @param memento the memento to write to.
	 */
	public static void writeWaypoints(List<IResourceWaypointDescriptor> descriptors, IMemento memento) {
		for (IResourceWaypointDescriptor desc : descriptors) {
			writeWaypoint(desc, memento.createChild("waypoint"));
		}
	}
	
	/**
	 * Reads the waypoints from the given memento. All waypoint mementos are expected to have the type "waypoint"
	 * @param memento the memento to read.
	 * @return the list of descriptors.
	 */
	public static List<IResourceWaypointDescriptor> readWaypoints(IMemento memento) {
		IMemento[] waypointMememtos = memento.getChildren("waypoint");
		List<IResourceWaypointDescriptor> descriptors = new ArrayList<IResourceWaypointDescriptor>();
		for (IMemento wpM : waypointMememtos) {
			IResourceWaypointDescriptor desc = readMemento(wpM);
			if (desc != null) {
				descriptors.add(desc);
			}
		}
		return descriptors;
	}

}
