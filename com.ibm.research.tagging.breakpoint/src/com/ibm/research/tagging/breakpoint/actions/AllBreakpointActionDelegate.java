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
package com.ibm.research.tagging.breakpoint.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.research.tagging.breakpoint.BreakpointWaypoint;

public abstract class AllBreakpointActionDelegate implements IObjectActionDelegate 
{
	private BreakpointWaypoint[] fSelectedBreakpointWaypoints;
	private IStructuredSelection fStructuredSelection;
	private IWorkbenchPart fTargetPart;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) 
	{
		fTargetPart = targetPart;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) 
	{
		fStructuredSelection = (IStructuredSelection)selection;
		Object[] selected = fStructuredSelection.toArray();
		List<BreakpointWaypoint> selectedBreakpoints = new ArrayList<BreakpointWaypoint>();
		
		if(selected.length > 0)
		{
			for(Object o : selected)
			{
				if(o instanceof BreakpointWaypoint)
				{
					BreakpointWaypoint waypoint = (BreakpointWaypoint)o;
					selectedBreakpoints.add(waypoint);
				}
			}
		}
		
		fSelectedBreakpointWaypoints = new BreakpointWaypoint[0];
		fSelectedBreakpointWaypoints = selectedBreakpoints.toArray(fSelectedBreakpointWaypoints);
		selectionChanged(action,fSelectedBreakpointWaypoints,allEnabed(fSelectedBreakpointWaypoints),allDisabled(fSelectedBreakpointWaypoints));
	}
	
	protected void selectionChanged(IAction action,BreakpointWaypoint[] array, boolean allEnabled, boolean allDisabled) 
	{
		//For subclasses
	}

	protected boolean allEnabed(BreakpointWaypoint[] waypoints)
	{
		for(BreakpointWaypoint waypoint : waypoints)
		{
			try 
			{
				if(!waypoint.getBreakpoint().isEnabled())
					return false;
			} 
			catch (CoreException e) 
			{
				e.printStackTrace();
			}
		}
		return true;
	}
	
	protected boolean allDisabled(BreakpointWaypoint[] waypoints)
	{
		for(BreakpointWaypoint waypoint : waypoints)
		{
			try 
			{
				if(waypoint.getBreakpoint().isEnabled())
					return false;
			} 
			catch (CoreException e) 
			{
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public BreakpointWaypoint[] getSelectedBreakpointWaypoints()
	{
		return fSelectedBreakpointWaypoints;
	}
	
	public IStructuredSelection getStructuredSelection()
	{
		return fStructuredSelection;
	}
	
	public IWorkbenchPart getTargetPart()
	{
		return fTargetPart;
	}
}
