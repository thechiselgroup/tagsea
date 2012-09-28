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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import ca.uvic.cs.tagsea.monitoring.ITagSEAMonitor;

/**
 * TODO comment
 * @author Del Myers
 *
 */
public class MonitorAcceptanceWizardPage extends WizardPage {
	
	private ITagSEAMonitor monitor;
	private boolean accepted;
	private Button accept;
	private Button reject;

	/**
	 * @param pageName
	 */
	public MonitorAcceptanceWizardPage(String pageName, ITagSEAMonitor monitor) {
		super(pageName);
		this.monitor = monitor;
		this.accepted = false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		page.setLayout(layout);
		page.setLayoutData(data);
		
		Browser text = new Browser(page, SWT.BORDER | SWT.MULTI | SWT.SCROLL_LINE);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.widthHint = 400;
		text.setText(monitor.getAgreementString());
		text.setLayoutData(data);
		
		//need a composite for individual radio behavior.
		Composite c = new Composite(page, SWT.NONE);
		layout = new GridLayout(1, false);
		c.setLayout(layout);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		c.setLayoutData(data);
		accept = new Button(c, SWT.RADIO);
		accept.setText("I accept this agreement.");
		accept.setLayoutData(data);
		SelectionAdapter selection = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.getSource() == accept && accept.getSelection()) {
					reject.setSelection(false);
					accepted = true;
				} else if (e.getSource() == reject && reject.getSelection()) {
					accept.setSelection(false);
					accepted = false;
				}
				if (reject.getSelection() || accept.getSelection())
					setPageComplete(true);
				else
					setPageComplete(false);
			}
		};
		accept.addSelectionListener(selection);
		
		c = new Composite(page, SWT.NONE);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		layout = new GridLayout(1, false);
		c.setLayout(layout);
		c.setLayoutData(data);
		reject = new Button(c, SWT.RADIO);
		reject.setText("I do not accept this agreement.");
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		reject.setLayoutData(data);
		reject.addSelectionListener(selection);
		accept.setSelection(false);
		reject.setSelection(false);
		setPageComplete(false);
		setMessage("Some monitors have been contributed to the TagSEA plugin. The following pages will " +
				"present you with agreements to allow TagSEA to monitor your interactions with it.\nPlease " +
				"read the agreement carefully. If you do not agree, TagSEA will not use the monitor to monitor " +
				"your usage of TagSEA.");
		setControl(page);

	}
	
	/**
	 * @return the monitor
	 */
	public ITagSEAMonitor getMonitor() {
		return monitor;
	}
	
	/**
	 * @return the accepted
	 */
	public boolean isAccepted() {
		return accepted;
	}

}
