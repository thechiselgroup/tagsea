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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

/**
 * Waypoint info that also carries the region information for tags and attributes.
 * @author Del Myers
 */

public class ParsedJavaWaypointInfo implements IParsedJavaWaypointInfo {
	
	private TreeMap<String, Set<IRegion>> tagRegionMap;
	private TreeMap<String, IRegion> attributeRegionMap;
	private TreeMap<String, String> attributes;
	private List<WaypointParseProblem> problems;
	private String waypointData;
	private String description;
	private IRegion descriptionRegion;
	private int offset;
	
	public ParsedJavaWaypointInfo(String waypointData, int offset) {
		this.waypointData = waypointData;
		tagRegionMap = new TreeMap<String, Set<IRegion>>();
		attributeRegionMap = new TreeMap<String, IRegion>();
		attributes = new TreeMap<String, String>();
		problems = new LinkedList<WaypointParseProblem>();
		this.offset = offset;
	}

	public void putAttribute(String key, String value, IRegion region) {
		attributes.put(key, value);
		attributeRegionMap.put(key, region);
	}
	
	public void putTag(String tag, IRegion region) {
		Set<IRegion> regions = tagRegionMap.get(tag);
		if (regions == null) {
			regions = new HashSet<IRegion>();
			tagRegionMap.put(tag, regions);
		} 
		regions.add(region);
	}
	
	public void setDescription(String description, IRegion region) {
		this.description = description;
		this.descriptionRegion = region;
	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getAttributes()
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getTags()
	 */
	public String[] getTags() {
		return tagRegionMap.keySet().toArray(new String[tagRegionMap.keySet().size()]);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo#getWaypointData()
	 */
	public String getWaypointData() {
		return waypointData;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo#getRegionsForTag(java.lang.String)
	 */
	public IRegion[] getRegionsForTag(String tag) {
		Set<IRegion> regions = tagRegionMap.get(tag);
		if (regions != null) {
			return regions.toArray(new IRegion[regions.size()]);
		}
		return new IRegion[0];
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo#getRegionForAttribute(java.lang.String)
	 */
	public IRegion getRegionForAttribute(String key) {
		return attributeRegionMap.get(key);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo#getProblems()
	 */
	public WaypointParseProblem[] getProblems() {
		return problems.toArray(new WaypointParseProblem[problems.size()]);
	}

	/**
	 * Adds a problem for the given exception.
	 * @param e
	 */
	public void caughtException(MalformedWaypointException e) {
		problems.add(new WaypointParseProblem(e.getMessage(), new Region(e.getOffset(), e.getLength())));		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo#getRegionForDescription()
	 */
	public IRegion getRegionForDescription() {
		return descriptionRegion;
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo#getOffset()
	 */
	public int getOffset() {
		return offset;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getLength()
	 */
	public int getLength() {
		return getWaypointData().length();
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getAuthor()
	 */
	public String getAuthor() {
		return getAttributes().get(IWaypoint.ATTR_AUTHOR);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getDate()
	 */
	public Date getDate() {
		String date = getAttributes().get(IWaypoint.ATTR_DATE);
		if (date == null) return null;
		//standard way of getting the date: two characters for local, two characters for
		//country, colon, short date.
		int colon = date.indexOf(':');
		Date result = null;
		if (colon == 4) {
			//interpret the locale.
			String lc = date.substring(0,2);
			String cn = date.substring(2,4);
			date = date.substring(colon+1);
			Locale locale = new Locale(lc, cn);
			try {
				result = DateFormat.getDateInstance(DateFormat.SHORT, locale).parse(date);
			} catch (ParseException e) {
			}
		} else if (colon == -1) {
			try {
				result = DateFormat.getDateInstance(DateFormat.SHORT).parse(date);
			} catch (ParseException e) {
			}
		}
		return result;
	}
}
