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
package com.ibm.research.tours.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewTourWizardPage2 extends WizardNewFileCreationPage 
{
	private static final String DESCRIPTION       = "Specify the location of the new tour.";
	private static final String TITLE             = "New Tour Location";
	public static final String  PAGE_NAME         = "NewTourWizardPage2";
	
	public NewTourWizardPage2(IWorkbench aWorkbench, IStructuredSelection selection) 
	{
		super(PAGE_NAME, selection);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) 
	{	
		super.createControl(parent);
		
		NewTourWizardPage1 page1 = (NewTourWizardPage1)getWizard().getPage(NewTourWizardPage1.PAGE_NAME);
		
		if(page1 != null)
		{
			String fileName = page1.getTourTitle();
			
			if(fileName.trim().length() > 0)
			{
				fileName = fileName.replaceAll("\\s+","");
				
				if(!fileName.endsWith(".tour"))
					fileName = fileName + ".tour";
				
				setFileName(fileName);
			}
		}
		
		setPageComplete(validatePage());
	}
	
	@Override
	protected void createAdvancedControls(Composite parent) 
	{
		// No linking to the file system
	}
	
	@Override
	protected IStatus validateLinkedResource() 
	{
		// No validation of linked resources
		return Status.OK_STATUS;
	}
	
	@Override
	protected void createLinkTarget() 
	{
		// No Link target
	}
}
