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
package com.ibm.research.tagging.java.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.ide.actions.BuildUtilities;

import com.ibm.research.tagging.java.JavaWaypointPlugin;
import com.ibm.research.tagging.java.dialogs.SynchronizeWaypointsDialog;
import com.ibm.research.tagging.java.jobs.SynchronizeWorkspaceJob;

public class SynchronizeAction implements IViewActionDelegate 
{
	private IStructuredSelection fSelection;
	private IViewPart fView;
	
	public void init(IViewPart view) 
	{
		fView = view;
	}

	public void run(IAction action)
	{
		IProject[] selected = BuildUtilities.findSelectedProjects(JavaWaypointPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow());
		SynchronizeWaypointsDialog dialog = new SynchronizeWaypointsDialog(JavaWaypointPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow(),selected);
		
		if(dialog.open() == Window.OK)
		{
			IProject[] projects = null;
			
			if(dialog.getAllSelected())
				projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			else
				projects = dialog.getSelectedProjects();
			
			if(projects.length > 0)
			{
				SynchronizeWorkspaceJob job = new SynchronizeWorkspaceJob("Scanning workspace for Java Waypoints",projects);
				job.setUser(true);
				job.schedule();
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) 
	{
		fSelection = (IStructuredSelection)selection;
	}
}
