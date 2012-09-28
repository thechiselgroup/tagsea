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
package com.ibm.research.tagging.core.ui.tags;

/**
 * 
 * @author mdesmond
 *
 */
public interface ITagViewListener 
{
	/**
	 * Create a tag
	 * @param view
	 */
	public void createTag(TagView view);
	
	/**
	 * delete a tag
	 * @param view
	 */
	public void deleteTag(TagView view);
	
	/**
	 * rename a tag
	 * @param view
	 */
	public void renameTag(TagView view);

	/**
	 * 
	 * @param view
	 */
	public void deleteUnusedTags(TagView view);
}
