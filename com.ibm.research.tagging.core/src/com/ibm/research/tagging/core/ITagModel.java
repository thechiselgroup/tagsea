/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/

package com.ibm.research.tagging.core;

/**
 * @author mdesmond
 */
public interface ITagModel 
{
	/**
	 * Get the set of tags
	 * @return
	 */
	public ITag[] getTags();
	
	/**
	 * Add a tag 
	 * @param tag
	 * @return
	 */
	public ITag addTag(String name);
	
	/**
	 * Remove a tag 
	 * @param tag
	 * @return
	 */
	public void removeTag(ITag tag);
	
	/**
	 * Rename the given tag
	 * @param tag
	 * @return
	 */
	public void renameTag(ITag tag, String newName);
	
	/**
	 * Attach an ITagModelListener instance to this TagModel
	 * Duplicates will be ignored 
	 * @param listener
	 */
	public void addTagModelListener(ITagModelListener listener);
	
	/**
	 * Remove an ITagModelListener instance from this TagModel 
	 * @param listener
	 */
	public void removeTagModelListener(ITagModelListener listener);
		
	/**
	 * Get the tag corresponding to a particular name
	 * @param name
	 * @return the tag
	 */
	public ITag getTag(String name);
	
}
