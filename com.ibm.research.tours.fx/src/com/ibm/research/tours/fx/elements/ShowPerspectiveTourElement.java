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
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.WorkbenchJob;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.fx.AlphaFx;
import com.ibm.research.tours.fx.EclipseFx;
import com.ibm.research.tours.fx.IHighlightEffect;
import com.ibm.research.tours.fx.ToursFxPlugin;

public class ShowPerspectiveTourElement extends HighlightableTourElement
{
	public static final String TEXT = "Show perspective";
	public static final String PERSPECTIVE_ID = "perspective-id";
	
	private IPerspectiveDescriptor fDescriptor;
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
		final IWorkbenchPage page = EclipseFx.showPerspective(getDescriptor().getId());
		
		if(getDescriptor()!=null && getHighlightEffect() != null)
		{
			fJob = new WorkbenchJob("")
			{
				public IStatus runInUIThread(IProgressMonitor monitor) 
				{
					if(page != null)
					{
						IHighlightEffect effect = getHighlightEffect();
						if ( effect!=null )
							fAlphaFX = effect.apply(fAlphaFX, page);
						
//						if(getHighlightEffect() instanceof DefaultHighlightEffect)
//						{
//							GlobalHighlightEffect.reset();
//							fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
//							Rectangle bounds = EclipseFx.getBounds(page);
//							fAlphaFX.focus(bounds, true);
//							fAlphaFX.fadeTo(128);
//						}
//						else if(getHighlightEffect() instanceof LetterboxEffect)
//						{
//							GlobalHighlightEffect.reset();
//							fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
//							Rectangle bounds = EclipseFx.getBounds(page);
//		
//							fAlphaFX.letterbox(bounds.y, bounds.height, true);
//							fAlphaFX.fadeTo(128);
//						}
//						else if(getHighlightEffect() instanceof UnhighlightEffect)
//						{
//							if (fAlphaFX!=null)
//							{
//								Rectangle bounds = EclipseFx.getBounds(page);
//								fAlphaFX.unfocus(bounds, true);
//							}
//							else
//							{
//								AlphaFx alphaFX = GlobalHighlightEffect.getAlphaFx();
//								if ( alphaFX!=null )
//								{
//									Rectangle bounds = EclipseFx.getBounds(page);
//									alphaFX.unfocus(bounds, true);
//								}
//							}
//						}
//						else if(getHighlightEffect() instanceof GlobalHighlightEffect)
//						{
//							AlphaFx alphaFX = GlobalHighlightEffect.getAlphaFx();
//							Rectangle bounds = EclipseFx.getBounds(page);
//							alphaFX.focus(bounds, true);
//							alphaFX.fadeTo(128);
//						}
					}
					return Status.OK_STATUS;
				}
			};
			fJob.schedule();
		}
	}

	public Image getImage() 
	{
		if(fDescriptor == null)
			return ToursFxPlugin.getDefault().getImageRegistry().get(ToursFxPlugin.IMG_PERSPECTIVE);
		else
			return fDescriptor.getImageDescriptor().createImage();
	}

	public String getShortText() 
	{
		String text = null;
		
		if(fDescriptor == null)
			text =  TEXT;
		else
			text = "Show " + fDescriptor.getLabel() + " perspective";
			
		return text;
	}
	
	public String getText() 
	{	
		return getShortText() + getTextAnnotations();
	}
	
	private String getTextAnnotations()
	{
		StringBuffer annotationBuffer = new StringBuffer();
		
		if(getTimeLimit()!=null)
			annotationBuffer.append(" [" + getTimeLimit().toString() + "]");
		
		if(getHighlightEffect()!=null)
			annotationBuffer.append(" (" + getHighlightEffect().getText() + ")");
		
		return annotationBuffer.toString();
	}
	
	public void load(IMemento memento) 
	{
		super.load(memento);
		
		IMemento perspectiveIdMemento = memento.getChild(PERSPECTIVE_ID);
		
		if(perspectiveIdMemento!=null)
		{
			String perspectiveId = perspectiveIdMemento.getTextData();
			
			if(perspectiveId!=null)
			{
				fDescriptor = PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(perspectiveId);
			}
		}
	}

	public void save(IMemento memento) 
	{
		super.save(memento);
		
		if(fDescriptor!=null)
		{
			IMemento perspectiveIdMemento = memento.createChild(PERSPECTIVE_ID);
			perspectiveIdMemento.putTextData(fDescriptor.getId());
		}
	}

	public ITourElement createClone() 
	{
		return new ShowPerspectiveTourElement();
	}
	
	public IPerspectiveDescriptor getDescriptor() 
	{
		return fDescriptor;
	}

	public void setDescriptor(IPerspectiveDescriptor descriptor) 
	{
		if(fDescriptor!=descriptor)
		{
			fDescriptor = descriptor;
			fireElementChangedEvent();
		}
	}
}
