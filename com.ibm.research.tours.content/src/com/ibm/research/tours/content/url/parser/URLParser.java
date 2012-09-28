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

public class URLParser 
{
	public static final String PARAMETER_START  = "?";
	public static final String PARAMETER_START_REGEX  = "\\?";
	public static final String PARAMETER_DELIM  = "&";
	public static final String PARAMETER_EQUALS = "=";
	public static final String ECLIPSE_PREAMBLE = "eclipse:";
	public static final String HTTP_PREAMBLE = "http://";
	public static final String COLON = ":";
	
	public static URLInfo parse(String urlString)
	{
		if(urlString!=null)
		{
			urlString = urlString.trim();
		
			// Hack to support http
			if(urlString.toLowerCase().startsWith(HTTP_PREAMBLE))
			{
				URLInfo info = new URLInfo();
				info.setPreamble(HTTP_PREAMBLE);
				info.setBody(urlString);
				return info;
			}
			
			if(urlString.toLowerCase().startsWith(ECLIPSE_PREAMBLE))
			{
				String parameterString = null;
				int colon =  urlString.indexOf(COLON);				
				String body = urlString.substring(colon + 1, urlString.length());				
				int parameterDelim = body.indexOf(PARAMETER_START);
				
				if(parameterDelim != -1)
				{
					parameterString = body.substring(parameterDelim,body.length());
					parameterString = parameterString.replaceFirst(PARAMETER_START_REGEX, "");
					body = body.substring(0,parameterDelim);
				}	

				URLInfo info = new URLInfo(); 
				info.setPreamble(ECLIPSE_PREAMBLE);
				info.setBody(body);
				info.setParameterString(parameterString);
				return info;
			}
		}
		return null;
	}
}
