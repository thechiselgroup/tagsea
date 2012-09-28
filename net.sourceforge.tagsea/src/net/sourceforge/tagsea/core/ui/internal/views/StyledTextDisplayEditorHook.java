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


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;

/**
 * A hook between a a StyledText "Editor" and an item in a table or tree.
 * The purpose of it is to allow custom highlighting and styling on 
 * the table item or tree item. 
 * @author Del Myers
 */

public abstract class StyledTextDisplayEditorHook {
	
	private Item item;
	private ControlEditor editor;
	private StyledText styledText;
	private int column;
	private Control parent;
	private ItemControlListener itemControlListener;

	 
	class ItemControlListener implements Listener {
		/* (non-Javadoc)
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		int stateMask = 0;
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.MouseDown:
				//styledText.setVisible(false);
			case SWT.Selection:
			case SWT.FocusIn:
				if (isThisItemSelected()) {
					styledText.setVisible(false);
				} else {
					styledText.setSelection(0, 0);
					styledText.setVisible(true);
					editor.layout();
				}
			}
			
		}
	}
	
	private class StyledTextListener implements Listener, DisposeListener {
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
		 */
		public void widgetDisposed(DisposeEvent e) {
			parent.removeListener(SWT.Selection, itemControlListener);
			parent.removeListener(SWT.FocusIn, itemControlListener);
			parent.removeListener(SWT.MouseDown, itemControlListener);
		}
		/* (non-Javadoc)
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		public void handleEvent(Event event) {
		}
	}
	
	public StyledTextDisplayEditorHook(Composite parent, Item item, int column) {
		this.item = item;
		this.column = column;
		this.parent = parent;
		this.styledText = new StyledText(parent, SWT.NONE);
		this.editor = createEditor(parent, item, column);
		
		styledText.setBackground(parent.getBackground());
		styledText.setForeground(parent.getForeground());
		
		styledText.setText(getItemText());
		styledText.setEditable(false);
		styledText.setEnabled(false);
		this.itemControlListener = new ItemControlListener();
		parent.addListener(SWT.Selection, itemControlListener);
		parent.addListener(SWT.FocusIn, itemControlListener);
		parent.addListener(SWT.MouseDown, itemControlListener);
		parent.addListener(SWT.KeyDown, itemControlListener);
		parent.addListener(SWT.KeyUp, itemControlListener);
		StyledTextListener stListener = new StyledTextListener();
		styledText.addDisposeListener(stListener);
		styledText.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
//		styledText.addListener(SWT.MouseDown, stListener);
//		styledText.addListener(SWT.KeyDown, stListener);
		//styledText.addListener(SWT.FocusIn, stListener);
	}
	
	public int getColumn() {
		return column;
	}
	
	public void setRanges(StyleRange[] ranges) {
		styledText.setStyleRanges(ranges);
	}
	
	protected StyledText getStyledText() {
		return styledText;
	}
 	
	/**
	 * Creates the editor for the item. The editor is expected to be
	 * one of TableEditor or TreeEditor, and to have a StyledText as
	 * its editor control.
	 * @param item
	 * @return
	 */
	abstract ControlEditor createEditor(Control parent, Item item, int column);
	
	
	/**
	 * Returns the item.
	 * @return the item.
	 */
	public Item getItem() {
		return item;
	}
	
	/**
	 * Returns true if the focus event is for the item underneat this
	 * cell.
	 * @param e
	 * @return
	 */
	abstract boolean isThisItemSelected();
	
	public Control getItemControl() {
		return parent;
	}
	
	/**
	 * Returns the background color of the item.
	 * @return
	 */
	abstract Color getItemBackground();
	
	abstract Font getItemFont();

	abstract String getItemText();

	public ControlEditor getEditor() {
		return editor;
	}

	public void dispose() {
		editor.dispose();
		styledText.dispose();
	}
}
