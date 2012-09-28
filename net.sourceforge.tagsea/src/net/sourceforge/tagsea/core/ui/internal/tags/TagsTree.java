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

import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Represents the tags in the workbench as a tree.
 * @author Del Myers
 */

public class TagsTree implements IAdaptable {
	
	private RootTagTreeItem root;
	
	public static class RootTagTreeItem extends TagTreeItem {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private boolean flat;
		
		public RootTagTreeItem() {
			this (false);
		}
		
		protected RootTagTreeItem(boolean flat) {
			this.flat = flat;
		}
		
		@Override
		public boolean isFlat() {
			return flat;
		}
		
		@Override
		public TagTreeItem[] getChildren() {
			ITag[] tags = TagSEAPlugin.getTagsModel().getAllTags();
			TagTreeItem[] children;
			if (flat) {
				children = new TagTreeItem[tags.length];
				for (int i = 0; i < tags.length; i++) {
					children[i] = new TagTreeItem(tags[i].getName(), this, true);
				}
			} else {
				TreeSet<String> childNames = new TreeSet<String>();
				for (ITag tag : tags) {
					String name = tag.getName();
					int dot = name.indexOf('.');
					if (dot >= 0) {
						name = name.substring(0, dot);
					}
					childNames.add(name);
				}
				children = new TagTreeItem[childNames.size()];
				int i = 0;
				for (String name : childNames) {
					children[i] = new TagTreeItem(name, this, false);
					i++;
				}
			}
			return children;
		}
		
		@Override
		public String getName() {
			return "All Tags";
		}
		
		@Override
		public int getWaypointCount() {
			return TagSEAPlugin.getWaypointsModel().getAllWaypoints().length;
		}
	}

	public TagsTree(boolean flat) {
		this.root = new RootTagTreeItem(flat);
	}
	
	public TagTreeItem[] getChildren() {
		return new TagTreeItem[] {root};
	}
	
	public TagTreeItem getRoot() {
		return root;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (IWorkbenchAdapter.class.equals(adapter)) return TagTreeItemAdapter.INSTANCE;
		return null;
	}
	
	
	public boolean isFlat() {
		return getRoot().isFlat();
	}

	/**
	 * @param b
	 */
	public void setFlat(boolean flat) {
		if (isFlat() != flat) {
			this.root = new RootTagTreeItem(flat);
		}
	}
	

}
