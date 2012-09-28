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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.ibm.research.tours.content.SlideRange;

public class PowerpointRangeGroup 
{
	private final static String RANGE_TITLE = "Slide Range";
	private final static String ALL_RANGE = "All Slides";
	private final static String RANGE = "Range";
	private final static String START_RANGE = "From";
	private final static String END_RANGE = "To";
	private final static String RANGE_INFO = "Select a subrange of the powerpoint slide show.";

	private Button fAllButton;
	private Button fRangeButton;
	private Combo fComboStart;
	private Combo fComboEnd;
	
	private int fNumSlides;
	private SlideRange fRange;
	
	public PowerpointRangeGroup(int numSlides, SlideRange range)
	{
		fNumSlides = numSlides;
		fRange = range;
	}
	
	public SlideRange getSlideRange() 
	{
		return fRange;
	}

	public Composite createComposite(Composite parent)
	{
	    Group rangeGroup = new Group(parent,SWT.SHADOW_ETCHED_IN);
	    GridLayout groupLayout = new GridLayout(6, false);
	    rangeGroup.setLayout(groupLayout);
	    
	    GridData groupData = new GridData(GridData.FILL_HORIZONTAL);
	    groupData.horizontalSpan = 2;
	    rangeGroup.setLayoutData(groupData);
	    rangeGroup.setText(RANGE_TITLE);
	   
	    fAllButton = createAllRadioButton(rangeGroup);
	    fRangeButton = createRangeRadioButton(rangeGroup);
	    createStartLabel(rangeGroup);
	    fComboStart = createStartRangeCombo(rangeGroup);
	    createEndLabel(rangeGroup);
	    fComboEnd = createEndRangeCombo(rangeGroup);
	    createInfoLabel(rangeGroup);
	    
	    if(fRange!=null)
	    {
	    	fAllButton.setSelection(false);
	    	fRangeButton.setSelection(true);	
	    	fComboStart.setText(Integer.toString(fRange.getStart()));
	    	fComboEnd.setText(Integer.toString(fRange.getEnd()));
			fComboStart.setEnabled(true);
			fComboEnd.setEnabled(true);
	    }
	    else
	    {
	    	fAllButton.setSelection(true);
	    	fRangeButton.setSelection(false);
	    }

	    return rangeGroup;
	}

	protected Combo createStartRangeCombo(Composite parent)
	{
	    String[] items = new String[fNumSlides];
		 
	    for(int i = 0; i < fNumSlides ; i++)
	    	items[i] = Integer.toString(i+1);
	    
	    
	    Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
	    combo.setItems(items);
	    combo.setText(items[0]);
	    combo.setEnabled(false);
	    combo.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) 
			{
				rangeChanged();
			}
		
			public void widgetDefaultSelected(SelectionEvent e) 
			{
			}
		});
	   
	    combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    return combo;
	}
	
	protected Combo createEndRangeCombo(Composite parent)
	{
	    String[] items = new String[fNumSlides];
		 
	    for(int i = 0; i < fNumSlides ; i++)
	    	items[i] = Integer.toString(i+1);
	    
	    
	    Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
	    combo.setItems(items);
	    combo.setText(items[fNumSlides-1]);
	    combo.setEnabled(false);
	    combo.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) 
			{
				rangeChanged();
			}
		
			public void widgetDefaultSelected(SelectionEvent e) 
			{
			}
		});
	   
	    combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    return combo;
	}
	
	protected Label createInfoLabel(Composite parent)
	{
	    Label infoLabel = new Label(parent,SWT.NONE);
	    infoLabel.setText(RANGE_INFO);
	    GridData infoData = new GridData();
	    infoData.horizontalSpan = 6;
	    infoLabel.setLayoutData(infoData);
	    return infoLabel;
	}
	
	protected Label createStartLabel(Composite parent)
	{
	    Label fromLabel = new Label(parent,SWT.NONE);
	    fromLabel.setText(START_RANGE);
	    return fromLabel;
	}
	
	protected Label createEndLabel(Composite parent)
	{
	    Label toLabel = new Label(parent,SWT.NONE);
	    toLabel.setText(END_RANGE);
	    return toLabel;
	}
	
	protected Button createAllRadioButton(Composite parent) 
	{
		Button allButton = new Button(parent,SWT.RADIO);
	    GridData allButtonData = new GridData();
	    allButtonData.horizontalSpan = 6;
	    allButton.setLayoutData(allButtonData);
	    allButton.setText(ALL_RANGE);
	    
	    allButton.addSelectionListener(new SelectionListener() 
	    {
			public void widgetSelected(SelectionEvent e) 
			{
				rangeChanged();
			}
		
			public void widgetDefaultSelected(SelectionEvent e) 
			{
			}
		});
	    return allButton;
	}
	
	protected Button createRangeRadioButton(Composite parent) 
	{
		Button rangeButton = new Button(parent,SWT.RADIO);
	    GridData allButtonData = new GridData();
	    allButtonData.horizontalSpan = 2;
	    rangeButton.setLayoutData(allButtonData);
	    rangeButton.setText(RANGE);
	    rangeButton.addSelectionListener(new SelectionListener() {
		
			public void widgetSelected(SelectionEvent e) 
			{
				rangeChanged();
			}
		
			public void widgetDefaultSelected(SelectionEvent e) 
			{
			}
		});
	    return rangeButton;
	}
	
	protected void rangeChanged() 
	{	
		if(fRangeButton.getSelection())
		{
			fAllButton.setSelection(false);
			fComboStart.setEnabled(true);
			fComboEnd.setEnabled(true);
			
			int startSlide = Integer.parseInt(fComboStart.getText());
			int endSlide = Integer.parseInt(fComboEnd.getText());
			
			if(startSlide > endSlide)
			{
				startSlide = endSlide;
				fComboStart.setText(Integer.toString(endSlide));
			}
			
			fRange = new SlideRange(startSlide,endSlide);
		}
		
		if(fAllButton.getSelection())
		{
			fRangeButton.setSelection(false);
			fComboStart.setEnabled(false);
			fComboEnd.setEnabled(false);
			
			fRange = null;
		}
	}	
}
