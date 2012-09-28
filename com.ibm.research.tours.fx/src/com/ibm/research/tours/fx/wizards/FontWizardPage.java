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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ibm.research.tours.fx.controls.FontGroup;

public class FontWizardPage extends WizardPage
{
	private FontGroup fFontGroup;
	private Button fResetButton;
	private FontData fFontData;
	private boolean fReset;

	protected FontWizardPage(String pageName) 
	{
		super(pageName);
		setTitle("Font preferences");
		setDescription("Select the font preferences for the element.");
	}

	public void init(FontData fontData, boolean reset)
	{
		fFontData = fontData;
		fReset = reset;
	}

	public void createControl(Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 6;
		composite.setLayout(layout);

		Label label = new Label(composite,SWT.WRAP);
		label.setText("Configure font for this element");
		GridData data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		label.setData(data);

		fFontGroup = new FontGroup(fFontData);
		GridData fontGroupData = new GridData(GridData.FILL_HORIZONTAL);
		fontGroupData.heightHint = 80;
		fontGroupData.widthHint = 300;
		fFontGroup.createComposite(composite, fontGroupData);

		fResetButton = new Button(composite,SWT.CHECK);
		fResetButton.setText("Reset font after transition");
		fResetButton.setSelection(fReset);
		fResetButton.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				fReset = fResetButton.getSelection();
			}

			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e); 
			}
		});

		setControl(composite);
	}

	public FontData getFontData()
	{
		return fFontGroup.getFontData();
	}

	public boolean getReset() 
	{
		return fReset;
	}
}
