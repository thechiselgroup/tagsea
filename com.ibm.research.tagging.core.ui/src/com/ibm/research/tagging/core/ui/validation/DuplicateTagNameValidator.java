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
package com.ibm.research.tagging.core.ui.validation;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.TagCorePlugin;

/**
 * 
 * @author mdesmond
 *
 */
public class DuplicateTagNameValidator extends NoWhitespaceNameValidator 
{
	private static final String ERROR_DUPLICATE = "Another tag with that name already exists.";

	@Override
	public String isValid(String newText)
	{
		// will verify if the name contains empty spaces
		String result = super.isValid(newText);
	
		if(result==null)
		{
			// look for duplicates
			for (ITag tag : TagCorePlugin.getDefault().getTagCore().getTagModel().getTags()) {
				String name = tag.getName();
				if (name.equals(newText)) 
				{
					return ERROR_DUPLICATE;
				}
			}
		}
		
		return result;
	}
	
	public String getDuplicateTagError()
	{
		return ERROR_DUPLICATE;
	}
}
