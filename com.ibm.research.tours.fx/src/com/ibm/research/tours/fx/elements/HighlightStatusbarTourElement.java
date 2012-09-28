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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.WorkbenchJob;

import com.ibm.research.tours.AbstractTourElement;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.fx.AlphaFx;
import com.ibm.research.tours.fx.EclipseFx;
import com.ibm.research.tours.fx.ToursFxPlugin;

public class HighlightStatusbarTourElement extends AbstractTourElement 
{
	public static final String TEXT = "Highlight statusbar";

	private AlphaFx fAlphaFX;
	private WorkbenchJob fJob;
	
	public void start() 
	{
	}

	public void stop() 
	{
		if(fJob!=null)
			fJob.cancel();
		
		if(fAlphaFX !=null && !fAlphaFX.getAlphaShell().getShell().isDisposed())
			fAlphaFX.dispose();
	}

	public void transition() 
	{
		fJob = new WorkbenchJob("")
		{
			public IStatus runInUIThread(IProgressMonitor monitor) 
			{
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
				Rectangle bounds = EclipseFx.getLowerAreaBounds(page);
				fAlphaFX.focus(bounds, true);
				fAlphaFX.fadeTo(128);
				
				return Status.OK_STATUS;
			}
		};
		fJob.schedule();
	}

	public Image getImage() 
	{
		return ToursFxPlugin.getDefault().getImageRegistry().get(ToursFxPlugin.IMG_EFFECT);
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
		return new HighlightStatusbarTourElement();
	}
}
