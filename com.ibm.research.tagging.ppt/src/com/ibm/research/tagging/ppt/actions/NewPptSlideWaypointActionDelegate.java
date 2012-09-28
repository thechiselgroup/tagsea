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

package com.ibm.research.tagging.ppt.actions;

import java.io.File;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.cue.tourist.internal.win32.ppt.PowerpointApplication;
import com.ibm.research.tagging.ppt.PptWaypoint;
import com.ibm.research.tagging.ppt.wizards.NewPptWaypointWizard;
import com.ibm.research.tagging.resource.ResourceWaypoint;
import com.ibm.research.tagging.resource.ResourceWaypointUtil;

public class NewPptSlideWaypointActionDelegate implements IObjectActionDelegate 
{	
	private IWorkbenchPart fActivePart;
	private ISelection fSelection;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		fActivePart = targetPart;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		if(fSelection != null)
		{
			StructuredSelection selection = (StructuredSelection)fSelection;

			if(selection.toArray().length == 1)
			{
				Object o = selection.toArray()[0];

				if(o instanceof IFile)
				{
					IFile file = (IFile)o;
					
					if("ppt".equalsIgnoreCase(file.getFileExtension()))
					{
						Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
						Shell tempShell = new Shell(shell.getDisplay());
						PowerpointApplication powerpoint = new PowerpointApplication(tempShell,SWT.NONE);
						
						powerpoint.open(file.getLocation().toString());
						int numberOfSlides = powerpoint.getCount();
						powerpoint.quit();
						
						if(numberOfSlides == 0)
						{
						   MessageDialog.openInformation(fActivePart.getSite().getShell(), "Powerpoint slide waypoint", "The selected slide show contains no slides and cannot be tagged, add some slides and try again."); 
						   return;	
						}

						NewPptWaypointWizard wizard= new NewPptWaypointWizard(file,numberOfSlides);
						wizard.init(fActivePart.getSite().getWorkbenchWindow().getWorkbench(),new StructuredSelection());
						WizardDialog dialog = new WizardDialog(fActivePart.getSite().getShell(), wizard);
						dialog.create();
						dialog.open();
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) 
	{
		fSelection = selection;
	}
}
