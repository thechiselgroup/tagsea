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

import java.util.HashMap;
import java.util.Map;

class TourElementClipBoard implements ITourElementClipBoard
{
	private Map<String,ITourElement> fMap;
	
	public String putTourElement(ITourElement element) 
	{
		String hash = Integer.toString(element.hashCode());
		getMap().put(hash, element);
		return hash;
	}

	public void clear() 
	{
		getMap().clear();
	}

	public ITourElement getTourElement(String id) 
	{
		return getMap().get(id);
	}

	protected Map<String,ITourElement> getMap()
	{
		if(fMap == null)
			fMap = new HashMap<String,ITourElement>();
		
		return fMap;
	}
}
