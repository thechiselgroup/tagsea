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

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.content.ToursContentPlugin;
import com.ibm.research.tours.content.elements.ResourceURLTourElement;
import com.ibm.research.tours.editors.TourEditor;

public class DropFileActionDelegate implements IEditorActionDelegate 
{
	private IWorkbenchPart fTargetPart;
	private ISelection fSelection;
	
	public void run(IAction action) 
	{
		TourEditor editor = (TourEditor)fTargetPart;
		ITour tour = editor.getTour();
		int index = tour.getElementCount();
					
		Object file = ToursContentPlugin.getDefault().getFileClipBoard().get();
		
		if(file != null)
		{
			ResourceURLTourElement element = null;
			
			if(file instanceof IFile)
			{
				element = new ResourceURLTourElement((IFile)file);
				tour.addElements(index, new ITourElement[]{element});
			}
			else if(file instanceof IClassFile)
			{
				element = new ResourceURLTourElement((IClassFile)file);
				tour.addElements(index, new ITourElement[]{element});
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) 
	{
		fSelection = selection;
		//updateEnablment(action);
	}
	
	private void updateEnablment(IAction action)
	{
		if(ToursContentPlugin.getDefault().getTextRegionClipBoard().getTextRegion() == null)
			action.setEnabled(false);
		else
			action.setEnabled(true);
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) 
	{
		fTargetPart = targetEditor;
		//updateEnablment(action);
	}
}
