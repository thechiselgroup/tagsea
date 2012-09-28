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

import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * 
 * @author mdesmond
 *
 */
public class InvalidNameValidator implements IInputValidator 
{
	private static final String ERROR_INVALID = "The name contains invalid characters.";
	
	private static Pattern pattern = Pattern.compile("[^a-zA-Z0-9_ \\.\\-\\'#!\\+]"); // allowed characters

	public String isValid(String newText) 
	{
		if(newText != null && newText.trim().length() > 0)
		{
			if (!isAllowed(newText))
				return getInvalidNameError();
		}
		
		return null;
	}

	protected String getInvalidNameError()
	{
		return ERROR_INVALID;
	}
	
	/**
	 * Checks if the given string is contains any illegal
	 * characters.  Also checks to make sure the text isn't just spaces.
	 * @param text
	 * @return boolean if the text is a valid name
	 */
	private boolean isAllowed(String text) 
	{
		boolean allowed = false;
		
		if ((text != null) && (text.length() > 0)) 
		{
			allowed = !pattern.matcher(text).find();	
		}
		
		return allowed;
	}	
}
