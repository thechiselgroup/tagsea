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
import org.eclipse.swt.graphics.FontData;

import com.ibm.research.tours.fx.elements.SetPreferenceFontTourElement;

public class FontPreferenceWizard extends Wizard {

	SetPreferenceFontTourElement fElement;
	FontPreferenceWizardPage fPage;
	
	public FontPreferenceWizard(SetPreferenceFontTourElement element) 
	{
		fElement = element;
		fPage = new FontPreferenceWizardPage("FontPreferenceWizardPage");
		
		if(element.getFontData()!=null)
		{
			FontData data = new FontData(element.getFontData().getName(),element.getFontData().getHeight(),element.getFontData().getStyle());
			data.data = element.getFontData().data;
			fPage.init(data,element.getPreferenceId(),element.getLabel(),element.getReset());
		}
		
		addPage(fPage);
		setWindowTitle("Font Preferences");
	}
	
	@Override
	public boolean performFinish() 
	{
		fElement.setFontData(fPage.getFontData());
		fElement.setReset(fPage.getReset());
		fElement.setPreferenceId(fPage.getId());
		fElement.setLabel(fPage.getLabel());
		return true;
	}

}
