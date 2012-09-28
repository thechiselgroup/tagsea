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

import org.eclipse.swt.widgets.TreeItem;


/**
 * Rename event object.  Holds the TreeItem being renamed.
 * The newName might be null.
 * 
 * @author Chris Callendar
 */
public class TreeItemRenameEvent extends AbstractTreeItemEvent {

	/**
	 * The original name of the item.
	 */
	public String oldName;
	
	/**
	 * The new name of the item.  
	 * This can be null for before rename events.
	 */
	public String newName;
	
	public TreeItemRenameEvent(TreeItem item) {
		this(item, null);
	}
	
	public TreeItemRenameEvent(TreeItem item, String newName) {
		super(item);
		this.oldName = item.getText();
		this.newName = newName;
	}
	
	public String toString() {
		String str = "Renaming " + super.toString();
		if (newName != null) {
			str += " to '" + newName + "'";
		}
		return str;
	}
	
}
