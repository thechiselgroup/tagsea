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
import org.eclipse.ui.PlatformUI;

import com.ibm.research.tours.ITour;
import com.ibm.research.tours.ToursPlugin;
import com.ibm.research.tours.runtime.DefaultTourRuntime;
import com.ibm.research.tours.runtime.ITourRuntimeListener;

public class GlobalHighlightEffect implements IHighlightEffect {

	private static GlobalHighlightEffect instance;
	private static AlphaFx alphaFx;
	
	public GlobalHighlightEffect()
	{
		instance = this;
		((DefaultTourRuntime) ToursPlugin.getDefault().getTourRuntime()).addTourRuntimeListener(new ITourRuntimeListener() {
			public void tourEnded(ITour tour) {
				reset();
			}
			public void tourStarted(ITour tour) {
			}
		});
	}
	
	public static GlobalHighlightEffect getInstance()
	{
		if(instance == null)
		{
			instance = new GlobalHighlightEffect();
		}
		
		return instance;
	}

	public String getText() 
	{
		return "Global Highlight";
	}
	
	public static AlphaFx getAlphaFx()
	{
		if ( alphaFx==null )
			alphaFx = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
		return alphaFx;
	}
	
	public static void setAlphaFx(AlphaFx newAlphaFx)
	{
		if ( alphaFx!=newAlphaFx && alphaFx!=null )
			alphaFx.dispose();
		
		alphaFx = newAlphaFx;
	}
	
	public static void reset()
	{
		if ( alphaFx!=null )
			alphaFx.dispose();
		alphaFx = null;
	}

	public AlphaFx apply(AlphaFx oldAlpha, IWorkbenchPart part) {
		AlphaFx alphaFX = GlobalHighlightEffect.getAlphaFx();
		Rectangle bounds = EclipseFx.getBounds(part);
		alphaFX.focus(bounds, true);
		alphaFX.fadeTo(128);
		return oldAlpha;
	}

	public AlphaFx apply(AlphaFx oldAlpha, IWorkbenchPage page) {
		AlphaFx alphaFX = GlobalHighlightEffect.getAlphaFx();
		Rectangle bounds = EclipseFx.getBounds(page);
		alphaFX.focus(bounds, true);
		alphaFX.fadeTo(128);
		return oldAlpha;
	}
	
}
