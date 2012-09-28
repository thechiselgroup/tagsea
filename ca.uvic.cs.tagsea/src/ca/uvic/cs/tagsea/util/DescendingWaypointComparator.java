/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.util;

import java.util.Comparator;

import org.eclipse.core.resources.IMarker;

import ca.uvic.cs.tagsea.core.Waypoint;

/**
 * Sorts waypoints based on their resource filenames.  For waypoints that are in the same file,
 * they are sorted based on line number in reverse order - waypoints at the bottom of a file
 * will come before the waypoints at the top of a file.
 * 
 * @author Chris Callendar
 */
public class DescendingWaypointComparator implements Comparator<Waypoint> {

	public int compare(Waypoint wp1, Waypoint wp2) {
		int compare = 0;
		try {
			IMarker m1 = wp1.getMarker(), m2 = wp2.getMarker();
			if ((m1 != null) && (m2 != null) && m1.exists() && m2.exists()) {
				compare = m1.getResource().getName().compareTo(m2.getResource().getName());
				if (compare == 0) {
					// same resource, so compare by their position in the file.  
					compare = (Integer)m2.getAttribute(IMarker.CHAR_START) - (Integer)m1.getAttribute(IMarker.CHAR_START);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return compare;
	}
	
}
