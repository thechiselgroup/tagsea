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

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.WorkbenchJob;

import com.ibm.research.cue.projector.internal.InterceptorAdapter;
import com.ibm.research.cue.projector.internal.InterceptorListener;
import com.ibm.research.cue.projector.internal.MouseKeyboardInterceptor;
import com.ibm.research.tours.AbstractTourElement;
import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.fx.AlphaFx;
import com.ibm.research.tours.fx.EclipseFx;
import com.ibm.research.tours.fx.ToursFxPlugin;

/**
 * highlights area under the cursor in an alpha-blended workbench.  highlighting
 * triggered by left mouse clicking. 
 * 
 * @tag spotlight fx tours todo : need to deal with maximized views better 
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */
public class SpotlightTourElement extends AbstractTourElement 
{	
	private AlphaFx fAlphaFX;
	private WorkbenchJob fJob;
	private Rectangle[] fRegions;
	private Rectangle fPreviousRegion, fCurrentRegion;
	private Point fPos = new Point(-1,-1);
	private boolean fEnableSpotlight = false;
	private IWorkbenchPage fPage;  // in case of null page values...
	
	private IWorkbenchPart fLastPart;
	private boolean        fLastMaximized=false;
	private Listener listener = new Listener() {

		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.MouseMove:
				synchronized (fPos) {
					Point newPoint = ((Control)event.widget).toDisplay(event.x, event.y);
					fPos.x = newPoint.x;
					fPos.y = newPoint.y;
				}
				event.doit = false;
				break;
			case SWT.MouseUp:
				if (event.button == 1) {
					refreshRegions();
					IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
					if ( part==fLastPart )
					{
						boolean isMaximized = EclipseFx.isMaximized(part);
						if ( isMaximized!=fLastMaximized )
							fEnableSpotlight = true;
						fLastMaximized = isMaximized;
					}
					
					fLastPart = part;
					event.doit = false;
				}
				break;
			}
			
		}
		
	};
//	private MouseKeyboardInterceptor interceptor; 
//	private InterceptorListener listener = new InterceptorAdapter() {
//		@Override
//		public boolean MouseMove(int x, int y) {
//			synchronized (fPos)
//			{
//				fPos.x = x;
//				fPos.y = y;
//			}
//			
//			return false;
//		}
//		
//		@Override
//		public boolean LeftButtonUp(int x, int y) {
//			refreshRegions();
//			
//			IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
//			if ( part==fLastPart )
//			{
//				boolean isMaximized = EclipseFx.isMaximized(part);
//				if ( isMaximized!=fLastMaximized )
//					fEnableSpotlight = true;
//				fLastMaximized = isMaximized;
//			}
//			
//			fLastPart = part;
//			return false;
//		}
//	};

	private TimerTask fTask;
	private Timer fTimer;
	
	private class SpotlightJob extends WorkbenchJob
	{
		private Rectangle unfocus, focus;
		
		public SpotlightJob(Rectangle unfocus, Rectangle focus)
		{
			super("");
			if ( unfocus!=null )
				this.unfocus = new Rectangle(unfocus.x,unfocus.y,unfocus.width,unfocus.height);
			if ( focus!=null )
				this.focus   = new Rectangle(focus.x,focus.y,focus.width,focus.height);
		}
		
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			if ( unfocus!=null )
				fAlphaFX.unfocus(unfocus, true);
			
			if ( focus!=null )
				fAlphaFX.focus(focus, true);
			return Status.OK_STATUS;
		}
	};
	
	private void setupTimer()
	{
		if(fTask!=null)
			fTask.cancel();
		
		if(fTimer!=null)
			fTimer.cancel();

		fTask = new TimerTask() {
			@Override
			public void run() {
				try {
					int x,y;
					
					synchronized (fPos)
					{
						if ( !fEnableSpotlight )
							return;
						
						x = fPos.x;
						y = fPos.y;
					}
					
					fPreviousRegion = fCurrentRegion;
					
					for (Rectangle rectangle : fRegions)
					{
						if ( rectangle.contains(x,y) )
						{
							fCurrentRegion = rectangle;
							break;
						}
					}
					
					if ( fCurrentRegion!=null && fCurrentRegion.equals(fPreviousRegion) )
						return;
					
					SpotlightJob job = new SpotlightJob(fPreviousRegion,fCurrentRegion);
					job.schedule();
					
					synchronized (fPos)
					{
						fEnableSpotlight = false;
					}
					
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		};
		
		fTimer = new Timer();
		fTimer.scheduleAtFixedRate(fTask, 0, 250);
	}
	
	private void refreshRegions()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if ( page==null )
			page = fPage;
		
		Rectangle[] shellBounds = EclipseFx.getShellBounds(page, new Shell[] {fAlphaFX.getAlphaShell().getShell(),page.getWorkbenchWindow().getShell()});
		Rectangle[] partBounds  = EclipseFx.getAllBounds(page);
		
		if ( shellBounds!=null && shellBounds.length>0 )
		{
			fRegions = new Rectangle[shellBounds.length + partBounds.length];
			for (int i=0; i<shellBounds.length; i++)
				fRegions[i] = shellBounds[i];
			for (int i=0; i<partBounds.length; i++)
				fRegions[i+shellBounds.length] = partBounds[i];
			
			fEnableSpotlight = true;
		}
		else
			fRegions = partBounds; 
	}
	
	public void transition() 
	{
		fPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		fEnableSpotlight = false;
		
//		interceptor = MouseKeyboardInterceptor.getInstance();
//		interceptor.addListener(listener);
		
		fJob = new WorkbenchJob("Transitioning Spotlight")
		{
			public IStatus runInUIThread(IProgressMonitor monitor) 
			{
				Display.getCurrent().addFilter(SWT.MouseMove, listener);
				Display.getCurrent().addFilter(SWT.MouseUp, listener);
				fAlphaFX = new AlphaFx(PlatformUI.getWorkbench().getDisplay());
				fAlphaFX.fadeTo(128);
				
				fAlphaFX.getAlphaShell().getShell().addMouseListener(new MouseAdapter() {
					public void mouseUp(MouseEvent e) {
						fEnableSpotlight = true;
					}
				});
				
				if ( monitor.isCanceled() )
					return Status.CANCEL_STATUS;

				refreshRegions();
				setupTimer();
				
				return Status.OK_STATUS;
			}
		};
		fJob.schedule();
	}

	public void stop() 
	{
		fEnableSpotlight = false;
		
		WorkbenchJob cleanJob = new WorkbenchJob("Clearing spotlight") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				Display.getCurrent().removeFilter(SWT.MouseMove, listener);
				Display.getCurrent().removeFilter(SWT.MouseUp, listener);
				return Status.OK_STATUS;
			}
		};
		cleanJob.schedule();
//		if ( interceptor!=null )
//			interceptor.removeListener(listener);
		
		if(fTask!=null)
			fTask.cancel();
		
		if(fTimer!=null)
			fTimer.cancel();
		
		if(fJob!=null)
			fJob.cancel();
		
		if(fAlphaFX !=null && !fAlphaFX.getAlphaShell().getShell().isDisposed())
			fAlphaFX.dispose();
		
		fPreviousRegion = null;
	}
	

	public Image getImage() 
	{
		return ToursFxPlugin.getDefault().getImageRegistry().get(ToursFxPlugin.IMG_EFFECT);
	}


	public String getText() 
	{
		return "Spotlight the workbench";
	}
	
	public String getShortText() 
	{
		return getText();
	}
	
	public ITourElement createClone() 
	{
		return new SpotlightTourElement();
	}

	public void start() 
	{
		
	}
}
