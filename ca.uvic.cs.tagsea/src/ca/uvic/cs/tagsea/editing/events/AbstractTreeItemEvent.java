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
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.TreeItem;


/**
 * Base class for TreeItem events.
 * 
 * @author Chris Callendar
 */
public abstract class AbstractTreeItemEvent {

	/** 
	 * The tree item for the event.  This can't be null.
	 */
	public TreeItem item = null;
	
	/** 
	 * This is the same object that is found in item.getData(). 
	 */
	public Object data = null;
	
	/**
	 * If the event should happen.  Setting this to false
	 * might cause the event to not happen.
	 */
	public boolean doit = true;

	/**
	 * Sets the item and data objects.
	 * @param item the item being acted on - can't be null
	 * @throws SWTException if item is null
	 */
	public AbstractTreeItemEvent(TreeItem item) {
		if (item == null) 
			SWT.error(SWT.ERROR_NULL_ARGUMENT);

		this.item = item;
		this.data = item.getData();
	}
	
	public String toString() {
		String str = item.toString();
		if (data != null) {
			str += " [" + data.toString() + "]";
		}
		if (!doit) {
			str += " [doit=false]";
		}
		return str;
	}

}
