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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.content.ToursContentPlugin;
import com.ibm.research.tours.content.elements.ResourceURLTourElement;
import com.ibm.research.tours.content.url.IURL;

public class ClassFileTextRegionURLTourElementDelegate implements IURLTourElementDelegate
{
	private JavaElementLabelProvider fJavaElementLabelProvider;
	private JavaElementLabelProvider fRuntimeJavaElementLabelProvider;
	private IClassFile fFile;
	private IRegion fTextRegion;
	private IRegion fLineRegion;
	private IMarker fMarker;
	private String fSnippet;

	public ClassFileTextRegionURLTourElementDelegate(IClassFile file, IRegion textRegion)
	{
		fFile = file;
		fTextRegion = textRegion;
		init();
	}

	private void init() 
	{
		IPackageFragmentRoot root= null;
		root = JavaModelUtil.getPackageFragmentRoot(fFile);
		
		IClasspathEntry entry = null;
		try 
		{
			entry = root.getRawClasspathEntry();
		} 
		catch (JavaModelException e) 
		{
			e.printStackTrace();
		}
		
		
		
		if (entry != null && entry.getEntryKind() == IClasspathEntry.CPE_VARIABLE) 
		{
			IPath path= entry.getPath().makeRelative();
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
		return new ResourceURLTourElement(fFile,fTextRegion);
	}

	public Image getImage() 
	{
		return ToursContentPlugin.getImage(fFile);
	}

	private String getLineRegionString() 
	{
		if(fLineRegion.getLength() == 0)
			return " (Line #" + fLineRegion.getOffset() + ")";
		else
			return " (Lines #" + fLineRegion.getOffset() + "-" + (fLineRegion.getOffset() + fLineRegion.getLength()+")");
	}

	public String getShortText() 
	{
		return getRuntimeJavaElementLabelProvider().getText(fFile) + getLineRegionString();
	}

	public String getText() 
	{
		return getJavaElementLabelProvider().getText(fFile) + getLineRegionString();
	}

	public void start() 
	{
//		try 
//		{
//			fMarker = fFile.createMarker("com.ibm.research.tours.content.tourtextmarker");
//			fMarker.setAttribute(IMarker.CHAR_START, fTextRegion.getOffset());
//			fMarker.setAttribute(IMarker.CHAR_END, fTextRegion.getOffset() + fTextRegion.getLength());
//		} 
//		catch (CoreException e) 
//		{
//		e.printStackTrace();
//		}
	}

	public void stop() 
	{
		try 
		{
			fMarker.delete();
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}

	public void transition() 
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		try 
		{
			IEditorPart part = IDE.openEditor(page, fMarker, true);

			if(part instanceof ITextEditor)
			{
				ITextEditor editor = (ITextEditor)part;
				editor.setHighlightRange(0,0,true);
			}
		} 
		catch (PartInitException e) 
		{
			e.printStackTrace();
		}
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
