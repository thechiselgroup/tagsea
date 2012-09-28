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

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * Drop adapter for the tags view.
 * @author Del Myers
 */

public class TagsViewDropListener extends DropTargetAdapter {
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DropTargetAdapter#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	@Override
	public void dragEnter(DropTargetEvent event) {
		event.detail = DND.DROP_NONE;
		for (TransferData type : event.dataTypes) {
			if (TagTreeItemTransfer.getInstance().isSupportedType(type)) {
				event.detail = DND.DROP_MOVE;
				event.currentDataType = type;
				return;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DropTargetAdapter#drop(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	@Override
	public void drop(DropTargetEvent event) {
		TreeItem item = (TreeItem) event.item;
		if (TagTreeItemTransfer.getInstance().isSupportedType(event.currentDataType)) {
			TagTreeItem[] tagItems = (TagTreeItem[]) event.data;
			if (item != null) {
				renameTags((TagTreeItem) item.getData(), tagItems);
			} else {
				//null to make it have no parent.
				renameTags(null, tagItems);				
			}
			
			
		}
		
		
	}
	
	public void dragOver(DropTargetEvent event) {
		TagsTree tree = null;
		Object data = null;
		if (event.widget instanceof DropTarget) {
			Widget control = ((DropTarget)event.widget).getControl();
			data = control.getData();
		} else {
			data = event.widget.getData();
		}
		if (data instanceof Object[]) {
			for (Object o : ((Object[])data)) {
				if (o instanceof TagsTree) {
					tree = (TagsTree) o;
					break;
				}
			}
		} else if (data instanceof TagsTree) {
			tree = (TagsTree) data;
		}
		
		if (tree != null && tree.isFlat()) {
			event.operations = DND.DROP_NONE;
			event.detail = DND.DROP_NONE;
			event.feedback = DND.FEEDBACK_NONE;
		} else {
			event.feedback = DND.FEEDBACK_INSERT_AFTER | DND.FEEDBACK_SCROLL;
		}
	} 

	/**
	 * @param item the new parent, or null if it is to be top-level.
	 * @param tagItems
	 */
	private void renameTags(final TagTreeItem newParent, final TagTreeItem[] tagItems) {
		TagSEAPlugin.run(new TagSEAOperation("Moving Tags...") {
			@Override
			public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
				monitor.beginTask("Moving Tags....", tagItems.length);
				String newParentName = (newParent != null) ? newParent.getName() : "";
				MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, IStatus.OK, "", null);
				for (TagTreeItem item : tagItems) {
					TagTreeItem parent = item.getParent();
					//no need to move if the parents are equal.
					if ((parent == null && newParent == null) || ((parent != null && parent.equals(newParent))))
						continue;
					status.merge(reparentChildren(item, parent, newParentName));
				}
				return status;
			}

			private IStatus reparentChildren(TagTreeItem item, TagTreeItem parent, String newParentName) {
				ITag tag = item.getTag();
				MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, IStatus.OK, "", null);
				if (tag != null) {
					String oldParentName = (parent != null) ? parent.getName() : "";
					String name = item.getName();
					name = name.substring(oldParentName.length());
					if (!name.startsWith(".")) {
						name = "." + name;
					}
					name = newParentName + name;
					if (name.startsWith(".")) {
						//remove leading periods: this should be a top-level tag.
						char c = name.charAt(0);
						int i = 1;
						while (c == '.' && i < name.length()) {
							c = name.charAt(i);
							i++;
						}
						name = name.substring(i-1);
					}
					if (!tag.setName(name)) {
						moveWaypoints(tag, name);
					}
				}
				for (TagTreeItem child : item.getChildren()) {
					status.merge(reparentChildren(child, parent, newParentName));
				}
				return Status.OK_STATUS;
			}

			/**
			 * Moves the waypoints for the given tag to the new name
			 * if the name already exists.
			 * @param tag
			 * @param name
			 */
			private void moveWaypoints(ITag tag, String name) {
				if (TagSEAPlugin.getTagsModel().getTag(name) != null) {
					for (IWaypoint wp : tag.getWaypoints()) {
						//add the new tag first in order to avoid problems
						//of removing the waypoint entirely.
						wp.addTag(name);
						wp.removeTag(tag);
					}
				}
			}
		}, true);
	}
}
