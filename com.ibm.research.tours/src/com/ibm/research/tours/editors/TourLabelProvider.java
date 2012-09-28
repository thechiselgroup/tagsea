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
package com.ibm.research.tours.editors;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.adapters.LabelProviderAdapter;

public class TourLabelProvider extends LabelProviderAdapter implements IColorProvider, IFontProvider
{
	public Image getImage(Object element) 
	{
//		if(element instanceof TourTitle)
//			return ((TourTitle)element).getImage();
//		else if(element instanceof TourAuthor)
//			return ((TourAuthor)element).getImage();
//		else if(element instanceof TourDescription)
//			return ((TourDescription)element).getImage();
//		else 
		if(element instanceof TourElements)
			return ((TourElements)element).getImage();
		else if(element instanceof ITourElement)
			return ((ITourElement)element).getImage();
		return null;
	}

	public String getText(Object element) 
	{
//		if(element instanceof TourTitle)
//			return ((TourTitle)element).getText();
//		else if(element instanceof TourAuthor)
//			return ((TourAuthor)element).getText();
//		else if(element instanceof TourDescription)
//			return ((TourDescription)element).getText();
//		else 
		if(element instanceof TourElements)
			return ((TourElements)element).getText();
		else if(element instanceof ITourElement)
			return ((ITourElement)element).getText();
		return "";
	}

	public Color getBackground(Object element) 
	{
//		if(element instanceof TourTitle)
//			return ((TourTitle)element).getBackground();
//		else if(element instanceof TourAuthor)
//			return ((TourAuthor)element).getBackground();
//		else if(element instanceof TourDescription)
//			return ((TourDescription)element).getBackground();
//		else 
		if(element instanceof TourElements)
			return ((TourElements)element).getBackground();
		else if(element instanceof ITourElement)
		{
			ITourElement tourElement = (ITourElement) element;
			Color background = tourElement.getBackground();
			if ( background==null )
			{
				// unless the tour element has its own color, color based on notes
				
				String notes = tourElement.getNotes();
				if ( notes!=null && notes.trim().length()>0 )
					return Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
			}
			
			return background;
		}
		
		return null;
	}

	public Color getForeground(Object element) 
	{
//		if(element instanceof TourTitle)
//			return ((TourTitle)element).getForeground();
//		else if(element instanceof TourAuthor)
//			return ((TourAuthor)element).getForeground();
//		else if(element instanceof TourDescription)
//			return ((TourDescription)element).getForeground();
//		else 
		if(element instanceof TourElements)
			return ((TourElements)element).getForeground();
		else if(element instanceof ITourElement)
			return ((ITourElement)element).getForeground();
		return null;
	}

	public Font getFont(Object element) 
	{
//		if(element instanceof TourTitle)
//			return ((TourTitle)element).getFont();
//		else if(element instanceof TourAuthor)
//			return ((TourAuthor)element).getFont();
//		else if(element instanceof TourDescription)
//			return ((TourDescription)element).getFont();
//		else 
		if(element instanceof TourElements)
			return ((TourElements)element).getFont();
		else if(element instanceof ITourElement)
			return ((ITourElement)element).getFont();
		return null;
	}
}
