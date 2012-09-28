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

public class SetJavaEditorFontTourElement extends ResettingFontTourElement 
{
	public static final String TEXT = "Set java editor font";
	private FontData fOldFontData;
	
	public SetJavaEditorFontTourElement() 
	{
		setFontData(FontFx.getJavaEditorsFont());
	}
	
	public void start() 
	{
		
	}

	public void stop() 
	{
		if(getReset())
			if(fOldFontData!=null)
				FontFx.setJavaEditorsFont(fOldFontData);
	}

	public void transition() 
	{
		if(getFontData() != null)
			fOldFontData = FontFx.setJavaEditorsFont(getFontData());
	}

	public Image getImage() 
	{
		return ToursFxPlugin.getDefault().getImageRegistry().get(ToursFxPlugin.IMG_JAVA_EDITOR);
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
		return new SetJavaEditorFontTourElement();
	}
}
