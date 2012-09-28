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
public class EmptyNameValidator extends InvalidNameValidator 
{
	private static final String ERROR_EMPTY = "The name can not be empty.";

	public String isValid(String newText)
	{
		String result = super.isValid(newText);
		
		if(newText == null || newText.trim().length() == 0)
			return getEmptyNameError();
		
		return result;
	}

	protected String getEmptyNameError()
	{
		return ERROR_EMPTY;
	}
	
}
