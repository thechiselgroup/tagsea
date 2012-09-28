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
package com.ibm.research.tours.content;

import org.eclipse.jface.text.IRegion;

public class TextRegion 
{
	private Object fFile;
	private IRegion fRegion;
	
	public TextRegion(Object file,IRegion region) 
	{
		fFile = file;
		fRegion = region;
	}
	
	public Object getFile()
	{
		return fFile;
	}
	
	public IRegion getRegion()
	{
		return fRegion;
	}
}
