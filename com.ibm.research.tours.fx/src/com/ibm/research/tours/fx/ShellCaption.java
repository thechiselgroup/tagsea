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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * a text caption to apply on a Shell.  captions are applied directly on the shell's graphic context during paint events.
 * do not use too many captions on the same shell, otherwise you will get flicker (no back-buffering used).
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */
public class ShellCaption implements PaintListener {

	private Font   font;
	private Shell  shell;
	private Color  foreground, background;
	private String text;
	private int    x,y;
	private boolean useRelativeXY=false;
	
	/**
	 * constructor
	 * 
	 * @param shell
	 * @param x
	 * @param y
	 * @param text
	 * @param font
	 * @param foreground
	 * @param background
	 * @param useRelative  - if true, interpret x,y as relative to the bounds of the shell.  +ve values: offset from 0.  -ve values: offset from maximum extents
	 */
	public ShellCaption(Shell shell, int x, int y, String text, Font font, Color foreground, Color background, boolean useRelative)
	{
		setX(x);
		setY(y);
		setText(text);
		setFont(font);
		setForeground(foreground);
		setBackground(background);
		setRelativeXY(useRelative);
		setShell(shell); // important to do this last, because this hooks in the paint listener
	}

	/**
	 * constructor - need to call setShell later
	 * 
	 * @param x
	 * @param y
	 * @param text
	 * @param font
	 * @param foreground
	 * @param background
	 * @param useRelative
	 */
	public ShellCaption(int x, int y, String text, Font font, Color foreground, Color background, boolean useRelative)
	{
		setX(x);
		setY(y);
		setText(text);
		setFont(font);
		setForeground(foreground);
		setBackground(background);
		setRelativeXY(useRelative);
	}
	
	public void paintControl(PaintEvent e) {
		
		if ( text==null || foreground==null )
			return;
		
		GC gc = e.gc;
		
		Color oldForeground = gc.getForeground(),
			  oldBackground = gc.getBackground();
		Font  oldFont = gc.getFont();
		int   oldAntiAlias = gc.getTextAntialias();
		
		gc.setTextAntialias(SWT.ON);
		
		if ( background!=null )
			gc.setBackground(background);
		if ( font!=null )
			gc.setFont(font);
		
		int finalX = x,
		    finalY = y;
		
		if ( useRelativeXY )
		{
			Point size 		 = shell.getSize();
			Point textExtent = gc.textExtent(text);

			if ( x>=0 )
				finalX = x;
			else
			{
				finalX  = size.x + x;
				finalX -= textExtent.x;
			}
			
			if ( y>=0 )
				finalY = y;
			else
			{
				finalY  = size.y + y;
				finalY -= textExtent.y;
			}
		}
	
		gc.setForeground(foreground);
		gc.drawText(text, finalX, finalY, background==null);
		
		gc.setForeground(oldForeground);
		gc.setBackground(oldBackground);
		gc.setFont(oldFont);
		gc.setTextAntialias(oldAntiAlias);
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Color getForeground() {
		return foreground;
	}

	public void setForeground(Color foreground) {
		this.foreground = foreground;
	}

	public Shell getShell() {
		return shell;
	}

	/**
	 * associates shell paint listener with this caption
	 * 
	 * @param shell - use null to remove association 
	 */
	public void setShell(Shell shell) {
		
		if ( this.shell!=null )  // remove any previous association
			this.shell.removePaintListener(this);
		
		this.shell = shell;
		
		if ( this.shell!=null )
			this.shell.addPaintListener(this);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public boolean getRelativeXY() {
		return useRelativeXY;
	}
	
	public void setRelativeXY(boolean enabled) {
		this.useRelativeXY = enabled;
	}
	
	public void refresh() {
		shell.redraw();
		shell.update();
	}
	
	public static void main(String[] args) {
		Display display = new Display();

		Shell shell = new Shell(display);
		
		Font  font  	 = new Font(display,"Tahoma",24,SWT.BOLD);
		Color foreground = display.getSystemColor(SWT.COLOR_RED),
			  background = display.getSystemColor(SWT.COLOR_YELLOW);
		
		new ShellCaption(shell,0,0,"upper left",font,foreground,background,false);
		new ShellCaption(shell,-10,0,"upper right",font,foreground,null,true);
		new ShellCaption(shell,0,-30,"lower left",font,foreground,null,true);
		new ShellCaption(shell,-10,-30,"lower right",font,foreground,background,true); 
		
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		font.dispose();
		
		display.dispose();
	}
}
