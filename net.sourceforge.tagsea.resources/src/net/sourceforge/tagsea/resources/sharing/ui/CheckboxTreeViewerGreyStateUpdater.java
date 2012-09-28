/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.resources.sharing.ui;

import java.util.LinkedList;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * A listener that updates the greyed state of elements based on user selection.
 * This class is only meant for use on CheckboxTreeViewers. Using it on other
 * viewers will result in a ClassCastException in the checkStateChanged() meathod.
 * @author Del Myers
 *
 */
public class CheckboxTreeViewerGreyStateUpdater implements ICheckStateListener {

	public void checkStateChanged(CheckStateChangedEvent event) {
		CheckboxTreeViewer viewer = (CheckboxTreeViewer) event.getCheckable();
		setCheckedState(viewer, event.getElement(), event.getChecked());
		updateGrayedState(viewer, event.getElement(), event.getChecked());
	}

	/**
	 * @param viewer
	 * @param element
	 * @param checked
	 */
	private void updateGrayedState(CheckboxTreeViewer viewer, Object element, boolean checked) {
		//sometimes content providers don't follow the API for ITreeContentProvider, and
		//neglect to supply parents because they are too hard to calculate. So, use
		//the elements in the tree instead.
		Tree tree = viewer.getTree();
		TreeItem[] children = tree.getItems();
		//breadth-first search for the element.
		LinkedList<TreeItem> items = new LinkedList<TreeItem>();
		TreeItem item = null;
		for (TreeItem child : children) {
			items.add(child);
		}
		while (items.size() > 0) {
			TreeItem current = items.removeFirst();
			if (current.getData() == element) {
				item = current;
				break;
			}
			TreeItem[] childItems = current.getItems();
			for (TreeItem child : childItems) {
				items.add(child);
			}
		}
		if (item == null) return;
		TreeItem parent = item.getParentItem();
		while (parent != null) {
			TreeItem[] childItems = parent.getItems();
			if (childItems.length > 0) {
				int checkedItems = 0;
				int greyedItems = 0;
				for (TreeItem child : childItems) {
					if (child.getChecked()) {
						checkedItems++;
					}
					if (child.getGrayed()) {
						greyedItems++;
					}
				}
				if (checkedItems == 0) {
					parent.setChecked(false);
					parent.setGrayed(false);
				} else if (checkedItems < childItems.length) {
					parent.setChecked(true);
					parent.setGrayed(true);
				} else if (checkedItems == childItems.length && greyedItems==0) {
					parent.setChecked(true);
					parent.setGrayed(false);
				} else if (greyedItems > 0) {
					parent.setChecked(true);
					parent.setGrayed(true);
				}
				parent = parent.getParentItem();
			}
		}
		
	}
	
	/**
	 * Initializes the greyed state of the items in the given viewer based on the
	 * current checked/greyed state of the items.
	 * @param viewer
	 */
	public static void initializeGrayedState(CheckboxTreeViewer viewer) {
		TreeItem[] roots = viewer.getTree().getItems();
		for (TreeItem parent : roots) {
			updateGrayedChildItems(parent);
		}
	}
	
	private static void updateGrayedChildItems(TreeItem parent) {
		for (TreeItem child : parent.getItems()) {
			updateGrayedChildItems(child);
		}
		TreeItem[] childItems = parent.getItems();
		if (childItems.length == 0) return;
		int checkedItems = 0;
		int greyedItems = 0;
		for (TreeItem child : childItems) {
			if (child.getChecked()) {
				checkedItems++;
			}
			if (child.getGrayed()) {
				greyedItems++;
			}
		}
		if (checkedItems == 0) {
			parent.setChecked(false);
			parent.setGrayed(false);
		} else if (checkedItems < childItems.length) {
			parent.setChecked(true);
			parent.setGrayed(true);
		} else if (checkedItems == childItems.length && greyedItems==0) {
			parent.setChecked(true);
			parent.setGrayed(false);
		} else if (greyedItems > 0) {
			parent.setChecked(true);
			parent.setGrayed(true);
		}
		
	}

	/**
	 * Sets the checked state of the given element and all of its children to the value
	 * of "checked".
	 * @param viewer
	 * @param element
	 * @param checked
	 */
	private void setCheckedState(CheckboxTreeViewer viewer, Object element, boolean checked) {
		ITreeContentProvider provider = (ITreeContentProvider) viewer.getContentProvider();
		LinkedList<Object> elements = new LinkedList<Object>();
		elements.add(element);
		while (elements.size() > 0) {
			Object current = elements.removeFirst();
			viewer.setChecked(current, checked);
			viewer.setGrayed(current, false);
			for (Object child : provider.getChildren(current)) {
				elements.add(child);
			}
		}
	}

}
