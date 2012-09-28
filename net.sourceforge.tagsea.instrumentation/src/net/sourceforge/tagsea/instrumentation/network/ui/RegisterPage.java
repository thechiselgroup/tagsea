/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.instrumentation.network.ui;

import net.sourceforge.tagsea.instrumentation.TagSEAInstrumentationPlugin;
import net.sourceforge.tagsea.instrumentation.network.ui.RegisterComposite.IValidator;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

/**
 * Dialog for registering TagSEA.
 * @author Del Myers
 *
 */
public class RegisterPage extends WizardPage implements IValidator {
	private Button agreementButton;
	private RegisterComposite registerComposite;

	/**
	 * @param pageName
	 */
	protected RegisterPage(String pageName) {
		super(pageName, "Registering TagSEA", TagSEAInstrumentationPlugin.imageDescriptorFromPlugin(TagSEAInstrumentationPlugin.PLUGIN_ID, "/icons/check.png"));
	}

	public void createControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout());
		registerComposite = new RegisterComposite(page, SWT.NONE);
		registerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), "net.sourceforge.tagsea.instrumentation.registration");
		registerComposite.setValidator(this);
		setControl(page);
		agreementButton = new Button(page, SWT.CHECK);
		agreementButton.setText("*I agree to the terms of the TagSEA Monitoring Consent Agreement");
		agreementButton.setForeground(agreementButton.getDisplay().getSystemColor(SWT.COLOR_RED));
		agreementButton.setLayoutData(new GridData());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(agreementButton, "net.sourceforge.tagsea.instrumentation.consent");
		agreementButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				//force the tray open
				agreementButton.notifyListeners(SWT.Help, new Event());
				validateInput(registerComposite);
			}
		});
		agreementButton.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
		getShell().addShellListener(new ShellAdapter(){
			@Override
			public void shellActivated(ShellEvent e) {
				getShell().notifyListeners(SWT.Help, new Event());
			}
		});
		validateInput(registerComposite);
	}

	public String validateInput(RegisterComposite composite) {
		if (composite.isAnonymous()) {
			setErrorMessage(null);
			setPageComplete(true);
			return null;
		}
		String s = composite.getFirstName();
		if (s == null || "".equals(s.trim())) {
			String error = "First name missing.";
			setErrorMessage(error);
			setPageComplete(false);
			return error;
		} 
		s = composite.getLastName();
		if (s == null || "".equals(s.trim())) {
			String error = "Last name missing.";
			setErrorMessage(error);
			setPageComplete(false);
			return error;
		}
		s = composite.getEmail();
		if (s != null) {
			if (!s.matches(".+\\@.+\\..+")) {
				String error = "Invalid email address.";
				setErrorMessage(error);
				setPageComplete(false);
				return error;
			}
		} else {
			String error = "Invalid email address.";
			setPageComplete(false);
			return error;
		}
		if (!agreementButton.getSelection()) {
			String error = "You must agree to the TagSEA Monitoring Consent Agreement.";
			setErrorMessage(error);
			return error;
		}
		setErrorMessage(null);
		setPageComplete(true);
		return null;
	}
	
	public RegisterComposite getRegisterComposite() {
		return registerComposite;
	}
	
	public boolean willMonitor() {
		return agreementButton.getSelection();
	}

}
