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

import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Specific event for moving TreeItem objects.
 * 
 * @author Chris Callendar
 * @see TreeItemListener
 * @see AbstractTreeItemEvent
 */
public class TreeItemMoveEvent extends AbstractTreeItemEvent {
	
	/** The tree object. */
	public Tree tree = null;
	
	/** 
	 * The parent TreeItem of the item that is being moved.  
	 * If it is null then the item object is a root node.
	 */
	public TreeItem parent = null;
	
	/** 
	 * The position of TreeItem in the parent's list of children. 
	 * Defaults to -1.
	 */
	public int index = -1;
	
	/**
	 * Creates a new tree item event and initializes the type, 
	 * item, data, parent, and tree variables.
	 * @param item the tree item that is being moved (can't be null)
	 * @throws SWTException if item is null
	 */
	public TreeItemMoveEvent(TreeItem item) {
		super(item);

		this.parent = item.getParentItem();
		this.tree = item.getParent();
		if (parent != null) {
			index = parent.indexOf(item);
		} else if (tree != null) {
			index = tree.indexOf(item);
		} else {
			index = -1;
		}
	}
	
	public String toString() {
		String str = "Moving: " + super.toString();
		if (index != -1) {
			str += " to position " + index;
		}
		return str;
		
	}
}
