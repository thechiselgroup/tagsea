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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;

import com.ibm.research.tagging.core.ITagCore;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.IWaypointModelExtension;
import com.ibm.research.tagging.core.TagCorePlugin;

public class BreakpointWaypointModelExtension implements IWaypointModelExtension
{		
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModelExtension#initialize(com.ibm.research.tagging.core.ITagModel)
	 */
	public void initialize(ITagCore core) 
	{
		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
		manager.addBreakpointListener(new BreakpointListener());
		
		TagCorePlugin.getDefault().getTagCore().getTagModel().addTagModelListener(new TagModelListener());
		TagCorePlugin.getDefault().getTagCore().getWaypointModel().addWaypointModelListener(new WaypointModelListener());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModelExtension#loadWaypoints()
	 */
	public void loadWaypoints() 
	{
		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
		IBreakpoint[] breakpoints = manager.getBreakpoints();
		
		for(IBreakpoint breakpoint : breakpoints)
		{
			BreakpointWaypoint waypoint = null;
			
			if(breakpoint instanceof ILineBreakpoint)
				waypoint = new LineBreakpointWaypoint((ILineBreakpoint)breakpoint);
			else
				waypoint = new BreakpointWaypoint(breakpoint);
			
			// Pull data from the breakpoint marker
			waypoint.load();

			TagCorePlugin.getDefault().getTagCore().getWaypointModel().addWaypoint(waypoint);
			
			// we assume that this is a new waypoint that we dont know about
			if(waypoint.getTags().length == 0)
				BreakpointWaypointUtil.applyDefaults(waypoint);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModelExtension#saveWaypoints()
	 */
	public void saveWaypoints() 
	{
		IWaypoint[] waypoints = TagCorePlugin.getDefault().getTagCore().getWaypointModel().getWaypoints();
		
		for(IWaypoint waypoint : waypoints)
		{
			if(waypoint instanceof BreakpointWaypoint)
			{
				try
				{
					// flush the local data to the marker
					((BreakpointWaypoint)waypoint).save();
				} 
				catch (CoreException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}
