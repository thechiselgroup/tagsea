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
package com.ibm.research.tours.serializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import com.ibm.research.tours.ITimeLimit;
import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.ITourElementListener;
import com.ibm.research.tours.ITourListener;
import com.ibm.research.tours.ToursPlugin;

class XMLTour implements ITour
{
	private class TourElementListener implements ITourElementListener
	{
		public void elementChanged(ITourElement element) 
		{
			fireTourChangedEvent();
		}
	}
	
	private TourElementListener fTourElementListener = new TourElementListener();
	private String fTitle;
	private String fDescription;
	private String fAuthor;	
	private Date fDate;
	private ITimeLimit fTimeLimit;
	private List<ITourElement> fElements; 
	private List<ITourListener> fListeners; 
	private IFile fFile;
	private String fID;
	
	public XMLTour(String title, String description, String author) {
		this.fTitle = title;
		this.fDescription = description;
		this.fAuthor = author;
		setID(generateUniqueID(title));
	}
	
	public String getTitle() 
	{
		if(fTitle == null)
			fTitle = new String();
		
		return fTitle;
	}

	public void setTitle(String title) 
	{
		if(!getTitle().equals(title))
		{
			fTitle = title;
			fireTourChangedEvent();
		}
	}
	
	public String getDescription() 
	{
		if(fDescription == null)
			fDescription = new String();
		
		return fDescription;
	}

	public void setDescription(String description) 
	{
		fDescription = description;
		fireTourChangedEvent();
	}

	public String getAuthor() 
	{
		if(fAuthor == null)
			fAuthor = new String();
		
		return fAuthor;
	}

	public void setAuthor(String author) 
	{
		fAuthor = author;
		fireTourChangedEvent();
	}

	public Date getDate() 
	{
		return fDate;
	}
	
	public void setDate(Date date)
	{
		fDate = date;
		fireTourChangedEvent();
	}
	
