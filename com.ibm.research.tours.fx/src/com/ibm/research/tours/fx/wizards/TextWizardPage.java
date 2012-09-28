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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ibm.research.tours.fx.controls.TextAreaGroup;
import com.ibm.research.tours.fx.location.ILocation;
import com.ibm.research.tours.fx.location.ILocationProvider;

public class TextWizardPage extends WizardPage
{
	private TextAreaGroup fTextAreaGroup;

	protected TextWizardPage(String pageName) 
	{
		super(pageName);
		setTitle("TextArea preferences");
		setDescription("Select the preferences for the text area.");
	}

	public void init(String text,
		   	 ILocationProvider provider, 
		   	 ILocation selectedLocation, 
		   	 RGB foreground, 
		   	 RGB background, 
		   	 FontData fontData)
	{
		fTextAreaGroup = new TextAreaGroup(text,provider,selectedLocation,foreground,background,fontData);
	}

	public void createControl(Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 6;
		composite.setLayout(layout);

		GridData fontGroupData = new GridData(GridData.FILL_HORIZONTAL);
		fTextAreaGroup.createComposite(composite,fontGroupData);

		setControl(composite);
	}

	public TextAreaGroup getTextAreaGroup()
	{
		return fTextAreaGroup;
	}
}
