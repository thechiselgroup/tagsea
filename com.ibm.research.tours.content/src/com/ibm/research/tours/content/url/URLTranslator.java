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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import com.ibm.research.tours.content.SlideRange;
import com.ibm.research.tours.content.url.parser.URLInfo;
import com.ibm.research.tours.content.url.parser.URLParameter;
import com.ibm.research.tours.content.url.parser.URLParser;

public class URLTranslator 
{
	private static final int PARAM_POWERPOINT_SLIDE_RANGE = 1;
	private static final int PARAM_TEXT_RANGE   = 2;
	private static final int PARAM_JAVA_ELEMENT = 3;
	private static final int PARAM_NONE = 4;
	private static final int PARAM_UNKNOWN = 5;

	/**
	 * @param urlString
	 * @return
	 */
	public static IURL getURL(String urlString)
	{
		// Parse the URL
		URLInfo info = URLParser.parse(urlString);

		if(info != null)
		{
			// http
			if(info.getPreamble().equals(URLParser.HTTP_PREAMBLE))
			{
				return new HttpURL(info.getBody());
			}
			
			IPath path = Path.fromPortableString(info.getBody());
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);

			if(resource != null)
			{		
				if(resource.getType() == IResource.FILE)
				{
					IFile file = (IFile)resource;

					if(file.getFileExtension().equalsIgnoreCase("java"))
					{
						return getURLFromJavaFile(info,file);
					}
					else if(file.getFileExtension().equalsIgnoreCase("ppt"))
					{
						return getURLFromPowerPointFile(info,file);
					}
					else
					{
						return getURLFromFile(info,file);
					}
				}
				else 
					return new ResourceURL(resource);
			}
		}

