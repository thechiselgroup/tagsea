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
package net.sourceforge.tagsea.core;

/**
 * The central tags model for the TagSEA platform. There is only one tags model in the TagSEA
 * platform, which can be accessed via TagSEAPlugin.getDefault().getTagsModel().
 * 
 * 
 * @author Del Myers
 * 
 */

public interface ITagsModel {

	/**
	 * Returns the tag of the given name.
	 * @param name the name of the tag.
	 * @return the tag of the given name.
	 */
	public ITag getTag(String name);
	
	/**
	 * Returns all tags visible in the workbench. They are garanteed to be in lexicographical order.
	 * @return all tags visible in the workbench.
	 */
	public ITag[] getAllTags();
	
	/**
	 * Returns the number of tags in the model.
	 * @return the number of tags in the model.
	 */
	public int tagCount();
	
}
