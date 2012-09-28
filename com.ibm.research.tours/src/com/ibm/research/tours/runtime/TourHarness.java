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
package com.ibm.research.tours.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.ibm.research.tours.controls.HorizontalGrip;

public class TourHarness 
{
	private static final int GRIP_HEIGHT = 10;

	private CTabFolder fFolder;
	private Shell fShell;
	private Composite fComposite;
	private HorizontalGrip fGrip;
	private Label fSeperator;
	private Timer fKeepOnTopTimer;

	private List<ITourHarnessListener> fListeners;

	public TourHarness() 
	{
		createShell();
		createRootCompsoite();
		createTabFolder();
		createSeperator();
		createGrip();
	}

	private Shell createShell()
	{
		fShell = new Shell(Display.getDefault(),SWT.ON_TOP|SWT.MODELESS);
		fShell.setLayout(new FillLayout());
		fShell.setBackground(getBackground());
		fShell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				fireDisposeEvent();
			}
		});
		return fShell;
	}

	private Composite createRootCompsoite()
	{
		fComposite = new Composite(fShell,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		fComposite.setLayout(layout);
		fComposite.setBackground(getBackground());
		return fComposite;
	}

	private CTabFolder createTabFolder()
	{
		Display display = fShell.getDisplay();
		Color c1 = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND),
		c2 = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);

		fFolder = new CTabFolder(fComposite,SWT.FLAT|SWT.NO_TRIM);
		fFolder.addFocusListener(new FocusListener() 
		{
			public void focusLost(FocusEvent e) 
			{

			}

			public void focusGained(FocusEvent e) 
			{
				// Get rid of the ugly underline
				if(fFolder.getItemCount() > 0)
					fFolder.getItem(0).getControl().setFocus();
			}

		});
		fFolder.setLayoutData(new GridData());
		fFolder.setSelectionBackground(new Color[] {c1, c2},new int[] {100}, true);
		fFolder.setSelectionForeground(fShell.getDisplay().getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
		fFolder.setBackground(getBackground());
		fFolder.setUnselectedImageVisible(false);
		fFolder.setSimple(false);
		return fFolder;
	}

	public Label createSeperator()
	{
		fSeperator = new Label(fComposite,SWT.SEPARATOR|SWT.HORIZONTAL);
		GridData seperatorData = new GridData(GridData.FILL_HORIZONTAL);
		fSeperator.setLayoutData(seperatorData);
		return fSeperator;
	}

	public HorizontalGrip createGrip()
	{
		fGrip = new HorizontalGrip(fComposite,SWT.HORIZONTAL);
		GridData gripData = new GridData(GridData.FILL_HORIZONTAL);
		gripData.heightHint = GRIP_HEIGHT;
		fGrip.setLayoutData(gripData);
		fGrip.setBackground(getBackground());
		fGrip.setTooltips("Hide Controls", "Show Controls");

		fGrip.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				int x = fShell.getBounds().x;

				if(!fGrip.getToggleState())
				{	
					int height = fShell.getBounds().height;
					fShell.setLocation(x,GRIP_HEIGHT - height);
				}
				else
				{
					fShell.setLocation(x,0);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});	
		return fGrip;
	}


	/**
	 * Open the harness
	 */
	public void open()
	{
		Monitor monitor = Display.getDefault().getActiveShell().getMonitor();
		int width = monitor.getBounds().width;

		fShell.pack();
		fShell.setLocation(width/2 - fShell.getSize().x/2,0);
		fShell.open();

		fKeepOnTopTimer = new Timer();
		fKeepOnTopTimer.schedule(new TimerTask() 
		{
			@Override
			public void run() 
			{
				if ( Display.getDefault()==null )
					return;
				
				Display.getDefault().asyncExec(new Runnable() 
				{
					public void run() 
					{
						moveAbove();
					}	
				});

			}

		},0, 100);
	}

	private Color getBackground()
	{
		return Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	}

	/**
	 * Add a tour control to the harness
	 * @param tourControl
	 */
	public CTabFolder getTabFolder() 
	{
		return fFolder;
	}

	public void changed() 
	{
		if(fShell!=null && !fShell.isDisposed())
		{
			if(fFolder.getItemCount() == 0)
			{
				fKeepOnTopTimer.cancel();
				fKeepOnTopTimer = null;
				fShell.dispose();
				return;
			}

			CTabItem[] items = fFolder.getItems();
			int max = 0;

			for(CTabItem item : items)
			{	
				int width = item.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).x;

				if(width > max)
					max = width;
			}

			GridData data = (GridData)fFolder.getLayoutData();
			data.widthHint = max;
			fFolder.setLayoutData(data);

			Monitor monitor = Display.getDefault().getActiveShell().getMonitor();
			int width = monitor.getBounds().width;
			fShell.pack();
			fShell.setLocation(width/2 - fShell.getSize().x/2,0);
		}
	}

	private void fireDisposeEvent() 
	{
		for(ITourHarnessListener listener : getListeners())
			listener.disposed(this);
	}

	public void addTourHarnessListener(ITourHarnessListener listener)
	{
		if(listener!=null && !getListeners().contains(listener))
			getListeners().add(listener);
	}

	public void removeTourHarnessListener(ITourHarnessListener listener)
	{
		getListeners().remove(listener);
	}

	protected List<ITourHarnessListener> getListeners() 
	{
		if(fListeners == null)
			fListeners = new ArrayList<ITourHarnessListener>();
		return fListeners;
	}

	public void moveAbove() 
	{
		if(fShell!=null && !fShell.isDisposed())
			if(Display.getDefault().getActiveShell() != fShell)
				fShell.moveAbove(null);
	}
}