		return null;
	}


	private static IURL getURLFromFile(URLInfo info, IFile file) 
	{
		int param = getParamaterType(info);
		IURL url= null;

		switch (param) 
		{
		case PARAM_NONE:
		case PARAM_UNKNOWN:
		case PARAM_JAVA_ELEMENT:
		case PARAM_POWERPOINT_SLIDE_RANGE:	
			url = new ResourceURL(file);
			break;

		case PARAM_TEXT_RANGE:
			IRegion region = getTextRegion(info);
			url = new TextRegionURL(file,region);
			break;

		default:
			break;
		}

		return url;
	}

	private static IURL getURLFromPowerPointFile(URLInfo info, IFile file) 
	{
		PowerPointURL ppUrl = new PowerPointURL(file);
		SlideRange range = getSlideRange(info);

		if(range!=null)
			ppUrl.setSlideRange(range);

		return ppUrl;
	}

	private static SlideRange getSlideRange(URLInfo info) 
	{
		URLParameter[] params = info.getParameters();

		if(contains(PowerPointURL.START_PARAM,params) && contains(PowerPointURL.START_PARAM,params))
		{
			int start = 0;
			int end = 0;

			String startString = getParameterValue(PowerPointURL.START_PARAM,params);
			String endString = getParameterValue(PowerPointURL.END_PARAM,params);

			try 
			{
				start = Integer.parseInt(startString);
				end = Integer.parseInt(endString);
			} 
			catch (NumberFormatException e) 
			{
				e.printStackTrace();
				return null;
			}

			return new SlideRange(start,end);
		}

		return null;
	}


	private static IURL getURLFromJavaFile(URLInfo info, IFile file) 
	{
		int param = getParamaterType(info);
		IURL url= null;

		switch (param) 
		{
		case PARAM_NONE:
		case PARAM_UNKNOWN:
			url = new ResourceURL(file);
			break;

		case PARAM_TEXT_RANGE:
			IRegion region = getTextRegion(info);
			url = new TextRegionURL(file,region);
			break;

		case PARAM_JAVA_ELEMENT:
			IJavaElement element = getJavaElement(file,info);
			if(element != null)
				url = new JavaURL(element);
			break;

		default:
			break;
		}

		return url;
	}


	private static IJavaElement getJavaElement(IFile file, URLInfo info) 
	{
		URLParameter[] params = info.getParameters();

		if(contains(JavaURL.TYPE_PARAM,params) && contains(JavaURL.SIGNATURE_PARAM,params))
		{
			ICompilationUnit unit = JavaCore.createCompilationUnitFrom(file);

			String typeString = getParameterValue(JavaURL.TYPE_PARAM,params);
			String signString = getParameterValue(JavaURL.SIGNATURE_PARAM,params);

			if(typeString.equalsIgnoreCase(JavaURL.TYPE_TYPE))
			{
				try 
				{
					String typeName = signString;
					IType[] types = unit.getTypes();

					IType type = getType(types, typeName);

					if(type !=null)
						return type;
				} 
				catch (JavaModelException e) 
				{
					e.printStackTrace();
				}
			}
			else if(typeString.equalsIgnoreCase(JavaURL.FIELD_TYPE))
			{
				try 
				{
					String qualifiedFieldName = signString;
					int lastDot = qualifiedFieldName.lastIndexOf('.');

					if(lastDot != -1)
					{
						String fieldName = qualifiedFieldName.substring(lastDot + 1, qualifiedFieldName.length());
						String typeName = qualifiedFieldName.substring(0, lastDot);

						IType[] types = unit.getTypes();
						IType type = getType(types, typeName);

						if(type !=null)
						{
							IField field = type.getField(fieldName);

							if(field != null)
								return field;
						}
					}
				} 
				catch (JavaModelException e) 
				{
					e.printStackTrace();
				}
			}
			else if(typeString.equalsIgnoreCase(JavaURL.METHOD_TYPE))
			{
				try 
				{
					String qualifiedMethodSignature = signString;
					int firstBracket = qualifiedMethodSignature.indexOf('(');

					if(firstBracket != -1)
					{
						String firstPart = qualifiedMethodSignature.substring(0, firstBracket);
						int lastDot = firstPart.lastIndexOf('.');
						String typeName = firstPart.substring(0, lastDot);
						String methodSignature = qualifiedMethodSignature.substring(lastDot + 1,qualifiedMethodSignature.length());

						IType[] types = unit.getTypes();
						IType type = getType(types, typeName);

						if(type !=null)
						{
							IMethod[] methods = type.getMethods();

							for(IMethod method : methods)
							{
								String signature = null;

								try
								{
									signature = Signature.toPortableString(method.getSignature(), method.getElementName(), false, false);
								} 
								catch (JavaModelException e) 
								{
									e.printStackTrace();
								}

								if(signature!=null)
								{
									if(signature.equals(methodSignature))
										return method;
								}
							}
						}
					}
				} 
				catch (JavaModelException e) 
				{
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private static IType getType(IType[] types, String typeName) throws JavaModelException
	{
		for(IType type : types)
			if(type.getTypeQualifiedName('.').equals(typeName))
				return type;
			else
				return getType(type.getTypes(), typeName);

		return null;
	}

	private static IRegion getTextRegion(URLInfo info) 
	{
		URLParameter[] params = info.getParameters();

		if(contains(TextRegionURL.OFFSET,params) && contains(TextRegionURL.LENGTH,params))
		{
			int offset = 0;
			int length = 0;

			String offsetString = getParameterValue(TextRegionURL.OFFSET,params);
			String lengthString = getParameterValue(TextRegionURL.LENGTH,params);

			try 
			{
				offset = Integer.parseInt(offsetString);
				length = Integer.parseInt(lengthString);
			} 
			catch (NumberFormatException e) 
			{
				e.printStackTrace();
			}

			return new Region(offset,length);
		}

		return null;
	}


	private static int getParamaterType(URLInfo info)
	{
		URLParameter[] params = info.getParameters();

		if(params.length == 0)
			return PARAM_NONE;

		if(contains(TextRegionURL.OFFSET,params) && contains(TextRegionURL.LENGTH,params))
			return PARAM_TEXT_RANGE;

		if(contains(PowerPointURL.START_PARAM,params) && contains(PowerPointURL.END_PARAM,params))
			return PARAM_POWERPOINT_SLIDE_RANGE;

		if(contains(JavaURL.TYPE_PARAM,params) && contains(JavaURL.SIGNATURE_PARAM,params))
			return PARAM_JAVA_ELEMENT;

		return PARAM_UNKNOWN;
	}


	public static boolean contains(String name,URLParameter[] parameters)
	{
		for(URLParameter parameter : parameters)
		{
			if(parameter.getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public static String getParameterValue(String name,URLParameter[] parameters)
	{
		for(URLParameter parameter : parameters)
		{
			if(parameter.getName().equalsIgnoreCase(name))
				return parameter.getValue();
		}
		return null;
	}
}
