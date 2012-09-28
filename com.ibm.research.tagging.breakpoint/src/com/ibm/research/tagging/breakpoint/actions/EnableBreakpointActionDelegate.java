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

import com.ibm.research.tagging.breakpoint.BreakpointWaypoint;

public class EnableBreakpointActionDelegate extends AllBreakpointActionDelegate
{
	public void run(IAction action) 
	{
		if(getSelectedBreakpointWaypoints().length == 1)
		{
			BreakpointWaypoint waypoint = getSelectedBreakpointWaypoints()[0];

			try 
			{
				waypoint.setEnabled(!waypoint.isEnabled());
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
		if(allEnabled)
			action.setChecked(true);
	}
}
