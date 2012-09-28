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
package com.ibm.research.tagging.core.ui.waypoints;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.ibm.research.tagging.core.IWaypoint;

/**
 * 
 * @author mdesmond
 *
 */
public class WaypointTableDoubleClickListener implements IDoubleClickListener {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	public void doubleClick(DoubleClickEvent event) 
	{
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		
		for(Object o : selection.toList())
		{
			IWaypoint waypoint = (IWaypoint)o;
			waypoint.navigate();
		}
	}
}
