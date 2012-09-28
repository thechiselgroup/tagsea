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
package com.ibm.research.tours.editors;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ITourListener;

public class TourTreeSelectionChangedListener implements ISelectionChangedListener 
{
	private TourEditor fEditor;
	private ITourElement flastSelectedElement;
	
	public TourTreeSelectionChangedListener(TourEditor editor) 
	{
		fEditor = editor;
		
		fEditor.getTour().addTourListener(new ITourListener() 
		{	
			public void tourChanged(ITour tour) 
			{
			}
		
			public void elementsRemoved(ITour tour, ITourElement[] elements) 
			{
				for(ITourElement element : elements)
				{
					if(element == flastSelectedElement)
					{
						flastSelectedElement = null;
						fEditor.getNotesText().setText("");
						fEditor.getNotesText().setEditable(false);
					}
				}
			}
		
			public void elementsAdded(ITour tour, ITourElement[] elements) 
			{
			}
		
		}, false);
	}
	
	public void selectionChanged(SelectionChangedEvent event) 
	{
		IStructuredSelection structuredSelection = (IStructuredSelection)event.getSelection();
		
		performSave();
		flastSelectedElement = null;
		
		if(!structuredSelection.isEmpty())
		{
			Object[] selected = structuredSelection.toArray();
			
			if(selected.length == 1 && selected[0] instanceof ITourElement)
			{
				flastSelectedElement = (ITourElement)selected[0];
				fEditor.getNotesText().setText(flastSelectedElement.getNotes());
				fEditor.getNotesText().setEditable(true);
				return;
			}
		}
		
		fEditor.getNotesText().setText("");
		fEditor.getNotesText().setEditable(false);
		flastSelectedElement = null;
	}
	
	public void performSave()
	{
		// Fill out the last selected element
		if(flastSelectedElement!=null)
			if(!flastSelectedElement.getNotes().equals(fEditor.getNotesText().getText()))
				flastSelectedElement.setNotes(fEditor.getNotesText().getText());
	}
}
