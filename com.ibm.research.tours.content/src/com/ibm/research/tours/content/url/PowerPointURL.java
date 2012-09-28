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

import java.util.Vector;

import org.eclipse.core.resources.IFile;

import com.ibm.research.tours.content.SlideRange;
import com.ibm.research.tours.content.url.parser.URLParser;

// Need to distinguish urls pointing to powerpoint shows
public class PowerPointURL implements IURL
{
	public static final String START_PARAM = "startSlide";
	public static final String END_PARAM = "endSlide";
	private SlideRange fRange;
	private IFile fFile;
	
	public PowerPointURL(IFile file) 
	{
		fFile = file;
	}
	
	public IFile getPowerPointFile()
	{
		return fFile;
	}
	
	public SlideRange getSlideRange()
	{
		return fRange;
	}
	
	public void setSlideRange(SlideRange range)
	{
		fRange = range;
	}
	
	public String toPortableString() 
	{
		String parameters = new String();
		
		if(fRange != null)
			parameters = URLParser.PARAMETER_START + START_PARAM + URLParser.PARAMETER_EQUALS + fRange.getStart() + URLParser.PARAMETER_DELIM + END_PARAM + URLParser.PARAMETER_EQUALS + fRange.getEnd();
				
		String base =  URLParser.ECLIPSE_PREAMBLE + fFile.getFullPath().toPortableString();
		
		return base + parameters;
	}
}
