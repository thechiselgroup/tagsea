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

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;

import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ToursPlugin;

public class TourRunner implements ITourControlListener, IRuntimeContext
{
	private TourHarness fTourHarness;
	private TourNotesViewer fViewer;
	private TourControl fTourController;
	private int fIndex;
	private ITour fTour;
	private ITourElement[] fElements;
	private Timer fTimer;
	private long startTime = 0;
	
	public TourRunner(ITour tour, TourHarness harness, TourNotesViewer viewer) 
	{
		fTour = tour;
		fViewer = viewer;
		fTourHarness = harness;
		
		// Cache the elements to prevent changes screwing up the running tour
		fElements = tour.getElements();
		// Index begins at -1, the first element will be 0
		fIndex = -1;
	}
	
	public TourControl getTourControl()
	{
		return fTourController;
	}
	
	public void init()
	{
		fTourController = new TourControl(fTourHarness.getTabFolder());
		fTourController.init();
		
		fTourController.addTourControlListener(this);
		
		if(fTour.getTitle().trim().length() > 0)
			fTourController.getTabItem().setText(fTour.getTitle());
		else
			fTourController.getTabItem().setText("Untitled");
		
		fTourController.getElementLabel().setText("Click next to start");
		int width = fTourController.getElementLabel().computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		
		// Approximate the size of the element label based on the longest element label and image
		// This will prevent cropping of text and images which looks dumb
		for(ITourElement element : fElements)
		{
			fTourController.getElementLabel().setImage(element.getImage());
			fTourController.getElementLabel().setText(element.getShortText());
			int newWidth = fTourController.getElementLabel().computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			
			if(newWidth > width)
				width = newWidth;
		}
		
		fTourController.getElementLabel().setImage(null);
		fTourController.getElementLabel().setText("Click next to start");

		GridData data = (GridData)fTourController.getElementLabel().getLayoutData();
		data.widthHint = width;
		fTourController.getElementLabel().setLayoutData(data);
		
		updateCounter();
		updateToolBar();
		fTourHarness.changed();
		// pack/layout and open
		//fTourController.open();
	}

	public void next() 
	{
		if(fTimer == null)
		{
			fTimer = new Timer();
			startTime = System.currentTimeMillis();
			
			fTimer.schedule(new TimerTask() 
			{
				@Override
				public void run() 
				{
					final long elapsedTime = System.currentTimeMillis() - startTime;
					Display.getDefault().syncExec(new Runnable() 
					{
						public void run() 
						{
							if(!fTourController.getTimeLabel().isDisposed())
								fTourController.getTimeLabel().setText(toFormattedTime(elapsedTime));
						}
					});
				}
			},0,500);
		}

		stopCurrentElement();
		
		saveNoteChanges();  // this must be called BEFORE index is updated
		
		// Jump to the next index
		fIndex = fIndex + 1;
		
		updateElement();
		updateCounter();
		updateToolBar();
		
		runCurrentElement();
	}

	private void stopCurrentElement() 
	{
		if(fIndex >= 0 && fIndex < fElements.length)
			// Stop the executing element
			fElements[fIndex].stop();
	}
	
	private void runCurrentElement() 
	{
		if(fIndex >= 0 && fIndex < fElements.length)
		{
			// Start the element
			fElements[fIndex].start();
			
			// Perfrom the transition i.e. do whatever it does
			fElements[fIndex].transition();
		}
	}
	
	public void previous() 
	{
		stopCurrentElement();

		saveNoteChanges();  // this must be called BEFORE index is updated
		
		// Jump back to the previous index
		fIndex = fIndex - 1;
		
		updateElement();
		updateCounter();
		updateToolBar();
		
		runCurrentElement();
	}

	public void stop() 
	{
		if(fTimer!=null)
			fTimer.cancel();
		
		stopCurrentElement();

		saveNoteChanges();  // this must be called BEFORE index is updated
		
		fTourController.dispose();
		// This tells the harness that a tab was added or removed so that it 
		// can tell when to dispose of itself
		fTourHarness.changed();
	}
	
	private void updateCounter()
	{
		fTourController.getCounterLabel().setText(getCurrentElementLabel()+"/"+getNumberOfElementsLabel());
	}
	
	private void updateElement()
	{
		fTourController.getElementLabel().setText(fElements[fIndex].getShortText());
		fTourController.getElementLabel().setImage(fElements[fIndex].getImage());
		fViewer.getStyledText().setText(fElements[fIndex].getNotes());
		if ( fElements[fIndex].getNotes()!=null && !fElements[fIndex].getNotes().trim().equals("") )
			fViewer.setGripBackground(Display.getDefault().getSystemColor(SWT.COLOR_YELLOW));
		else
			fViewer.setGripBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	}
	
	private void updateToolBar() 
	{
		if(fIndex <= 0)
			fTourController.getPrevious().setEnabled(false);
		else
			fTourController.getPrevious().setEnabled(true);
		
		if(fIndex >= fElements.length - 1)
			fTourController.getNext().setEnabled(false);
		else
			fTourController.getNext().setEnabled(true);
	}
	
	private String getCurrentElementLabel()
	{
		return Integer.toString(fIndex + 1);	
	}
	
	private String getNumberOfElementsLabel()
	{
		return Integer.toString(fElements.length);	
	}
	
	private String toFormattedTime(long elapsedTime)
	{
		long seconds = elapsedTime/1000;
		long minutes = seconds/60;
		long remainder = seconds %60;
		
		String minuteString = Long.toString(minutes);
		if(minutes<10)
			minuteString = "0" + minuteString;
		
		String secondString = Long.toString(remainder);
		if(remainder<10)
			secondString = "0" + secondString;
		
		return minuteString + ":" + secondString;
	}
	
	// must call before index is changed
	private void saveNoteChanges()
	{
		if ( fIndex<0 || fIndex>=fElements.length )
			return;
		
		String viewerNoteText = fViewer.getStyledText().getText(),
			   noteText = fElements[fIndex].getNotes();
		
		if ( noteText!=null && viewerNoteText!=null && !noteText.trim().equals(viewerNoteText.trim()) )
		{
			fElements[fIndex].setNotes(viewerNoteText);
			
			IFile file = fTour.getFile();
			if ( file!=null )
			{
				try {
					fTour.write(file);
				} catch (Exception e) {
					ToursPlugin.log("error while trying to save updated note at index=" + fIndex + " in tour=" + fTour.getTitle() + " text=" + viewerNoteText, e);
				}
			}
		}
	}
}

