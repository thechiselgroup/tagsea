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
package net.sourceforge.tagsea.core.ui.tags;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.core.ui.internal.views.FilteredTable;
import net.sourceforge.tagsea.core.ui.internal.views.TagFilter;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

/**
 * A convenience dialog that allows tag selection.
 * @author Del Myers
 */

public class TagSelectionDialog extends Dialog {

	private Set<ITag> tags;
	private TableViewer tableViewer;
	private ITag[] initialChecks;
	
	public TagSelectionDialog(Shell shell) {
		super(shell);
		initialChecks = new ITag[0];
	}
	
	public TagSelectionDialog(IShellProvider shellProvider) {
		super(shellProvider);
		initialChecks = new ITag[0];
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout());
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 400;
		page.setLayoutData(data);
		FilteredTable filteredTable = new FilteredTable(page, SWT.FLAT | SWT.CHECK | SWT.BORDER | SWT.V_SCROLL, new TagFilter());
		tableViewer = filteredTable.getViewer();
		tableViewer.setContentProvider(new ArrayContentProvider());
		List<ITag> tags = new LinkedList<ITag>(Arrays.asList(TagSEAPlugin.getTagsModel().getAllTags()));
		tags.remove(TagSEAPlugin.getTagsModel().getTag(ITag.DEFAULT));
		if (tags.size() > 30) {
			data.heightHint = 300;
		} else {
			data.heightHint = tags.size()*(filteredTable.getFont().getFontData()[0].getHeight()+3)+100;
		}
		tableViewer.setInput(tags);
		setCheckedElements(initialChecks);
		//page.pack();
		return page;
	}
	
	 public void setCheckedElements(Object[] elements) {
	        for (Object element :elements) {
	        	assert(element != null);
	        }
	        HashSet<Object> set = new HashSet<Object>();
	        for (int i = 0; i < elements.length; ++i) {
	            set.add(elements[i]);
	        }
	        TableItem[] items = tableViewer.getTable().getItems();
	        for (int i = 0; i < items.length; ++i) {
	            TableItem item = items[i];
	            Object element = item.getData();
	            if (element != null) {
	                boolean check = set.contains(element);
	                // only set if different, to avoid flicker
	                if (item.getChecked() != check) {
	                    item.setChecked(check);
	                }
	            }
	        }
	    }
	
	public TableViewer getViewer() {
		return tableViewer;
	}
	
	public void setInitialChecks(ITag[] tags) {
		this.initialChecks = tags;
	}
	
	public ITag[] getSelectedTags() {
		if (tags == null) {
			return new ITag[0];
		}
		return tags.toArray(new ITag[tags.size()]);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		tags = new TreeSet<ITag>();
		TableItem[] items = tableViewer.getTable().getItems();
		for (TableItem item : items) {
			if (item.getChecked()) {
				tags.add((ITag)item.getData());
			}
		}
		super.okPressed();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Tags");
		newShell.setImage(TagSEAPlugin.getDefault().getImageRegistry().get(ITagSEAImageConstants.IMG_TAG));
	}
}
