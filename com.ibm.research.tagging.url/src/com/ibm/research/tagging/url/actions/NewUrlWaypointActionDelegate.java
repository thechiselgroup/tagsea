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
package com.ibm.research.tagging.url.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.ibm.research.tagging.url.wizards.NewUrlWaypointWizard;

/**
 * 
 * @author mdesmond
 *
 */
public class NewUrlWaypointActionDelegate implements IViewActionDelegate 
{
	private IViewPart fView;
	
	public void init(IViewPart view) 
	{
		fView = view;
	}

	public void run(IAction action)
	{
		NewUrlWaypointWizard wizard= new NewUrlWaypointWizard();
		wizard.init(fView.getSite().getWorkbenchWindow().getWorkbench(),new StructuredSelection());
		WizardDialog dialog = new WizardDialog(fView.getSite().getShell(), wizard);
		dialog.create();
		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
