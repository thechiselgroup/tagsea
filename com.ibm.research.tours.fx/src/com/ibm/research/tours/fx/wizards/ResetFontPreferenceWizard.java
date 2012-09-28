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
package com.ibm.research.tours.fx.wizards;

import org.eclipse.jface.wizard.Wizard;

import com.ibm.research.tours.fx.elements.ResetPreferenceFontTourElement;

public class ResetFontPreferenceWizard extends Wizard {

	ResetPreferenceFontTourElement fElement;
	ResetFontPreferenceWizardPage fPage;
	
	public ResetFontPreferenceWizard(ResetPreferenceFontTourElement element) 
	{
		fElement = element;
		fPage = new ResetFontPreferenceWizardPage("ResetFontPreferenceWizardPage");

		fPage.init(element.getPreferenceId(),element.getLabel());

		addPage(fPage);
		setWindowTitle("Font Preferences");
	}
	
	@Override
	public boolean performFinish() 
	{
		fElement.setPreferenceId(fPage.getId());
		fElement.setLabel(fPage.getLabel());
		return true;
	}

}
