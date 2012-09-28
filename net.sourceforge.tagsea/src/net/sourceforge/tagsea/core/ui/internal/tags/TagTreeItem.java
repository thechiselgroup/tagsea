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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.internal.ITagSEAPreferences;
import net.sourceforge.tagsea.core.ui.IWaypointFilter;
import net.sourceforge.tagsea.core.ui.internal.TagSEAUI;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * A subtree of tags represented in a tree structure.
 * @author Del Myers
 */

public class TagTreeItem implements IAdaptable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The text displayed for this item.
	 */
	String name;
	private TagTreeItem parent;
	private boolean flat;
	private int waypointCount;
	private TagTreeItem[] children;
	
	protected TagTreeItem() {
		this("", null, false);
	}
	
	public TagTreeItem(String text, TagTreeItem parent, boolean flat) {
		this.name = text;
		this.parent = parent;
		this.flat = flat;
		children = null;
		waypointCount = -1;
	}
	
	public ITag getTag() {
		return TagSEAPlugin.getTagsModel().getTag(getName());
	}
	
	
	public TagTreeItem[] getChildren() {
		if (flat) return new TagTreeItem[0];
		if (this.children == null) {
			List<ITag> childTags = getChildTags();
			TreeSet<String> descendants = new TreeSet<String>();
			for (ITag tag : childTags) {
				String text = tag.getName();
				text = text.substring(getName().length());
				if (!text.startsWith(".")) {
					continue;
				} else {
					text = text.substring(1);
				}
				int dot = text.indexOf('.');
				if (dot < 0) {
					dot = text.length();
				}
				descendants.add(getName() + "." + text.substring(0,dot));
			}
			this.children = new TagTreeItem[descendants.size()];
			int i = 0;
			for (String name : descendants) {
				this.children[i] = new TagTreeItem(name, this, false);
				i++;
			}
		}
		return this.children;
	}
	
	public boolean hasChildren() {
		ITag[] tags = TagSEAPlugin.getTagsModel().getAllTags();
		int index = binarySearch(tags, 0, tags.length-1);
		if (index >= tags.length) {
			return false;
		}
		return tags[index].getName().startsWith(getName() + ".");
	}
	
	public List<ITag> getChildTags() {

		List<ITag> children = new LinkedList<ITag>();
		if (isFlat()) return children;
		ITag[] tags = TagSEAPlugin.getTagsModel().getAllTags();
		if (tags.length == 0) return children;
		int index = binarySearch(tags, 0, tags.length-1);
		index--;
//		get the next biggest one.
		if (index < 0)
			index++;
		while (index < tags.length && tags[index].getName().compareTo(getName()) < 0) {
			index++;
		} 
		if (index >= tags.length) {
			return children;
		}
		while (index < tags.length && tags[index].getName().startsWith(getName())) {
			String childName = tags[index].getName();
			if (childName.length() > getName().length()) {
				if (childName.charAt(getName().length()) != '.')
					break;
			}
			children.add(tags[index]);
			index++;
		}
		return children;
		
	}
	
	/**
	 * Finds the index of the tag with the same name as this tree item. If
	 * it can not be found, the tag with the next "smallest" name to the name
	 * of this tree item is reaturned.
	 * @param tags
	 * @param i
	 * @param j
	 * @return
	 */
	private int binarySearch(ITag[] tags, int i, int j) {
		if (j <= i) return i;
		int middle = (j+i)/2;
		int diff = tags[middle].getName().compareTo(getName());
		if (j-i == 1) {
			if (diff > 0)
				return i;
			return j;
		}
		if (diff > 0) {
			//check the lower half
			return binarySearch(tags, i, middle);
		} else if (diff < 0) {
			//check the upper half
			return binarySearch(tags, middle, j);
		}
		return middle;
	}

	public String getName() {
		return name;
	}
	
	public String getText() {
		if (flat) {
			return getName();
		}
		String text = getName();
		int dot = text.lastIndexOf('.');
		if (dot < 0) return text;
		return text.substring(dot+1);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (IWorkbenchAdapter.class.equals(adapter)) return TagTreeItemAdapter.INSTANCE;
		return null;
	}
	
	/**
	 * @return the parent
	 */
	public TagTreeItem getParent() {
		return parent;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TagTreeItem)) return false;
		return getName().equals(((TagTreeItem)obj).getName());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	public int getWaypointCount() {
		if (this.waypointCount == -1) {
			this.waypointCount = countWaypoints();
		}
		return this.waypointCount;
	}

	/**
	 * @return
	 */
	private int countWaypoints() {
		ITag tag = getTag();
		int count = 0;
		if (tag != null) {
			boolean isfilter = 
				TagSEAPlugin.
					getDefault().
					getPreferenceStore().
					getBoolean(ITagSEAPreferences.FILTER_TAGS_TO_WAYPOINTS);
			if (!isfilter) {
				count = tag.getWaypointCount();
			} else {
				IWaypoint[] waypoints = tag.getWaypoints();
				TagSEAUI ui = (TagSEAUI)TagSEAPlugin.getDefault().getUI();
				for (IWaypoint wp : waypoints) {
					if (!ui.isFilteredOutType(wp.getType())) {
						IWaypointFilter filter = ui.getFilter(wp.getType());
						if (filter != null) {
							if (filter.select(wp)) {
								count++;
							}
						} else {
							count++;
						}
					}
				}
			}
		}
		TagTreeItem[] childItems = getChildren();
		for (TagTreeItem child : childItems) {
			count += child.getWaypointCount();
		}
		return count;
	}
	
	/**
	 * @return the if this item is flat or not.
	 */
	public boolean isFlat() {
		return flat;
	}
}
