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

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

// @tag tagsea.mylyn.hyperlink : Example of a hyperlink object
public class WaypointHyperlink implements IHyperlink {

	public static final String LINK_TAG = "<wp>";
	private final IRegion region;
	private final IWaypoint waypoint;

	public WaypointHyperlink(IRegion sregion, IWaypoint waypoint) {
		this.region = sregion;
		this.waypoint = waypoint;

	}

	public IRegion getHyperlinkRegion() {
		return this.region;
	}

	public String getHyperlinkText() {
		return "Go to waypoint";
	}

	public String getTypeLabel() {
		return "Waypoint Hyperlink";
	}

	public void open() {
		if (waypoint != null) {
			TagSEAPlugin.getDefault().navigate(waypoint);
		}
	}

}
