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
package net.sourceforge.tagsea.resources.ui;



import java.util.Date;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog for creating/adding wayoitns to a resource.
 * @author Del Myers
 */

public class WaypointCreateDialog extends Dialog {

	private IResource[] resources;
	private int line;
	private TagEditorComposite editorComposite;
	private String[] tagNames;
	private Date date;
	private String message;
	private String author;

	public WaypointCreateDialog(IShellProvider parentShell) {
		super(parentShell);
		initialize();
	}
	
	/**
	 * 
	 */
	private void initialize() {
		this.resources = new IResource[0];
		this.line = -1;
	}

	public WaypointCreateDialog(Shell shell) {
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
		this.editorComposite = new TagEditorComposite(parent, SWT.NONE, this.resources, this.line);
		editorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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
	
	public void setResources(IResource[] resources) {
		this.resources = resources;
	}
	
	public void setLineNumber(int line) {
		this.line = line;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add Waypoint");
		newShell.setImage(TagSEAPlugin.getDefault().getImageRegistry().get(ITagSEAImageConstants.IMG_WAYPOINT));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		this.tagNames = this.editorComposite.getTagNames();
		this.date = this.editorComposite.getDate();
		this.message = this.editorComposite.getMessage();
		this.author = this.editorComposite.getAuthor();
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
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @return the resources
	 */
	public IResource[] getResources() {
		return resources;
	}
	
	/**
	 * @return the line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @return
	 */
	public String getAuthor() {
		return this.author;
	}

}
