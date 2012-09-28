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

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.IBreakpointsListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.TagCorePlugin;

public class BreakpointListener implements IBreakpointsListener
{
	/**
	 * Notifies this listener that the given breakpoints have been added
	 * to the breakpoint manager.
	 *
	 * @param breakpoints the added breakpoints
	 */
	public void breakpointsAdded(IBreakpoint[] breakpoints)
	{
		for(IBreakpoint breakpoint : breakpoints)
		{
			BreakpointWaypoint waypoint = null;
			
			if(breakpoint instanceof ILineBreakpoint)
				waypoint = new LineBreakpointWaypoint((ILineBreakpoint)breakpoint);
			else
				waypoint = new BreakpointWaypoint(breakpoint);
			
			TagCorePlugin.getDefault().getTagCore().getWaypointModel().addWaypoint(waypoint);
			BreakpointWaypointUtil.applyDefaults(waypoint);
			
			try 
			{
				waypoint.save();
			} 
			catch (CoreException e) 
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * Notifies this listener that the given breakpoints have been removed
	 * from the breakpoint manager.
	 * If a breakpoint has been removed because it has been deleted,
	 * the associated marker delta is also provided.
	 *
	 * @param breakpoints the removed breakpoints
	 * @param deltas the associated marker deltas. Entries may be
	 *  <code>null</code> when a breakpoint is removed from the breakpoint
	 *  manager without being deleted
	 *
	 * @see org.eclipse.core.resources.IMarkerDelta
	 */
	public void breakpointsRemoved(IBreakpoint[] breakpoints, IMarkerDelta[] deltas)
	{
			for(IMarkerDelta delta : deltas)
			{
				if(delta!=null)
				{
					IWaypoint waypoint = TagCorePlugin.getDefault().getTagCore().getWaypointModel().getWaypoint(Long.toString(delta.getId()));

					if(waypoint instanceof BreakpointWaypoint)
						TagCorePlugin.getDefault().getTagCore().getWaypointModel().removeWaypoint(waypoint);
				}
			}
		
		// Often The marker deltas sometimes come back as null so use the breakpoints
		for(IBreakpoint breakpoint : breakpoints)
		{
			IWaypoint waypoint = TagCorePlugin.getDefault().getTagCore().getWaypointModel().getWaypoint(Long.toString(breakpoint.getMarker().getId()));
			
			if(waypoint instanceof BreakpointWaypoint)
				TagCorePlugin.getDefault().getTagCore().getWaypointModel().removeWaypoint(waypoint);
		}
		
	}
	
	/**
	 * Notifies this listener that the given breakpoints have
	 * changed, as described by the corresponding deltas.
	 *
	 * @param breakpoints the changed breakpoints
	 * @param deltas the marker deltas that describe the changes
	 *  with the markers associated with the given breakpoints. Entries
	 *  may be <code>null</code> when a breakpoint change does not generate
	 *  a marker delta
	 *
	 * @see org.eclipse.core.resources.IMarkerDelta
	 */
	public void breakpointsChanged(IBreakpoint[] breakpoints, IMarkerDelta[] deltas)
	{
		
	}
}
