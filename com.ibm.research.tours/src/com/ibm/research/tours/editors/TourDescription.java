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
import com.ibm.research.tours.ITourDescription;
import com.ibm.research.tours.ToursPlugin;

public class TourDescription extends TourTreeElement implements ITourDescription
{
	public TourDescription(ITour tour) 
	{
		super(tour);
	}
	
	public String getDescription()
	{
		return getTour().getDescription();
	}

	@Override
	public Image getImage() 
	{
		return ToursPlugin.getDefault().getImageRegistry().get(ToursPlugin.IMG_DESCRIPTION);
	}

	@Override
	public String getText() 
	{
		if(getDescription().trim().length() > 0)
			return getDescription();
		else
			return "No description";
	}
}
