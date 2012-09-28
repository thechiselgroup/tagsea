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
package com.ibm.research.tours.content.url.delegates;

import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.content.ToursContentPlugin;
import com.ibm.research.tours.content.elements.ResourceURLTourElement;
import com.ibm.research.tours.content.url.IURL;

public class ClassFileURLTourElementDelegate implements IURLTourElementDelegate
{
	private JavaElementLabelProvider fJavaElementLabelProvider;
	private JavaElementLabelProvider fRuntimeJavaElementLabelProvider;
	private IClassFile fFile;

	public ClassFileURLTourElementDelegate(IClassFile file)
	{
		fFile = file;
		init();
	}

	private void init() 
	{
		IPackageFragmentRoot root= null;
		root = JavaModelUtil.getPackageFragmentRoot(fFile);

		String[] entries= JavaCore.getClasspathVariableNames();
		ArrayList elements= new ArrayList(entries.length);

		for (int i= 0; i < entries.length; i++) 
		{
			String name= entries[i];
			IPath entryPath= JavaCore.getClasspathVariable(name);
			IPath fullPath = root.getPath();
			
			if (entryPath != null) 
			{
				if(entryPath.isPrefixOf(fullPath))
				{
					int matchingSegments = fullPath.matchingFirstSegments(entryPath);
					int numSegments = fullPath.segmentCount();
					String[] segments = fullPath.segments();
					String path = name + Path.SEPARATOR;
					
					for(int j = matchingSegments; j < numSegments; j++)
					{
						path = path + segments[j];
						
						if(j != numSegments - 1)
							path = path + Path.SEPARATOR;
					}	
					break;
				}
			}
		}

		
		
		
	}

	private JavaElementLabelProvider getJavaElementLabelProvider()
	{
		if(fJavaElementLabelProvider == null)
			fJavaElementLabelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_QUALIFIED|
					JavaElementLabelProvider.SHOW_SMALL_ICONS|
					JavaElementLabelProvider.SHOW_PARAMETERS);
		return fJavaElementLabelProvider;
	}

	private JavaElementLabelProvider getRuntimeJavaElementLabelProvider()
	{
		if(fRuntimeJavaElementLabelProvider == null)
			fRuntimeJavaElementLabelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_SMALL_ICONS|
					JavaElementLabelProvider.SHOW_PARAMETERS);
		return fRuntimeJavaElementLabelProvider;
	}

	public ITourElement createClone() 
	{ 
		return new ResourceURLTourElement(fFile);
	}

	public Image getImage() 
	{
		return ToursContentPlugin.getImage(fFile);
	}


	public String getShortText() 
	{
		return getRuntimeJavaElementLabelProvider().getText(fFile);
	}

	public String getText() 
	{
		return getJavaElementLabelProvider().getText(fFile);
	}

	public void start() 
	{
//		try 
//		{
//		fMarker = fFile.createMarker("com.ibm.research.tours.content.tourtextmarker");
//		fMarker.setAttribute(IMarker.CHAR_START, fTextRegion.getOffset());
//		fMarker.setAttribute(IMarker.CHAR_END, fTextRegion.getOffset() + fTextRegion.getLength());
//		} 
//		catch (CoreException e) 
//		{
//		e.printStackTrace();
//		}
	}

	public void stop() 
	{
	}

	public void transition() 
	{

	}

	public IURL getUrl() 
	{
		return null;
	}

	/**
	 * Returns the package fragment root of this file.
	 */
	private IPackageFragmentRoot getPackageFragmentRoot(IClassFile file) {

		IJavaElement element= file.getParent();
		while (element != null && element.getElementType() != IJavaElement.PACKAGE_FRAGMENT_ROOT)
			element= element.getParent();

		return (IPackageFragmentRoot) element;
	}
}
