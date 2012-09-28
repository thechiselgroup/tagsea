/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 *******************************************************************************/
package ca.uvic.cs.tagsea.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.ide.actions.BuildUtilities;

import ca.uvic.cs.tagsea.dialogs.RefreshTagsDialog;

/**
 * @author mdesmond
 * From CleanAction
 */
public class RefreshTagsAction extends Action
{
	private IWorkbenchWindow fWindow;

	/**
	 * Creates a new RefreshTagsAction
	 * 
	 * @param window The window for parenting this action
	 */
	public RefreshTagsAction(IWorkbenchWindow window) 
	{
		super();
		fWindow = window;
	}

	public void run() 
	{
		IProject[] selected = BuildUtilities.findSelectedProjects(fWindow);
		new RefreshTagsDialog(fWindow, selected).open();
	}
}
