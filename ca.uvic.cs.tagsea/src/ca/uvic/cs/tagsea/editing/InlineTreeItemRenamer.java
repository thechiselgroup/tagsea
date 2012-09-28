/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.editing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.bindings.keys.KeyLookupFactory;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ca.uvic.cs.tagsea.editing.events.TreeItemListener;
import ca.uvic.cs.tagsea.editing.events.TreeItemRenameEvent;


/**
 * Adds a TreeEditor to a Tree which allows for inline editing of TreeItem objects.
 * 
 * @author Chris Callendar
 */
public class InlineTreeItemRenamer {

	private Tree tree;
	private TreeEditor inlineEditor;
	private TreeItem editItem = null;
	private IInputValidator validator = null;
	private List<TreeItemListener> listeners;
	
	public InlineTreeItemRenamer(Tree tree) {
		this(tree, null);
	}

	/**
	 * @param validator the validator which is used to confirm if a rename is accepted
	 */
	public InlineTreeItemRenamer(Tree tree, IInputValidator validator) {
		this.tree = tree;
		this.inlineEditor = new TreeEditor(tree);
		this.validator = validator;
		this.listeners = new ArrayList<TreeItemListener>(1);
		
		addTreeEditor();
		addKeyListener();
	}
	
	public void setValidator(IInputValidator validator) {
		this.validator = validator;
	}
	
	public IInputValidator getValidator() {
		return validator;
	}
	
	/** Adds a listener to the list. */
	public void addListener(TreeItemListener listener) {
		listeners.add(listener);
	}
	
	/** Removes a listener from the list. */
	public void removeListener(TreeItemListener listener) {
		listeners.remove(listener);
	}

	public void stopEditting() {
		if ((inlineEditor.getEditor() != null) && !inlineEditor.getEditor().isDisposed()) {
			inlineEditor.getEditor().setVisible(false);
			inlineEditor.getEditor().dispose();
			inlineEditor.setEditor(null, null);
		}
		editItem = null;
	}
	
