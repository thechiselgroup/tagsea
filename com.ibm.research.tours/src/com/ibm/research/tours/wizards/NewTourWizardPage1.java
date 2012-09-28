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

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.fieldassist.FieldAssistColors;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;

public class NewTourWizardPage1 extends WizardPage 
{
	private static final String ERROR_EMPTY_TITLE = "The title is empty.";
	private static final String DESCRIPTION       = "Create a new Tour.";
	private static final String TITLE             = "New Tour";
	public static final String  PAGE_NAME         = "NewTourWizardPage1";
	
	private ISelection fSelection;
	private Text fTitleText;
	private Text fDescriptionText;
	private Text fAuthorText;
	
	public NewTourWizardPage1(IWorkbench workench, ISelection selection) 
	{
		super(PAGE_NAME);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		fSelection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) 
	{
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2,false);
		layout.verticalSpacing = 9;
		container.setLayout(layout);
		
		Label nameLabel = new Label(container,SWT.NONE);
		nameLabel.setText("Title:");
		fTitleText = new Text(container,SWT.BORDER);
		fTitleText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				setPageComplete(validatePage());
			}
		});
		GridData nameTextdata = new GridData(GridData.FILL_HORIZONTAL);
		fTitleText.setLayoutData(nameTextdata);
		fTitleText.setBackground(FieldAssistColors.getRequiredFieldBackgroundColor(fTitleText));
		
		Label descriptionLabel = new Label(container,SWT.NONE);
		descriptionLabel.setText("Description:");
		fDescriptionText = new Text(container,SWT.MULTI|SWT.BORDER);
		
		GridData descriptionTextdata = new GridData(GridData.FILL_HORIZONTAL);
		descriptionTextdata.heightHint = 48;
		fDescriptionText.setLayoutData(descriptionTextdata);
		
		Label authorLabel = new Label(container,SWT.NONE);
		authorLabel.setText("Author:");
		fAuthorText = new Text(container,SWT.BORDER);
		fAuthorText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fAuthorText.setText(System.getProperty("user.name"));
		
		setControl(container);
		setPageComplete(validatePage());
	}

	protected boolean validatePage() 
	{
		String name = fTitleText.getText();
		
		if(name.trim().length() == 0)
		{
			setErrorMessage(ERROR_EMPTY_TITLE);
			return false;
		}
		else
			setErrorMessage(null);

		return true;
	}
	
	public String getTourTitle()
	{
		return fTitleText.getText();
	}
	
	public String getTourDescription()
	{
		return fDescriptionText.getText();
	}
	
	public String getAuthor()
	{
		return fAuthorText.getText();
	}
}