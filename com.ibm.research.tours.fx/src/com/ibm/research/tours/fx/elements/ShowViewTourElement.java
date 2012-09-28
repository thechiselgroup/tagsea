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
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.views.IViewDescriptor;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.fx.AlphaFx;
import com.ibm.research.tours.fx.EclipseFx;
import com.ibm.research.tours.fx.IHighlightEffect;
import com.ibm.research.tours.fx.ToursFxPlugin;

public class ShowViewTourElement extends MaximizableTourElement 
{
	public static final String TEXT = "Show View";
	public static final String VIEW_ID = "view-id";

	private IViewDescriptor fDescriptor;
	protected AlphaFx fAlphaFX;
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
		final IViewPart part = EclipseFx.showView(getDescriptior().getId(), getMaximixedHint());

		if(getHighlightEffect()!=null)
		{
			fJob = new WorkbenchJob("") 
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) 
				{
					IHighlightEffect effect = getHighlightEffect();
					if ( effect!=null )
						fAlphaFX = effect.apply(fAlphaFX, part);
					
//					if(getHighlightEffect() instanceof DefaultHighlightEffect)
//					{
//						GlobalHighlightEffect.reset();
//						fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
//						Rectangle bounds = EclipseFx.getBounds(part);
//						fAlphaFX.focus(bounds, true);
//						fAlphaFX.fadeTo(128);
//					}
//					else if(getHighlightEffect() instanceof LetterboxEffect)
//					{
//						GlobalHighlightEffect.reset();
//						fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
//						Rectangle bounds = EclipseFx.getBounds(part);
//						fAlphaFX.letterbox(bounds.y, bounds.height, true);
//						fAlphaFX.fadeTo(128);
//					}
//					else if(getHighlightEffect() instanceof UnhighlightEffect)
//					{
//						if (fAlphaFX!=null)
//						{
//							Rectangle bounds = EclipseFx.getBounds(part);
//							fAlphaFX.unfocus(bounds, true);
//						}
//						else
//						{
//							AlphaFx alphaFX = GlobalHighlightEffect.getAlphaFx();
//							if ( alphaFX!=null )
//							{
//								Rectangle bounds = EclipseFx.getBounds(part);
//								alphaFX.unfocus(bounds, true);
//							}
//						}
//					}
//					else if(getHighlightEffect() instanceof GlobalHighlightEffect)
//					{
//						AlphaFx alphaFX = GlobalHighlightEffect.getAlphaFx();
//						Rectangle bounds = EclipseFx.getBounds(part);
//						alphaFX.focus(bounds, true);
//						alphaFX.fadeTo(128);
//					}
//	
					return Status.OK_STATUS;
				}
			};
			fJob.schedule();
		}
	}

	public Image getImage() 
	{
		if(fDescriptor == null)
			return ToursFxPlugin.getDefault().getImageRegistry().get(ToursFxPlugin.IMG_VIEW);
		else
			return fDescriptor.getImageDescriptor().createImage();

	}

	public String getShortText() 
	{
		String text;

		if(fDescriptor == null)
			text = TEXT;
		else
			text = "Show " + fDescriptor.getLabel() + " view";

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

		if(getMaximixedHint())
			annotationBuffer.append(" (" + "maximized" + ")");

		if(getHighlightEffect()!=null)
			annotationBuffer.append(" (" + getHighlightEffect().getText() + ")");

		return annotationBuffer.toString();
	}


	public void load(IMemento memento) 
	{
		super.load(memento);

		IMemento viewIdMemento = memento.getChild(VIEW_ID);

		if(viewIdMemento!=null)
		{
			String viewId = viewIdMemento.getTextData();

			if(viewId!=null)
			{
				fDescriptor = PlatformUI.getWorkbench().getViewRegistry().find(viewId);
			}
		}
	}

	public void save(IMemento memento) 
	{
		super.save(memento);

		if(fDescriptor!=null)
		{
			IMemento viewIdMemento = memento.createChild(VIEW_ID);
			viewIdMemento.putTextData(fDescriptor.getId());
		}
	}

	public ITourElement createClone() 
	{
		return new ShowViewTourElement();
	}

	public IViewDescriptor getDescriptior() 
	{
		return fDescriptor;
	}

	public void setDescriptior(IViewDescriptor descriptior) 
	{
		if(fDescriptor != descriptior)
		{
			fDescriptor = descriptior;
			fireElementChangedEvent();
		}
	}
}
