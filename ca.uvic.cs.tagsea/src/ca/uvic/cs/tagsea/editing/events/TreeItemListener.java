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

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.widgets.Item;


/**
 * Listener for drag, rename, and deletion events.
 * 
 * @author Chris Callendar
 */
public interface TreeItemListener {

	/**
	 * Notifies the listener that a TreeItem is about to move.
	 * You can set the <b>doit</b> variable to false to stop the move from happening.
	 * @param event	the move event object: <br>
	 * 		The item variable will contain the TreeItem that is about to be moved. <br>
	 * 		The tree and parent variables are set to the parent Tree and the parent TreeItem.
	 */
	public void beforeMove(TreeItemMoveEvent event);

	/**
	 * This event gets called when a new TreeItem has been created
	 * and before the old TreeItem gets disposed.  This is where you 
	 * should copy over any keyed data from the old TreeItem (event.item) to 
	 * the new item (event.newItem). Keyed widget data can't be copied
	 * unless you know the key.
	 * @param event the copy event object:<br>
	 * 		The item variable is set to the original TreeItem (hasn't been disposed)<br>
	 * 		The newItem variable is set to the new TreeItem that has been created<br>
	 */
	public void copyData(TreeItemCopyEvent event);
	
	/**
	 * This gets fired after a copy has happened.
	 * Both the oldItem and the new item will exist.
	 * @param event
	 */
	public void afterCopy(TreeItemCopyEvent event);
		
	/**
	 * Notifies the listener that a TreeItem has moved.
	 * @param event	the move event object: <br>
	 * 		The item variable will contain the newly created TreeItem. <br>
	 * 		The tree and parent variables are set to the parent Tree and the parent TreeItem.
	 * 		The index variable is set to the position in the parent's list of children.
	 */
	public void afterMove(TreeItemMoveEvent event);
	
	/**
	 * This gets called after all move events have occurred.
	 * If multiple items are being moved, this only gets called once after all move 
	 * events are done.
	 */
	public void finishedMove(Item[] moveItems);
	
	/**
	 * Notifies that a tree item's text is about to be renamed.
	 * You can set the <b>doit</b> variable to false to stop the rename from happening.
	 * @param event	the move event object: <br>
	 * 		The item variable is set to the TreeItem being renamed. <br>
	 * 		The oldName variable is set to the current text. <br>
	 * 		The newName variable will be null until after the rename.
	 */
	public void beforeRename(TreeItemRenameEvent event);

	/**
	 * Notifies that a tree item's text has changed.
	 * @param event	the move event object: <br>
	 * 		The item variable is set to the TreeItem being renamed. <br>
	 * 		The newName and oldName variables are set accordingly.
	 */
	public void afterRename(TreeItemRenameEvent event);
	
	/**
	 * Notifies that a TreeItem is about to be deleted.
	 * You can set the <b>doit</b> variable to false to stop the deletion from happening.
	 * @param event	the move event object: <br>
	 * 		The item variable is set to the TreeItem that is about to be deleted.
	 */
	public void beforeDelete(TreeItemDeleteEvent event);

	/**
	 * Notifies that a TreeItem has been deleted (has been disposed).
	 * @param event	the move event object: <br>
	 * 		The item variable is set to the TreeItem that has been deleted AND disposed.
	 */
	public void afterDelete(TreeItemDeleteEvent event);
	
	/**
	 * This gets called after all items have been deleted.
	 */
	public void finishedDelete();
	
	/**
	 * This gets called when a TreeItem is dragged.
	 * The target variable will be null. Setting doit to false will 
	 * cause the icon to change to the {@link DND#DROP_NONE} cursor.
	 * @param event
	 */
	public void dragStart(ItemDragEvent event);
	
	/**
	 * This is called when the dragged item is over 
	 * another TreeItem.  Setting doit to false will 
	 * cause the icon to change to the {@link DND#DROP_NONE} cursor.
	 * @param event
	 */
	public void dragOver(ItemDragEvent event);
	
	/**
	 * This is called when an item is dropped.
	 * Setting doit to false will cause the drop to be aborted.
	 * @param event
	 */
	public void drop(ItemDragEvent event);

}