	/**
	 * Allows in place renaming of a TreeItem.
	 */
	private void addTreeEditor() {
		inlineEditor = new TreeEditor(tree);
		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				final TreeItem item = (TreeItem)event.item;
				if ((item != null) && (item == editItem)) {
					// notify listeners that an inline rename is about to happen
					TreeItemRenameEvent renameEvent = new TreeItemRenameEvent(item);
					fireBeforeRenameEvent(renameEvent);
					if (!renameEvent.doit)
						return;
					
					final Composite composite = new Composite(tree, SWT.NONE);
					//System.out.println("Created composite");
					
					boolean isCarbon = SWT.getPlatform().equals("carbon");
					if (!isCarbon) {
						composite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
					}
					
					final Text text = new Text(composite, SWT.NONE);
					final int inset = (isCarbon ? 0 : 1);
					composite.addListener(SWT.Resize, new Listener() {
						public void handleEvent (Event e) {
							Rectangle rect = composite.getClientArea();
							text.setBounds (rect.x + inset, rect.y + inset, rect.width - inset * 2, rect.height - inset * 2);
						}
					});
					
					Listener textListener = new Listener() {
						public void handleEvent(final Event e) {
							switch (e.type) {
								case SWT.FocusOut:
									//System.out.println("Focus out");
									validateRename(item, text);
									text.dispose();
									composite.dispose();
									editItem = null;
									break;
								case SWT.Verify:
									String newText = text.getText();
									String leftText = newText.substring(0, e.start);
									String rightText = newText.substring(e.end, newText.length());
									GC gc = new GC(text);
									Point size = gc.textExtent(leftText + e.text + rightText);
									gc.dispose();
									size = text.computeSize(size.x, SWT.DEFAULT);
									inlineEditor.horizontalAlignment = SWT.LEFT;
									Rectangle itemRect = item.getBounds(), rect = tree.getClientArea();
									inlineEditor.minimumWidth = Math.max(size.x, itemRect.width) + inset * 2;
									int left = itemRect.x, right = rect.x + rect.width;
									inlineEditor.minimumWidth = Math.min(inlineEditor.minimumWidth, right - left);
									inlineEditor.minimumHeight = size.y + inset * 2;
									inlineEditor.layout();
									break;
								case SWT.Traverse:
									switch (e.detail) {
										case SWT.TRAVERSE_RETURN:
											validateRename(item, text);
											//FALL THROUGH
										case SWT.TRAVERSE_ESCAPE:
											text.dispose();
											composite.dispose();
											editItem = null;
											e.doit = false;
									}
									break;
							}
						}
					};
					text.addListener(SWT.FocusOut, textListener);
					text.addListener(SWT.Traverse, textListener);
					text.addListener(SWT.Verify, textListener);
					inlineEditor.setEditor(composite, item);
					text.setText(item.getText());
					text.selectAll();
					text.setFocus();
				}
				editItem = item;
			}
		});
	}
	
	/**
	 * Adds a key listener to watch for rename (F2) events.
	 */
	private void addKeyListener() {
		final IKeyLookup lookup = KeyLookupFactory.getSWTKeyLookup();
		final int F2 = lookup.formalKeyLookup(IKeyLookup.F2_NAME);
		
		tree.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				int code = e.keyCode;
				if (code == F2) {
					TreeItem[] selection = tree.getSelection();
					if (selection.length == 1) {
						renameTreeItem(selection[0]);
					}
				}
			}
		});
	}	
	
	/**
	 * Selects the given item and shows the inline tree editor.
	 * 
	 * @param item the item to rename
	 */
	public void renameTreeItem(TreeItem item) {
//		String oldName = item.getText();
//		// Pops up a dialog box asking for a new name
//		InputDialog input = new InputDialog(tree.getShell(), "Rename...", "New name:", oldName, validator);
//		input.setBlockOnOpen(true);
//		if (input.open() == InputDialog.OK) {
//			String newName = input.getValue();
//			item.setText(newName);
//			// notify listeners
//			fireRenameEvent(item, oldName, newName);
//		}
		if (item == null)
			return;

		editItem = item;
		// send the selection event which causes the inline rename
		Event e = new Event();
		e.item = item;
		tree.notifyListeners(SWT.Selection, e);
	}	

	
	/**
	 * Compares the text in the Text control with the original text
	 * in the TreeItem.  If they are different, and the new name is valid
	 * then the TreeItem will be updated and a Rename event will be fired.
	 * If they are the same false is returned.  If the new name is not valid
	 * an error dialog will popup.
	 * @param item	The TreeItem (containing the original name)
	 * @param text	The Text control (containing the new name)
	 * @return boolean if the rename was successful
	 */
	protected boolean validateRename(TreeItem item, Text text) {
		boolean ok = true;
		String oldName = item.getText().trim();
		String newName = text.getText().trim();

		// nothing changed
		if (newName.equals(oldName))
			return false;
		
		if (validator != null) {
			String errorMsg = validator.isValid(newName);
			if (errorMsg != null) {
				// invalid name - revert back to old name
				text.setText(oldName);
				text.selectAll();
				ok = false;
				// note - this starts a new thread
				MessageDialog.openError(tree.getShell(), "Invalid Name", errorMsg);
			} else {
				ok = true;
			}
		}
		if (ok) {
			TreeItemRenameEvent event = new TreeItemRenameEvent(item, newName);
			item.setText(newName);
			fireAfterRenameEvent(event);
		}
		return ok;
	}
	

	protected void fireBeforeRenameEvent(TreeItemRenameEvent event) {
		for (Iterator<TreeItemListener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().beforeRename(event);
			if (!event.doit)
				break;
		}
	}

	protected void fireAfterRenameEvent(TreeItemRenameEvent event) {
		for (Iterator<TreeItemListener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().afterRename(event);
		}
	}
	
}
