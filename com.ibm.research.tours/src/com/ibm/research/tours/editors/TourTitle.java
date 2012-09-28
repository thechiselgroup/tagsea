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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ITourTitle;
import com.ibm.research.tours.ToursPlugin;

public class TourTitle extends TourTreeElement implements ITourTitle
{
	private Font fFont;
	
	public TourTitle(ITour tour) 
	{
		super(tour);
	}
	
	public String getTitle()
	{
		return getTour().getTitle();
	}

	@Override
	public Image getImage() 
	{
		return ToursPlugin.getDefault().getImageRegistry().get(ToursPlugin.IMG_TITLE);
	}

	@Override
	public String getText() 
	{
		if(getTitle().trim().length() > 0)
			return getTitle();
		else
			return "Untitled Tour";
	}
	
	@Override
	public Font getFont() 
	{
		if(fFont == null)
			fFont = JFaceResources.getFontRegistry().getBold("org.eclipse.jface.viewerfont");

		return fFont;
	}
}
