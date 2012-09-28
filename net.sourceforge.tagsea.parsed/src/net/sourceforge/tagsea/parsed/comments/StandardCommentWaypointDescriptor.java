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
package net.sourceforge.tagsea.parsed.comments;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.parsed.parser.IParsedWaypointDescriptor;

import org.eclipse.jface.text.IRegion;

/**
 * Waypoint info that also carries the region information for tags and attributes.
 * @author Del Myers
 */

public class StandardCommentWaypointDescriptor implements IParsedWaypointDescriptor {
	
	private TreeMap<String, Set<IRegion>> tagRegionMap;
	private TreeMap<String, IRegion> attributeRegionMap;
	private TreeMap<String, String> attributes;
	private String waypointData;
	private String description;
	private IRegion descriptionRegion;
	private int offset;
	private String detail;
	private int line;
	
	public static final int UNKNOWN_CONTEXT = 0;
	public static final int TAG_CONTEXT = 1;
	public static final int ATTRIBUTE_CONTEXT = 2;
	public static final int ATTRIBUTE_VALUE_CONTEXT = 3;
	public static final int MESSAGE_CONTEXT = 4;
	
	/**
	 * Creates a new waypoint descriptor that contains the given text and starts at the given
	 * offset in the document.
	 * @param text the text for the waypoint.
	 * @param offset the offset in the document at which the waypoint starts.
	 * @param line the line in the document at which the waypoint starts.
	 */
	StandardCommentWaypointDescriptor(String text, int offset, int line) {
		this.waypointData = text;
		tagRegionMap = new TreeMap<String, Set<IRegion>>();
		attributeRegionMap = new TreeMap<String, IRegion>();
		attributes = new TreeMap<String, String>();
		this.offset = offset;
		this.line = line;
	}

	/**
	 * Records the given attribute at the given location in the text (relative to the start of the
	 * text, not the document).
	 * @param key
	 * @param value
	 * @param region
	 */
	void putAttribute(String key, String value, IRegion region) {
		attributes.put(key, value);
		attributeRegionMap.put(key, region);
	}
	
	/**
	 * Records the given tag at the given location in the text (relative to the start of the text, not
	 * the document).
	 * @param tag
	 * @param region
	 */
	void putTag(String tag, IRegion region) {
		Set<IRegion> regions = tagRegionMap.get(tag);
		if (regions == null) {
			regions = new HashSet<IRegion>();
			tagRegionMap.put(tag, regions);
		} 
		regions.add(region);
	}
	
	void setMessage(String description, IRegion region) {
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
	public String getMessage() {
		return description;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getTags()
	 */
	public String[] getTags() {
		return tagRegionMap.keySet().toArray(new String[tagRegionMap.keySet().size()]);
	}
	
	/**
	 * Returns the text that represents the waypoint.
	 * @return
	 */
	public String getText() {
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
	
	/**
	 * Returns the region of text that represents the given attribute relative to the beginning of the
	 * text.
	 * @param key
	 * @return
	 */
	public IRegion getRegionForAttribute(String key) {
		return attributeRegionMap.get(key);
	}
	

	/**
	 * Returns the region of text that represents the message relative to the beginning of the text.
	 * @return
	 */
	public IRegion getRegionForMessage() {
		return descriptionRegion;
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo#getOffset()
	 */
	public int getCharStart() {
		return offset;
	}
	
	public int getCharEnd() {
		return getCharStart() + getLength();
	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getLength()
	 */
	public int getLength() {
		return getText().length();
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getAuthor()
	 */
	public String getAuthor() {
		return getAttributes().get(IWaypoint.ATTR_AUTHOR);
	}

	public Date getDate() {
		return parseDate(getAttributes().get(IWaypoint.ATTR_DATE));
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getDate()
	 */
	public static Date parseDate(String date) {
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

	public String getDetail() {
		return detail;
	}
	
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	/**
	 * Returns the context at a given character index in the parsed descriptor. One of:
	 * {@link #UNKNOWN_CONTEXT}
	 * {@link #TAG_CONTEXT}
	 * {@link #ATTRIBUTE_CONTEXT}
	 * {@link #ATTRIBUTE_VALUE_CONTEXT}
	 * {@link #UNKNOWN_CONTEXT}
	 * @param index
	 * @return
	 */
	public int getContextAt(int index) {
		if (descriptionRegion != null) {
			if (!"".equals(description) && descriptionRegion.getOffset() <= index) {
				return MESSAGE_CONTEXT;
			}
		}
		if (index >= 0 && index <= getText().length()) {	
			for (String key : tagRegionMap.keySet()) {
				Set<IRegion> regions = tagRegionMap.get(key);
				for (IRegion region : regions) {
					if (region.getOffset() <= index && index <= region.getOffset()+region.getLength()) {
						return TAG_CONTEXT;
					}
				}
			}
			for (String key : attributeRegionMap.keySet()) {
				IRegion region = attributeRegionMap.get(key);
				if (region.getOffset() <= index && index <= region.getOffset()+region.getLength()) {
					if (region.getOffset()+key.length()+2 < index) {
						return ATTRIBUTE_VALUE_CONTEXT;
					}
					return ATTRIBUTE_CONTEXT;
				}
			}
		}
		return UNKNOWN_CONTEXT;
	}

	public int getLine() {
		return line;
	}

}
