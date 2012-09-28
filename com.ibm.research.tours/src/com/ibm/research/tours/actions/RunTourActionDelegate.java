/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tours.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ToursPlugin;
import com.ibm.research.tours.serializer.XMLTourFactory;

public class RunTourActionDelegate implements IObjectActionDelegate
{
	IWorkbenchPart fTargetPart;
	IStructuredSelection fSelection;
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) 
	{
		fTargetPart = targetPart;
	}

	public void run(IAction action) 
	{
		Object[] selection = fSelection.toArray();
		
		for(Object selected : selection)
		{
			if(selected instanceof IFile)
			{
				IFile file = (IFile)selected;
				try 
				{
					ITour tour = XMLTourFactory.createTour(file);
					boolean open = true;
					
					if(tour != null && tour.getElementCount() == 0) {
					    if (!MessageDialog.openQuestion(fTargetPart.getSite().getShell(), "Run Tour", "This tour contains no runnable elements, open anyway?")) 
					    	open = false;
						
					}
					
					if(open) {
						ToursPlugin.getDefault().getTourRuntime().run(tour);
					}	
				} 
				catch (CoreException e) 
				{
					MessageBox box = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
					box.setText("Error opening tour");
					box.setMessage(e.getMessage());
					box.open();
					e.printStackTrace();
				}
				
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) 
	{
		fSelection = (IStructuredSelection)selection;
	}
}
