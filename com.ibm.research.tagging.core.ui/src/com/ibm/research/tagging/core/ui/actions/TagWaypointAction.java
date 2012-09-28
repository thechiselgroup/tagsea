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
package com.ibm.research.tagging.core.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.ui.dialogs.QuickTagDialog;

/**
 * 
 * @author mdesmond
 *
 */
public class TagWaypointAction implements IObjectActionDelegate 
{
	private IStructuredSelection fSelection;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) 
	{
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		QuickTagDialog dialog = new QuickTagDialog(shell,"Add tags to selected waypoints", "Enter tags");
		
		if(dialog.open() == Window.OK)
		{
			String[] tagNames = dialog.getTags();
			List<ITag> tags = new ArrayList<ITag>();
			
			for(String tagName : tagNames)
				tags.add(TagCorePlugin.getDefault().getTagCore().getTagModel().addTag(tagName));
			
			for(Object o : fSelection.toArray())
			{
				IWaypoint waypoint = (IWaypoint)o;
				
				for(ITag tag : tags)
					waypoint.addTag(tag);
			}
		}
	}	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) 
	{
		fSelection = (StructuredSelection)selection;
	}

}
