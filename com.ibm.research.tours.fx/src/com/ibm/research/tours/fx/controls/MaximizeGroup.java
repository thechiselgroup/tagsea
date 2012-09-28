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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class MaximizeGroup 
{
	private Group fMaximizeGroup;
    private Button fMaximizedButton;
	private boolean fMaximized;
	
	public MaximizeGroup(Boolean maximized)
	{
		fMaximized = maximized;
	}

	public Group createComposite(Composite parent, String groupName, String message)
	{
		fMaximizeGroup = new Group(parent,SWT.SHADOW_ETCHED_IN);
		fMaximizeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fMaximizeGroup.setLayout(new GridLayout());
		fMaximizeGroup.setText(groupName);

    	fMaximizedButton = new Button(fMaximizeGroup, SWT.CHECK);
    	fMaximizedButton.setText(message);
    	fMaximizedButton.addSelectionListener(new SelectionListener() {
		
			public void widgetSelected(SelectionEvent e) 
			{
				fMaximized = fMaximizedButton.getSelection();
			}
		
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
    	fMaximizedButton.setSelection(fMaximized);
 
		return fMaximizeGroup;
	}

	public boolean getMaximized() 
	{
		return fMaximized;
	}
}
