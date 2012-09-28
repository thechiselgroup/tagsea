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
package com.ibm.research.tours.fx.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.ibm.research.tours.fx.IHighlightEffect;

public class HighlightGroup 
{
	private Group fEffectGroup;
	private Button fHighlightedButton;
	private Combo fHighlightEffectCombo;

	private boolean fHighlighted;
	private IHighlightEffect[] fEffects;
	private IHighlightEffect fSelectedEffect;
	
	public HighlightGroup(Boolean highlighted, IHighlightEffect[] effects, IHighlightEffect selectedEffect)
	{
		fHighlighted = highlighted;
		fEffects = effects;
		fSelectedEffect = selectedEffect;
	}

	public Group createComposite(Composite parent)
	{
		fEffectGroup = new Group(parent,SWT.SHADOW_ETCHED_IN);
		fEffectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fEffectGroup.setLayout(new GridLayout());
		fEffectGroup.setText("Effects");

		fHighlightedButton = new Button(fEffectGroup,SWT.CHECK);
		fHighlightedButton.setText("Add highlighting");
		fHighlightedButton.setSelection(fHighlighted);

		fHighlightedButton.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				fHighlighted = fHighlightedButton.getSelection();
				fHighlightEffectCombo.setEnabled(fHighlighted);
			}

			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		
		fHighlightEffectCombo = new Combo(fEffectGroup,SWT.DROP_DOWN);
		fHighlightEffectCombo.setEnabled(fHighlightedButton.getSelection());

		for(IHighlightEffect effect : fEffects)
			fHighlightEffectCombo.add(effect.getText());
		
		if(fSelectedEffect==null)
			fSelectedEffect = fEffects[0];
			
		fHighlightEffectCombo.setText(fSelectedEffect.getText());

		fHighlightEffectCombo.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				String text = fHighlightEffectCombo.getText();
				
				for(IHighlightEffect effect : fEffects)
					if(text.equals(effect.getText()))
						fSelectedEffect = effect;
			}

			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});    
		
		return fEffectGroup;
	}

	public boolean getHighlighted() 
	{
		return fHighlighted;
	}

	public IHighlightEffect getSelectedEffect() 
	{
		return fSelectedEffect;
	}
}
