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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.ibm.research.tours.content.controls.TextPresentationGroup;
import com.ibm.research.tours.content.elements.ResourceURLTourElement;
import com.ibm.research.tours.content.url.delegates.JavaURLTourElementDelegate;
import com.ibm.research.tours.fx.IHighlightEffect;
import com.ibm.research.tours.fx.controls.HighlightGroup;
import com.ibm.research.tours.fx.controls.MaximizeGroup;

public class TextRegionWizardPage extends WizardPage
{	
	private ResourceURLTourElement fElement;
	private JavaURLTourElementDelegate fDelegate;
	private TextPresentationGroup fTextGroup;
	private MaximizeGroup fMaxGroup;
	private HighlightGroup fHighlightGroup;
	private Button fEnableTextHighlightingButton;
	private boolean fEnableTextHighlighting;
	
	protected TextRegionWizardPage(String pageName,String pagetTitle,String description) 
	{
		super(pageName);
		setTitle(pagetTitle);
		setDescription(description);
	}

	public void init(ResourceURLTourElement element)
	{
		fElement = element;
	}

	public void createControl(Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 6;
		composite.setLayout(layout);

		fEnableTextHighlightingButton = new Button(composite,SWT.CHECK);
		fEnableTextHighlightingButton.setText("Text highlighting");
		
		fEnableTextHighlightingButton.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				fEnableTextHighlighting = fEnableTextHighlightingButton.getSelection();
				fTextGroup.setEnabled(fEnableTextHighlighting);
			}
		
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		
		});
		
		fEnableTextHighlighting = fElement.getTextHighlighting();
		fEnableTextHighlightingButton.setSelection(fEnableTextHighlighting);
		
		fTextGroup = new TextPresentationGroup(fElement.getTextHighlighting(),
																fElement.getSupportedTextEffects(),
																fElement.getSelectedTextEffect(),
																fElement.getShowInOverview(),
																fElement.getShowInVertical(),
																fElement.getTextPresentationColor());
		fTextGroup.createComposite(composite,"Text settings","Show element");
		fTextGroup.setEnabled(fElement.getTextHighlighting());
		//fEnableTextHighlightingButton.setSelection(fElement.getTextHighlighting());
		
		fMaxGroup = new MaximizeGroup(fElement.getMaximixedHint());
		fMaxGroup.createComposite(composite,"Editor state","Maximize editor pane");
		
		boolean highlighted = fElement.getHighlightEffect()!=null;
		IHighlightEffect[] supported = fElement.getSupportedHighlightEffects();
		IHighlightEffect selected = fElement.getHighlightEffect(); // null defaults to the first element in the supported array
		
		fHighlightGroup = new HighlightGroup(highlighted,supported,selected);
		fHighlightGroup.createComposite(composite);
		
		setControl(composite);
	}

	public TextPresentationGroup getTextPresentationGroup() {
		return fTextGroup;
	}

	public MaximizeGroup getMaximizeGroup() {
		return fMaxGroup;
	}

	public HighlightGroup getHighlightGroup() {
		return fHighlightGroup;
	}

	public boolean getTextHighlightingEnabled() {
		return fEnableTextHighlighting;
	}

}
