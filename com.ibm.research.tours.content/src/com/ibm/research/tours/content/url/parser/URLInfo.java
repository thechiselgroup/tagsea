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

import java.util.Vector;

public class URLInfo 
{
	private String fBody;
	private String fParameterString;
	private String fPreamble;
	
	public String getBody() 
	{
		return fBody;
	}
	
	protected void setBody(String body) 
	{
		fBody = body;
	}
	
	public String getParameterString() 
	{
		return fParameterString;
	}
	
	protected void setParameterString(String parameterString) 
	{
		fParameterString = parameterString;
	}
	
	public URLParameter[] getParameters()
	{
		if(getParameterString()!=null)
		{
			Vector<URLParameter> parameters = new Vector<URLParameter>();
			String[] parts = getParameterString().split(URLParser.PARAMETER_DELIM);

			for(String part : parts)
			{
				String[] nameValue = getNameValue(part);

				if(nameValue != null)
				{	
					if(nameValue.length == 2)
					{
						parameters.add(new URLParameter(nameValue[0],nameValue[1]));
					}
				}
			}
			return parameters.toArray(new URLParameter[0]);
		}
		
		return new URLParameter[0];
	}
	
	private String[] getNameValue(String nameValuePair)
	{
		if(nameValuePair !=null)
		{
			String[] values = nameValuePair.split(URLParser.PARAMETER_EQUALS);

			if(values.length == 2)
			{
				String name = values[0].trim();
				String value = values[1].trim();
				return new String[]{name,value};
			}
		}
		return null;
	}

	public String getPreamble() {
		return fPreamble;
	}

	public void setPreamble(String preamble) {
		fPreamble = preamble;
	}
}
