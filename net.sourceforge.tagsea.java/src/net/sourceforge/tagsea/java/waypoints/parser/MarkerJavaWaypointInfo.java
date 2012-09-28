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
package net.sourceforge.tagsea.java.waypoints.parser;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.java.IJavaWaypointAttributes;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

/**
 * Waypoint info based on a marker.
 * @author Del Myers
 */

public class MarkerJavaWaypointInfo extends JavaWaypointInfo {

	private IMarker marker;

	/**
	 * 
	 */
	public MarkerJavaWaypointInfo(IMarker marker) {
		this.marker = marker;
	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getAttributes()
	 */
	public Map<String, String> getAttributes() {
		TreeMap<String, String> attributes = new TreeMap<String, String>();
		try {
			Map<?, ?> markerAttributes = marker.getAttributes();
			int start = 0;
			int end = 0;
			for (Iterator<?> iter = markerAttributes.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				if (IMarker.CHAR_START.equals(key)) {
					start = (Integer)markerAttributes.get(key);
				} else if (IMarker.CHAR_END.equals(key)) {
					end = (Integer)markerAttributes.get(key);
				} else if (IMarker.MESSAGE.equals(key)) {
					attributes.put(IWaypoint.ATTR_MESSAGE, markerAttributes.get(key).toString());
				} else {
					attributes.put(key, markerAttributes.get(key).toString());
				}		
			}
			attributes.put(IJavaWaypointAttributes.ATTR_CHAR_START, ""+start);
			attributes.put(IJavaWaypointAttributes.ATTR_CHAR_END, ""+(end));
			attributes.put(IJavaWaypointAttributes.ATTR_RESOURCE, marker.getResource().getFullPath().toPortableString());
		} catch (CoreException e) {
		}
		
		return attributes;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getDescription()
	 */
	public String getDescription() {
		return getAttributes().get(IWaypoint.ATTR_MESSAGE);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getTags()
	 */
	public String[] getTags() {
		String tagString = getAttributes().get("tags");
		if ("".equals(tagString) || tagString == null) {
			return new String[0];
		}
		return tagString.split("\\w+");
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getOffset()
	 */
	public int getOffset() {
		return marker.getAttribute(IMarker.CHAR_START, -1);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getLength()
	 */
	public int getLength() {
		return marker.getAttribute(IMarker.CHAR_END, -1) - getOffset();
	}

}
