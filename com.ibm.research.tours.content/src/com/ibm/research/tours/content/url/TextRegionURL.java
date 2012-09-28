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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;

import com.ibm.research.tours.content.url.parser.URLParser;


public class TextRegionURL extends ResourceURL implements IURL
{	
	public static final String OFFSET      = "offset";
	public static final String LENGTH      = "length";
	public static final String LINE_OFFSET = "lineOffset";
	public static final String LINE_LENGTH = "lineLength";
	public static final String LINE_START  = "lineStart";
	public static final String LINE_END    = "lineEnd";

	private IRegion fTextRegion;

	public TextRegionURL(IFile file, IRegion region) 
	{
		super(file);
		fTextRegion = region;
	}

	public IRegion getTextRegion()
	{
		return fTextRegion;
	}

	public String toPortableString() 
	{
		return super.toPortableString() + URLParser.PARAMETER_START + OFFSET + URLParser.PARAMETER_EQUALS + fTextRegion.getOffset() + URLParser.PARAMETER_DELIM + LENGTH + URLParser.PARAMETER_EQUALS + fTextRegion.getLength();
	}
}
