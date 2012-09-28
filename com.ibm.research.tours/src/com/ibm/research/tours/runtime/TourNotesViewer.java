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

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.ibm.research.tours.controls.HorizontalGrip;

public class TourNotesViewer 
{
	private static final int GRIP_HEIGHT = 10;

	private StyledText fStyledText;
	private Shell fShell;
	private Composite fComposite;
	private HorizontalGrip fGrip;
	private Label fSeperator;
	private CLabel fLabel;
	private Timer fKeepOnTopTimer;
	private Font fFont;

//	private List<ITourHarnessListener> fListeners;
	
	public TourNotesViewer() 
	{
		createShell();
		createRootCompsoite();
		createGrip();
		createSeperator();
		//createLabel();
		createStyledText();
	}

	private void createLabel() 
	{
		fLabel = new CLabel(fComposite,SWT.CENTER);
		fLabel.setText("Notes");
		fLabel.setBackground(getBackground());
		GridData data = new GridData();
		data.horizontalAlignment = GridData.CENTER;
		fLabel.setLayoutData(data);
	}

	private Shell createShell()
	{
		fShell = new Shell(Display.getDefault(),SWT.ON_TOP|SWT.MODELESS);
		fShell.setLayout(new FillLayout());
		fShell.setBackground(getBackground());
		return fShell;
	}

	private Composite createRootCompsoite()
	{
		fComposite = new Composite(fShell,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 4;
		layout.marginWidth = 4;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		fComposite.setLayout(layout);
		fComposite.setBackground(getBackground());
		return fComposite;
	}

	private StyledText createStyledText()
	{
		Monitor monitor = Display.getDefault().getActiveShell().getMonitor();
		int width = monitor.getBounds().width;
		int height = monitor.getBounds().height;
		
		fStyledText = new StyledText(fComposite,SWT.WRAP|SWT.V_SCROLL);
		
		GridData data = new GridData();
		data.widthHint = width/3;
		data.heightHint = height/10;
		fStyledText.setLayoutData(data);

		createContextMenu(fStyledText);
		
		return fStyledText;
	}
	
	private Menu createContextMenu(final Control control)
	{
		Menu menu = new Menu(control);
		
		MenuItem increaseFont = new MenuItem(menu,SWT.CASCADE);
		increaseFont.setText("+ increase font");
		increaseFont.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				adjustFont(control, 1);
			}
		});
		
		MenuItem decreaseFont = new MenuItem(menu,SWT.CASCADE);
		decreaseFont.setText("- decrease font");
		decreaseFont.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				adjustFont(control, -1);
			}
		});
		
		fStyledText.setMenu(menu);
		
		return menu;
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
		fGrip.setTooltips("Hide Notes", "Show Notes");

		fGrip.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				redraw();
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
		int height = monitor.getBounds().height;

		fShell.pack();
		
		// @tag tours notes minimized : set default to minimized for now
		fGrip.setToggleState(false);
		fShell.setLocation(width/2 - fShell.getSize().x/2,height - GRIP_HEIGHT);
		
		// fShell.setLocation(width/2 - fShell.getSize().x/2,height - fShell.getSize().y);
		
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

	/**
	 * Open the harness
	 */
	public void dispose()
	{
		fKeepOnTopTimer.cancel();
		fShell.dispose();
		if ( fFont!=null )
			fFont.dispose();
	}
	
	private Color getBackground()
	{
		return Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	}

	public StyledText getStyledText() 
	{
		return fStyledText;
	}
	
	public void setGripBackground(Color color)
	{
		fGrip.setBackground(color);
	}

	public void moveAbove() 
	{
		if(fShell!=null && !fShell.isDisposed())
			if(Display.getDefault().getActiveShell() != fShell)
			{
				redraw();
				fShell.moveAbove(null);
			}
	}
	
	private void redraw()
	{
		Monitor monitor = null;
		if ( Display.getDefault().getActiveShell()!=null )
			monitor = Display.getDefault().getActiveShell().getMonitor();
		else
			monitor = Display.getDefault().getPrimaryMonitor();
		
		int height = monitor.getBounds().height;
		int width = monitor.getBounds().width;

		
		if(!fGrip.getToggleState())
		{	
			fShell.setLocation(width/2 - fShell.getSize().x/2,height - GRIP_HEIGHT);
		}
		else
		{
			fShell.setLocation(width/2 - fShell.getSize().x/2,height - fShell.getSize().y);
		}		
	}
	
	private void adjustFont(Control control, int deltaHeight)
	{
		Font oldFont = control.getFont();
		FontData oldFontData = oldFont.getFontData()[0];
		if ( oldFontData.height+deltaHeight<1 )
			return;
		// @tag fontdata tours : new FontData(String,float,int) no longer visible, using (String,int,int) instead
		FontData newFontData = new FontData(oldFontData.getName(),(int) (oldFontData.height+deltaHeight),oldFontData.getStyle());
		newFontData.setLocale(oldFontData.getLocale());
		
		boolean disposeOldFont = fFont!=null;
			
		fFont = new Font(oldFont.getDevice(),newFontData);
		control.setFont(fFont);

		// only dispose fonts created in this method - the first time this method gets called, oldFont is the default system font used in many controls
		if (disposeOldFont)
			oldFont.dispose();
	}	
	
//	private void fireDisposeEvent() 
//	{
//		for(ITourHarnessListener listener : getListeners())
//			listener.disposed(this);
//	}
//
//	public void addTourHarnessListener(ITourHarnessListener listener)
//	{
//		if(listener!=null && !getListeners().contains(listener))
//			getListeners().add(listener);
//	}
//	
//	public void removeTourHarnessListener(ITourHarnessListener listener)
//	{
//		getListeners().remove(listener);
//	}
//	
//	protected List<ITourHarnessListener> getListeners() 
//	{
//		if(fListeners == null)
//			fListeners = new ArrayList<ITourHarnessListener>();
//		return fListeners;
//	}
}
