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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.research.tagging.breakpoint.BreakpointWaypoint;

public class DisableAllBreakpointActionDelegate extends AllBreakpointActionDelegate
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) 
	{
		BreakpointWaypoint[] waypoints = getSelectedBreakpointWaypoints();
		
		for(BreakpointWaypoint waypoint : waypoints)
		{
			try 
			{
				waypoint.setEnabled(false);
			} 
			catch (CoreException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void selectionChanged(IAction action, BreakpointWaypoint[] array, boolean allEnabled, boolean allDisabled) 
	{
		if(allDisabled)
			action.setEnabled(false);
	}
}
