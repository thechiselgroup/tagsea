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
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.tours.fx.IHighlightEffect;
import com.ibm.research.tours.fx.dialogs.SelectPerspectiveDialog;
import com.ibm.research.tours.fx.elements.ShowPerspectiveTourElement;

public class ShowPerspectiveDoubleClickActionDelegate extends DoubleClickActionDelegate
{
	@Override
	public void run() 
	{
		if(getSelection().length == 1 && getSelection()[0] instanceof ShowPerspectiveTourElement)
		{
			ShowPerspectiveTourElement element = (ShowPerspectiveTourElement)getSelection()[0];
			selectPerspective(element);
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void selectPerspective(ShowPerspectiveTourElement element) 
	{
		SelectPerspectiveDialog dialog = new SelectPerspectiveDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), PlatformUI.getWorkbench().getPerspectiveRegistry());
		dialog.setSelectedDescriptor(element.getDescriptor());
		dialog.showHighlightControls(true);

		boolean highlighted = element.getHighlightEffect()!=null;
		IHighlightEffect[] supported = element.getSupportedHighlightEffects();
		IHighlightEffect selected = element.getHighlightEffect(); // null defaults to the first element in the supported array
		dialog.setHighlighted(highlighted,supported,selected);

		if (dialog.open() == Window.OK) 
		{
			IPerspectiveDescriptor desc = dialog.getSelection();

			if (desc != null) 
				element.setDescriptor(desc);

			if(dialog.getHighlighted())
				element.setHighlightEffect(dialog.getSelectedEffect());
			else
				element.setHighlightEffect(null);	
		}

	}
}
