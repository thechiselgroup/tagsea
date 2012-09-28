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
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.core.internal.ITagSEAPreferences;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.ActionContext;

/**
 * An action for generalizing tags.
 * @author Del Myers
 */

public class GeneralizeAction extends TagContextAction {
	public void run() {
		if (getContext() == null) return;
		if (!(getContext().getSelection() instanceof IStructuredSelection)) return;
		IStructuredSelection ss = (IStructuredSelection) getContext().getSelection();
		Object o = ss.getFirstElement();
		if (o instanceof TagTreeItem) {
			generalizeItem((TagTreeItem)o);
		}
	}

	/**
	 * Generalizes the item.
	 * @param item
	 */
	private void generalizeItem(final TagTreeItem item) {
		TagSEAOperation op = new TagSEAOperation("Pushing-up Tags...") {
			@Override
			public IStatus run(IProgressMonitor monitor) throws  InvocationTargetException {
				if (!item.isFlat()) {
					MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, IStatus.OK, "", null);
					int dot = item.getName().lastIndexOf('.');
					int end = 0;
					if (dot < 0) {
						dot = 0;
					}
					end = item.getText().length() + dot + 1;
					
					List<ITag> childTags = item.getChildTags();
					ITag rootTag = item.getTag();
					if (rootTag != null) {
						TagTreeItem parent = item.getParent();
						for (IWaypoint wp : rootTag.getWaypoints()) {
							if (parent != null) {
								wp.addTag(parent.getName());
							}
							status.add(wp.removeTag(rootTag).getStatus());
						}
					}
					for (ITag tag : childTags) {
						StringBuilder nameBuilder = new StringBuilder(tag.getName());
						nameBuilder.replace(dot, end, "");
						String newName = nameBuilder.toString();
						if (!tag.setName(newName)) {
							status.add(moveWaypoints(tag, newName));
						}
						
					}
					return status;
				} else {
					return Status.OK_STATUS;
				}
			}
		};
		TagSEAPlugin.run(op, true);
	}
	
	/*package*/ IStatus moveWaypoints(ITag tag, String newName) {
		MultiStatus status = new MultiStatus(TagSEAPlugin.PLUGIN_ID, IStatus.OK, "", null);
		if ((TagSEAPlugin.getTagsModel().getTag(newName)) != null) {
			for (IWaypoint wp : tag.getWaypoints()) {
				ITag tag2 = wp.addTag(newName);
				if (tag2 != null)
					status.add(wp.removeTag(tag).getStatus());
				else {
					status.add(new Status(IStatus.WARNING, TagSEAPlugin.PLUGIN_ID, IStatus.WARNING, "Could not add tag " + tag.getName(), null));
				}
			}
		}
		return status;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.tags.ContextAction#setContext(org.eclipse.ui.actions.ActionContext)
	 */
	@Override
	public void setContext(ActionContext context) {
		super.setContext(context);
		if (context == null) return;
		ISelection selection = context.getSelection();
		setEnabled(false);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			if (ss.size() == 1) {
				Object o = ss.getFirstElement();
				if (o instanceof TagTreeItem) {
					TagTreeItem item = (TagTreeItem) o;
					if (!ITag.DEFAULT.equals(item.getName())) {
						IPreferenceStore store = TagSEAPlugin.getDefault().getPreferenceStore();
						boolean asTree = store.getBoolean(ITagSEAPreferences.TAGS_VIEW_TREE);
						setEnabled(asTree);
					}
				}
			}
		}
	}
}
