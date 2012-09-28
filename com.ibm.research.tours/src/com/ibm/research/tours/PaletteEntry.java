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

import org.eclipse.swt.graphics.Image;


class PaletteEntry implements IPaletteEntry 
{
	private ITourElementProvider fProvider;
	private String fDescription;
	private Image fImage;
	private String fText;
	private String fId;
	
	public PaletteEntry(String id, 
					    ITourElementProvider provider,
						Image image, 
						String text, 
						String description) 
	{

		fId = id;
		fProvider = provider;
		fImage = image;
		fText = text;
		fDescription = description;
	}
	
	public String getDescription() 
	{
		if(fDescription == null)
			fDescription = new String();
		
		return fDescription;
	}

	public String getId() 
	{
		return fId;
	}
	
	public Image getImage() 
	{		
		return fImage;
	}

	public String getText() 
	{
		if(fText == null)
			fText = new String();
		
		return fText;
	}

	public ITourElementProvider getProvider() 
	{
		return fProvider;
	}
}
