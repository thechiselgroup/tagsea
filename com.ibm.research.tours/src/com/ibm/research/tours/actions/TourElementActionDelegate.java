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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.research.tours.ITourElement;

public class TourElementActionDelegate implements IObjectActionDelegate 
{
	private List<ITourElement> fSelectedElements;
	private IAction fAction;
	private IWorkbenchPart fTargetPart;
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) 
	{
		fAction = action;
		fTargetPart = targetPart;
	}

	public void run(IAction action) 
	{

	}

	public void selectionChanged(IAction action, ISelection selection) 
	{
		IStructuredSelection structuredSelection = (IStructuredSelection)selection;
		fSelectedElements = new ArrayList<ITourElement>();
		
		if(!structuredSelection.isEmpty())
		{
			Object[] selectionArray = structuredSelection.toArray();

			for(Object o : selectionArray)
			{
				if(o instanceof ITourElement)
					fSelectedElements.add((ITourElement)o);
			}
		}
	}

	public IAction getAction() {
		return fAction;
	}

	public ITourElement[] getSelectedElements() {
		return fSelectedElements.toArray(new ITourElement[0]);
	}

	public IWorkbenchPart getTargetPart() {
		return fTargetPart;
	}
}
