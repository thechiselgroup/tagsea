/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.research.UserIDDialog;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By subclassing
 * <samp>FieldEditorPreferencePage</samp>, we can use the field support built into JFace that allows us to create a
 * page that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that belongs to the main
 * plug-in class. That way, preferences can be accessed directly via the preference store.
 * 
 * @see PreferenceInitializer
 * @see TagSEAPreferences
 * @author Chris Callendar
 */

public class TagSEAPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private BooleanFieldEditor authorAllowedEditor;
	private UserNameStringFieldEditor authorNameEditor;
	
	public TagSEAPreferencePage() {
		super(GRID);
		setPreferenceStore(TagSEAPlugin.getDefault().getPreferenceStore());
		setDescription("TagSEA Preferences.");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		//create a field editor area, and a non-field editor area;
		Composite page = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		page.setLayout(layout);
		page.setLayoutData(GridDataFactory.fillDefaults().create());
		super.createContents(page);
		Composite page2 = new Composite(parent, SWT.NONE);
		layout = new GridLayout();
		page2.setLayout(layout);
		page2.setLayoutData(GridDataFactory.fillDefaults().create());
		createNonFields(page2);
		return page;
	}
	/**
	 * @param page
	 */
	private void createNonFields(Composite parent) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(GridDataFactory.fillDefaults().create());
		final Label idLabel = new Label(buttonComposite, SWT.NONE);
		String idString = "";
		int id = TagSEAPlugin.getDefault().getUserID();
		if (id == -1) {
			idString = "Not Registered";
		} else {
			idString = "" +id;
		}
		idLabel.setText("TagSEA user id: " + idString);
		final Button b = new Button(buttonComposite, SWT.PUSH);
		b.setText("Register TagSEA");
		if (id != -1) {
			b.setEnabled(false);
		} else {
			b.addSelectionListener(new SelectionAdapter(){
				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					UserIDDialog dialog = new UserIDDialog(getShell());
					int result = dialog.open();
					if (result == UserIDDialog.FINISH) {
						int id = dialog.getID();
						TagSEAPreferences.setUserID(id);
						idLabel.setText("TagSEA user id: " +id);
						b.setEnabled(false);
					}
					TagSEAPreferences.setAskedForID(true);
				}
			});
		}
		
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
//		addField(new BooleanFieldEditor(TagSEAPreferences.KEY_ADD_TASK_TAG,
//				"Automatically add the @tag compiler task tag", getFieldEditorParent()));

		addField(new BooleanFieldEditor(TagSEAPreferences.KEY_ADD_DATE,
				"Add &date information to tags", getFieldEditorParent()));

		authorAllowedEditor = new BooleanFieldEditor(TagSEAPreferences.KEY_ADD_AUTHOR, 
				"Add &author information to tags", getFieldEditorParent()) {
			protected void valueChanged(boolean oldValue, boolean newValue) {
				super.valueChanged(oldValue, newValue);
				authorNameEditor.setEnabled(authorAllowedEditor.getBooleanValue(), getFieldEditorParent());
			}
		};
		addField(authorAllowedEditor);
		
		authorNameEditor = new UserNameStringFieldEditor(TagSEAPreferences.KEY_AUTHOR_NAME,
				"Author &name", getFieldEditorParent());
		addField(authorNameEditor);
		authorNameEditor.setEnabled(TagSEAPreferences.isAddAuthor(), getFieldEditorParent());
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}