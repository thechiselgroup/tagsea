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
package net.sourceforge.tagsea.core.ui.internal.views;

import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * An editor hook for filtered tables to highlight filter matches.
 * @author Del Myers
 */

public class TableItemDisplayEditorHook extends StyledTextDisplayEditorHook {

	/**
	 * 
	 * @param parent
	 * @param item
	 * @param column
	 */
	public TableItemDisplayEditorHook(Table parent, TableItem item, int column) {
		super(parent, item, column);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.StyledTextDisplayEditorHook#createEditor(org.eclipse.swt.widgets.Control, org.eclipse.swt.widgets.Item, int)
	 */
	@Override
	ControlEditor createEditor(Control parent, Item item, int column) {
		Table table = (Table)parent;
		TableItem tableItem = (TableItem)item;
		TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		editor.setEditor(getStyledText(), tableItem, column);
		return editor;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.StyledTextDisplayEditorHook#getItemBackground()
	 */
	@Override
	Color getItemBackground() {
		return getTableItem().getBackground();
	}

	private TableItem getTableItem() {
		return (TableItem)getItem();
	}
	
	private Table getTable() {
		return (Table)getItemControl();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.StyledTextDisplayEditorHook#getItemFont()
	 */
	@Override
	Font getItemFont() {
		return getTableItem().getFont();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.StyledTextDisplayEditorHook#isThisItemSelected()
	 */
	@Override
	boolean isThisItemSelected() {
		TableItem[] selection = getTable().getSelection();
		for (TableItem item : selection) {
			if (item == getTableItem()) return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.StyledTextDisplayEditorHook#getItemText()
	 */
	@Override
	String getItemText() {
		return getTableItem().getText(getColumn());
	}

}
