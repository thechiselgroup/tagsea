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
package ca.uvic.cs.tagsea.editing.events;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;


/**
 * The event that gets called when copying a TreeItem.
 * When copying an Item, you can't copy the keyed data from
 * the Item unless you know the key.  For this reason the copy event is useful
 * to let the listeners copy of any keyed data.  
 * 
 * @author Chris Callendar
 */
public class TreeItemCopyEvent extends AbstractTreeItemEvent {

	/**
	 * The original Item.  Copy the keyed data to the item variable.
	 */
	public Item oldItem;
	
	/**
	 * The position in item's parent's list of children.
	 */
	public int index;
	
	public TreeItemCopyEvent(Item item, TreeItem newItem) {
		super(newItem);
		if ((newItem == null) || (item == null)) 
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		this.oldItem = item;
		
		if (newItem.getParentItem() == null) {
			this.index = newItem.getParent().indexOf(newItem);
		} else {
			this.index = newItem.getParentItem().indexOf(newItem);
		}
	}
	
	public String toString() {
		String str = "Copying " + oldItem.toString() + " to " + super.toString();
		str += ", index = " + index;
		return str;
	}

}
