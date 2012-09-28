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

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.tagsea.instrumentation.InstrumentationPreferences;
import net.sourceforge.tagsea.instrumentation.TagSEAInstrumentationPlugin;
import net.sourceforge.tagsea.instrumentation.network.RegisterWithProgress;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Del Myers
 *
 */
public class PerformRegisterPage extends WizardPage {
	/**
	 * @param pageName
	 */
	protected PerformRegisterPage(String pageName) {
		super(pageName, "Registering TagSEA", TagSEAInstrumentationPlugin.imageDescriptorFromPlugin(TagSEAInstrumentationPlugin.PLUGIN_ID, "/icons/check.png"));
	}
	Text infoText;
	public void createControl(Composite parent) {
		infoText = new Text(parent, SWT.MULTI | SWT.READ_ONLY);
		infoText.setEditable(false);
		infoText.setEnabled(false);
		infoText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (InstrumentationPreferences.isRegistered()) {
			String name = "";
			if (!InstrumentationPreferences.isAnonymous()) {
				name = InstrumentationPreferences.getFirstName() + " " + InstrumentationPreferences.getLastName();
			} else {
				name = "anonymous";
			}
			infoText.setText("Registered to " + name + ". User ID: " + InstrumentationPreferences.getUID());
			setPageComplete(true);
		} else {
			infoText.setText("Unregistered...");
			setPageComplete(false);
		}
		setControl(infoText);
	}
	
	
	@Override
	public void setVisible(boolean visible) {
		if (visible && !InstrumentationPreferences.isRegistered()) {
			RegisterWizard wizard = (RegisterWizard) getWizard();
			RegisterWithProgress progress = new RegisterWithProgress(
				wizard.getFirstName(),
				wizard.getLastName(),
				wizard.getEmail(),
				wizard.isAnonymous(),
				wizard.getJob(),
				wizard.getCompany(),
				wizard.getCompanySize(),
				wizard.getFieldOfWork()
			);
			try {
				getContainer().run(true, false, progress);
				if (progress.success()) {
					//set all of the registration info.
					InstrumentationPreferences.setFirstName(wizard.getFirstName());
					InstrumentationPreferences.setLastName(wizard.getLastName());
					InstrumentationPreferences.setEmail(wizard.getEmail());
					InstrumentationPreferences.setAnonymous(wizard.isAnonymous());
					InstrumentationPreferences.setJob(wizard.getJob());
					InstrumentationPreferences.setCompany(wizard.getCompany());
					InstrumentationPreferences.setCompanySize(wizard.getCompanySize());
					InstrumentationPreferences.setFieldOfWork(wizard.getFieldOfWork());
					InstrumentationPreferences.setAskForRegistration(false);
					InstrumentationPreferences.setUID(progress.getUserID());
					InstrumentationPreferences.setMonitoringEnabled(wizard.willMonitor());
					String name = "";
					if (!InstrumentationPreferences.isAnonymous()) {
						name = InstrumentationPreferences.getFirstName() + " " + InstrumentationPreferences.getLastName();
					} else {
						name = "anonymous";
					}
					infoText.setText("Registered to " + name + ". User ID: " + InstrumentationPreferences.getUID());
					setPageComplete(true);
				} else {
					infoText.setText("Error During Registration. See error log for details.");
					Status error = new Status(
							Status.ERROR,
							TagSEAInstrumentationPlugin.PLUGIN_ID,
							Status.ERROR,
							progress.getResponse(),
							null
						);
						TagSEAInstrumentationPlugin.getDefault().getLog().log(error);
					MessageDialog.openError(getShell(), "Error During Registration.", "An error ocurred during registration. See log for details.");
					setPageComplete(false);
				}
			} catch (InvocationTargetException e) {
				MessageDialog.openError(getShell(), "Error During Registration.", "An error ocurred during registration. See log for details.");
				infoText.setText("Error During Registration. See error log for details.");
				TagSEAInstrumentationPlugin.getDefault().log(e);
			} catch (InterruptedException e) {
			}
		}
		super.setVisible(visible);
	}
	
}
