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
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;


/**
 * Drag event.
 * 
 * @author Chris Callendar
 */
public class ItemDragEvent {

	/**
	 * This is the source items (the ones being dragged).
	 */
	public Item[] source;
	
	/**
	 * The target drag item (to drop on).
	 */
	public TreeItem target;
	
	/**
	 * The index of where the item was dropped.
	 * Only applies to drop events.
	 */
	public int index = 0;
	
	/**
	 * The drag type - either move or copy.
	 */
	public int dragType = DND.DROP_MOVE;
	
	/**
	 * If the drag event should happen.
	 */
	public boolean doit = true;

	/**
	 * Creates a drag event with a null drop target.
	 * @param source the dragged items
	 */
	public ItemDragEvent(Item source) {
		this(new Item[] { source }, null);
	}
	
	/**
	 * Creates a drag event with a null drop target.
	 * @param source the dragged items (can't be null or empty)
	 */
	public ItemDragEvent(Item[] source) {
		this(source, null);
	}

	/**
	 * Creates a drag event with the given source and target items.
	 * @param source the dragged item (can't be null)
	 * @param target the drop target (can be null)
	 */
	public ItemDragEvent(Item source, TreeItem target) {
		this(new Item[] { source }, target);
	}
	
	/**
	 * Creates a drag event with the given source and target items.
	 * @param source the dragged items (can't be null or empty)
	 * @param target the drop target (can be null)
	 */
	public ItemDragEvent(Item[] source, TreeItem target) {
		if (source == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		if (source.length == 0)
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		
		this.source = source;
		this.target = target;
		
		if (target != null) {
			if (target.getParentItem() != null) {
				this.index = target.getParentItem().indexOf(target);
			} else {
				this.index = target.getParent().indexOf(target);
			}
		}
	}
	
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("Dragging ");
		for (Item item : source) {
			str.append(item.toString() + ",");
		}
		str.deleteCharAt(str.length()-1);

		if (target != null) {
			str.append(" to " + target.toString());
			if (target.getData() != null) {
				str.append(" [" + target.getData().toString() + "]");
			}
		}
		if (!doit) {
			str.append(" [doit=false]");
		}
		return str.toString();
	}

}
