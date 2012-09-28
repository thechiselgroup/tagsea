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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * Dialog that displays tabs with log data.
 * @author Del Myers
 *
 */
public class LogTabDialog extends Dialog {

	private File[] files;
	private LinkedList<FileDocument> documents;
	private class FileDocument extends Document {
		private File file;
		private boolean loading;
		FileDocument(File file) {
				loading = false;
				this.file = file;
				
		}
		
		protected void load() {
			try {
				set("loading data...");
				final FileReader reader = new FileReader(file);
				String result = "";
				set("");
				loading = true;
				Display.getCurrent().asyncExec(new Runnable(){
					public void run() {
						try {
							char[] buffer = new char[2048];
							int read = reader.read(buffer);
							if (read >= 0 && loading) {
								String append = new String(buffer, 0, read);
								replace(getLength(), 0, append);
								Display.getCurrent().asyncExec(this);
							} else {
								reader.close();
							}
						} catch (OutOfMemoryError e) {
							set("Error: file too large");
						} catch (BadLocationException e) {
							e.printStackTrace();
						} catch (IOException e) {
							set("");
						}
					}
				});
				set(result);
			} catch (FileNotFoundException e) {
				set("Error: Could not find file");
			} catch (OutOfMemoryError e) {
				set("Error: file too large");
			}
		}
		protected void unload() {
			loading = false;
			set("");
		}
	}
	
	private class TabItemFocusListener implements Listener {

		public void handleEvent(Event event) {
			IDocument document = (IDocument) event.widget.getData("document");
			if (document instanceof FileDocument) {
				final FileDocument fd = (FileDocument) document;
				switch (event.type) {
				case SWT.Show:
					fd.load();
					break;
				
				case SWT.Hide:
					fd.unload();
					break;
				}
			}

		}
		
	}

	/**
	 * @param parentShell
	 */
	protected LogTabDialog(Shell parentShell, File[] files) {
		super(parentShell);
		this.files = files;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite control =  (Composite) super.createDialogArea(parent);
		TabItemFocusListener focusListener = new TabItemFocusListener();
		TabFolder folder = new TabFolder(control, SWT.BORDER);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 500;
		data.widthHint = 500;
		folder.setLayoutData(data);
		documents = new LinkedList<FileDocument>();
		for(File file : files) {
			TextViewer viewer = new TextViewer(folder, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
			viewer.setEditable(false);
			FileDocument fd = new FileDocument(file);
			documents.add(fd);
			viewer.setDocument(fd);
			TabItem item = new TabItem(folder, SWT.NONE);
			item.setText(file.getName());
			viewer.getControl().setData("document", fd);
			item.setControl(viewer.getControl());
			viewer.getControl().addListener(SWT.Show, focusListener);
		}
		if (folder.getItemCount() > 0) {
			((FileDocument)folder.getItem(0).getControl().getData("document")).load();
		}
		control.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				while (documents.size() > 0) {
					FileDocument d = documents.remove();
					d.unload();
				}
			}
		});
		return control;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}
}
