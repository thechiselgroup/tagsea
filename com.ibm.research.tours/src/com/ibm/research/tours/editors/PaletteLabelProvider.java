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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ibm.research.tours.IPaletteEntry;
import com.ibm.research.tours.adapters.TableLabelProviderAdapter;

// implements ILabelProvider is necessary for the viewer sorter to work
public class PaletteLabelProvider extends TableLabelProviderAdapter implements ILabelProvider
{
	public Image getColumnImage(Object element,int columnIndex) 
	{		
		return getImage(element);
	}

	public String getColumnText(Object element,int columnIndex) 
	{		
		return getText(element);
	}

	public Image getImage(Object element) 
	{
		if(element instanceof IPaletteEntry)
		{
			return ((IPaletteEntry)element).getImage();
		}
		
		return null;
	}

	public String getText(Object element) 
	{
		if(element instanceof IPaletteEntry)
		{
			return ((IPaletteEntry)element).getText();
		}
		
		return null;
	}
}