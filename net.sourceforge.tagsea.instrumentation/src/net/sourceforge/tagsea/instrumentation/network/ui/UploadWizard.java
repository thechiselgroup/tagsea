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

import java.io.File;

import org.eclipse.jface.wizard.Wizard;

/**
 * Simple wizard for uploading files.
 * @author Del Myers
 *
 */
public class UploadWizard extends Wizard {

	private File[] files;
	private UploadSelectionWizardPage uploadPage;
	private File[] result;

	public UploadWizard(File[] files) {
		this.files = files;
	}
	
	@Override
	public boolean performFinish() {
		this.result = uploadPage.getSelectedFiles();
		return true;
	}
	
	@Override
	public void addPages() {
		this.uploadPage = new UploadSelectionWizardPage();
		uploadPage.setFiles(files);
		addPage(uploadPage);
	}

	public File[] getResult() {
		return (result != null) ? result : new File[0];
	}
}
