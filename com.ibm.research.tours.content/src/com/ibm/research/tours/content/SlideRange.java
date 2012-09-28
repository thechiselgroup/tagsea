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
package com.ibm.research.tours.content;

public class SlideRange 
{
	private int fStart;
	private int fEnd;
	
	public SlideRange(int start, int end)
	{
		fStart = start;
		fEnd = end;
	}

	public int getStart() {
		return fStart;
	}

	public void setStart(int start) {
		fStart = start;
	}

	public int getEnd() {
		return fEnd;
	}

	public void setEnd(int end) {
		fEnd = end;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if(obj !=null && obj instanceof SlideRange)
		{
			if(this == obj)
				return true;
			
			SlideRange range = (SlideRange)obj;
			if(range.getStart() == getStart() && range.getEnd()==getEnd())
				return true;
		}
		
		return false;
	}
}
