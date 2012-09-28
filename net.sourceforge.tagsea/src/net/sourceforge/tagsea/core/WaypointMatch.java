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
package net.sourceforge.tagsea.core;

import java.util.Map;
import java.util.SortedSet;
import java.util.Map.Entry;


/**
 * Describes a match between a list of attributes and a waypoint.
 * @author Del Myers
 *
 */
public class WaypointMatch implements Comparable<WaypointMatch> {
	private final Map<String, Object> attributes;
	private final float certainty;
	private final IWaypoint waypoint;
	private SortedSet<String> tagNames;
	
	/**
	 * Creates a matching between the given attributes and tagNames and the given waypoint with the
	 * given certainty. Certainties on waypoint matchings don't have to be unique, but should reflect
	 * how close of a match there is between the given attributes and tagNames and the waypoint.
	 * 
	 * The attributes and tagNames passed to this match are considered immutable and should not be
	 * changed after being set in this constructor.
	 */
	public WaypointMatch(Map<String, Object> attributes, SortedSet<String> tagNames, IWaypoint waypoint, float certainty) {
		this.attributes = attributes;
		this.waypoint = waypoint;
		this.certainty = certainty;
		this.tagNames = tagNames;
	}
	
	/**
	 * 
	 * @return the attributes used to make this match.
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	/**
	 * @return the tag names
	 */
	public SortedSet<String> getTagNames() {
		return tagNames;
	}
	
	/**
	 * @return the waypoint the waypoint matched.
	 */
	public IWaypoint getWaypoint() {
		return waypoint;
	}
	
	/**
	 * @return the certainty the certainty that this waypoint is the correct one. A number between 0 and 1.
	 */
	public float getCertainty() {
		return certainty;
	}

	public int compareTo(WaypointMatch that) {
		int thisCertainty = (int) (this.getCertainty() * 1000);
		int thatCertainty = (int) (that.getCertainty() * 1000);
		int diff = thisCertainty - thatCertainty;
		if (diff == 0) {
			if (this.getAttributes().equals(that.getAttributes())) {
				if (this.getTagNames().equals(that.getTagNames())) {
					return 0;
				} else {
					String thisCompareString = "";
					String thatCompareString = "";
					for (String s : this.getTagNames()) {
						thisCompareString += s;
					}
					for (String s : that.getTagNames()) {
						thatCompareString += s;
					}
					return -thisCompareString.compareTo(thatCompareString);
				}
			} else {
				String thisCompareString = "";
				String thatCompareString = "";
				for (Entry<String, Object> e : this.getAttributes().entrySet()) {
					thisCompareString += e.getKey().toString()+e.getValue().toString(); 
				}
				for (Entry<String, Object> e : that.getAttributes().entrySet()) {
					thatCompareString += e.getKey().toString()+e.getValue().toString(); 
				}
				return -thisCompareString.compareTo(thatCompareString);
			}
		}
		return -diff;
	}
}
