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

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;



/**
 * Specific event used when deleting TreeItem objects.
 * 
 * @author Chris Callendar
 */
public class TreeItemDeleteEvent extends AbstractTreeItemEvent {
	
	/**
	 * The tree that the item belongs to.
	 */
	public Tree tree;
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
	
	
	
	
	public TreeItemDeleteEvent(TreeItem item) {
		super(item);
		this.tree = item.getParent();
		this.parent = item.getParentItem();
		this.index = (parent != null) ? parent.indexOf(item) : tree.indexOf(item);
	}
	
	public String toString() {
		String str = "Deleting " + super.toString();
		return str;
	}
	
}
