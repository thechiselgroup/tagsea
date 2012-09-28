/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core.ui.waypoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.TagSEAUtils;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.core.ui.tags.TagProposalProvider;
import net.sourceforge.tagsea.core.ui.tags.TagSelectionDialog;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.fieldassist.TextControlCreator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistField;

/**
 * A simple dialog for editing tags on a waypoint.
 * @author Del Myers
 */
@Deprecated
public class TagEditDialog extends StatusDialog {

	private Text tagText;
	private TreeSet<String> tagNames;
	private Button addButton;
	private ListViewer tagNameListViewer;

	public TagEditDialog(Shell parentShell) {
		super(parentShell);
		this.tagNames = new TreeSet<String>();
	}
	
	public void setInitialNames(String[] tagNames) {
		this.tagNames.clear();
		this.tagNames.addAll(Arrays.asList(tagNames));
		this.tagNames.remove(ITag.DEFAULT);
	}
	
	public void setInitialTags(ITag[] tags) {
		this.tagNames.clear();
		for (ITag tag : tags) {
			if (!ITag.DEFAULT.equals(tag.getName()))
				this.tagNames.add(tag.getName());
		}
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout());
		Composite page = new Composite(parent, SWT.FLAT);
		page.setLayout(new GridLayout(2, false));
		page.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite listComposite = new Composite(page, SWT.FLAT);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		listComposite.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint=200;
		data.widthHint=200;
		listComposite.setLayoutData(data);
		List tagNameList = new List(listComposite, SWT.FLAT | SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		tagNameListViewer = new ListViewer(tagNameList);
		tagNameListViewer.setContentProvider(new ArrayContentProvider());
		tagNameList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite textComposite = new Composite(listComposite, SWT.FLAT);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		textComposite.setLayout(layout);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		textComposite.setLayoutData(data);
		tagText = createTagText(textComposite, SWT.BORDER);
		tagText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				String text = tagText.getText();
				text = text.trim();
				if ("".equals(text)) {
					updateStatus(Status.OK_STATUS);
					addButton.setEnabled(false);
					return;
				}
				int valid = TagSEAUtils.isValidTagName(text);
				switch (valid) {
				case TagSEAUtils.TAG_NAME_BAD_CHARACTER:
					updateStatus(
						new Status(
							IStatus.ERROR, 
							TagSEAPlugin.PLUGIN_ID, 
							IStatus.ERROR, 
							"Tag name contains an invalid character.", 
							null
						)
					);
					break;
				case TagSEAUtils.TAG_NAME_SYNTAX_ERROR:
					updateStatus(
						new Status(
							IStatus.ERROR, 
							TagSEAPlugin.PLUGIN_ID, 
							IStatus.ERROR, 
							"Tag name has bad syntax.", 
							null
						)
					);
					break;
				case TagSEAUtils.TAG_NAME_VALID:
					updateStatus(Status.OK_STATUS);
					break;
				}
				addButton.setEnabled(getStatus().isOK());
			}
		});
		tagText.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				getShell().setDefaultButton(addButton);
			}

			public void focusLost(FocusEvent e) {
				getShell().setDefaultButton(getButton(Dialog.OK));
			}
		});
		tagText.getParent().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addButton = new Button(textComposite, SWT.PUSH);
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		addButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				updateTagsForText();
				tagText.setText("");
			}});
		addButton.setText("Add");
		tagText.setText("");
		Composite buttonComposite = new Composite(page, SWT.NONE);
		buttonComposite.setLayout(new GridLayout());
		buttonComposite.setLayoutData(new GridData(SWT.FILL, GridData.BEGINNING, true, true));
		Button selectButton = new Button(buttonComposite, SWT.PUSH);
		selectButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
		selectButton.setText("Select...");
		selectButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				TagSelectionDialog selectionDialog = new TagSelectionDialog(TagEditDialog.this.getShell());
				ArrayList<ITag> tags = new ArrayList<ITag>();
				for (String name : tagNames) {
					ITag tag = TagSEAPlugin.getTagsModel().getTag(name);
					if (tag != null) {
						tags.add(tag);
					}
				}
				selectionDialog.setInitialChecks(tags.toArray(new ITag[tags.size()]));
				int result = selectionDialog.open();
				if (result == OK) {
					setInitialTags(selectionDialog.getSelectedTags());
					tagNameListViewer.refresh();
				}
				
			}
		});
		Button deleteButton = new Button(buttonComposite, SWT.PUSH);
		deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
		deleteButton.setText("Remove...");
		deleteButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tagNameListViewer.getSelection();
				for (Object o : selection.toList()) {
					tagNames.remove(o);
				}
				tagNameListViewer.refresh();
			}
		});
		tagNameListViewer.setInput(tagNames);
		return page;
	}

	/**
	 * Creates a text area that can have content assist on it.
	 * @param textComposite
	 * @param border
	 * @return
	 */
	private Text createTagText(Composite parent, int style) {
		ContentAssistField field = new ContentAssistField(
				parent, 
				style,
				new TextControlCreator(),
				new TextContentAdapter(),
				new TagProposalProvider(parent),
				null, null
			);
		
		return (Text)field.getControl();
	}
	
	public String[] getTagNames() {
		return tagNames.toArray(new String[tagNames.size()]);
	}
	
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}
	
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Edit Tags");
		shell.setImage(TagSEAPlugin.getDefault().getImageRegistry().get(ITagSEAImageConstants.IMG_TAG));
	}

	void updateTagsForText() {
		String text = tagText.getText();
		if (getStatus().isOK()) {
			text = text.trim();
			if (!"".equals(text)) {
				if (!tagNames.contains(text)) {
					tagNames.add(text);
					tagNameListViewer.refresh();
				}
			}
		}
	}
}