	public void addElements(int index, ITourElement[] elements) 
	{
		if(elements == null || (elements != null &&elements.length == 0))
			return;
		
		if(index < 0 || index > getElementCount())
			return;
		
		final Vector<ITourElement> added = new Vector<ITourElement>();
		int insertionIndex = index;
		
		for(ITourElement element : elements)
		{
			if(element!=null)
			{
				// If not already in the tour
				if(!getElementList().contains(element))
				{
					getElementList().add(insertionIndex,element);
					element.addTourElementListener(fTourElementListener);
					added.add(element);
					insertionIndex = insertionIndex + 1;
				}
			}
		}
		
		if(!added.isEmpty())
		{
			ToursPlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
			{
				public void run() 
				{
					for(ITourListener listener : getListeners())
						listener.elementsAdded(XMLTour.this,added.toArray(new ITourElement[0]));
				}
			});
		}
	}

	public void addElements(ITourElement[] elements) 
	{
		if(elements == null || (elements != null &&elements.length == 0))
			return;
		
		final Vector<ITourElement> added = new Vector<ITourElement>();
		
		for(ITourElement element : elements)
		{
			// If not already in the tour
			if(!getElementList().contains(element))
			{
				getElementList().add(element);
				element.addTourElementListener(fTourElementListener);
				added.add(element);
			}
		}
		
		if(!added.isEmpty())
		{
			ToursPlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
			{
				public void run() 
				{
					for(ITourListener listener : getListeners())
						listener.elementsAdded(XMLTour.this,added.toArray(new ITourElement[0]));
				}
			});
		}
	}

	public ITourElement getElement(int index) 
	{
		return getElementList().get(index);
	}

	public void removeElements(ITourElement[] elements) 
	{
		if(elements == null || (elements != null &&elements.length == 0))
			return;
		
		final Vector<ITourElement> removed = new Vector<ITourElement>();
		
		for(ITourElement element : elements)
		{
			// check and remove
			if(getElementList().remove(element))
			{
				element.removeTourElementListener(fTourElementListener);
				removed.add(element);
			}
		}
		
		if(!removed.isEmpty())
		{
			ToursPlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
			{
				public void run() 
				{
					for(ITourListener listener : getListeners())
						listener.elementsRemoved(XMLTour.this,removed.toArray(new ITourElement[0]));
				}
			});
		}
	}

	public ITourElement removeElement(int index)
	{
		final ITourElement element = getElementList().remove(index);

		if(element!=null)
		{
			element.removeTourElementListener(fTourElementListener);
			ToursPlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
			{
				public void run() 
				{
					for(ITourListener listener : getListeners())
						listener.elementsRemoved(XMLTour.this,new ITourElement[]{element});
				}
			});
		}
		
		return element;
	}

	protected List<ITourElement> getElementList() 
	{
		if(fElements == null)
			fElements = new ArrayList<ITourElement>();
		
		return fElements;
	}
	
	protected List<ITourListener> getListeners() 
	{
		if(fListeners == null)
			fListeners = new ArrayList<ITourListener>();
		
		return fListeners;
	}

	public ITourElement[] getElements() 
	{
		return getElementList().toArray(new ITourElement[0]);
	}

	public int getElementCount() 
	{
		return getElementList().size();
	}
	
	public void fireTourChangedEvent()
	{
		ToursPlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
		{
			public void run() 
			{
				for(ITourListener listener: getListeners())
				{
					listener.tourChanged(XMLTour.this);
				}
			}
		});
	}
	
	public void addTourListener(ITourListener listener, boolean getCatchupEvents) 
	{
		if(listener!=null && !getListeners().contains(listener))
		{
			getListeners().add(listener);
		
			if(getElementCount() > 0 && getCatchupEvents)
			{
				ToursPlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
				{
					public void run() 
					{
						for(ITourListener listener : getListeners())
							listener.elementsAdded(XMLTour.this,getElementList().toArray(new ITourElement[0]));
					}
				});	
			}
		}
	}
	
	public void removeTourListener(ITourListener listener) 
	{
		getListeners().remove(listener);
	}

	public boolean contins(ITourElement element) 
	{
		return getElementList().contains(element);
	}
	
	public int getIndex(ITourElement element) 
	{
		return getElementList().indexOf(element);
	}
	
	public ITourElement setElement(int index, ITourElement element) 
	{
		return getElementList().set(index, element);
	}
	
	// Write the tour to the file specified
	public void write(IFile file) throws Exception
	{
		fFile = file;
		
		TourSerializer.write(file,this);
	}
	
	// Read tour information from the file specified
	public void read(IFile file) throws CoreException {	
		fFile = file;
		
		// Ensure everything comes in fresh
		setTitle(null);
		setDescription(null);
		setAuthor(null);
		setTimeLimit(null);
		setDate(null);
		clear();
		TourSerializer.read(file,this);
	}

	public void clear() 
	{
		removeElements(getElementList().toArray(new ITourElement[0]));
	}

	
	public void setTimeLimit(ITimeLimit limit) 
	{
		if(fTimeLimit == null && limit !=null)
		{
			if(limit !=null)
			{
				fTimeLimit = limit;
				fireTourChangedEvent();
			}
		}	
		else if(fTimeLimit!=null && !fTimeLimit.equals(limit))
		{
			fTimeLimit = limit;
			fireTourChangedEvent();
		}
	}

	public ITimeLimit getTimeLimit() 
	{
		return fTimeLimit;
	}

	public IFile getFile() {
		return fFile;
	}

	public void setID(String uniqueID) {
		this.fID = uniqueID;
	}
	
	public String getID() {
		return fID;
	}
	
	/**
	 * 
	 * @return
	 */
	private static String generateUniqueID(String title) {
		String titleID = title.replaceAll("\\s+", "");
		titleID = titleID.replaceAll("\\.", "");
		titleID = titleID.replaceAll("\\(", "");
		titleID = titleID.replaceAll("\\)", "");
		return titleID + "." + System.currentTimeMillis();
	}
}
