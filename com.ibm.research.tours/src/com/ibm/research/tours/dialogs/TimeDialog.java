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
package com.ibm.research.tours.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.ibm.research.tours.ITimeLimit;
import com.ibm.research.tours.TimeLimit;

public class TimeDialog extends Dialog 
{
	private String fTitle;
	private String fMessage;
	private Button fTimeCheckButton;
	private ITimeLimit fTimeLimit;
	private boolean fIsTimeSet;
	private Spinner fMinuteSpinner;
	private Spinner fSecondSpinner;
	
	public TimeDialog(Shell parentShell, String title, String message) 
	{
		super(parentShell);
		fTitle = title;
		fMessage = message;
		
	}
	
	public void init(ITimeLimit limit)
	{
		if(limit != null)
		{
			fTimeLimit = limit;
			fIsTimeSet = true;
		}	
		else
			fTimeLimit = new TimeLimit();
	}
	
    public boolean isTimeLimitSet() 
    {
		return fIsTimeSet;
	}
	
    public ITimeLimit getTimeLimit() 
    {
		return fTimeLimit;
	}

	protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (fTitle != null) 
        {
			shell.setText(fTitle);
		}
    }
	
	@Override
	protected Control createDialogArea(Composite parent) 
	{
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		
		fTimeCheckButton = new Button(composite,SWT.CHECK);
        GridData data = new GridData(GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                | GridData.VERTICAL_ALIGN_CENTER);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        fTimeCheckButton.setLayoutData(data);
		fTimeCheckButton.setText(fMessage);
		fTimeCheckButton.setSelection(fIsTimeSet);
		
		fTimeCheckButton.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				fIsTimeSet = fTimeCheckButton.getSelection();
				fMinuteSpinner.setEnabled(fIsTimeSet);
				fSecondSpinner.setEnabled(fIsTimeSet);
			}
		
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		
		Group timeGroup = new Group(composite,SWT.SHADOW_ETCHED_IN);
		GridLayout groupLayout = new GridLayout(4,false);
		timeGroup.setLayout(groupLayout);
		GridData timeGroupData = new GridData(GridData.FILL_BOTH);
		timeGroupData.horizontalSpan = 2;
		timeGroup.setLayoutData(timeGroupData);
		timeGroup.setText("Time Limit");
		
		Label minuteLabel = new Label(timeGroup,SWT.NONE);
		minuteLabel.setText("Minutes");
		fMinuteSpinner = new Spinner(timeGroup,SWT.BORDER);
		
		if(fTimeLimit!=null)
			fMinuteSpinner.setSelection(fTimeLimit.getMinutes());
		
		fMinuteSpinner.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				if(fTimeLimit!=null)
					fTimeLimit.setMinutes(fMinuteSpinner.getSelection());
			}
		});
		
		Label secondLabel = new Label(timeGroup,SWT.NONE);
		secondLabel.setText("Seconds");
		fSecondSpinner = new Spinner(timeGroup,SWT.BORDER);
		fSecondSpinner.setMaximum(60);
		
		if(fTimeLimit!=null)
			fSecondSpinner.setSelection(fTimeLimit.getSeconds());
		
		fSecondSpinner.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				if(fTimeLimit!=null)
					fTimeLimit.setSeconds(fSecondSpinner.getSelection());
			}
		});
		
		fMinuteSpinner.setEnabled(fIsTimeSet);
		fSecondSpinner.setEnabled(fIsTimeSet);
		
		return parent;
	}
}
