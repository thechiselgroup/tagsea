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

import java.util.Vector;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.TimeLimit;
import com.ibm.research.tours.ToursPlugin;
import com.ibm.research.tours.dialogs.TimeDialog;

public class TourEditorActionDelegate 
{
	private TourEditor fEditor;
	
	public void init(TourEditor editor) 
	{
		fEditor = editor;
	}

	public void delete() 
	{
		Object[] selectedObjects = fEditor.getSelection();

		// Multiple items selected so delete the tour elements
		Vector<ITourElement> elements = new Vector<ITourElement>();

		for(Object o : selectedObjects)
			if(o instanceof ITourElement)
				elements.add((ITourElement)o);

		if(elements.size() > 0)
		{
			ITour tour = fEditor.getTour();
			tour.removeElements(elements.toArray(new ITourElement[0]));
		}
	}

	public void deleteAll() 
	{
		Object[] selectedObjects = fEditor.getSelection();
		
		if(selectedObjects.length == 1 && selectedObjects[0] instanceof TourElements)
		{
			// Clear all tour items
			ITour tour = fEditor.getTour();
			tour.clear();
		}
	}

	public void preview() 
	{
		Object[] selectedObjects = fEditor.getSelection();
		
	}

	public void timing() 
	{
//		Object[] selection = fEditor.getSelection();
//		
//		if(selection.length == 1 && selection[0] instanceof ITourElement)
//		{
//			ITourElement element = (ITourElement)selection[0];
//				
//			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//			TimeDialog dialog = new TimeDialog(shell,"Set Time Limit");
//			
//			if(element.getTimeLimit() !=null)
//				dialog.init(new TimeLimit(element.getTimeLimit()));
//			else
//				dialog.init(null);
//			
//			if(dialog.open() == Window.OK)
//			{
//				if(dialog.isTimeLimitSet())
//					element.setTimeLimit(dialog.getTimeLimit());
//				else
//					element.setTimeLimit(null);
//			}
//		}
	}

	public void masterTiming() 
	{
		Object[] selection = fEditor.getSelection();
		ITour tour = fEditor.getTour();
		
		if(selection.length == 1 && selection[0] instanceof TourElements)
		{
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			TimeDialog dialog = new TimeDialog(shell,"Time Limit","Set a time limit on this tour");
			
			if(tour.getTimeLimit() !=null)
				dialog.init(new TimeLimit(tour.getTimeLimit()));
			else
				dialog.init(null);
			
			if(dialog.open() == Window.OK)
			{
				if(dialog.isTimeLimitSet())
					tour.setTimeLimit(dialog.getTimeLimit());
				else
					tour.setTimeLimit(null);
			}
		}
	}

	public void transitionOnClick() 
	{
		Object[] selection = fEditor.getSelection();
		
		if(selection.length == 1 && selection[0] instanceof ITourElement)
		{
			ITourElement element = (ITourElement)selection[0];
			element.setTransition(ITourElement.START_ON_CLICK);
		}		
	}

	public void transitionWithPrevious() 
	{
		Object[] selection = fEditor.getSelection();
		
		if(selection.length == 1 && selection[0] instanceof ITourElement)
		{
			ITourElement element = (ITourElement)selection[0];
			element.setTransition(ITourElement.START_WITH_PREVIOUS);
		}	
	}

	public void transitionAfterPrevious() 
	{
		Object[] selection = fEditor.getSelection();
		
		if(selection.length == 1 && selection[0] instanceof ITourElement)
		{
			ITourElement element = (ITourElement)selection[0];
			element.setTransition(ITourElement.START_AFTER_PREVIOUS);
		}
	}

	public void run() 
	{
		ToursPlugin.getDefault().getTourRuntime().run(fEditor.getTour());
	}
}
