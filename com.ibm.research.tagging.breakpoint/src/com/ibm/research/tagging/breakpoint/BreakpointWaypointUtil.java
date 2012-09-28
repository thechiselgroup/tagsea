/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tagging.breakpoint;

import java.util.Date;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.ui.texteditor.MarkerUtilities;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.TagCorePlugin;

public class BreakpointWaypointUtil 
{
	public static final String MARKER_ATTR_WAYPOINTED   = "com.ibm.research.tagging.breakpoint.waypointed";

	public static void applyDefaults(BreakpointWaypoint waypoint)
	{
		// Add the default break and debug tags to each debug waypoint
		ITag breakpointTag = TagCorePlugin.getDefault().getTagCore().getTagModel().addTag(BreakpointWaypoint.BREAKPOINT_TAG);
		ITag debugTag = TagCorePlugin.getDefault().getTagCore().getTagModel().addTag(BreakpointWaypoint.DEBUG_TAG);
		waypoint.addTag(breakpointTag);
		waypoint.addTag(debugTag);
		
		// Set the default data values
		waypoint.setDate(new Date());
		waypoint.setDescription(MarkerUtilities.getMessage(waypoint.getBreakpoint().getMarker()));
		waypoint.setAuthor(System.getProperty("user.name"));

	}
}
