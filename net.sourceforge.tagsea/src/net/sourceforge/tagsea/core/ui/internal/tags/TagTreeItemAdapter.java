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

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.core.ui.internal.tags.TagsTree.RootTagTreeItem;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * 
 * @author Del Myers
 */

public class TagTreeItemAdapter implements IWorkbenchAdapter {
	public static TagTreeItemAdapter INSTANCE = new TagTreeItemAdapter();
	private TagTreeItemAdapter(){}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object o) {
		if (o instanceof TagsTree) return ((TagsTree)o).getChildren();
		return ((TagTreeItem)o).getChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		if (object instanceof RootTagTreeItem) {
			return TagSEAPlugin.getImageDescriptor("icons/taggroup.gif");
		}
		return TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_TAG);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
	 */
	public String getLabel(Object o) {
		return ((TagTreeItem)o).getText();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
	 */
	public Object getParent(Object o) {
		return ((TagTreeItem)o).getParent();
	}

}
