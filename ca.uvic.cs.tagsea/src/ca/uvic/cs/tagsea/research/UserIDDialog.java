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
package ca.uvic.cs.tagsea.research;

import java.lang.reflect.InvocationTargetException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * A dialog that prompts users for some information so that they can get a user id for the study.
 * @author Del Myers
 *
 */
public class UserIDDialog extends Dialog{	
	DialogComposite composite;
	private Button finish;
	private int id;
	private Button okay;
	private Button cancel;
	
	public static final int FINISH = 2;
	public static final int ERROR = -1;
	
	/**
	 * @param parentShell
	 */
	public UserIDDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new DialogComposite(parent, SWT.RESIZE);
		return composite;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		okay = createButton(parent, IDialogConstants.OK_ID, "Get ID", true);
		// create OK and Cancel buttons by default
		finish = createButton(parent, IDialogConstants.FINISH_ID, IDialogConstants.FINISH_LABEL,
				false);
		cancel = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		finish.setEnabled(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		ProgressMonitorDialog progress = new ProgressMonitorDialog(this.getParentShell());
		GetIDWithProgress getID = 
			new GetIDWithProgress(
				composite.getFirstName(), 
				composite.getLastName(),
				composite.getEmail(),
				composite.isAnonymous(),
				composite.getJob(),
				composite.getCompanySize(),
				composite.getCompany()
			);
		try {
			progress.run(false, true,getID);
		} catch (InvocationTargetException ex) {
			Exception e = (Exception)ex.getCause();
			if (e instanceof NoRouteToHostException || e instanceof UnknownHostException) {
				MessageDialog.openError(null, "Error Communicating",
						"There was an error getting a new user id. \n"
								+ "No network connection.  Please try again later");
			} else {
				MessageDialog.openError(null, "Error Communicating",
						"There was an error getting a new user id: \n" + e.getClass().getCanonicalName()
								+ e.getMessage());
				TagSEAPlugin.log("", e);
			}
			return;
		} catch (InterruptedException e) {
		}
		if (getID.success()) {
			composite.setIDText(getID.getUID());
			this.id = getID.getUID();
			finish.setEnabled(true);
			cancel.setEnabled(false);
			okay.setEnabled(false);
		} else {
			setReturnCode(ERROR);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		switch (buttonId) {
			case IDialogConstants.OK_ID:
				okPressed();
				break;
			case IDialogConstants.CANCEL_ID:
				cancelPressed();
				break;
			case IDialogConstants.FINISH_ID:
				finishPressed();
				break;
		}
	}
	
	protected void finishPressed() {
		setReturnCode(FINISH);
		close();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	@Override
	protected void cancelPressed() {
		if (getReturnCode() == ERROR) {
			close();
		} else {
			super.cancelPressed();
		}
	}
	
	/**
	 * Opens the dialog.
	 * @return FINISH if the user registered successfully, CANCEL if cancelled without registration, ERROR
	 * if cancelled due to an error.
	 *  
	 */
	@Override
	public int open() {
		int result = super.open();
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Register TagSEA");
	}
	
	
	public int getID() {
		return id;
	}
	
}
