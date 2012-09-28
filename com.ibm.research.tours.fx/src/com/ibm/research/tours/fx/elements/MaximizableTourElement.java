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
package com.ibm.research.tours.fx.elements;

import org.eclipse.ui.IMemento;

public abstract class MaximizableTourElement extends HighlightableTourElement
{
	public static final String MAXIMIZE_ELEMENT = "maximized";
	public static final String TRUE = "true";
	
	private boolean fMaximixedHint;
	
	public boolean getMaximixedHint() 
	{
		return fMaximixedHint;
	}

	public void setMaximixedHint(boolean maximixedHint) 
	{
		if(fMaximixedHint!=maximixedHint)
		{
			fMaximixedHint = maximixedHint;
			fireElementChangedEvent();
		}
	}
	
	@Override
	public void load(IMemento memento) 
	{
		super.load(memento);
		
		IMemento maximizeMemento = memento.getChild(MAXIMIZE_ELEMENT);
		
		if(maximizeMemento !=null)
		{
			String maxString = maximizeMemento.getTextData();
			
			if(maxString!=null && maxString.equals(TRUE))
				fMaximixedHint = true;
		}
	}
	
	@Override
	public void save(IMemento memento) 
	{
		super.save(memento);
		if(fMaximixedHint)
		{
			IMemento maximize = memento.createChild(MAXIMIZE_ELEMENT);
			maximize.putTextData(TRUE);
		}
	}
}	


