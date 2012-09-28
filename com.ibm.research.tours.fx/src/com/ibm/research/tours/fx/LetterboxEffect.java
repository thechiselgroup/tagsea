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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class LetterboxEffect implements IHighlightEffect 
{
	private static LetterboxEffect instance;
	
	public static LetterboxEffect getInstance()
	{
		if(instance == null)
			instance = new LetterboxEffect();
	
		return instance;
	}

	public String getText() 
	{
		return "LetterBox";
	}

	public AlphaFx apply(AlphaFx oldAlpha, IWorkbenchPart part) {
		GlobalHighlightEffect.reset();
		AlphaFx fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
		Rectangle bounds = EclipseFx.getBounds(part);
		fAlphaFX.letterbox(bounds.y, bounds.height, true);
		fAlphaFX.fadeTo(128);
		return fAlphaFX;
	}

	public AlphaFx apply(AlphaFx oldAlpha, IWorkbenchPage page) {
		GlobalHighlightEffect.reset();
		AlphaFx fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
		Rectangle bounds = EclipseFx.getBounds(page);
		fAlphaFX.letterbox(bounds.y, bounds.height, true);
		fAlphaFX.fadeTo(128);
		return fAlphaFX;
	}

}
