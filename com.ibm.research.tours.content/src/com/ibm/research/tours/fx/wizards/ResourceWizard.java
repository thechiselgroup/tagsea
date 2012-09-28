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

import com.ibm.research.tours.content.elements.ResourceURLTourElement;

public class ResourceWizard extends Wizard
{
	ResourceURLTourElement fElement;
	ResourceWizardPage fPage;

	public ResourceWizard(ResourceURLTourElement element, String title,String pageTitle, String description) 
	{
		fElement = element;
		fPage = new ResourceWizardPage("ResourceWizardPage",pageTitle,description);
		fPage.init(fElement);
		addPage(fPage);
		setWindowTitle(title);
	}

	@Override
	public boolean performFinish() 
	{
		fElement.setMaximixedHint(fPage.getMaximizeGroup().getMaximized());

		if(fPage.getHighlightGroup().getHighlighted())
			fElement.setHighlightEffect(fPage.getHighlightGroup().getSelectedEffect());
		else
			fElement.setHighlightEffect(null);	

		return true;
	}
}
