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
package com.ibm.research.tours.content.actions;

import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.research.tours.content.ToursContentPlugin;

public class PickFileActionDelegate implements IObjectActionDelegate 
{
	private IWorkbenchPart fTargetPart;
	private ISelection fSelection;
	private TextSelection fTextSelection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) 
	{
		fTargetPart = targetPart;
	}

	public void run(IAction action) 
	{
		IStructuredSelection structuredSelection = (IStructuredSelection)fSelection;
		Object[] selection = structuredSelection.toArray();
		Object file = null;

		if(selection[0] instanceof IFileEditorInput)
		{
			IFileEditorInput input = (IFileEditorInput)selection[0];
			file = input.getFile();
		}
		else if(selection[0] instanceof IClassFileEditorInput) 
		{
			IClassFileEditorInput input = (IClassFileEditorInput)selection[0];
			file = input.getClassFile();
		}

		if(file !=null)
			ToursContentPlugin.getDefault().getFileClipBoard().put(file);
	}

	public void selectionChanged(IAction action, ISelection selection) 
	{
		fSelection = selection;

//		ITextEditor editor = (ITextEditor)fTargetPart;
//		fTextSelection = (TextSelection)editor.getSelectionProvider().getSelection();
//
//		if(fTextSelection.getLength() <= 0)
//			action.setEnabled(false);
	}
}
