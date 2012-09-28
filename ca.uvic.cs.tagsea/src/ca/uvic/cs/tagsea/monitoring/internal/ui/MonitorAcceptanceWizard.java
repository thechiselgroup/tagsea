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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.monitoring.internal.Monitoring;


/**
 * A wizard for accepting monitoring of tagsea.
 * @author Del Myers
 *
 */
public class MonitorAcceptanceWizard extends Wizard {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		IPreferenceStore prefs = TagSEAPlugin.getDefault().getPreferenceStore();
		IWizardPage[] pages = getPages();
		for (IWizardPage page : pages) {
			if (page instanceof MonitorAcceptanceWizardPage) {
				MonitorAcceptanceWizardPage p = (MonitorAcceptanceWizardPage)page;
				prefs.setValue(Monitoring.getPreferenceKey(p.getMonitor()), Boolean.toString(p.isAccepted()));
			}
		}
		return true;
	}

	
}
