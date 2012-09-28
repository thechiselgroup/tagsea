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

public abstract class ResettingFontTourElement extends FontTourElement 
{
	public static final String RESET_ELEMENT = "reset-font";
	public static final String TRUE = "true", FALSE = "false";
	private boolean fReset = true;
	
	public boolean getReset()
	{
		return fReset;
	}
	
	public void setReset(boolean reset)
	{
		if(fReset!=reset)
		{
			fReset = reset;
			fireElementChangedEvent();
		}
	}
	
	
	@Override
	public void save(IMemento memento) 
	{
		super.save(memento);
	
		if(getReset())
		{
			IMemento resetMemento = memento.createChild(RESET_ELEMENT);
			resetMemento.putTextData(TRUE);
		}
		else
		{
			IMemento resetMemento = memento.createChild(RESET_ELEMENT);
			resetMemento.putTextData(FALSE);
		}
	}
	
	@Override
	public void load(IMemento memento)
	{
		super.load(memento);
		
		IMemento resetMemento = memento.getChild(RESET_ELEMENT);
		
		if(resetMemento != null)
		{
			String reset = resetMemento.getTextData();
			
			if(reset!=null && reset.trim().equalsIgnoreCase(TRUE))
				setReset(true);
			else
				setReset(false);
		}
	}
}
