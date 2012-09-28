/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.monitoring.internal.ui;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.monitoring.ITagSEAMonitor;
import ca.uvic.cs.tagsea.monitoring.internal.Monitoring;

/**
 * A preference page for monitors.
 * @author Del Myers
 *
 */
public class MonitoringPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	private class AgreementSelection extends SelectionAdapter {
		ITagSEAMonitor monitor;
		/**
		 * 
		 */
		public AgreementSelection(ITagSEAMonitor mon) {
			monitor = mon;
		}
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			new MonitorAgreementDialog(getShell(), monitor).open();
		}
	}

	private class BooleanStringFieldEditor extends BooleanFieldEditor {
		
		/**
		 * 
		 */
		public BooleanStringFieldEditor(String name, String label, int style, Composite parent) {
			super(name, label, style, parent);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.preference.BooleanFieldEditor#doStore()
		 */
		@Override
		protected void doStore() {
			boolean b =  getBooleanValue();
			getPreferenceStore().setValue(getPreferenceName(), Boolean.toString(b));
		}
	}
	
	private ArrayList<BooleanFieldEditor> editors;

	/**
	 * @param style
	 */
	public MonitoringPreferencePage() {
		super("TagSEA Monitoring");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	protected void createFieldEditors() {
		
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
				
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		GridDataFactory fill = GridDataFactory.fillDefaults();
		GridDataFactory but = GridDataFactory.createFrom(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		page.setLayout(layout);
		GridData data = fill.create();
		data.widthHint = 300;
		page.setLayoutData(data);
		Text intro = new Text(page, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY);
		//intro.setEditable(false);
		//@tag incomplete
		intro.setText("Some monitors have been contributed to the TagSEA plugin. The following pages will " +
				"present you with agreements to allow TagSEA to monitor your interactions with it. Please " +
				"read the agreement carefully. If you do not agree, TagSEA will not use the monitor to monitor " +
				"your usage of TagSEA.");
		data = fill.create();
		data.horizontalSpan=2;
		data.grabExcessHorizontalSpace=false;
		data.widthHint = 300;
		intro.setLayoutData(data);
		String[] keys = Monitoring.getDefault().getPreferenceKeys();
		editors = new ArrayList<BooleanFieldEditor>();
		IPreferenceStore store = TagSEAPlugin.getDefault().getPreferenceStore();
		for (String key : keys) {
			ITagSEAMonitor mon = Monitoring.getDefault().getMonitorForPreference(key);
			if (mon != null) {
				Composite editorParent = new Composite(page, SWT.NONE);
				editorParent.setLayoutData(fill.create());
				BooleanFieldEditor editor = new BooleanStringFieldEditor(key, mon.getName(), BooleanFieldEditor.DEFAULT, editorParent);
				editor.setPreferenceStore(store);
				editors.add(editor);
				Button agreement = new Button(page, SWT.PUSH);
				agreement.setText("Agreement...");
				agreement.addSelectionListener(new AgreementSelection(mon));
				agreement.setLayoutData(but.create());
			}
		}
		loadEditors();
		return page;
	}

	/**
	 * 
	 */
	private void loadEditors() {
		for (BooleanFieldEditor editor : editors) {
			editor.load();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		for (BooleanFieldEditor editor : editors) {
			editor.store();
		}
		return super.performOk();
	}

}
