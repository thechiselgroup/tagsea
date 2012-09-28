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
package net.sourceforge.tagsea.parsed.core.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointLocator;
import net.sourceforge.tagsea.core.WaypointMatch;
import net.sourceforge.tagsea.parsed.IParsedWaypointAttributes;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.core.ParsedWaypointUtils;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

/**
 * Checks the waypoint kind to see if it has a locator. If not, then uses some processing based on equality
 * of attributes.
 * @author Del Myers
 *
 */
class ParsedWaypointLocator implements IWaypointLocator {

	public WaypointMatch[] findMatches(Map<String, Object> attributes, SortedSet<String> tagNames) {
		//must have a waypoint kind in the attributes.
		Object kind = attributes.get(IParsedWaypointAttributes.ATTR_KIND);
		if (kind == null) {
			return new WaypointMatch[0];
		}
		IParsedWaypointDefinition def = 
			ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getDefinition(kind.toString());
		if (def == null) {
			return new WaypointMatch[0];
		}
		if (def.getLocator() != null) {
			return def.getLocator().findMatches(attributes, tagNames);
		}
		
		ArrayList<WaypointMatch> matches = new ArrayList<WaypointMatch>();
		
		//run through the different possible attributes according to priority.
		for (IWaypoint wp : TagSEAPlugin.getWaypointsModel().getWaypoints(ParsedWaypointPlugin.WAYPOINT_TYPE)) {
			if (!ParsedWaypointUtils.getKind(wp).equals(kind.toString())) {
				//ignore other kinds.
				continue;
			}
			float score = 0;
			TreeSet<String> wpTags = new TreeSet<String>();
			for (ITag tag : wp.getTags()) {
				wpTags.add(tag.getName());
			}
			SortedSet<String> larger = tagNames;
			SortedSet<String> smaller = wpTags;
			if (tagNames.size() < wpTags.size()) {
				larger = wpTags;
				smaller = tagNames;
			}
			float tagScore = 5f/larger.size();
			for (String name : smaller) {
				if (larger.contains(name)) {
					score += tagScore;
				}
			}
			try {
				String attrResourceString = (String) attributes.get(IParsedWaypointAttributes.ATTR_RESOURCE);
				if (attrResourceString != null) {
					IResource attrResource = 
						ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(attrResourceString));
					if (attrResource != null && attrResource.exists()) {
						IResource wpResource = ParsedWaypointUtils.getResource(wp);
						if (wpResource != null && wpResource.exists()) {
							if (wpResource.equals(attrResource)) {
								score += 10.0;
							}  else if (wpResource.getName().equals(wpResource.getName())) {
								score += 3;
							}
						}
					}
				}
				
			} catch (Exception e) {
				//add nothing if there is an exception
			}
			
			try {
				int wpCharStart = ParsedWaypointUtils.getCharStart(wp);
				int wpCharEnd = ParsedWaypointUtils.getCharEnd(wp);
				//try a proportion from the difference between the offsets.
				int attrCharStart = (Integer)attributes.get(IParsedWaypointAttributes.ATTR_CHAR_START);
				int attrCharEnd = (Integer)attributes.get(IParsedWaypointAttributes.ATTR_CHAR_END);
				
				int offsetDiff = Math.abs(wpCharStart - attrCharStart);
				int lengthDiff = Math.abs((wpCharEnd - wpCharStart) - (attrCharEnd-attrCharStart));
				
				if (lengthDiff == 0) {
					//add one point
					score += 1;
				} else {
					score += 1.0/lengthDiff;
				}
				
				if (offsetDiff == 0) {
					score += 1;
				} else {
					score += 1.0/offsetDiff;
				}
			} catch (Exception e) {
				//ad no points if there was an exception because of a null value, or
				//a class cast.
			}
			
			try {
				String attrMessage = (String) attributes.get(IParsedWaypointAttributes.ATTR_MESSAGE);
				if (attrMessage != null) {
					String wpMessage = wp.getText();
					if (attrMessage.equals(wpMessage)) {
						score += 2;
					}
				}
			} catch (Exception e) {
				
			}
			
			try {
				Date attrDate = (Date)attributes.get(IParsedWaypointAttributes.ATTR_DATE);
				Date wpDate = wp.getDate();
				if (attrDate != null) {
					long diff = Math.abs(attrDate.getTime() - wpDate.getTime());
					if (diff == 0) {
						score += 1;
					} else {
						score += 1/diff;
					}
				}
			} catch (Exception e) {
				
			}
			

			Object attrDomain = attributes.get(IParsedWaypointAttributes.ATTR_DOMAIN);
			if (attrDomain != null) {
				if (attrDomain.equals(ParsedWaypointUtils.getDomainDetail(wp))) {
					score += 5;
				}
			}
			
			if (score != 0.0) {
				float certainty = score/25;
				if (certainty > 0.005) {
					matches.add(new WaypointMatch(attributes, tagNames, wp, certainty));
				}
			}
			
		}
		Collections.sort(matches);
		return matches.toArray(new WaypointMatch[matches.size()]);
	}

}
