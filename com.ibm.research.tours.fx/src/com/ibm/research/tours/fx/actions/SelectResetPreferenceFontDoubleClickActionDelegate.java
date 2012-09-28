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

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.tours.fx.elements.ResetPreferenceFontTourElement;
import com.ibm.research.tours.fx.wizards.ResetFontPreferenceWizard;

public class SelectResetPreferenceFontDoubleClickActionDelegate extends DoubleClickActionDelegate
{
	@Override
	public void run() 
	{
		if(getSelection().length == 1 && getSelection()[0] instanceof ResetPreferenceFontTourElement)
		{
			ResetPreferenceFontTourElement element = (ResetPreferenceFontTourElement)getSelection()[0];
			selectFont(element);
			return;
		}
	}
	
	/**
	 * Opens a view selection dialog, allowing the user to chose a view.
	 * @param element 
	 */
	private final void selectFont(ResetPreferenceFontTourElement element) 
	{
		ResetFontPreferenceWizard wizard = new ResetFontPreferenceWizard(element);
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		dialog.create();
		dialog.open();
		
	}
}
