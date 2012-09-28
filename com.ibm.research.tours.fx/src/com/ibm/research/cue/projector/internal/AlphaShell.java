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
package com.ibm.research.cue.projector.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * SWT API built on top of LayeredWindow.  Choose a constructor, use setAlpha / setColorKey, and
 * call getShell().open() to see this in action.
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */

public class AlphaShell {
	private Shell shell = null;
	private RGB   colorKey = null;
	private int   alpha = 1;
	
	/**
	 * creates a on-top black shell with no borders, no trimmings
	 */
	public AlphaShell(Display display)
	{
		shell = new Shell(display,SWT.NO_TRIM|SWT.MODELESS|SWT.ON_TOP);
		shell.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
	}

	/**
	 * creates a on-top shell with no borders, no trimmings 
	 * @param color - background color
	 */
	public AlphaShell(Color color) 
	{
		Display display = null;
		
		if ( color.getDevice() instanceof Display )
			display = (Display) color.getDevice();
		else
			display = new Display();
		
		shell = new Shell(display,SWT.NO_TRIM|SWT.MODELESS|SWT.ON_TOP);
		shell.setBackground(color);
	}
	
	/**
	 * sets up an existing shell as an alpha shell
	 * @param shell
	 */
	public AlphaShell(Shell shell)
	{
		this.shell = shell;
	}
	
	/**
	 * update alpha / color key settings on shell
	 */
	private void applyAlpha() {
		if (colorKey == null) {
			shell.setAlpha(alpha);
		} else {
			if (!colorKey.equals(shell.getBackground().getRGB())) {
				final Color tempColor = new Color(shell.getDisplay(), colorKey);
				shell.setBackground(tempColor);
				shell.addDisposeListener(new DisposeListener(){
					public void widgetDisposed(DisposeEvent e) {
						tempColor.dispose();
					}
				});
			}
		}
	}

	/**
	 * sets color key for alpha blending.  this requires you to set up a background image associated
	 * with the shell that has a color that matches the color key.
	 * 
	 * @param key - can be null to use no color keying
	 */
	public void setColorKey(RGB key)
	{
		colorKey = key;
		applyAlpha();
	}
	
	/**
	 * sets alpha blending level
	 * @param alpha - 1 to 255
	 */
	public void setAlpha(int alpha)
	{
		this.alpha = alpha;
		applyAlpha();
	}
	
	/**
	 * returns color key set by setColorKey
	 * @return RGB or null
	 */
	public RGB getColorKey()
	{
		return colorKey;
	}
	
	/**
	 * returns alpha set by setAlpha
	 * @return int
	 */
	public int getAlpha()
	{
		return alpha;
	}
	
	/**
	 * returns actual associated shell
	 * @return Shell
	 */
	public Shell getShell()
	{
		return shell;
	}
	
	/**
	 * Closes and disposes the shell.
	 */
	public void close() {
		if (getShell() != null && !getShell().isDisposed()) {
			getShell().close();
			getShell().dispose();
		}
	}
	
	// simple example - note that your launch config must include os/win32/x86/LayeredWindow.dll
	// e.g. -Djava.library.path=${project_loc}/os/win32/x86   // @tag dll os win32 x86 swt : add this to your launch config VM args to add dlls for standalone java apps
	public static void main(String[] args)
	{
		final Display display = new Display();
		AlphaShell alpha = new AlphaShell(display);
		Shell shell = alpha.getShell();
		
		shell.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				Shell shell = (Shell) e.getSource();
				Display display = shell.getDisplay();
				
				Region region = new Region(display);
				Rectangle bounds = shell.getBounds();
				region.add(0,0,bounds.width,bounds.height);
				
				AlphaShell hole = new AlphaShell(display.getSystemColor(SWT.COLOR_RED));
				hole.setAlpha(255);
				
				hole.getShell().setLocation(bounds.x+25, bounds.y+25);
				hole.getShell().setSize(500,500);
				
				hole.getShell().open();
				hole.getShell().moveBelow(shell);
				
				region.subtract(25,25,500,500);
				shell.setRegion(region);
				for (int i=255; i>=1; i--)
				{
					hole.setAlpha(i);
					try {
						Thread.sleep(22);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}  // repainting a smaller shell is a lot faster
				}
		
				hole.getShell().close();
				region.dispose();
				shell.close();
			}
		});
		
		alpha.setAlpha(1);
		
		shell.open();
		
		Region letterbox = new Region(display);
		final Rectangle shellBounds = shell.getBounds();
		int h = 255;
		letterbox.subtract(0,(shellBounds.height-h)/2,shellBounds.width,h);
		shell.setRegion(letterbox);
		
		final Font font = new Font(display,"Tahoma",24,SWT.NORMAL);
		shell.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				gc.setTextAntialias(SWT.ON);
				gc.setFont(font);
				gc.setForeground(display.getSystemColor(SWT.COLOR_YELLOW));
				gc.drawText("this is a text test\nin an alphaShell\nclick to quit", 48, shellBounds.height-150, true);
			}
		});
		
		for (int i=1; i<=255; i+=3)
		{
			h = 255-i;
			letterbox.add(0,0,shellBounds.width,shellBounds.height);
			letterbox.subtract(0,(shellBounds.height-h)/2,shellBounds.width,h);
			alpha.setAlpha(i);
			shell.setRegion(letterbox);
			shell.update();
		}

		letterbox.add(0,0,shellBounds.width,shellBounds.height);
		shell.setRegion(letterbox);
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		font.dispose();
		display.dispose();
	}
}
