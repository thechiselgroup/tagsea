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
package com.ibm.research.tours.content.controls;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.ibm.research.tours.content.ITextEffect;

public class TextPresentationGroup 
{	
	//private Button fShowInTextCheckBox;
	private Button fShowInOverviewRulerCheckBox;
	private Button fShowInVerticalRulerCheckBox;
	private Combo fTextEffectCombo;
	private ColorSelector fAnnotationForegroundColorEditor;

	//boolean fShowText;
	ITextEffect[] fEffects;
	ITextEffect fSelectedEffect;
	boolean fShowInOverview;
	boolean fShowInVertical;
	RGB fTextColor;

	public TextPresentationGroup(boolean showText,
			ITextEffect[] effects,
			ITextEffect selectedEffect,
			boolean showInOverview,
			boolean showInVertical,
			RGB highlightColor)
	{
		//fShowText = showText;
		fEffects = effects;
		fSelectedEffect = selectedEffect;
		fShowInOverview = showInOverview;
		fShowInVertical = showInVertical;
		fTextColor = highlightColor;
	}

	public void setEnabled(boolean state)
	{
		//fShowInTextCheckBox.setEnabled(state);
		fTextEffectCombo.setEnabled(state);
		fShowInOverviewRulerCheckBox.setEnabled(state);
		fShowInVerticalRulerCheckBox.setEnabled(state);
		fAnnotationForegroundColorEditor.setEnabled(state);
		
//		if(state == true)
//			fDecorationCombo.setEnabled(fShowText);
	}
	
	public Composite createComposite(Composite parent, String title,String message)
	{	
		Group textPresentationGroup = new Group(parent,SWT.SHADOW_ETCHED_IN);
		textPresentationGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textPresentationGroup.setText(title);
		GridLayout groupLayout = new GridLayout(2,false);
		textPresentationGroup.setLayout(groupLayout);
		// we only allow to set either "show in text" or "highlight in text", but not both
		
//		fShowInTextCheckBox= new Button(textPresentationGroup, SWT.CHECK);
//		fShowInTextCheckBox.setSelection(getShowText());
//		fShowInTextCheckBox.setText("Text");
//		fShowInTextCheckBox.addSelectionListener(new SelectionListener() 
//		{
//			public void widgetSelected(SelectionEvent e) 
//			{
//				fShowText = fShowInTextCheckBox.getSelection();
//				fDecorationCombo.setEnabled(fShowText);
//			}
//
//			public void widgetDefaultSelected(SelectionEvent e) 
//			{
//				widgetSelected(e);
//			}
//		});
		new Label(textPresentationGroup, SWT.NONE).setText("Effect: ");

		fTextEffectCombo = new Combo(textPresentationGroup, SWT.READ_ONLY);

		if(getTextEffects()!=null)
		{
			for(ITextEffect effect : getTextEffects())
				fTextEffectCombo.add(effect.getText());
			
			if(fSelectedEffect!=null)
			{
				fTextEffectCombo.setText(fSelectedEffect.getText());
			}
			else if(getTextEffects().length>0)
			{
				fTextEffectCombo.setText(getTextEffects()[0].getText());
				fSelectedEffect = getTextEffects()[0];
			}
		}

		//fDecorationCombo.setEnabled(fShowText);
		
		fTextEffectCombo.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				String text = fTextEffectCombo.getText();

				if(getTextEffects()!=null)
				{
					for(ITextEffect effect : getTextEffects())
						if(text.equals(effect.getText()))
							fSelectedEffect = effect;
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});


		fShowInOverviewRulerCheckBox= new Button(textPresentationGroup, SWT.CHECK);
		fShowInOverviewRulerCheckBox.setText("Overview ruler");
		GridData gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		fShowInOverviewRulerCheckBox.setLayoutData(gd);
		fShowInOverviewRulerCheckBox.setSelection(getShowInOverview());

		fShowInOverviewRulerCheckBox.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				fShowInOverview = fShowInOverviewRulerCheckBox.getSelection();
			}

			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		
		
		fShowInVerticalRulerCheckBox= new Button(textPresentationGroup, SWT.CHECK);
		fShowInVerticalRulerCheckBox.setText("Vertical ruler");
		gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		fShowInVerticalRulerCheckBox.setLayoutData(gd);
		fShowInVerticalRulerCheckBox.setSelection(getShowInVertical());

		fShowInVerticalRulerCheckBox.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				fShowInVertical = fShowInVerticalRulerCheckBox.getSelection();
			}

			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		
		Label label = new Label(textPresentationGroup, SWT.NONE);
		label.setText("Color");

		fAnnotationForegroundColorEditor= new ColorSelector(textPresentationGroup);
		fAnnotationForegroundColorEditor.setColorValue(getTextColor());
		
		fAnnotationForegroundColorEditor.getButton().addSelectionListener(new SelectionListener() 
		{
				public void widgetSelected(SelectionEvent e) 
				{
					fTextColor = fAnnotationForegroundColorEditor.getColorValue();
				}

				public void widgetDefaultSelected(SelectionEvent e) 
				{
					widgetSelected(e);
				}
			});;

		return textPresentationGroup;
	}

//	public boolean getShowText() {
//		return fShowText;
//	}

	public ITextEffect[] getTextEffects() {
		return fEffects;
	}

	public ITextEffect getSelectedEffect() {
		return fSelectedEffect;
	}

	public boolean getShowInOverview() {
		return fShowInOverview;
	}

	public boolean getShowInVertical() {
		return fShowInVertical;
	}

	public RGB getTextColor() 
	{
		if(fTextColor == null)
			fTextColor = new RGB(0,0,0);

		return fTextColor;
	}
}
