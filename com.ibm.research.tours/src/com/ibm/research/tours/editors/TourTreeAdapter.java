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

import com.ibm.research.tours.ITour;

public class TourTreeAdapter 
{
	private ITour fTour;
	private TourElements fTourElements;
	private TourTitle fTourTitle;
	private TourAuthor fTourAuthor;
	private TourDescription fTourDescription;
	
	public TourTreeAdapter(ITour tour) 
	{
		fTour = tour;
		fTourTitle = new TourTitle(fTour);
		fTourDescription = new TourDescription(fTour);
		fTourElements = new TourElements(fTour);
		fTourAuthor = new TourAuthor(fTour); 
	}

	public TourElements getTourElements() 
	{
		return fTourElements;
	}

	public TourTitle getTourTitle() 
	{
		return fTourTitle;
	}

	public TourAuthor getTourAuthor() 
	{
		return fTourAuthor;
	}
	
	public TourDescription getTourDescription() 
	{
		return fTourDescription;
	}
}
