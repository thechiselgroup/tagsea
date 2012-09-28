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

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.ibm.research.tours.content.url.parser.URLParser;


public class JavaURL implements IURL
{	
	public static final String TYPE_PARAM         = "type";
	public static final String SIGNATURE_PARAM    = "signature";
	public static final String TYPE_TYPE          = "type";
	public static final String METHOD_TYPE        = "method";
	public static final String FIELD_TYPE         = "field";
	
	private IJavaElement fElement;

	public JavaURL(IJavaElement element) 
	{
		fElement = element;
	}

	public IJavaElement getJavaElement()
	{
		return fElement;
	}

	public String toPortableString() 
	{
		String base = URLParser.ECLIPSE_PREAMBLE + fElement.getResource().getFullPath().toPortableString();
		
		if(fElement.getElementType() == IJavaElement.TYPE)
		{
			IType type = (IType)fElement;
			String typeParam = TYPE_PARAM + URLParser.PARAMETER_EQUALS + TYPE_TYPE;
			String nameParam = SIGNATURE_PARAM + URLParser.PARAMETER_EQUALS + type.getTypeQualifiedName('.');
			return base + URLParser.PARAMETER_START + typeParam + URLParser.PARAMETER_DELIM + nameParam;
		}
		if(fElement.getElementType() == IJavaElement.FIELD)
		{
			IField field = (IField)fElement;
			String typeParam = TYPE_PARAM + URLParser.PARAMETER_EQUALS + FIELD_TYPE;
			String nameParam = SIGNATURE_PARAM + URLParser.PARAMETER_EQUALS + field.getDeclaringType().getTypeQualifiedName('.') + "." + field.getElementName();
			return base + URLParser.PARAMETER_START + typeParam + URLParser.PARAMETER_DELIM + nameParam;
		}
		if(fElement.getElementType() == IJavaElement.METHOD)
		{
			IMethod method = (IMethod)fElement;
			String signature = null;

			try
			{
				signature = Signature.toPortableString(method.getSignature(), method.getElementName(), false, false);
			} 
			catch (JavaModelException e) 
			{
				e.printStackTrace();
			}
			
			String typeParam = TYPE_PARAM + URLParser.PARAMETER_EQUALS + METHOD_TYPE;
			String nameParam = SIGNATURE_PARAM + URLParser.PARAMETER_EQUALS + method.getDeclaringType().getTypeQualifiedName('.') + "." + signature;
			return base + URLParser.PARAMETER_START + typeParam + URLParser.PARAMETER_DELIM + nameParam;
		}
		return 
			"";
	}
}
