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
package com.ibm.research.tours.fx;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;


public class UnhighlightEffect implements IHighlightEffect {

	private static UnhighlightEffect instance;
	
	public static UnhighlightEffect getInstance()
	{
		if(instance == null)
			instance = new UnhighlightEffect();
	
		return instance;
	}

	public String getText() 
	{
		return "Unhighlight";
	}

	public AlphaFx apply(AlphaFx oldAlpha, IWorkbenchPart part) {
		AlphaFx fAlphaFX = oldAlpha;
		if (fAlphaFX!=null)
		{
			Rectangle bounds = EclipseFx.getBounds(part);
			fAlphaFX.unfocus(bounds, true);
		}
		else
		{
			AlphaFx alphaFX = GlobalHighlightEffect.getAlphaFx();
			if ( alphaFX!=null )
			{
				Rectangle bounds = EclipseFx.getBounds(part);
				alphaFX.unfocus(bounds, true);
			}
		}
		return fAlphaFX;
	}

	public AlphaFx apply(AlphaFx oldAlpha, IWorkbenchPage page) {
		AlphaFx fAlphaFX = oldAlpha; 
		if (fAlphaFX!=null)
		{
			Rectangle bounds = EclipseFx.getBounds(page);
			fAlphaFX.unfocus(bounds, true);
		}
		else
		{
			AlphaFx alphaFX = GlobalHighlightEffect.getAlphaFx();
			if ( alphaFX!=null )
			{
				Rectangle bounds = EclipseFx.getBounds(page);
				alphaFX.unfocus(bounds, true);
			}
			return oldAlpha;
		}
		return fAlphaFX;
	}
}
