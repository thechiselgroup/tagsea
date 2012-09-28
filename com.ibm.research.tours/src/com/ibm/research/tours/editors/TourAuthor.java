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

import org.eclipse.swt.graphics.Image;

import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ITourAuthor;
import com.ibm.research.tours.ToursPlugin;

public class TourAuthor extends TourTreeElement implements ITourAuthor 
{
	public TourAuthor(ITour tour) 
	{
		super(tour);
	}

	@Override
	public Image getImage() 
	{
		return ToursPlugin.getDefault().getImageRegistry().get(ToursPlugin.IMG_AUTHOR);
	}

	@Override
	public String getText() 
	{
		String author = getTour().getAuthor();
		
		if(author.trim().length() > 0)
			return getTour().getAuthor();
		else
			return "Unknown Author";
	}
}
