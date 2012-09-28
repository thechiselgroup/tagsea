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
package com.ibm.research.tours.content.url.delegates;

import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WebBrowserUIPlugin;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.texteditor.ITextEditor;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.content.ToursContentPlugin;
import com.ibm.research.tours.content.elements.ResourceURLTourElement;
import com.ibm.research.tours.content.url.HttpURL;
import com.ibm.research.tours.content.url.IURL;
import com.ibm.research.tours.fx.AlphaFx;
import com.ibm.research.tours.fx.DefaultHighlightEffect;
import com.ibm.research.tours.fx.EclipseFx;
import com.ibm.research.tours.fx.GlobalHighlightEffect;
import com.ibm.research.tours.fx.LetterboxEffect;
import com.ibm.research.tours.fx.UnhighlightEffect;

public class HttpURLTourElementDelegate implements IURLTourElementDelegate
{
	private String fUrl;
	protected AlphaFx fAlphaFX;
	private WorkbenchJob fJob;
	private ResourceURLTourElement fTourElement;

	public HttpURLTourElementDelegate(ResourceURLTourElement tourElement,String url)
	{
		fUrl = url;
		fTourElement = tourElement;
	}

	public ITourElement createClone() 
	{
		return new ResourceURLTourElement(fUrl);
	}

	public Image getImage() 
	{
		Image favIcon = ToursContentPlugin.getDefault().getFavIcon(fUrl);
		if ( favIcon==null )
			return ToursContentPlugin.getDefault().getImageRegistry().get(ToursContentPlugin.IMG_WEB);
		else
			return favIcon;
	}

	public String getShortText() 
	{
		return fUrl;
	}

	public String getText() 
	{
		return fUrl;
	}

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
		try 
		{
			IWorkbenchBrowserSupport browserSupport = WebBrowserUIPlugin.getInstance().getWorkbench().getBrowserSupport();
			IWebBrowser browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.NAVIGATION_BAR, null, null, null);
			URL url = new URL(fUrl);
			browser.openURL(url);

			final IEditorPart part = EclipseFx.getActiveEditor();

			if(fTourElement.getMaximixedHint())
				EclipseFx.maximizeActiveEditor();

			if(part instanceof ITextEditor)
			{
				ITextEditor editor = (ITextEditor)part;
				editor.setHighlightRange(0,0,true);
			}

			if(fTourElement.getHighlightEffect()!=null)
			{
				fJob = new WorkbenchJob("") 
				{
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor) 
					{
						if(fTourElement.getHighlightEffect() instanceof DefaultHighlightEffect)
						{
							GlobalHighlightEffect.reset();
							fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
							Rectangle bounds = EclipseFx.getBounds(part);
							fAlphaFX.focus(bounds, true);
							fAlphaFX.fadeTo(128);
						}
						else if(fTourElement.getHighlightEffect() instanceof LetterboxEffect)
						{
							GlobalHighlightEffect.reset();
							fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
							Rectangle bounds = EclipseFx.getBounds(part);
							fAlphaFX.letterbox(bounds.y, bounds.height, true);
							fAlphaFX.fadeTo(128);
						}
						else if(fTourElement.getHighlightEffect() instanceof UnhighlightEffect)
						{
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
						}
						else if(fTourElement.getHighlightEffect() instanceof GlobalHighlightEffect)
						{
							AlphaFx alphaFX = GlobalHighlightEffect.getAlphaFx();
							Rectangle bounds = EclipseFx.getBounds(part);
							alphaFX.focus(bounds, true);
							alphaFX.fadeTo(128);
						}


						return Status.OK_STATUS;
					}
				};
				fJob.schedule();
			}

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public IURL getUrl() 
	{
		return new HttpURL(fUrl);
	}
}
