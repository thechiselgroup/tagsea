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

import com.ibm.research.tours.fx.elements.TextTourElement;

public class TextWizard extends Wizard
{
	TextTourElement fElement;
	TextWizardPage fPage;
	
	public TextWizard(TextTourElement element) 
	{
		fElement = element;
		fPage = new TextWizardPage("TextWizardPage");
		
		fPage.init(element.getDisplayText(),
				element, 
				element.getLocation(), 
				element.getForegroundColor(), 
				element.getBackgroundColor(), 
				element.getFontData());

		addPage(fPage);
		setWindowTitle("Text Area Preferences");
	}
	
	@Override
	public boolean performFinish() 
	{
		fElement.setDisplayText(fPage.getTextAreaGroup().getText());
		fElement.setLocation(fPage.getTextAreaGroup().getSelectedLocation());
		fElement.setBackgroundColor(fPage.getTextAreaGroup().getBackground());
		fElement.setForegroundColor(fPage.getTextAreaGroup().getForeground());
		fElement.setFontData(fPage.getTextAreaGroup().getFontData());
		return true;
	}
}
