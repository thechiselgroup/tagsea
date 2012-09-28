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
package com.ibm.research.tagging.core.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.research.tagging.core.TagCorePlugin;

/**
 * 
 * @author mdesmond
 *
 */
public class TagCorePreferencesPage extends PreferencePage implements IWorkbenchPreferencePage
{
	@Override
	protected Control createContents(Composite parent) 
	{
		Composite root = new Composite(parent,SWT.NONE);
		root.setLayout(new GridLayout());
		
		Group tagsViewGroup = new Group(root,SWT.SHADOW_ETCHED_IN);
		tagsViewGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		tagsViewGroup.setLayout(new GridLayout());
		tagsViewGroup.setText("Tags View");
		
		Button linkTagsWithWaypoints = new Button(tagsViewGroup, SWT.CHECK);
		linkTagsWithWaypoints.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		linkTagsWithWaypoints.setText("Link tag view with waypoint view");
		linkTagsWithWaypoints.setSelection(true);
		
		Button hideUnusedTags = new Button(tagsViewGroup, SWT.CHECK);
		hideUnusedTags.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		hideUnusedTags.setText("Hide unused tags");
		hideUnusedTags.setSelection(true);
		
		return root;
	}

	public void init(IWorkbench workbench) 
	{
		setPreferenceStore(TagCorePlugin.getDefault().getPreferenceStore());
	}
}
