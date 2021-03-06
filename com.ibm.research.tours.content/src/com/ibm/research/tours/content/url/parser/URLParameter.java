/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tours.content.url.parser;

public class URLParameter 
{
	private String fName;
	private String fValue;
	
	public URLParameter(String name, String value)
	{
		fName = name;
		fValue = value;
	}

	public String getName() 
	{
		return fName;
	}

	public String getValue() 
	{
		return fValue;
	}	
}
