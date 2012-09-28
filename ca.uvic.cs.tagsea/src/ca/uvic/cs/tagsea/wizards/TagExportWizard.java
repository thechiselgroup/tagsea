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

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * Wizard for exporting tags as XML.
 * @author Del Myers
 *
 */
public class TagExportWizard extends Wizard implements IExportWizard {
	TagExportWizardPage page;
	//XMLTagEmailWizardPage email;
	XMLTagSendWizardPage send;
	/**
	 * 
	 */
	public TagExportWizard() {
		super();
		setNeedsProgressMonitor(true);
		page = new TagExportWizardPage("Export Tags");
		//email = new XMLTagEmailWizardPage("Email Tags");
		send = new XMLTagSendWizardPage("Send Tags");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		TagXMLGatherer gatherer = new TagXMLGatherer();
		try {
			getContainer().run(false, true, gatherer);
			String xmlString = gatherer.getXML();
			if (page.doSend()) {
				return send.send();
			} else {
				doSave(xmlString, page.getFileName());
			}
		} catch (InvocationTargetException e) {
			TagSEAPlugin.log("Error generating XML", e);
			MessageDialog.openError(getShell(), "Error", "Error generating XML. See log for details.");
			return false;
		} catch (InterruptedException e) {
			return false;
		} catch (IOException e) {
			TagSEAPlugin.log("Error saving file", e);
			MessageDialog.openError(getShell(), "Error", "Error saving file. See log for details.");
			return false;
		}
		
		return true;
	}
	

	/**
	 * @param xmlString
	 * @param fileName
	 * @throws IOException 
	 */
	private void doSave(String xmlString, String fileName) throws IOException {
		FileWriter writer = new FileWriter(fileName);
		writer.write(xmlString);
		writer.flush();
		writer.close();
	}



	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		addPage(page);
		addPage(send);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		if (page.doSend()) {
			return (send.isPageComplete());
		}
		return page.isPageComplete();
	}
	
	

}
