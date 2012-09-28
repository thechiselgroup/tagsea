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
package com.ibm.research.tours.content.url;

import org.eclipse.core.resources.IResource;

import com.ibm.research.tours.content.url.parser.URLParser;

public class ResourceURL implements IURL
{
	private IResource fResource;
	
	public ResourceURL(IResource resource) 
	{
		fResource = resource;
	}
	
	public IResource getResource()
	{
		return fResource;
	}
	
	public String toPortableString() 
	{
		return URLParser.ECLIPSE_PREAMBLE + fResource.getFullPath().toPortableString();
	}
}