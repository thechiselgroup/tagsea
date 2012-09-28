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
package net.sourceforge.tagsea.url.actions;



import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import net.sourceforge.tagsea.url.URLWaypointPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for creating/adding wayoitns to a resource.
 * @author Del Myers
 */

public class URLWaypointCreateDialog extends StatusDialog {

	private TagEditorComposite editorComposite;
	private String[] tagNames;
	private Date date;
	private String description;
	private String author;
	private String url;
	
	/**
	 * 
	 */
	private void initialize() 
	{

	}

	public URLWaypointCreateDialog(Shell shell) {
		super(shell);
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		page.setLayout(layout);
		page.setData(data);
		this.editorComposite = new TagEditorComposite(parent, SWT.NONE);
		editorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		editorComposite.addVerifyListener(new VerifyListener(){

			public void verifyText(VerifyEvent e) 
			{
				if (e.widget instanceof Text) {
					String text = ((Text)e.widget).getText();
					if (text.indexOf(':') == -1) {
						text = "http://" + text;
					}
					try {
						updateStatus(Status.OK_STATUS);
						new URL(text);
					} catch (MalformedURLException e1) {
						updateStatus(new Status(IStatus.ERROR, URLWaypointPlugin.PLUGIN_ID, "Malformed URL: " +text));
					}
				}
			}
			
		});
		return page;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
//		 create the top level composite for the dialog
		Composite container = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		container.setLayout(gridLayout);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite composite = new Composite(container, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		applyDialogFont(composite);
		// initialize the dialog units
		initializeDialogUnits(composite);
		// create the dialog area and button bar
		dialogArea = createDialogArea(composite);
		buttonBar = createButtonBar(composite);
		return composite;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, "Add",
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Tag a URL");
		newShell.setImage(URLWaypointPlugin.getDefault().getImageRegistry().get(URLWaypointPlugin.IMG_WEB));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		this.tagNames = this.editorComposite.getTagNames();
		this.date = this.editorComposite.getDate();
		this.description = this.editorComposite.getDescription();
		this.author = this.editorComposite.getAuthor();
		this.url = this.editorComposite.getURL();
//		if (url.indexOf(':') == -1) {
//			url = url + "http://";
//		}
		super.okPressed();
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#getShellStyle()
	 */
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.APPLICATION_MODAL;
	}
	
	/**
	 * @return the tagNames
	 */
	public String[] getTagNames() {
		return tagNames;
	}
	
	/**
	 * @return the url
	 */
	public String getURL() {
		return url;
	}
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * @return the message
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return
	 */
	public String getAuthor() {
		return this.author;
	}

}
