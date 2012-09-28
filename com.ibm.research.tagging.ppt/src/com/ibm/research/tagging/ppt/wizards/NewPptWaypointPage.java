/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/

package com.ibm.research.tagging.ppt.wizards;

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

import com.ibm.research.tagging.core.ui.wizards.WaypointPage;
import com.ibm.research.tagging.ppt.PptWaypointPlugin;

public class NewPptWaypointPage extends WaypointPage 
{	
	private final static String WIZARD_ICON = "icons/PowerpointAppIcon.png";
	private final static String PAGE_TITLE = "Powerpoint Slide Waypoint";
	private final static String PAGE_DESCRIPTION = "Fill in the provided fields to create a new powerpoint slide waypoint.";
	
	private final static String RANGE_TITLE = "Slide Range";
	private final static String ALL_RANGE = "All Slides";
	private final static String RANGE = "Range";
	private final static String START_RANGE = "From";
	private final static String END_RANGE = "To";
	private final static String RANGE_INFO = "Select a subrange of the powerpoint slide show to waypoint.";
	
	private int fNumberOfSlides;
	private int fStartSlide;
	private int fEndSlide;
	
	private Button fAllButton;
	private Button fRangeButton;
	private Combo fComboStart;
	private Combo fComboEnd;

	public NewPptWaypointPage(int numberOfSlides) 
	{
		super(PAGE_TITLE,PAGE_DESCRIPTION,PptWaypointPlugin.getImageDescriptor(WIZARD_ICON));
		fNumberOfSlides = numberOfSlides;
		fStartSlide = 1;
		fEndSlide = numberOfSlides;
	}
	
	protected void createPageContents(Composite parent) 
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
	    
	    super.createPageContents(parent);
	}
	
	protected Combo createStartRangeCombo(Composite parent)
	{
	    String[] items = new String[fNumberOfSlides];
		 
	    for(int i = 0; i < fNumberOfSlides ; i++)
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
	    String[] items = new String[fNumberOfSlides];
		 
	    for(int i = 0; i < fNumberOfSlides ; i++)
	    	items[i] = Integer.toString(i+1);
	    
	    
	    Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
	    combo.setItems(items);
	    combo.setText(items[fNumberOfSlides-1]);
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
	    allButton.addSelectionListener(new SelectionListener() {
		
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
			
			fStartSlide = Integer.parseInt(fComboStart.getText());
			fEndSlide = Integer.parseInt(fComboEnd.getText());
			
			if(fStartSlide > fEndSlide)
			{
				fStartSlide = fEndSlide;
				fComboStart.setText(Integer.toString(fEndSlide));
			}
		}
		
		if(fAllButton.getSelection())
		{
			fRangeButton.setSelection(false);
			fComboStart.setEnabled(false);
			fComboEnd.setEnabled(false);
			
			fStartSlide = 1;
			fEndSlide = fNumberOfSlides;
		}
	}

	protected void rangeChange() 
	{
		if(fRangeButton.getSelection())
		{
			fAllButton.setSelection(false);
			fComboStart.setEnabled(true);
			fComboEnd.setEnabled(true);
			
			fStartSlide = Integer.parseInt(fComboStart.getText());
			fEndSlide = Integer.parseInt(fComboEnd.getText());
			
			if(fStartSlide > fEndSlide)
			{
				fStartSlide = fEndSlide;
				fComboStart.setText(Integer.toString(fEndSlide));
			}
		}
		
		if(fAllButton.getSelection())
		{
			fRangeButton.setSelection(false);
			fComboStart.setEnabled(false);
			fComboEnd.setEnabled(false);
			
			fStartSlide = 1;
			fEndSlide = fNumberOfSlides;
		}
	}
	
	public int getStartSlide() 
	{
		return fStartSlide;
	}
	
	public int getEndSlide() 
	{
		return fEndSlide;
	}
}
