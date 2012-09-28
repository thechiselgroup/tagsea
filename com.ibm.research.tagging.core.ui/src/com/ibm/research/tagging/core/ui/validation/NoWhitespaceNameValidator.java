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

/**
 * 
 * @author mdesmond
 *
 */
public class NoWhitespaceNameValidator extends EmptyNameValidator 
{
	private static final String ERROR_WHITESPACE = "The name cannot contain spaces.";
	
	public String isValid(String newText) 
	{
		String result = super.isValid(newText);
		
		if(result == null && newText != null)
		{
			String text = newText.trim();
			
			if(text.split("\\s+").length > 1)
				return getWhitespaceError();
		}
		
		return result;
	}
	
	protected String getWhitespaceError()
	{
		return ERROR_WHITESPACE;
	}
}
