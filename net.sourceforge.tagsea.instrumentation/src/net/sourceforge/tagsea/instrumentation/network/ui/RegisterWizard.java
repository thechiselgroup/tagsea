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

import org.eclipse.jface.wizard.Wizard;

/**
 * Wizard for registering.
 * @author Del Myers
 *
 */
public class RegisterWizard extends Wizard {
	RegisterPage registerPage;
	@Override
	public boolean performFinish() {
		return true;
	}
	
	@Override
	public void addPages() {
		registerPage = new RegisterPage("Register TagSEA");
		addPage(registerPage);
		addPage(new PerformRegisterPage("Registering TagSEA"));
		setWindowTitle("TagSEA Registration");
	}

	/**
	 * @return
	 */
	public String getFirstName() {
		return getRegisterComposite().getFirstName();
	}

	/**
	 * @return
	 */
	public String getLastName() {
		return getRegisterComposite().getLastName();
	}

	/**
	 * @return
	 */
	public String getEmail() {
		return getRegisterComposite().getEmail();
	}

	/**
	 * @return
	 */
	public boolean isAnonymous() {
		return getRegisterComposite().isAnonymous();
	}

	/**
	 * @return
	 */
	public String getJob() {
		return getRegisterComposite().getJob();
	}

	/**
	 * @return
	 */
	public String getCompany() {
		return getRegisterComposite().getCompany();
	}

	/**
	 * @return
	 */
	public String getCompanySize() {
		return getRegisterComposite().getCompanySize();
	}

	/**
	 * @return
	 */
	public String getFieldOfWork() {
		return getRegisterComposite().getFieldOfWork();
	}
	
	private RegisterComposite getRegisterComposite() {
		return registerPage.getRegisterComposite();
	}

	/**
	 * @return
	 */
	public boolean willMonitor() {
		return registerPage.willMonitor();
	}
	
	

}
