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
package net.sourceforge.tagsea.model.internal;

import java.util.TreeMap;

import net.sourceforge.tagsea.TagSEAModelException;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ITagsModel;

/**
 * Concrete implementation of the tags model.
 * @author Del Myers
 */

public class TagsModel implements ITagsModel {
	
	/**
	 * A map of tag names to tags. The TreeMap guarantees logn search times.
	 */
	private TreeMap<String, ITag> fTagsMap;

	public static final TagsModel INSTANCE = new TagsModel();
	
	private TagsModel() {
		this.fTagsMap = new TreeMap<String, ITag>();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.ITagsModel#getAllTags()
	 */
	public ITag[] getAllTags() {
		synchronized (fTagsMap) {
			ITag[] tags = new ITag[fTagsMap.size()];
			int i = 0;
			for (String key : fTagsMap.keySet()) {
				tags[i] = fTagsMap.get(key);
				i++;
			}
			return tags;
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.ITagsModel#getTag(java.lang.String)
	 */
	public ITag getTag(String name) {
		synchronized (fTagsMap) {
			return fTagsMap.get(name);
		}
	}
	
	/**
	 * Creates or retrieves the tag of the given name. If the tag already exists in the model,
	 * the pre-existing one is returned. The returned tag is not added to the tags model, because
	 * it first needs a set of waypoints added to it.
	 * @param name the name of the tag to be created or retrieved.
	 * @return the tag.
	 * @throws TagSEAModelException 
	 */
	ITag createOrGetTag(String name) throws TagSEAModelException {
		ITag tag = getTag(name);
		if (tag != null) return tag;
		tag = new Tag(name);
		return tag;
	}
	
	/**
	 * Adds the given tag to the model if one of the same name does not already exist. 
	 * @param tag
	 */
	boolean addToModel(ITag tag) {
		synchronized (getBlock()) {
			if (fTagsMap.containsKey(tag.getName())) return false;
			synchronized (fTagsMap) {
				fTagsMap.put(tag.getName(), tag);
			}
			if (tag instanceof Tag) {
				//should always be true
				((Tag)tag).create();
			}
			TagSEAChangeSupport.INSTANCE.postTagChange(TagChangeEvent.createNewEvent(tag));
			return true;
		}
	}
	
	boolean removeFromModel(ITag tag) {
		if (tag == null) return false;
		synchronized (getBlock()) {
			if (!tag.exists()) return false;
			synchronized (fTagsMap) {
				fTagsMap.remove(tag.getName());
			}
			if (tag instanceof Tag) {
				//should always be true
				((Tag)tag).delete();
			}
			TagSEAChangeSupport.INSTANCE.postTagChange(TagChangeEvent.createDeleteEvent(tag));
			return true;
		}
	}
	
	private Object getBlock() {
		return TagSEAChangeSupport.INSTANCE.getOperationBlocker();
	}

	/**
	 * Remaps the given tag.
	 * @param tag
	 */
	void remap(String oldName, Tag tag) {
		fTagsMap.remove(oldName);
		fTagsMap.put(tag.getName(), tag);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ITagsModel#tagCount()
	 */
	public int tagCount() {
		return fTagsMap.size();
	}

}
