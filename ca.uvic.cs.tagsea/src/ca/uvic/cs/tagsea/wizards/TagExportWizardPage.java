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

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

/**
 * Page for the tag export wizard.
 * @author Del Myers
 *
 */
public class TagExportWizardPage extends WizardPage {
	private Button sendRadio;
	private Button saveTags;
	private Text locationText;
	private Button browseButton;
	
	private class RadioSelectionListener extends SelectionAdapter {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			if (e.widget == sendRadio) {
				locationText.setEnabled(false);
				browseButton.setEnabled(false);
			} else if (e.widget == saveTags) {
				locationText.setEnabled(true);
				browseButton.setEnabled(true);
				validateState();
			}
		}
	}
	
	private class BrowseSelectionListener extends SelectionAdapter {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			//open a file browse dialog
			FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
			dialog.setFilterExtensions(new String[]{"*.xml"});
			dialog.setFilterNames(new String[]{"XML File"});
			dialog.setText("Save Tags");
			String file = dialog.open();
			if (file != null) {
				locationText.setText(file);
			} else {
				locationText.setText("");
			}
		}
		
	}
	/**
	 * @param pageName
	 */
	protected TagExportWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		setControl(createPage(parent));
		
	}

	/**
	 * @param parent
	 */
	private Control createPage(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridData gridData1 = new GridData();
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.CENTER;
		gridData1.horizontalAlignment = GridData.FILL;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		sendRadio = new Button(page, SWT.RADIO);
		sendRadio.setText("Send your tags to the developers of TagSEA (help us with our research).");
		sendRadio.setLayoutData(gridData);
		saveTags = new Button(page, SWT.RADIO);
		saveTags.setText("Save your tags");
		locationText = new Text(page, SWT.BORDER);
		locationText.setLayoutData(gridData1);
		browseButton = new Button(page, SWT.NONE);
		browseButton.setText("Browse...");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = false;
		page.setLayout(gridLayout);
		page.setSize(new Point(473, 311));
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		page.setLayoutData(data);
		hookListeners();
		sendRadio.setSelection(true);
		saveTags.setSelection(false);
		locationText.setEnabled(false);
		browseButton.setEnabled(false);
		return page;
	}
	
	private void validateState() {
		if (saveTags.getSelection()) {
			String fileName = locationText.getText();
			int folderIndex = fileName.lastIndexOf(File.separator);
			String shortFileName = "";
			boolean error = false;
			if (folderIndex != -1) {
				error = validatePath(fileName.substring(0, folderIndex));
				shortFileName = fileName.substring(folderIndex+1);
			} else {
				error = true;
				shortFileName = fileName;
			}
			if (error) {
				setErrorMessage("Invalid path");
				setPageComplete(false);
				return;
			}
			if (shortFileName == null || shortFileName.equals("")) {
				error = true;
			} else {
				try {
					File file = new File(fileName);
					if (file.isDirectory()) {
						setErrorMessage("The given file name is a directory");
						setPageComplete(false);
						return;
					}
					
				} catch (Exception e) {
					error = true;
					return;
				}
			}
			if (error) {
				setErrorMessage("Invalid file name");
				setPageComplete(false);
				return;
			}
		}
		setErrorMessage(null);
		setPageComplete(true);
	}
	
	/**
	 * @param string
	 * @return
	 */
	private boolean validatePath(String string) {
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			int dev = string.indexOf(':');
			if (dev > 0) {
				String devString = string.substring(0, dev);
				String pathString = string.substring(dev);
				devString = devString.toUpperCase();
				string = devString + pathString;
			}
		}
		File file = new File(string);
		return !file.isDirectory();
	}


	/**
	 * Hooks listeners on controls.
	 */
	private void hookListeners() {
		RadioSelectionListener radioListener = new RadioSelectionListener();
		sendRadio.addSelectionListener(radioListener);
		saveTags.addSelectionListener(radioListener);
		browseButton.addSelectionListener(new BrowseSelectionListener());
		locationText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				validateState();
			}
		});
		
	}

	/**
	 * @return
	 */
	public boolean doSend() {
		return sendRadio.getSelection();
	}

	/**
	 * @return
	 */
	public String getFileName() {
		return locationText.getText();
	}
	

}
