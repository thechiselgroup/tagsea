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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.ibm.research.tours.ITour;

public abstract class TourTreeElement 
{
	private ITour fTour;
	
	public TourTreeElement(ITour tour) 
	{
		fTour = tour;
	}
	
	public ITour getTour() 
	{
		return fTour;
	}
	
	public Color getForeground()
	{
		return null;
	}
	
	public Color getBackground()
	{
		return null;
	}
	
	public Font getFont()
	{
		return null;
	}
	
	public abstract String getText();
	public abstract Image getImage();
}
