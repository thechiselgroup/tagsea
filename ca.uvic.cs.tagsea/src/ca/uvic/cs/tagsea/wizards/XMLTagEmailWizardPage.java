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
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * TODO comment
 * @author Del Myers
 *
 */
public class XMLTagEmailWizardPage extends WizardPage {
	Text text;
	String textData;
	/**
	 * @param pageName
	 */
	protected XMLTagEmailWizardPage(String pageName) {
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
		
		setMessage("Please copy and paste the following text into your email." +
				"Email the text to: tagsea-user-help@lists.sourceforge.net or chisel-support@cs.uvic.ca.");
		
		
		text = new Text(page, SWT.MULTI | SWT.V_SCROLL);
		text.setEditable(false);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		text.setLayoutData(data);
				
		Button copy = new Button(page, SWT.NONE);
		copy.setText("Copy");
		data = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		copy.setLayoutData(data);
		copy.addSelectionListener(new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				Clipboard clip = new Clipboard(getShell().getDisplay());
				clip.setContents(new Object[] {text.getText()}, new Transfer[]{TextTransfer.getInstance()});
				clip.dispose();
			}
		});
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
				try {
					doEmail();
				} catch (PartInitException e) {
					TagSEAPlugin.log("", e);
				} catch (MalformedURLException e) {
					TagSEAPlugin.log("", e);
				}
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
	
	/**
	 * @param doc
	 * @throws PartInitException 
	 * @throws MalformedURLException 
	 */
	private void doEmail() throws PartInitException, MalformedURLException {
		String href = "mailto:" + "chisel-support@cs.uvic.ca, tagsea-user-help@lists.sourceforge.net" + "?subject=My Tags" + "&body=Please paste the XML text from the export wizard here.";
		IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
		IWebBrowser browser = support.getExternalBrowser();
		browser.openURL(new URL(href));
	}
	
	
	
}
