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
package com.ibm.research.tours;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.IMemento;

public abstract class AbstractTourElement implements ITourElement
{
	public static final String NOTES_ELEMENT       = "notes";
	public static final String TIME_LIMIT_ELEMENT  = "time-limit";
	public static final String MINUTES_ELEMENT     = "minutes";
	public static final String SECONDS_ELEMENT     = "seconds";
	public static final String TRANSITION_ELEMENT  = "transition";

	public static final String START_ON_CLICK_VALUE      = "onClick";
	public static final String START_WITH_PREVIOUS_VALUE  = "withPrevious";
	public static final String START_AFTER_PREVIOUS_VALUE  = "afterPrevious";
	
	private List<ITourElementListener> fListeners;
	private String fNotes;
	private ITimeLimit fTimeLimit;
	private int fTransition = START_ON_CLICK;
	
	public AbstractTourElement() 
	{
	}
	
	public int getTransition() 
	{
		return fTransition;
	}
	
	public void setTransition(int transition) 
	{
		if(transition == START_ON_CLICK || transition == START_WITH_PREVIOUS || transition == START_AFTER_PREVIOUS)
			if(fTransition!=transition)
			{
				fTransition = transition;
				fireElementChangedEvent();
			}
	}
	
	public String getNotes() 
	{
		if(fNotes == null)
			fNotes = new String();
		
		return fNotes;
	}
	
	public void setNotes(String notes) 
	{
		if(!getNotes().equals(notes))
		{
			fNotes = notes;
			fireElementChangedEvent();
		}
	}
	
	public void setTimeLimit(ITimeLimit limit) 
	{
		if(fTimeLimit == null && limit !=null)
		{
			if(limit !=null)
			{
				fTimeLimit = limit;
				fireElementChangedEvent();
			}
		}	
		else if(!fTimeLimit.equals(limit))
		{
			fTimeLimit = limit;
			fireElementChangedEvent();
		}
	}

	public ITimeLimit getTimeLimit() 
	{
		return fTimeLimit;
	}
	
	public void addTourElementListener(ITourElementListener listener) 
	{
		if(!getListeners().contains(listener))
			getListeners().add(listener);
	}
	
	public void removeTourElementListener(ITourElementListener listener) 
	{
		getListeners().remove(listener);
	}

	protected List<ITourElementListener> getListeners() 
	{
		if(fListeners == null)
			fListeners = new ArrayList<ITourElementListener>();
		
		return fListeners;
	}
	
	protected void fireElementChangedEvent()
	{
		ToursPlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
		{
			public void run() 
			{
				for(ITourElementListener listener : getListeners())
					listener.elementChanged(AbstractTourElement.this);
			}
		});
	}
	
	public Color getBackground() 
	{
		return null;
	}
	
	public Color getForeground() 
	{
		return null;
	}
	
	public Font getFont() 
	{
		return null;
	}
	
	public void load(IMemento memento) 
	{
		IMemento notesMemento = memento.getChild(NOTES_ELEMENT);

		if(notesMemento!=null)
		{
			String notes = notesMemento.getTextData();

			if(notes!=null)
			{
				setNotes(notes);
			}
		}

		IMemento timeLimitMemento = memento.getChild(TIME_LIMIT_ELEMENT);

		if(timeLimitMemento!=null)
		{
			IMemento minutesMemento = timeLimitMemento.getChild(MINUTES_ELEMENT);
			IMemento secondsMemento = timeLimitMemento.getChild(SECONDS_ELEMENT);

			int minutes = -1;
			int seconds = -1;

			if(minutesMemento!=null)
			{
				String minuteString = minutesMemento.getTextData();

				if(minuteString !=null)
				{
					try 
					{
						minutes = Integer.parseInt(minuteString);
					} 
					catch (NumberFormatException e) 
					{
						e.printStackTrace();
					}
				}
			}

			if(secondsMemento!=null)
			{
				String secondString = secondsMemento.getTextData();

				if(secondString !=null)
				{
					try 
					{
						seconds = Integer.parseInt(secondString);
					} 
					catch (NumberFormatException e) 
					{
						e.printStackTrace();
					}
				}
			}
			
			if(minutes>0 || seconds > 0)
			{
				fTimeLimit = new TimeLimit();
				
				if(minutes > 0)
					fTimeLimit.setMinutes(minutes);
				
				if(seconds > 0)
					fTimeLimit.setSeconds(minutes);
			}
		}

		String notes = notesMemento.getTextData();

		if(notes!=null)
		{
			setNotes(notes);
		}
		
		IMemento transitionMemento = memento.getChild(TRANSITION_ELEMENT);
		
		if(transitionMemento != null)
		{
			String transitionData = transitionMemento.getTextData();
			
			if(transitionData != null)
			{
				if(transitionData.trim().equalsIgnoreCase(START_ON_CLICK_VALUE))
					setTransition(START_ON_CLICK);
				else if(transitionData.trim().equalsIgnoreCase(START_WITH_PREVIOUS_VALUE))
					setTransition(START_WITH_PREVIOUS);
				else if(transitionData.trim().equalsIgnoreCase(START_AFTER_PREVIOUS_VALUE))
					setTransition(START_AFTER_PREVIOUS);
			}
		}
	}

	public void save(IMemento memento) 
	{
		IMemento notesMemento = memento.createChild(NOTES_ELEMENT);
		notesMemento.putTextData(getNotes());
		
		if(fTimeLimit != null)
		{
			IMemento timeLimitMemento = memento.createChild(TIME_LIMIT_ELEMENT);
			IMemento minutesMemento = timeLimitMemento.createChild(MINUTES_ELEMENT);
			minutesMemento.putTextData(Integer.toString(fTimeLimit.getMinutes()));
			IMemento secondsMemento = timeLimitMemento.createChild(SECONDS_ELEMENT);
			secondsMemento.putTextData(Integer.toString(fTimeLimit.getSeconds()));
		}
	
		IMemento transitionMemento = memento.createChild(TRANSITION_ELEMENT);
		
		switch(getTransition())
		{
		case START_ON_CLICK:
			transitionMemento.putTextData(START_ON_CLICK_VALUE);
			break;

		case START_WITH_PREVIOUS:
			transitionMemento.putTextData(START_WITH_PREVIOUS_VALUE);
			break;	

		case START_AFTER_PREVIOUS:
			transitionMemento.putTextData(START_AFTER_PREVIOUS_VALUE);
			break;	
		}

	}
}
