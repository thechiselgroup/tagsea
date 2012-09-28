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
package com.ibm.research.tagging.core.ui.tags;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.ui.PartInitException;

import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.ui.waypoints.WaypointView;

/**
 * 
 * @author mdesmond
 *
 */
public class TagTableDoubleClickListener implements IDoubleClickListener {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	public void doubleClick(DoubleClickEvent event) 
	{
		try 
		{
			TagCorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(WaypointView.ID);
		} 
		catch (PartInitException e) 
		{
			e.printStackTrace();
		}
	}
}
