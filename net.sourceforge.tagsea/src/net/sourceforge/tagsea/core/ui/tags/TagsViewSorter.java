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
package net.sourceforge.tagsea.core.ui.tags;

import net.sourceforge.tagsea.core.ui.internal.tags.TagTreeItem;

import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Sorter for the TagsView. Sorts by name on the elements in the tags view.
 * The default element is always first.
 * @author Del Myers
 */

public class TagsViewSorter extends ViewerSorter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
	 */
	@Override
	public int category(Object element) {
		if (element instanceof TagTreeItem) {
			TagTreeItem item = (TagTreeItem) element;
			String name = item.getName();
			if ("default".equals(name)) return 0;
			return 1;
		}
		return 2;
	}
}
