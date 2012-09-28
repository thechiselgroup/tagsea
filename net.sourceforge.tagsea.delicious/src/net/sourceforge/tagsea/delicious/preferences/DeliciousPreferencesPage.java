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
package net.sourceforge.tagsea.delicious.preferences;

import net.sourceforge.tagsea.delicious.DeliciousWaypointPlugin;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * 
 * @author mdesmond
 *
 */
public class DeliciousPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage, IDeliciousPreferences
{
	Text fUserNameText;
	Text fPasswordText;
	
	Text fTagFilterText;
	Text fUrlFilterText;
	
	@Override
	protected Control createContents(Composite parent) 
	{
		Composite root = new Composite(parent,SWT.NONE);
		root.setLayout(new GridLayout());

		Group group = new Group(root,SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(data);
		group.setText("Credentials");
		group.setLayout(new GridLayout(2,false));
		
		new Label(group,SWT.NONE).setText("Username");
		fUserNameText = new Text(group,SWT.BORDER);
		fUserNameText.setText(getPreferenceStore().getString(USERNAME));
		data = new GridData(GridData.FILL_HORIZONTAL);
		fUserNameText.setLayoutData(data);
		new Label(group,SWT.NONE).setText("Password");
		fPasswordText = new Text(group,SWT.BORDER|SWT.PASSWORD);
		fPasswordText.setText(getPreferenceStore().getString(PASSWORD));
		data = new GridData(GridData.FILL_HORIZONTAL);
		fPasswordText.setLayoutData(data);
		
		Group filterGroup = new Group(root,SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		filterGroup.setLayoutData(data);
		filterGroup.setText("Filters");
		filterGroup.setLayout(new GridLayout(2,false));
		
		new Label(filterGroup,SWT.NONE).setText("Tag filter (space seperated)");
		fTagFilterText = new Text(filterGroup,SWT.BORDER|SWT.MULTI);
		fTagFilterText.setText(getPreferenceStore().getString(TAG_FILTER));
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 50;
		fTagFilterText.setLayoutData(data);
		
		new Label(filterGroup,SWT.NONE).setText("Url filter");
		fUrlFilterText = new Text(filterGroup,SWT.BORDER);
		fUrlFilterText.setText(getPreferenceStore().getString(URL_FILTER));
		data = new GridData(GridData.FILL_HORIZONTAL);
		fUrlFilterText.setLayoutData(data);
		
		return root;
	}

	public void init(IWorkbench workbench) 
	{
		setPreferenceStore(DeliciousWaypointPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void performDefaults() 
	{
		super.performDefaults();
		fUserNameText.setText(getPreferenceStore().getDefaultString(USERNAME));
		fPasswordText.setText(getPreferenceStore().getDefaultString(PASSWORD));
		fTagFilterText.setText(getPreferenceStore().getDefaultString(TAG_FILTER));
		fUrlFilterText.setText(getPreferenceStore().getDefaultString(URL_FILTER));
	}

	@Override
	public boolean performOk() 
	{
		getPreferenceStore().setValue(USERNAME,fUserNameText.getText());
		getPreferenceStore().setValue(PASSWORD,fPasswordText.getText());
		getPreferenceStore().setValue(TAG_FILTER,fTagFilterText.getText());
		getPreferenceStore().setValue(URL_FILTER,fUrlFilterText.getText());
		return super.performOk();
	}
}
