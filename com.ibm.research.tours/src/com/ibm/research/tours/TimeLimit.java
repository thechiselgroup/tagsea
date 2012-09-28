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

public class TimeLimit implements ITimeLimit {

	private int fMinutes = 0;
	private int fSeconds = 0;
	
	public TimeLimit() {
		// TODO Auto-generated constructor stub
	}
	
	public TimeLimit(ITimeLimit timeLimit)
	{
		setMinutes(timeLimit.getMinutes());
		setSeconds(timeLimit.getSeconds());
	}
	
	public int getMinutes() 
	{
		return fMinutes;
	}

	public int getSeconds() 
	{
		return fSeconds;
	}

	public void setMinutes(int minutes) 
	{
		if(minutes >= 0)
			fMinutes = minutes;
	}

	public void setSeconds(int seconds) 
	{
		if(seconds >= 0)
			fSeconds = seconds;
	}
	
	@Override
	public String toString() 
	{
		String minutes = getMinutes()>9?Integer.toString(getMinutes()):"0"+ Integer.toString(getMinutes());
		String seconds = getSeconds()>9?Integer.toString(getSeconds()):"0"+ Integer.toString(getSeconds());
		return new String(minutes + ":" + seconds);
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if(obj != null)
		{
			ITimeLimit limit = (ITimeLimit)obj;

			if(getMinutes() == limit.getMinutes() && getSeconds() == limit.getSeconds())
				return true;
		}
		
		return false;
	}
}
