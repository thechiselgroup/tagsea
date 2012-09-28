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

import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.views.IViewDescriptor;

import com.ibm.research.tours.fx.IHighlightEffect;
import com.ibm.research.tours.fx.dialogs.SelectViewDialog;
import com.ibm.research.tours.fx.elements.ShowViewTourElement;

public class ShowViewDoubleClickActionDelegate extends DoubleClickActionDelegate
{
	@Override
	public void run() 
	{
		if(getSelection().length == 1 && getSelection()[0] instanceof ShowViewTourElement)
		{
			ShowViewTourElement element = (ShowViewTourElement)getSelection()[0];
			selectView(element);
			return;
		}
	}
	
	/**
	 * Opens a view selection dialog, allowing the user to chose a view.
	 * @param element 
	 */
	private final void selectView(ShowViewTourElement element) 
	{
		SelectViewDialog dialog = new SelectViewDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),WorkbenchPlugin.getDefault().getViewRegistry());
		dialog.setSelectedDescriptor(element.getDescriptior());
		
		dialog.showMaximizedControls(true);
		dialog.setMaximized(element.getMaximixedHint());
		
		dialog.showHighlightControls(true);
		boolean highlighted = element.getHighlightEffect()!=null;
		IHighlightEffect[] supported = element.getSupportedHighlightEffects();
		IHighlightEffect selected = element.getHighlightEffect(); // null defaults to the first element in the supported array
		dialog.setHighlighted(highlighted,supported,selected);

		if (dialog.open() == Window.OK) 
		{
			IViewDescriptor[] descriptors = dialog.getSelection();
			
			if(descriptors.length == 1)
				element.setDescriptior(descriptors[0]);
				
			element.setMaximixedHint(dialog.getMaximized());
			
			if(dialog.getHighlighted())
				element.setHighlightEffect(dialog.getSelectedEffect());
			else
				element.setHighlightEffect(null);	
		}
	}
}
