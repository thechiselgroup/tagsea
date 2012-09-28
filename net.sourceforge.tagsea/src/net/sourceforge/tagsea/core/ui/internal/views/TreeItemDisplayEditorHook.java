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
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * A StyledTextDisplayEditorHook for tree items.
 * @author Del Myers
 */

public class TreeItemDisplayEditorHook extends StyledTextDisplayEditorHook {

	/**
	 * @param item
	 * @param column
	 */
	public TreeItemDisplayEditorHook(Tree parent, TreeItem item, int column) {
		super(parent, item, column);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.StyledTextDisplayEditorHook#createEditor(org.eclipse.swt.widgets.Item, int)
	 */
	@Override
	ControlEditor createEditor(Control parent, Item item, int column) {
		Tree tree = (Tree)parent;
		TreeEditor editor = new TreeEditor(tree);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		editor.setEditor(getStyledText(), (TreeItem)item, column);
		return editor;
	}
	
	private TreeItem getTreeItem() {
		return (TreeItem)getItem();
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.StyledTextDisplayEditorHook#getItemBackground()
	 */
	@Override
	Color getItemBackground() {
		return getTreeItem().getBackground(getColumn());
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.StyledTextDisplayEditorHook#isThisItem(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	boolean isThisItemSelected() {
		for (TreeItem item : getTree().getSelection()) {
			if (item == getItem()) return true;;
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.StyledTextDisplayEditorHook#getItemFont()
	 */
	@Override
	Font getItemFont() {
		return getTreeItem().getFont(getColumn());
	}
	
	private Tree getTree() {
		return (Tree)getItemControl();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.StyledTextDisplayEditorHook#getItemText()
	 */
	@Override
	String getItemText() {
		return getTreeItem().getText(getColumn());
	}

}
