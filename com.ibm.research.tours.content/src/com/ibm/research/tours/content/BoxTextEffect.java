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

public class BoxTextEffect implements ITextEffect
{
	private static BoxTextEffect instance;
	
	private BoxTextEffect() 
	{
	}
	
	public static BoxTextEffect getInstance()
	{
		if(instance == null)
			instance = new BoxTextEffect();
	
		return instance;
	}

	public String getText() 
	{
		return "Box";
	}
}
