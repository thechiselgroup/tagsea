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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.ibm.research.tours.ToursPlugin;

public class TourControl 
{
	private Composite fInnerComposite;
	private ToolBar fToolBar;
	private ToolBar fStopBar;
	
	private ToolItem fPrevious;
	private ToolItem fNext;
	private ToolItem fStop;
//	private CLabel fTitleLabel;
	private CLabel fCounterLabel;
	private CLabel fTimeLabel;
	private CLabel fElementLabel;
	private List<ITourControlListener> fListeners;
	private CTabItem fTabItem;
	private CTabFolder fFolder;
	/**
	 * The tour runtime UI
	 */
	public TourControl(CTabFolder folder) 
	{
		fFolder = folder;
	}	
	
	private Color getBackground()
	{
		return Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	}
	
	public void init()
	{
		fTabItem = new CTabItem(fFolder,SWT.NONE);
		fTabItem.setText("MyTour.tour");
		fTabItem.setImage(ToursPlugin.getDefault().getImageRegistry().get(ToursPlugin.IMG_TOUR));
		
		fInnerComposite = new Composite(fFolder,SWT.NONE);
		GridLayout innerlayout = new GridLayout(6,false);
		innerlayout.marginHeight = 0;
		innerlayout.marginTop = 2;
		innerlayout.marginBottom = 2;
		innerlayout.marginWidth = 2;
		innerlayout.horizontalSpacing = 3;
		innerlayout.verticalSpacing = 0;
		fInnerComposite.setLayout(innerlayout);
		fInnerComposite.setBackground(getBackground());
		
		fToolBar = new ToolBar(fInnerComposite,SWT.FLAT|SWT.HORIZONTAL);
		fToolBar.setBackground(getBackground());
		
		fPrevious = new ToolItem(fToolBar,SWT.PUSH);
		fPrevious.setImage(ToursPlugin.getDefault().getImageRegistry().get(ToursPlugin.IMG_PREVIOUS));
		fPrevious.setToolTipText("Previous");
		fPrevious.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				firePreviousEvent();
			}
		
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		
		fNext = new ToolItem(fToolBar,SWT.PUSH);
		fNext.setImage(ToursPlugin.getDefault().getImageRegistry().get(ToursPlugin.IMG_NEXT));
		fNext.setToolTipText("Next");
		fNext.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				fireNextEvent();
			}
		
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		
//		fTitleLabel = new CLabel(fInnerComposite,SWT.NONE);
//		fTitleLabel.setText("Tour Title");
//		fTitleLabel.setLayoutData(new GridData());
//		fTitleLabel.setBackground(getBackground());
//		fTitleLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.BANNER_FONT));
		
		fCounterLabel = new CLabel(fInnerComposite,SWT.CENTER|SWT.SHADOW_OUT);
		fCounterLabel.setBackground(getBackground());
		fCounterLabel.setText("000/000"); // Were good up to 999/999 slides
		int counterWidth = fCounterLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		GridData counterData = new GridData();
		counterData.widthHint = counterWidth;
		fCounterLabel.setLayoutData(counterData);
		fCounterLabel.setText("00/00");
		
		fElementLabel = new CLabel(fInnerComposite,SWT.CENTER);
		fElementLabel.setBackground(getBackground());
		fElementLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		fTimeLabel = new CLabel(fInnerComposite,SWT.CENTER|SWT.SHADOW_OUT);
		fTimeLabel.setBackground(getBackground());
		
		fTimeLabel.setText("999:99"); // Were good up to 999:99
		int timeWidth = fTimeLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		GridData timeData = new GridData();
		timeData.widthHint = timeWidth;
		fTimeLabel.setLayoutData(timeData);
		fTimeLabel.setText("00:00");
		
		fStopBar = new ToolBar(fInnerComposite,SWT.FLAT|SWT.HORIZONTAL);
		fStopBar.setBackground(getBackground());
		
		fStop = new ToolItem(fStopBar,SWT.PUSH);
		fStop.setImage(ToursPlugin.getDefault().getImageRegistry().get(ToursPlugin.IMG_DELETE));
		fStop.setToolTipText("Exit");
		fStop.addSelectionListener(new SelectionListener() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				fireStopEvent();
			}
		
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		
		fInnerComposite.pack();
		fTabItem.setControl(fInnerComposite);
	}

	public CTabItem getTabItem()
	{
		return fTabItem;
	}
	
	protected void fireStopEvent() 
	{
		for(ITourControlListener listener : getListeners())
			listener.stop();
	}

	protected void firePreviousEvent() 
	{
		for(ITourControlListener listener : getListeners())
			listener.previous();
	}

	protected void fireNextEvent() 
	{
		for(ITourControlListener listener : getListeners())
			listener.next();
	}

	public void dispose()
	{
		fTabItem.dispose();
	}
	
	public ToolItem getPrevious() 
	{
		return fPrevious;
	}

	public ToolItem getNext() 
	{
		return fNext;
	}

	public ToolItem getStop() 
	{
		return fStop;
	}

//	public CLabel getTitleLabel() 
//	{
//		return fTitleLabel;
//	}

	public CLabel getCounterLabel() 
	{
		return fCounterLabel;
	}

	public CLabel getTimeLabel() 
	{
		return fTimeLabel;
	}

	public CLabel getElementLabel() 
	{
		return fElementLabel;
	}

	public void addTourControlListener(ITourControlListener listener)
	{
		if(listener!=null && !getListeners().contains(listener))
			getListeners().add(listener);
	}
	
	public void removeTourControlListener(ITourControlListener listener)
	{
		getListeners().remove(listener);
	}
	
	protected List<ITourControlListener> getListeners() 
	{
		if(fListeners == null)
			fListeners = new ArrayList<ITourControlListener>();
		return fListeners;
	}
}
