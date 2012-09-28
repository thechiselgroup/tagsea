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
package net.sourceforge.tagsea;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import net.sourceforge.tagsea.core.ITag;



/**
 * Simple utility class for TagSEA.
 * @author Del Myers
 */
public class TagSEAUtils {
	
	/**
	 * Return code for a valid tag name.
	 */
	public static final int TAG_NAME_VALID = 0;
	
	/**
	 * Return code for a tag name with a syntax error.
	 */
	public static final int TAG_NAME_SYNTAX_ERROR = 1;
	
	/**
	 * Return code for a tag name with a bad character.
	 */
	public static final int TAG_NAME_BAD_CHARACTER = 2;
	
	
	public static final int isValidTagName(String name) {
		int size = name.length();
		char syntax = 0;
		if (name.trim().length() != size) return TAG_NAME_SYNTAX_ERROR;
		if ("".equals(name)) return TAG_NAME_SYNTAX_ERROR;
		//scan for dot positions to make sure that there are no two consecutive dots
		//and that the tag name doesn't begin or end with a dot.
		int lastDot = -1;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c == '.') {
				if (syntax != '(') {
					syntax = '.';
				} else {
					return TAG_NAME_SYNTAX_ERROR;
				}
				if (i == 0) {
					return TAG_NAME_BAD_CHARACTER;
				} else if (i == name.length() - 1) {
					return TAG_NAME_BAD_CHARACTER;
				} else if (lastDot == i-1) {
					return TAG_NAME_SYNTAX_ERROR;
				}
				lastDot = i;
			} else if (Character.isWhitespace(c)) {
				return TAG_NAME_BAD_CHARACTER;
			} else if (c == '(') {
				if (syntax != '.') {
					syntax = '(';
				} else {
					return TAG_NAME_SYNTAX_ERROR;
				}
			}
		}
		return TAG_NAME_VALID;
	}
	
	/**
	 * Uses the dot-separated naming convension to find children tags of the given tag.
	 * This method returns all subchildren (i.e. all children of children).
	 * @param tag
	 * @return
	 */
	public static final ITag[] getAllChildTags(ITag tag) {
		List<ITag> tagList = findChildTags(tag);
		return tagList.toArray(new ITag[tagList.size()]);
	}
	
	/**
	 * Uses the dot-separated naming convension to find child tags of the given tag.
	 * This method only returns direct descendants.
	 * @param tag the target tag.
	 * @return
	 */
	public static final ITag[] getChildTags(ITag tag) {
		List<ITag> tagList = findChildTags(tag);
		for (int i = 0; i < tagList.size(); i++) {
			ITag child = tagList.get(i);
			int dot = child.getName().lastIndexOf('.');
			if (dot > tag.getName().length()) {
				tagList = tagList.subList(0, i);
				break;
			}
		}
		return tagList.toArray(new ITag[tagList.size()]);
	}
	
	/**
	 * Returns the index of the given tag in the tags array returned by
	 * getAllTags() from the tags model. NOTE: this method should only be
	 * called from within a TagSEAOperation, otherwise it is possible for
	 * the index value to become out of sync with the model.
	 * @param tag the tag to index.
	 * @return the index of the tag.
	 */
	public static final int indexOf(ITag tag) {
		ITag[] tags = TagSEAPlugin.getTagsModel().getAllTags();
		return binarySearch(tag,tags, 0, tags.length);
	}
	
	private static List<ITag> findChildTags(ITag tag) {
		ITag[] tags = TagSEAPlugin.getTagsModel().getAllTags();
		int index = binarySearch(tag, tags, 0, tags.length-1);
		List<ITag> children = new LinkedList<ITag>();
//		get the next biggest one.
		if (index < tags.length) {
			if (tags[index].getName().compareTo(tag.getName()) <= 0)
				index++;
		} else {
			return children;
		}
		while (index < tags.length && tags[index].getName().startsWith(tag.getName() + ".")) {
			children.add(tags[index]);
			index++;
		}
		return children;
		
	}
	
	
	/**
	 * Convenience method that uses the dot notation to determine tag names that can be considered "roots".
	 * For example, given the tag a.b.c, 'a' would be considered the 'root' tag name. Note, 'a' need not
	 * exist as a real tag.
	 * @return
	 */
	public static String[] getRootTagNames() {
		ITag[] tags = TagSEAPlugin.getTagsModel().getAllTags();
		TreeSet<String> rootNames = new TreeSet<String>();
		for (ITag tag : tags) {
			String name = tag.getName();
			int dot = name.indexOf('.');
			if (dot == -1) {
				rootNames.add(name);
			} else {
				rootNames.add(name.substring(0, dot));
			}
		}
		return rootNames.toArray(new String[rootNames.size()]);
	}
	
	/**
	 * Convenience method that uses the dot noation to determine tag names that can be considered
	 * as "children" of the given parent name. For example, given the tags a, a.b, a.b.c, a.b.c.d, a.b.d,
	 * and the input 'a.b', this method would return a.b.c, and a.b.d. Note that none of a.b, a.b.c, or
	 * a.b.d need exist as a real tag. Returns all tag names if the input is the empty string.
	 * @param parentName
	 * @return
	 */
	public static String[] getChildTagNames(String parentName) {
		ITag[] tags = TagSEAPlugin.getTagsModel().getAllTags();
		if ("".equals(parentName)) {
			String[] tagNames = new String[tags.length];
			for (int i = 0; i < tags.length; i++) {
				tagNames[i] = tags[i].getName();
			}
			return tagNames;
		}
		TreeSet<String> childNames = new TreeSet<String>();
		String dottedParentName = parentName + '.';
		boolean done = false;
		boolean parentFound = false;
		for (int i = 0; i < tags.length && !done; i++) {
			String name = tags[i].getName();
			if (parentFound) {
				//make sure to quit as early as possible.
				done = !name.startsWith(parentName); 
			} else {
				parentFound = name.startsWith(parentName);
			}
			if (name.startsWith(dottedParentName)) {
				if (name.length() > dottedParentName.length() + 1) {
					//find the next dot.
					int cIndex = dottedParentName.length();
					while (cIndex < name.length() && name.charAt(cIndex) != '.') {
						cIndex++;
					}
					childNames.add(name.substring(0, cIndex));
				}
			}
		}
		return childNames.toArray(new String[childNames.size()]);
	}
	
	/**
	 * Returns all of the tags that are prefixed with the parent name in the dot notation.
	 * Returns all tags if the input is the empty string.
	 * @param parentName
	 * @return all of the tags that are prefixed with the parent name in the dot notation.
	 */
	public static ITag[] getChildTagsForName(String parentName) {
		ITag[] tags = TagSEAPlugin.getTagsModel().getAllTags();
		if ("".equals(parentName)) return tags;
		String dottedParentName = parentName + '.';
		boolean done = false;
		boolean parentFound = false;
		LinkedList<ITag> foundTags = new LinkedList<ITag>();
		for (int i = 0; i < tags.length && !done; i++) {
			String name = tags[i].getName();
			if (parentFound) {
				done = !name.startsWith(parentName);
			} else {
				parentFound = name.startsWith(parentName);
			}
			if (name.startsWith(dottedParentName)) {
				foundTags.add(tags[i]);
			}
		}
		return foundTags.toArray(new ITag[foundTags.size()]);
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
	private static int binarySearch(ITag target, ITag[] tags, int i, int j) {
		if (j <= i) return i;
		int middle = (j+i)/2;
		int diff = tags[middle].getName().compareTo(target.getName());
		if (j-i == 1) {
			if (diff > 0)
				return i;
			return j;
		}
		if (diff > 0) {
			//check the lower half
			return binarySearch(target, tags, i, middle);
		} else if (diff < 0) {
			//check the upper half
			return binarySearch(target, tags, middle, j);
		}
		return middle;
	}
}
