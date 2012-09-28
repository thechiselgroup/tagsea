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
package net.sourceforge.tagsea.core.ui.internal.tags;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.tagsea.core.ITag;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.ActionContext;

/**
 * A context action with easy methods for getting selected tags.
 * @author Del Myers
 */

public class TagContextAction extends ContextAction {

	/**
	 * Returns only the tags in the current selection. Can only be called 
	 * after the context is set.
	 * @return
	 */
	public Set<ITag> getSelectedTags() {
		ActionContext context = getContext();
		ISelection selection = context.getSelection();
		TreeSet<ITag> tags = new TreeSet<ITag>();
		if (selection instanceof IStructuredSelection) {
			Iterator<?> it = ((IStructuredSelection)selection).iterator();
			while (it.hasNext()) {
				Object o = it.next();
				if (o instanceof TagTreeItem) {
					TagTreeItem item = (TagTreeItem) o;
					ITag tag = item.getTag();
					if (tag != null) {
						tags.add(tag);
					}
				}
			}
		}
		return tags;
	}
	
	/**
	 * Returns the selected tags and all of their children. Must be called
	 * only after the context has been set.
	 * @return
	 */
	public Set<ITag> getAllTags() {
		ActionContext context = getContext();
		ISelection selection = context.getSelection();
		TreeSet<ITag> tags = new TreeSet<ITag>();
		if (selection instanceof IStructuredSelection) {
			Iterator<?> it = ((IStructuredSelection)selection).iterator();
			while (it.hasNext()) {
				Object o = it.next();
				if (o instanceof TagTreeItem) {
					TagTreeItem item = (TagTreeItem) o;
					ITag tag = item.getTag();
					if (tag != null) {
						tags.add(tag);
					}
					tags.addAll(item.getChildTags());
				}
			}
		}
		
		
		return tags;
	}
}
