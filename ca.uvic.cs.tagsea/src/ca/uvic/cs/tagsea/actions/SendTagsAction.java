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
package ca.uvic.cs.tagsea.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewSite;

import ca.uvic.cs.tagsea.wizards.TagExportWizard;

/**
 * Uploads the user's tags to the UVic server.
 * @author Del Myers
 *
 */
public class SendTagsAction extends Action {
	
	private IViewSite site;
	/**
	 * 
	 */
	public SendTagsAction(IViewSite site) {
		this.site = site;
	}
	
	public void run() {
		WizardDialog d = new WizardDialog(site.getShell(), new TagExportWizard());
		d.open();
	}
}
