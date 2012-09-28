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

public class SquigglesTextEffect implements ITextEffect
{
	private static SquigglesTextEffect instance;
	
	private SquigglesTextEffect() 
	{
	}
	
	public static SquigglesTextEffect getInstance()
	{
		if(instance == null)
			instance = new SquigglesTextEffect();
	
		return instance;
	}

	public String getText() 
	{
		return "Squiggles";
	}
}
