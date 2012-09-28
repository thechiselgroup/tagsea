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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ui.TagTransfer;
import net.sourceforge.tagsea.core.ui.tags.TagNameTransfer;

import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

/**
 * Handles dragging for the Tags view.
 * @author Del Myers
 */

public class TagsDragListener extends DragSourceAdapter {
	private TreeViewer tree;
	/**
	 * Creates a new tag tree drag listener with the given tree containing
	 * the tags.
	 * @param tagsTree
	 */
	public TagsDragListener(TreeViewer tagsTree) {
		this.tree = tagsTree;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceAdapter#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragSetData(DragSourceEvent event) {
		if (TagTreeItemTransfer.getInstance().isSupportedType(event.dataType)) {
			ISelection selection = tree.getSelection();
			if (selection instanceof ITreeSelection) {
				List<TagTreeItem> items = pruneSelection((ITreeSelection)selection);
				event.data = items.toArray(new TagTreeItem[items.size()]);
				event.doit = true;
			}
		} else if (TagTransfer.getInstance().isSupportedType(event.dataType)) {
			
			ISelection selection = tree.getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection ss = (IStructuredSelection) selection;
				HashSet<ITag> tags = new HashSet<ITag>();
				Iterator<?> it=ss.iterator();
				for (int i = 0; it.hasNext(); i++) {
					TagTreeItem item = (TagTreeItem) it.next();
					if (item.getTag() != null) {
						tags.add(item.getTag());
					}
					tags.addAll(item.getChildTags());
				}
				event.data = tags.toArray(new ITag[tags.size()]);
			}
		} else if (TagNameTransfer.getInstance().isSupportedType(event.dataType)) {
			ISelection selection = tree.getSelection();
			if (selection instanceof IStructuredSelection) {
				Set<String> nameSet = new TreeSet<String>();
				for (Iterator<?> i = ((IStructuredSelection)selection).iterator(); i.hasNext();) {
					TagTreeItem item = (TagTreeItem) i.next();
					nameSet.add(item.getName());
				}
				event.data = nameSet.toArray(new String[nameSet.size()]);
				//event.detail = DND.DROP_COPY;
				event.doit = true;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceAdapter#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragStart(DragSourceEvent event) {
		event.detail = DND.DROP_COPY | DND.DROP_MOVE;
	}
	
	private class TreeItemComparer implements IElementComparer {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IElementComparer#equals(java.lang.Object, java.lang.Object)
		 */
		public boolean equals(Object a, Object b) {
			return a.toString().equals(b.toString());
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IElementComparer#hashCode(java.lang.Object)
		 */
		public int hashCode(Object element) {
			return element.hashCode();
		}
	}
	/**
	 * @param selection
	 */
	private List<TagTreeItem> pruneSelection(ITreeSelection selection) {
		TreePath[] paths = selection.getPaths();
		List<TreePath> pathList = new LinkedList<TreePath>(Arrays.asList(paths));
		TreeItemComparer comparer = new TreeItemComparer();
		for (int i = 0; i < pathList.size(); i++) {
			TreePath path = pathList.remove(0);
			//check to see if the tree path has a parent in the list.
			//if yes, take it off.
			boolean hasParent = false;
			for (TreePath possibleParent : pathList) {
				if (path.startsWith(possibleParent, comparer)){
					hasParent = true;
					break;
				}
			}
			if (!hasParent)
				pathList.add(path);
		}
		List<TagTreeItem> itemList = new LinkedList<TagTreeItem>();
		for (TreePath path : pathList) {
			itemList.add((TagTreeItem) path.getLastSegment());
		}
		return itemList;
	}
}
