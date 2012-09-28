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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class PaletteModel implements IPaletteModel
{
	private Map<String,IPaletteEntry> fPaletteEntries;
	private List<IPaletteModelListener> fModelListeners;
	
	protected Map<String,IPaletteEntry> getPaletteEntryMap()
	{
		if(fPaletteEntries == null)
			fPaletteEntries = new HashMap<String,IPaletteEntry>();
		
		return fPaletteEntries;
	}
	
	protected List<IPaletteModelListener> getPaletteModelListeners()
	{
		if(fModelListeners == null)
			fModelListeners = new ArrayList<IPaletteModelListener>();
		
		return fModelListeners;
	}
	
	public void addPaletteEntry(final IPaletteEntry entry) 
	{
		if(entry.getId() == null)
			return;
		
		if(!getPaletteEntryMap().keySet().contains(entry.getId()))
		{
			getPaletteEntryMap().put(entry.getId(),entry);
			
			ToursPlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
			{
				public void run() 
				{
					for(IPaletteModelListener listener :getPaletteModelListeners())
						listener.entriesAdded(new IPaletteEntry[]{entry});
				}
			});		
		}
	}

	public IPaletteEntry[] getPaletteEntries()
	{
		return getPaletteEntryMap().values().toArray(new IPaletteEntry[0]);
	}
	
	public void addPaletteModelListener(IPaletteModelListener listener) 
	{
		if(!getPaletteModelListeners().contains(listener))
		{
			getPaletteModelListeners().add(listener);
			
			ToursPlugin.getDefault().getEventDispatcher().dispatch(new Runnable() 
			{
				public void run() 
				{
					for(IPaletteModelListener listener :getPaletteModelListeners())
						listener.entriesAdded(getPaletteEntries());
				}
			});
		}
	}
	
	public void removePaletteModelListener(IPaletteModelListener listener) 
	{
		getPaletteModelListeners().remove(listener);
	}

	public IPaletteEntry getPaletteEntry(String id) 
	{
		return getPaletteEntryMap().get(id);
	}
}
