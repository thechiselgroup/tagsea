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
package net.sourceforge.tagsea.core.ui.internal.actions;

import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ui.internal.tags.TagTreeItem;

import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.ActionContext;

/**
 * An action for renaming tags.
 * @author Del Myers
 */

public class TagRenameAction extends TagContextAction {
	private ICellModifier modifier;
	private String[] columnProperties;
	private Tree tree;
	private TextCellEditor editor;
	private TreeEditor treeEditor;
	
	/**
	 * Cell listener to update the model.
	 * @author Del Myers
	 */
	
	private final class CellListener implements ICellEditorListener {
		public void applyEditorValue() {
			modifier.modify(treeEditor.getItem().getData(), columnProperties[treeEditor.getColumn()].toString(), editor.getValue());
			treeEditor.setEditor(null);
		}

		public void cancelEditor() {
			treeEditor.setEditor(null);
		}

		public void editorValueChanged(boolean oldValidState, boolean newValidState) {
		}
	}

	public TagRenameAction(TreeViewer treeViewer, ICellModifier modifier, String[] columnProperties) {
		this.tree = treeViewer.getTree();
		this.modifier = modifier;
		this.columnProperties = columnProperties;
		this.editor = new TextCellEditor(tree, SWT.BORDER);
		this.editor.getControl().addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {

			}
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.ESC) {
					TagRenameAction.this.editor.deactivate();
					TagRenameAction.this.treeEditor.setEditor(null);
				}
			}
		});
		this.treeEditor = new TreeEditor(tree);
		this.editor.addListener(new CellListener());
		tree.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				treeEditor.dispose();
				editor.dispose();
			}
		});
	}
	
	/**
	 * @param event
	 */
	public void openEditor(TreeItem treeItem, int column) {
		if (treeItem != null && !treeItem.isDisposed()) {
			if (modifier.canModify(treeItem.getData(), columnProperties[column].toString())) {
				treeEditor.setEditor(editor.getControl(), treeItem, column);
				treeEditor.grabHorizontal = true;
				treeEditor.grabVertical = true;
				Object value = modifier.getValue(treeItem.getData(), columnProperties[column].toString());
				if (value instanceof String) {
					((Text)editor.getControl()).setText((String)value);
				} else {
					((Text)editor.getControl()).setText("");
				}
				editor.activate();
				((Text)editor.getControl()).selectAll();
				editor.getControl().forceFocus();
			}
		}
	}
	
		
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#runWithEvent(org.eclipse.swt.widgets.Event)
	 */
	@Override
	public void runWithEvent(Event event) {
		switch(event.type) {
		case SWT.KeyUp:
			if (event.detail == 0) {
				if (event.character == ' ') {
					openEditor(tree.getSelection()[0], 0);
				}
			}
			break;
		case SWT.MouseDoubleClick:
			if (event.detail == 0) {
				if ((event.keyCode & (SWT.MOD1 | SWT.MOD2 | SWT.MOD3 | SWT.MOD4))== 0) {
					if (event.button == 1) {
						TreeItem item = getItemForMouseEvent(event);
						int column = getColumnForMouseEvent(item, event);
						if (column >=0)
							openEditor(item, column);
					}
				}
			}
			break;
		default:
			run();
		}
	}
	
	public void run() {
		if (getContext() != null) {
			ISelection selection = getContext().getSelection();
			if (selection instanceof IStructuredSelection) {
				Object o= ((IStructuredSelection)selection).getFirstElement();
				if (o instanceof TagTreeItem) {
					openEditor(tree.getSelection()[0], 0);
				}
			}
		}
	}
	
	
	
	TreeItem getItemForMouseEvent(Event event) {
		return tree.getItem(new Point(event.x, event.y));
	}
	
	int getColumnForMouseEvent(TreeItem treeItem, Event event) {
		int column = 0;
		if (treeItem == null) return -1;
		Point p = new Point(event.x, event.y);
		for (int i = 0; i < tree.getColumnCount(); i++) {
			if (treeItem.getBounds(i).contains(p)) {
				column = i;
				break;
			}
		}
		return column;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.tags.ContextAction#setContext(org.eclipse.ui.actions.ActionContext)
	 */
	@Override
	public void setContext(ActionContext context) {
		super.setContext(context);
		if (context == null) return;
		
		ISelection selection = context.getSelection();
		setEnabled(false);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			if (ss.size() == 1) {
				Object o = ss.getFirstElement();
				if (o instanceof TagTreeItem) {
					TagTreeItem item = (TagTreeItem) o;
					if (!ITag.DEFAULT.equals(item.getName())) {
						setEnabled(true);
					}
				}
			}
		}
	}
}
