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
package net.sourceforge.tagsea.instrumentation;
 
import net.sourceforge.tagsea.core.ITagSEAStartParticipant;
import net.sourceforge.tagsea.instrumentation.network.ui.RegisterWizard;
import net.sourceforge.tagsea.instrumentation.tasks.TaskListMonitoring;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Early startup for this plugin. Registers model listeners.
 * @author Del Myers
 *
 */
public class EarlyStarter implements ITagSEAStartParticipant {
	private TaskListMonitoring monitoring;
	public void tagSEAStarted() {
		//TagSEAInstrumentationPlugin.getDefault().getLog().log(new Status(Status.INFO, TagSEAInstrumentationPlugin.PLUGIN_ID, "TagSEA started signal."));
		TagSEAInstrumentationPlugin.getDefault().startLogging();
		Display.getDefault().asyncExec(new Runnable(){
			public void run() {
				monitoring = new TaskListMonitoring();
				monitoring.connect();
			}
			
		});
	}

	public void tagSEAStarting() {
		if (!InstrumentationPreferences.isRegistered() && InstrumentationPreferences.getAskForRegistration()) {
			Display.getDefault().syncExec(new Runnable(){
				public void run() {
					Shell shell = PlatformUI.
					getWorkbench().
					getActiveWorkbenchWindow().
					getShell();
				WizardDialog wizard = new WizardDialog(shell, new RegisterWizard());
				wizard.setTitle("Register TagSEA");
				int code = wizard.open();
				if (code == WizardDialog.CANCEL) {
					InstrumentationPreferences.setAskForRegistration(false);
				}
				}
			});
			
		}
		TagSEAInstrumentationPlugin.getDefault().initializeLogging();
		
	}
}
