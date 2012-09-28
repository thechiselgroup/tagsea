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

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.fx.FontFx;
import com.ibm.research.tours.fx.ToursFxPlugin;

public class SetTextEditorFontTourElement extends ResettingFontTourElement 
{
	public static final String TEXT = "Set text editor font";
	private FontData fOldFontData;
	
	public SetTextEditorFontTourElement()
	{
		setFontData(FontFx.getTextEditorsFont());
	}
	
	public void start() 
	{
		
	}

	public void stop() 
	{
		if(getReset())
			if(fOldFontData!=null)
				FontFx.setTextEditorsFont(fOldFontData);
	}

	public void transition() 
	{
		if(getFontData() != null)
			fOldFontData = FontFx.setTextEditorsFont(getFontData());

	}

	public Image getImage() 
	{
		return ToursFxPlugin.getDefault().getImageRegistry().get(ToursFxPlugin.IMG_FONT);
	}

	public String getShortText() 
	{
		return TEXT;
	}
	
	public String getText() 
	{
		return TEXT + getTextAnnotations();
	}
	
	public ITourElement createClone() 
	{
		return new SetTextEditorFontTourElement();
	}
}
