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
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * associates a rounded Region with a given Shell.  call setRadius() to specify rounded size.
 * call refresh() to apply effect.  call dispose() when done.  see main() for example usage.
 * 
 * @tag todo roundedshell : need to fix radius calculation - getting uneven rounded shells
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */
public class RoundedShell {

	private Shell shell;
	private Region region;
	private int radius = 10;
	private boolean showUL = true, showUR = true, showLL = true, showLR =true;

	/**
	 * constructor 
	 * @param shell
	 */
	public RoundedShell(Shell shell)
	{
		this.shell = shell;
		region = new Region(shell.getDisplay());
		
		this.shell.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				computeRegion();
			}
		});
		
		this.shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});
	}
	
	public void setRadius(int radius)
	{
		this.radius = radius;
	}
	
	public int getRadius()
	{
		return radius;
	}
	
	public Shell getShell()
	{
		return shell;
	}
	
	public void dispose()
	{
		if ( region!=null && !region.isDisposed() )
			region.dispose();
		
		if ( shell!=null && !shell.isDisposed() )
			shell.dispose();
	}
	
	public void setRoundedCorners(boolean upperLeft, boolean upperRight, boolean lowerLeft, boolean lowerRight)
	{
		showUL = upperLeft;
		showUR = upperRight;
		showLL = lowerLeft;
		showLR = lowerRight;
	}
	
	public void refresh()
	{
		computeRegion();
	}
	
	private void computeRegion()
	{
		// @tag todo roundedshell : eventually this should be optimized (e.g. check if size changed since last call, etc)
		
		Rectangle bounds = shell.getBounds();
		region.add(0,0,bounds.width,bounds.height);
		
		if ( radius<1 )
		{
			shell.setRegion(region);
			return;
		}
		
		double y[] = new double[(int) radius+1];
		         
		for (double x=0; x<=radius; x++)
		{
			double angle = Math.acos(x/radius);
			y[(int) x] = radius * Math.sin(angle);
		}
		
		int size = (((int) radius+1)*2)+2;
		
		int upperLeft[] = new int[size];
		{
			int i=0;
			
			upperLeft[i++] = 0;
			upperLeft[i++] = 0;
			
			for (int x=0; x<=radius; x++)
			{
				upperLeft[i++] = (int) (radius - x);
				upperLeft[i++] = (int) (radius - y[x]);
			}
			
			if ( showUL )
				region.subtract(upperLeft);
		}

		int upperRight[] = new int[size];
		{
			int i=0;
			
			upperRight[i++] = bounds.width;
			upperRight[i++] = 0;
			
			for (int x=0; x<=radius; x++)
			{
				upperRight[i] = bounds.width - upperLeft[i];
				i++;
				upperRight[i] = upperLeft[i];  
				i++;
			}
			
			if ( showUR )
				region.subtract(upperRight);
		}
		
		int lowerLeft[] = new int[size];
		{
			int i=0;
			
			lowerLeft[i++] = 0;
			lowerLeft[i++] = bounds.height;

			for (int x=0; x<=radius; x++)
			{
				lowerLeft[i] = upperLeft[i];
				i++;
				lowerLeft[i] = bounds.height - upperLeft[i];  
				i++;
			}
			
			if ( showLL )
				region.subtract(lowerLeft);
		}
		
		int lowerRight[] = new int[size];
		{
			int i=0;
			
			lowerRight[i++] = bounds.width;
			lowerRight[i++] = bounds.height;

			for (int x=0; x<=radius; x++)
			{
				lowerRight[i] = bounds.width - upperLeft[i];
				i++;
				lowerRight[i] = bounds.height - upperLeft[i];  
				i++;
			}
			
			if ( showLR )
				region.subtract(lowerRight);
		}
		
		shell.setRegion(region);
	}

	public static void main(String[] args) {
		Display display = new Display();

		final RoundedShell rounded = new RoundedShell(new Shell(display,SWT.NO_TRIM|SWT.ON_TOP));
		Shell shell = rounded.getShell();
		
		shell.setBackground(display.getSystemColor(SWT.COLOR_DARK_CYAN));
		
		GridLayout layout = new GridLayout(1,true);
		layout.marginTop = 100;
		layout.marginBottom = 100;
		layout.marginLeft = 100;
		layout.marginRight = 100;
		layout.horizontalSpacing = 25;
		shell.setLayout(layout);
		
		Button decRadius = new Button(shell,SWT.PUSH);
		decRadius.setText("decrease radius");
		decRadius.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int r = rounded.getRadius();
				r-=2;
				if ( r<=0 ) r=0;
				rounded.setRadius(r);
				rounded.refresh();
			}
		});
		
		Button incRadius = new Button(shell,SWT.PUSH);
		incRadius.setText("increase radius");
		incRadius.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int r = rounded.getRadius();
				r+=2;
				rounded.setRadius(r);
				rounded.refresh();
			}
		});
		
		final Button upperLeft = new Button(shell,SWT.CHECK);
		upperLeft.setText("rounded upper left");
		upperLeft.setSelection(true);
		
		final Button upperRight = new Button(shell,SWT.CHECK);
		upperRight.setText("rounded upper right");
		upperRight.setSelection(true);
		
		final Button lowerLeft = new Button(shell,SWT.CHECK);
		lowerLeft.setText("rounded lower left");
		lowerLeft.setSelection(true);
		
		final Button lowerRight = new Button(shell,SWT.CHECK);
		lowerRight.setText("rounded lower right");
		lowerRight.setSelection(true);
		
		SelectionAdapter roundedListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				rounded.setRoundedCorners(upperLeft.getSelection(), upperRight.getSelection(), lowerLeft.getSelection(), lowerRight.getSelection());
				rounded.refresh();
			}
		};
		
		upperLeft.addSelectionListener(roundedListener);
		upperRight.addSelectionListener(roundedListener);
		lowerLeft.addSelectionListener(roundedListener);
		lowerRight.addSelectionListener(roundedListener);
		
		
		Button exit = new Button(shell,SWT.PUSH);
		exit.setText("exit");
		exit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rounded.dispose();
			}
		});
		
		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}
}
