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

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.ibm.research.cue.projector.internal.AlphaShell;
import com.ibm.research.tours.ToursPlugin;
import com.ibm.research.tours.fx.preferences.IToursPreferences;

/**
 * AlphaShell-based effects applied on the entire desktop display. 
 * 
 * Set up one alpha shell, and apply a bunch of different effects on it.
 * 
 * Call dispose() when finished with this object
 * 
 * @tag alpha fx todo fade : fading is done by simple for-loops - thus, fade speed related to CPU speed.  eventually we require a timing mechanism.
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */
public class AlphaFx {

	private Display    display;
	private AlphaShell alpha;
	private Region     region;
	
	private int monitorId = -1;  // which monitor to use for "desktop display", -1=choose monitor where workbench shell currently is
	private ShellCaption caption;
	private static int SLEEP_DURATION;

	static 
	{
		SLEEP_DURATION = ToursFxPlugin.getDefault().getPreferenceStore().getInt(IToursPreferences.FX_DELAY);
		ToursFxPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
		
			public void propertyChange(PropertyChangeEvent event) 
			{
				if(event.getProperty().equalsIgnoreCase(IToursPreferences.FX_DELAY))
					SLEEP_DURATION = ToursFxPlugin.getDefault().getPreferenceStore().getInt(IToursPreferences.FX_DELAY);
			}
		});
	}
	
	/**
	 * A class to fade in or out the alpha shell.
	 * @author Del Myers
	 *
	 */
	private class AlphaFader implements Runnable {

		private int start;
		private int end;
		private int value;
		private long startTime = 0;
		private int duration = 500;
		private AlphaShell alphaFader;
		private boolean closeOnFinish;
		private Runnable preRun;
		private Runnable postRun;

		public AlphaFader(AlphaShell alpha, int start, int end, boolean closeOnFinish, Runnable preRun, Runnable postRun) {
			this.alphaFader =alpha;
			this.start = start;
			this.end = end;
			this.value = -1;
			this.closeOnFinish = closeOnFinish;
			this.preRun = preRun;
			this.postRun = postRun;
		}
		

		public void run() {
			if (preRun != null) preRun.run();
			if (alphaFader == null || alphaFader.getShell().isDisposed()) {
				return;
			}
			if (value == -1) {
				//reset the alpha values
				value = start;
				this.startTime = System.currentTimeMillis();
				alphaFader.setAlpha(value);
				alphaFader.getShell().setVisible(true);
			}
			//set the value to scale based on the amount of time that it should take
			long timeFromStart = System.currentTimeMillis() - startTime;
			double percentDone = (double)timeFromStart/duration;
			if (percentDone >= 1.0) {
				alphaFader.setAlpha(end);
				if (closeOnFinish) {
					alphaFader.getShell().close();
				}
				if (postRun != null) postRun.run();
			} else {
				value = (int) (((end-start)*percentDone) + start);
				alphaFader.setAlpha(value);
				//about 30 fps
				alphaFader.getShell().getDisplay().timerExec(33, this);
			}
			
		}
		
		
	}
	
	/**
	 * sets up f/x for monitor whereever the workbench shell is
	 */
	public AlphaFx(Display display)
	{
		this(display,-1);
	}
	
	/**
	 * sets up f/x for a specific monitor
	 * @param monitor : 0, 1, ... ; -1 = choose monitor where workbench shell currently is
	 */
	public AlphaFx(Display display, int monitor)
	{
		initAlphaShell(display);
		monitorId = monitor;
	}
	
	/**
	 * returns associated AlphaShell
	 * @return AlphaShell
	 */
	public AlphaShell getAlphaShell()
	{
		return alpha;
	}
	
	/**
	 * disposes resources associated with this object
	 */
	public void dispose()
	{
		Shell shell = alpha.getShell();
		if ( shell!=null && !shell.isDisposed() )
			shell.dispose();
	}
	
	/**
	 * internal routine that creates AlphaShell instance 
	 */
	private void initAlphaShell(Display display)
	{
		this.display = display;
		alpha = new AlphaShell(display);
		alpha.getShell().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if ( region!=null && !region.isDisposed() )
					region.dispose();
			}
		});
	}
	
	/**
	 * internal routine that sets up the alphaShell to a "ready" state for any effect
	 */
	private void beginFX(boolean skipRender)
	{
		init();
		applyBounds();
		if ( caption!=null )
			caption.setShell(alpha.getShell());
		
		if ( region==null || region.isDisposed() )
		{
			region = new Region(display);
			Rectangle regionBounds = alpha.getShell().getBounds();
			Point p = alpha.getShell().toControl(regionBounds.x, regionBounds.y);
			regionBounds.x = p.x;
			regionBounds.y = p.y;
			region.add(regionBounds);
		}
		
		if ( !skipRender )
		{
			alpha.getShell().setRegion(region);
			alpha.setAlpha(alpha.getAlpha());  	// apply alpha effect before opening shell
			alpha.getShell().open();
			ToursPlugin.getDefault().getTourRuntime().moveAbove();
		}
		
		alpha.getShell().setVisible(true);	// to deal with setVisible(false) from  fadeOut()
	}
	
	private void init() 
	{
		if ( alpha.getShell()==null || alpha.getShell().isDisposed() )
			initAlphaShell(display);
	}
	
	private void applyBounds() 
	{
		Rectangle primaryBounds = null;
		
		if ( monitorId>-1 )
		{
			Monitor[] monitors 	    = alpha.getShell().getDisplay().getMonitors();
			primaryBounds = monitors[monitorId].getBounds();
		}
		else
		{
			primaryBounds = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getMonitor().getBounds();
		}
		
		alpha.getShell().setBounds(primaryBounds);
	}

	private void refreshCaption()
	{
		if ( caption!=null )
			caption.refresh();
	}
	
	/**
	 * fades current transparency to black
	 */
	public void fadeToBlack()
	{
		beginFX(false);
		
//		for (int a = alpha.getAlpha(); a<=255; a+=5)
//		{
//			alpha.setAlpha(a);
//			refreshCaption();
//			sleep();
//		}
		alpha.getShell().getDisplay().asyncExec(new AlphaFader(alpha, alpha.getAlpha(), 255, false, null, new Runnable(){

			public void run() {
				refreshCaption();
			}
		}));
		
	}
	
	/**
	 * fade currenct transparency to complete transparency
	 */
	public void fadeOut()
	{
		beginFX(false);
		
//		for (int a = alpha.getAlpha(); a>=1; a--)
//		{
//			alpha.setAlpha(a);
//			refreshCaption();
//			sleep();
//		}
		alpha.getShell().getDisplay().asyncExec(new AlphaFader(alpha, alpha.getAlpha(), 1, true, null, new Runnable(){
			public void run() {
				refreshCaption();
			}
		}));
		
		
		// complete transparency - hide the shell to allow click through
		//alpha.getShell().setVisible(false);
	}

	/**
	 * fade from current level of transparency to a new level of transparency
	 * 
	 * warning: fadeTo(1) will leave an "invisible" shell on top of the desktop, catching all click events
	 * 
	 * @param a
	 */
	public void fadeTo(int a)
	{
		beginFX(false);
		
		int currentAlpha = alpha.getAlpha();
		alpha.getShell().getDisplay().asyncExec(new AlphaFader(alpha, currentAlpha, a, false, null, null));
//		if ( a<currentAlpha )
//		{
//			
//			for (int i=currentAlpha; i>=a; i-=5)
//			{
//				alpha.setAlpha(i);
//				refreshCaption();
//				sleep();
//			}
//		}
//		else
//		{
//			for (int i=currentAlpha; i<=a; i+=5)
//			{
//				alpha.setAlpha(i);
//				refreshCaption();
//				sleep();
//			}
//		}
	}
	
	/**
	 * create a "hole" in the transparency at the given viewport.  the "hole" will remain
	 * until the associated alphaShell is destroyed or unfocus() is called with the
	 * same viewport coordinates.
	 * 
	 * warning: calling fadeOut() and then focus() will restore an "invisible" shell on top
	 * of the desktop, catching all click events.
	 * 
	 * @param viewport
	 * @param fade : apply a fade effect on the "hole"
	 */
	public void focus(Rectangle viewport, boolean fade)
	{
		beginFX(false);
		
		AlphaShell focusAlpha = null;
		if ( fade )
		{
			focusAlpha = new AlphaShell(display);
			focusAlpha.setAlpha(alpha.getAlpha());
			focusAlpha.getShell().setBounds(viewport);
			focusAlpha.getShell().open();
		}
		Point localTop = alpha.getShell().toControl(viewport.x, viewport.y);
		Rectangle localViewport = new Rectangle(localTop.x, localTop.y, viewport.width, viewport.height);
		region.subtract(localViewport);
		alpha.getShell().setRegion(region);
		
		if ( fade )
		{
			focusAlpha.getShell().getDisplay().asyncExec(new AlphaFader(focusAlpha, alpha.getAlpha(), 1, true, null, new Runnable(){
				public void run() {
					refreshCaption();
				}
			}));
//			for (int a=alpha.getAlpha(); a>=1; a--)
//			{
//				focusAlpha.setAlpha(a);
//				refreshCaption();
//				sleep();
//			}
//			focusAlpha.getShell().close();
		}
		
		
	}
	
	/**
	 * create a "hole" in the transparency at the given viewport.  the "hole" will remain
	 * until the associated alphaShell is destroyed or unfocus() is called with the
	 * same viewport coordinates.
	 * 
	 * warning: calling fadeOut() and then focus() will restore an "invisible" shell on top
	 * of the desktop, catching all click events.
	 * 
	 * @param viewport
	 * @param fade : apply a fade effect on the "hole"
	 */
	public void focus(Rectangle viewport, Rectangle viewport2, boolean fade)
	{
		beginFX(false);
		
		AlphaShell focusAlpha = null;
		if ( fade )
		{
			focusAlpha = new AlphaShell(display);
			focusAlpha.setAlpha(alpha.getAlpha());
			Region region2 = new Region();
			region2.add(viewport);
			region2.add(viewport2);
			focusAlpha.getShell().setRegion(region2);
			focusAlpha.getShell().open();
		}
		Point localTop = alpha.getShell().toControl(viewport.x, viewport.y);
		Rectangle localViewport = new Rectangle(localTop.x, localTop.y, viewport.width, viewport.height);
		region.subtract(localViewport);
		localTop = alpha.getShell().toControl(viewport2.x, viewport2.y);
		localViewport = new Rectangle(localTop.x, localTop.y, viewport2.width, viewport2.height);
		region.subtract(localViewport);		
		alpha.getShell().setRegion(region);
		
		if ( fade )
		{
			focusAlpha.getShell().getDisplay().asyncExec(new AlphaFader(focusAlpha, alpha.getAlpha(), 1, true, null, new Runnable(){
				public void run() {
					refreshCaption();
				}
			}));
//			for (int a=alpha.getAlpha(); a>=1; a--)
//			{
//				focusAlpha.setAlpha(a);
//				refreshCaption();
//				sleep();
//			}
//			
//			focusAlpha.getShell().close();
		}
		
		
	}
	
	/**
	 * clears a previously created "hole" in the transparency created via focus().
	 * 
	 * warning: calling fadeOut() and then unfocus() will restore an "invisible" shell on top
	 * of the desktop, catching all click events.
	 * 
	 * @param viewport : if null, use the entire display
	 * @param fade : apply a fade effect on the "hole"
	 */
	public void unfocus(Rectangle viewport, final boolean fade)
	{
		beginFX(false);
		
		if ( viewport==null )
		{
			Rectangle fullBounds = alpha.getShell().getBounds();
			viewport = new Rectangle(0,0,fullBounds.width,fullBounds.height);
		}
		
		AlphaShell focusAlpha = null;
		if ( fade )
		{
			alpha.setAlpha(128);
			Point localTop = alpha.getShell().toControl(viewport.x, viewport.y);
			Rectangle localViewport = new Rectangle(localTop.x, localTop.y, viewport.width, viewport.height);
			region.subtract(localViewport);	
			alpha.getShell().setRegion(region);
			focusAlpha = new AlphaShell(display);
			focusAlpha.setAlpha(1);
			focusAlpha.getShell().setBounds(viewport);
			focusAlpha.getShell().open();
			final Rectangle fViewport = viewport;
			final AlphaShell fFocusAlpha = focusAlpha;
			focusAlpha.getShell().getDisplay().asyncExec(new AlphaFader(focusAlpha, 1, alpha.getAlpha(), true, null, new Runnable(){
				public void run() {
					Point localTop = alpha.getShell().toControl(fViewport.x, fViewport.y);
					Rectangle localViewport = new Rectangle(localTop.x, localTop.y, fViewport.width, fViewport.height);
					region.add(localViewport);
					alpha.getShell().setRegion(region);
					
					if ( fade )
						fFocusAlpha.getShell().close();
					
					refreshCaption();
				}
			}));
		}
		
	}
	
	/**
	 * similar to focus() - applies a hole the width of the monitor associated with this AlphaFx object.
	 * this also clears all previous "holes" created by focus()
	 * 
	 * @param height
	 * @param fade
	 * 
	 * warning: calling fadeOut() and then focus() will restore an "invisible" shell on top
	 * of the desktop, catching all click events.
	 * 
	 * @return Rectangle associated with the letterboxed region, which can be used with unfocus()
	 */
	public Rectangle letterbox(int height, boolean fade)
	{
		beginFX(true);
		
		Rectangle
				shellBounds		  = alpha.getShell().getBounds(),
				fullBounds		  = new Rectangle(0,0,shellBounds.width,shellBounds.height),
				letterboxViewport = new Rectangle(0,(shellBounds.height-height)/2,shellBounds.width,height);
		
		if ( fade )
		{
			for (int i=shellBounds.height; i>=height; i-=2)
			{
				region.add(fullBounds);
				region.subtract(0, (shellBounds.height-i)/2, shellBounds.width, i);
				alpha.getShell().setRegion(region);
				refreshCaption();
			}
		}
		
		region.add(fullBounds);
		region.subtract(letterboxViewport);
		alpha.getShell().setRegion(region);

		refreshCaption();
		
		return letterboxViewport;
	}
	
	public void letterbox(int y, int height, boolean fade)
	{		
		init();
		applyBounds();
	
		Rectangle
				shellBounds		  = alpha.getShell().getBounds(),
				viewport = new Rectangle(shellBounds.x,y,shellBounds.width,height);
		
		focus(viewport, fade);

	}
	
	
	/**
	 * applies a ShellCaption onto the alphaShell associated with this object.  does not refresh alphaShell (call getAlphaShell().getShell()... )
	 * 
	 * @param caption - use null to remove caption
	 */
	public void setCaption(ShellCaption caption)
	{
		if ( caption==null && this.caption!=null )
			this.caption.setShell(null);
		
		this.caption = caption;
	}
	
	/**
	 * return ShellCaption associated with this object
	 * 
	 * @return ShellCaption
	 */
	public ShellCaption getCaption()
	{
		return caption;
	}
	
	// @tag todo : follow mouseover
	
//	private void sleep() 
//	{
//		sleep(SLEEP_DURATION);
//	}
//	
//	private void sleep(int duration) 
//	{
//		try {
//			Thread.sleep(duration);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		} 
//	}
}
