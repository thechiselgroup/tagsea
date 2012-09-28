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

import org.eclipse.swt.graphics.Image;

import com.ibm.research.tours.AbstractTourElement;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.fx.FontFx;
import com.ibm.research.tours.fx.ToursFxPlugin;

public class ResetConsoleFontTourElement extends AbstractTourElement 
{
	public static final String TEXT = "Reset console font";

	public void start() 
	{
	}

	public void stop() 
	{
	}

	public void transition() 
	{
		FontFx.resetConsoleFont();
	}

	public Image getImage() 
	{
		return ToursFxPlugin.getDefault().getImageRegistry().get(ToursFxPlugin.IMG_RESET);
	}

	public String getShortText() 
	{
		return getText();
	}

	
	public String getText() 
	{
		return TEXT;
	}

	public ITourElement createClone() 
	{
		return new ResetConsoleFontTourElement();
	}
}
