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
package ca.uvic.cs.tagsea.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * TODO comment
 * @author Del Myers
 *
 */
public class XMLTagSendWizardPage extends WizardPage {
	Text text;
	String textData;
	/**
	 * @param pageName
	 */
	protected XMLTagSendWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		page.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		page.setLayoutData(data);
		
		setMessage("This is the text that you are about to send to us. Please review it, and select 'Finish' to continue." +
				"If you have questions, please email tagsea-user-help@lists.sourceforge.net or chisel-support@cs.uvic.ca.");
		
		
		text = new Text(page, SWT.MULTI | SWT.V_SCROLL);
		text.setEditable(false);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		text.setLayoutData(data);
		setPageComplete(false);
		setControl(page);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			if (!isPageComplete()) {
				gatherXML();
				text.setText(textData);
				getControl().redraw();
				setPageComplete(true);
			}
		}
	}

	/**
	 * 
	 */
	private void gatherXML() {
		TagXMLGatherer gatherer = new TagXMLGatherer();
		try {
			getWizard().getContainer().run(false, true, gatherer);
		} catch (InvocationTargetException e) {
			TagSEAPlugin.log("Error generating XML", e);
			textData = "Error generating XML. See log for details.";
			setPageComplete(false);
			return;
		} catch (InterruptedException e) {
			textData = "Operation Cancelled";
			setPageComplete(false);
			return;
		}
		textData = gatherer.getXML();
		setPageComplete(true);
		
	}
	
	
	
	boolean send() {
		int id = TagSEAPlugin.getDefault().getUserID();
		if (id < 0) {
			boolean result = MessageDialog.openQuestion(getShell(), "Cannot Send", "You must register TagSEA before you can upload your tags. Would you like to register now?");
			if (result) {
				id = TagSEAPlugin.getDefault().askForUserID();
			}
		}
		if (id < 0) {
			MessageDialog.openError(getShell(), "Operation Incomplete", "Error getting ID.");
			return false;
		}
		final int fid = TagSEAPlugin.getDefault().askForUserID();
		try {
			getContainer().run(false, false, new IRunnableWithProgress(){
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					TagNetworkSender.send(textData, monitor, fid);
				}
			});
		} catch (InvocationTargetException ex) {
			MessageDialog.openError(getShell(), "Error", ex.getLocalizedMessage());
			TagSEAPlugin.log(ex.getLocalizedMessage(), ex);
			return false;
		} catch (InterruptedException e) {
		}
		MessageDialog.openInformation(getShell(), "Complete", "Upload Complete");
		return true;
	}
	
	
}
