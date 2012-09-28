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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.IWaypointFilter;
import net.sourceforge.tagsea.core.ui.internal.TagSEAUI;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * A filter that links the tags view with the filters for waypoints. This filter will not allow any tag
 * that does not have waypoints that match the waypoints filter contributions to be visible in the tags view.
 * @author Del Myers
 */
public class TagWaypointViewerFilter extends ViewerFilter {
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof TagTreeItem) {
			for (ITag tag :((TagTreeItem)element).getChildTags()) {
				TagSEAUI ui = (TagSEAUI)TagSEAPlugin.getDefault().getUI();
				IWaypoint[] wps = tag.getWaypoints();
				for (IWaypoint wp : wps) {
					if (!ui.isFilteredOutType(wp.getType())) {
						IWaypointFilter filter = ui.getFilter(wp.getType());
						if (filter == null || ui.getFilter(wp.getType()).select(wp)) return true;
					}
				}
			}
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#filter(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object[])
	 */
	@Override
	public Object[] filter(Viewer viewer, Object parent, Object[] elements) {
		HashSet<TagTreeItem> visibleElements = new HashSet<TagTreeItem>();
		HashSet<TagTreeItem> invisibleElements = new HashSet<TagTreeItem>();
		List<Object> result = new LinkedList<Object>();
		//gather up the visible and invisible elements.
		for (Object element : elements) {
			if (element instanceof TagTreeItem) {
				TagTreeItem item = (TagTreeItem) element;
				if (visibleElements.contains(item)) {
					result.add(item);
					visibleElements.remove(item);
				} else if (!invisibleElements.remove(item)) {
					gatherVisibleItems(item, visibleElements, invisibleElements);
					if (visibleElements.contains(item)) {
						result.add(item);
						visibleElements.remove(item);
					}
				}
			}
		}
		return result.toArray();
	}
	/**
	 * @param item 
	 * @param visibleElements
	 * @param invisibleElements
	 */
	private boolean gatherVisibleItems(TagTreeItem item, HashSet<TagTreeItem> visibleElements, HashSet<TagTreeItem> invisibleElements) {
		ITag tag = item.getTag();
		boolean visible = false;
		if (tag != null) {
			TagSEAUI ui = (TagSEAUI)TagSEAPlugin.getDefault().getUI();
			IWaypoint[] wps = tag.getWaypoints();
			for (IWaypoint wp : wps) {
				if (!ui.isFilteredOutType(wp.getType())) {
					IWaypointFilter filter = ui.getFilter(wp.getType());
					visible |= (filter == null || ui.getFilter(wp.getType()).select(wp));
					if (visible) break;
				}
			}
		}
		for (TagTreeItem child : item.getChildren()) {
			visible |= gatherVisibleItems(child, visibleElements, invisibleElements);
		}
		if (visible) {
			visibleElements.add(item);
		} else {
			invisibleElements.add(item);
		}
		return visible;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public boolean isImportantSelection(ISelection selection) {
		String[] types = TagSEAPlugin.getDefault().getWaypointTypes();
		TagSEAUI ui = (TagSEAUI) TagSEAPlugin.getDefault().getUI();
		for (String type : types) {
			if (!ui.isFilteredOutType(type)) {
				IWaypointFilter filter = ui.getFilter(type);
				if (filter != null && filter.isImportantSelection(selection)) return true;
			}
		}
		return false;
	}

}
