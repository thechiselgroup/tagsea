/*******************************************************************************
 * 
 *   Copyright 2007, 2008, CHISEL Group, University of Victoria, Victoria, BC, Canada. 
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *       The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.mylyn.waypoints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.mylyn.core.HyperLinkDetector;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class WaypointHyperlinkDetector extends HyperLinkDetector {

	private static final String regexp = WaypointHyperlink.LINK_TAG
			+ "\\s?((.|\\s)+)\\s?" + WaypointHyperlink.LINK_TAG;
	public static final Pattern PATTERN = Pattern.compile(regexp,
			Pattern.CASE_INSENSITIVE);

	protected IHyperlink extractHyperlink(ITextViewer viewer, int regionOffset,
			Matcher m) {

		IRegion sregion = getRegion(m, regionOffset, WaypointHyperlink.LINK_TAG);

		IWaypoint waypoint = WaypointsUtils
				.waypointFromText(getWaypointInfo(m));
		if (waypoint == null) {
			this.strikeoutText(viewer, sregion);
			return null;
		} else {
			return new WaypointHyperlink(sregion, waypoint);
		}
	}

	@Override
	protected Pattern getPattern() {
		return PATTERN;
	}

	public String getWaypointInfo(Matcher m) {
		return m.group(1);
	}

	protected IRegion getRegion(Matcher m, int regionOffset, String linkTag) {
		int start = m.start();
		int end = m.end();
		 
		start += (regionOffset + linkTag.length() +1);
		end += regionOffset - (linkTag.length() + 1);
	
		return new Region(start, end - start);
	}

}
