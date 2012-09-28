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

package com.ibm.research.tagging.resource.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.tagging.resource.wizards.NewResourceWaypointWizard;

public class TagActionDelegate implements IActionDelegate 
{
	private ISelection fSelection;
	
	public void run(IAction action)
	{
		IStructuredSelection structuredSelection = (IStructuredSelection) fSelection;
		
		NewResourceWaypointWizard wizard= new NewResourceWaypointWizard();
		wizard.init(PlatformUI.getWorkbench(),structuredSelection);
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		dialog.create();
		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = selection;
	}

}
