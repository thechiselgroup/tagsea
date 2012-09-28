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
package com.ibm.research.tours.fx.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.research.tours.IDoubleClickActionDelegate;

public class DoubleClickActionDelegate implements IDoubleClickActionDelegate
{
	private IWorkbenchPart fTargetPart;
	private IStructuredSelection fSelection;

	public void run() 
	{

	}

	public void selectionChanged(ISelection selection) 
	{
		if(selection instanceof IStructuredSelection)
			fSelection = (IStructuredSelection)selection;
	}

	public void setActivePart(IWorkbenchPart targetPart) 
	{
		fTargetPart = targetPart;
	}

	public IWorkbenchPart getTargetPart()
	{
		return fTargetPart;
	}
	
	public Object[] getSelection()
	{
		if(fSelection !=null && fSelection.size() > 0)
			return fSelection.toArray();
		
		return new Object[0];
	}
}
