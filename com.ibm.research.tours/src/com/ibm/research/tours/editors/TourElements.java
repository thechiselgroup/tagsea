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

import com.ibm.research.tours.ITimeLimit;
import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ITourContents;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ToursPlugin;

public class TourElements extends TourTreeElement  implements ITourContents
{
	private Font fFont;
	
	public TourElements(ITour tour) 
	{
		super(tour);
	}
	
	public ITourElement[] getElements()
	{
		return getTour().getElements();
	}

	public int getElementCount()
	{
		return getTour().getElementCount();
	}
	
	@Override
	public Image getImage() 
	{
		return ToursPlugin.getDefault().getImageRegistry().get(ToursPlugin.IMG_ELEMENTS);
	}

	@Override
	public String getText() 
	{
		ITimeLimit timeLimit = getTour().getTimeLimit();
		
		String timeLimitStr = "";
		if ( timeLimit!=null )
		{
			timeLimitStr += " [" + timeLimit.getMinutes() + " min " + timeLimit.getSeconds() + " sec]";
		}
		
		return "Contents" + " (" + getTour().getElementCount() + ")" + timeLimitStr;
	}
	
	@Override
	public Font getFont() 
	{
		if(fFont == null)
			fFont = JFaceResources.getFontRegistry().getBold("org.eclipse.jface.viewerfont");

		return fFont;
	}
}
