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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ca.uvic.cs.tagsea.monitoring.ITagSEAMonitor;

/**
 * TODO comment
 * @author Del Myers
 *
 */
public class MonitorAgreementDialog extends Dialog {

	private ITagSEAMonitor monitor;

	/**
	 * @param parentShell
	 */
	protected MonitorAgreementDialog(Shell parentShell, ITagSEAMonitor monitor) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.monitor = monitor;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Agreement");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		page.setLayout(layout);
		GridDataFactory df = GridDataFactory.fillDefaults();
		page.setLayoutData(df.create());
		
		/*Text t = new Text(page, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		t.setLayoutData(df.create());
		t.setText(monitor.getAgreementString());*/
		Browser browser = new Browser(page, SWT.NONE);
		browser.setText(monitor.getAgreementString());
		GridData data = df.create();
		data.widthHint = 400;
		data.heightHint = 400;
		browser.setLayoutData(data);
		return page;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}
	

}
