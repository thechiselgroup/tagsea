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

import com.ibm.research.tours.fx.elements.ResettingFontTourElement;

public class FontWizard extends Wizard
{
	ResettingFontTourElement fElement;
	FontWizardPage fPage;
	
	public FontWizard(ResettingFontTourElement element) 
	{
		fElement = element;
		fPage = new FontWizardPage("FontWizardPage");
		
		if(element.getFontData()!=null)
		{
			FontData data = new FontData(element.getFontData().getName(),element.getFontData().getHeight(),element.getFontData().getStyle());
			data.data = element.getFontData().data;
			fPage.init(data,element.getReset());
		}
		
		addPage(fPage);
		setWindowTitle("Font Preferences");
	}
	
	@Override
	public boolean performFinish() 
	{
		fElement.setFontData(fPage.getFontData());
		fElement.setReset(fPage.getReset());
		return true;
	}

}
