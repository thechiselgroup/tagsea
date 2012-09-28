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
package com.ibm.research.tours.runtime;

import java.util.ArrayList;
import java.util.List;

import com.ibm.research.tours.ITour;

public class DefaultTourRuntime implements ITourRuntime, ITourHarnessListener
{
	// The base tour execution harness
	private TourHarness fHarness;
	private TourNotesViewer fViewer;
	private TourRunner fTourRunner;
	private ITour fTour;
	private List<ITourRuntimeListener> fListeners = new ArrayList<ITourRuntimeListener>();
	
	public void run(ITour tour) 
	{
		fTour = tour;
		fTourRunner = new TourRunner(tour, getHarness(),getNotesViewer());
		fTourRunner.init();
		
		getHarness().open();
		getNotesViewer().open();
		
		for (ITourRuntimeListener listener : fListeners)
			listener.tourStarted(tour);
	}
	
	public TourRunner getTourRunner()
	{
		return fTourRunner;
	}
	
	public TourHarness getHarness()
	{
		if(fHarness == null)
		{
			fHarness = new TourHarness();
			fHarness.addTourHarnessListener(this);
		}
		return fHarness;
	}

	public TourNotesViewer getNotesViewer()
	{
		if(fViewer == null)
		{
			fViewer = new TourNotesViewer();
			//fHarness.addTourHarnessListener(this);
		}
		return fViewer;
	}
	
	/**
	 * The current harness was disposed
	 */
	public void disposed(TourHarness harness) 
	{
		fHarness = null;
		fViewer.dispose();
		fViewer = null;
		
		for (ITourRuntimeListener listener : fListeners)
			listener.tourEnded(fTour);
	}

	public void moveAbove() 
	{
		if(fHarness!=null)
			fHarness.moveAbove();
		
		if(fViewer!=null)
			fViewer.moveAbove();
	}

	public void addTourRuntimeListener(ITourRuntimeListener listener) {
		fListeners.add(listener);
	}

	public void removeTourRuntimeListener(ITourRuntimeListener listener) {
		fListeners.remove(listener);
	}
}
