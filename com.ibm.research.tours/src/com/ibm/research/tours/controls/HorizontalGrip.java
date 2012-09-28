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
package com.ibm.research.tours.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

/**
 * Creates a vertical rounded bar with "thumb" dots on it (similar to Malibu).
 * To use: instantiate, call setTooltips(), call setCornerBackground(), add your own
 * selection listneers via addSelectionListener.
 * 
 * default toggle state is true
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */
public class HorizontalGrip extends Composite {

	private final static int DOT_DISTANCE = 5; 
	private final static int DOT_NUMBER = 7;
	
	private List<SelectionListener> fListeners;
	
	private String  fToggleTooltip;
	private String  fUntoggleTooltip;
	private boolean fToggleState = true;
	
	private Cursor  fHandCursor;
	private Color   fCornerbackground = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	private Color   fLineColor;
	
	/**
	 * constructor
	 * @param style the style for the composite  - only vertical layout is currently supported
	 * @see SWT#VERTICAL
	 */
	public HorizontalGrip(Composite parent, int style) 
	{
		super(parent, checkStyle(style));
		createControl();
	}
	
	protected List<SelectionListener> getListeners()
	{
		if(fListeners == null)
			fListeners = new ArrayList<SelectionListener>();
		
		return fListeners;
	}
	
	static int checkStyle (int style) {
		/* Remove the vertical and horizontal styles. */
		return style & ~(SWT.VERTICAL | SWT.HORIZONTAL);
	}	
	
	protected void createControl() 
	{
		fLineColor = new Color(getDisplay(), new RGB(161,188,236));
		
		fHandCursor = new Cursor(getDisplay(), SWT.CURSOR_HAND);
		setCursor(fHandCursor);		
		setLayout(new FillLayout(SWT.VERTICAL));
		
		addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				widgetSelected(e);
			}
		});
		
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				fHandCursor.dispose();
				fLineColor.dispose();
			}
		});
		
		// paint the dots in the handle and rounded edge on top
		// @tag todo toggle handle : eventually needs to support horizontal layout and positioning of rounded corner
		addPaintListener(new PaintListener() {		
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;		
				
				Color white = getDisplay().getSystemColor(SWT.COLOR_WHITE),
				      grey  = getDisplay().getSystemColor(SWT.COLOR_GRAY),
				      oldForegroundColor = gc.getForeground(),
				      oldBackgroundColor = gc.getBackground();
				
				Rectangle bounds = getBounds();
				
				if(fCornerbackground != null)
					gc.setBackground(fCornerbackground);
				
				gc.setBackground(getBackground());
				
				int yOffset = bounds.height/2 - 1; 
				int startDrawing = (bounds.width / 2) - ((DOT_NUMBER / 2) * DOT_DISTANCE) - DOT_DISTANCE;
				for (int i = 0; i < DOT_NUMBER; i++)
				{
					gc.setBackground(white);
					gc.fillRectangle(startDrawing + 1 + (i * DOT_DISTANCE) ,yOffset+2 , 2, 2);					
					gc.setBackground(grey);
					gc.fillRectangle(startDrawing + (i * DOT_DISTANCE),yOffset+1, 2, 2);	
				}
				
				gc.setForeground(fLineColor);
				gc.setForeground(oldForegroundColor);
				gc.setBackground(oldBackgroundColor);
			}		
		});
	}
	
	/**
	 * Does nothing.  Must use custom layout
	 */
	@Override
	public void setLayout(Layout layout) {
	}
	
	/**
	 * Add a listener to be notified when one of the buttons is clicked.
	 * @param listener
	 */
	public void addSelectionListener(SelectionListener listener) {
		if (!getListeners().contains(listener)) {
			getListeners().add(listener);
		}
	}
	
	public boolean removeSelectionListener(SelectionListener listener) {
		return getListeners().remove(listener);
	}
	
	/**
	 * Sets the tooltips for the two states
	 * @param togggleState  the tooltip for the toggle=true state
	 * @param untoggleState the tooltip for the toggle=false state
	 */
	public void setTooltips(String toggleState, String untoggleState) {
		fToggleTooltip = toggleState;
		fUntoggleTooltip = untoggleState;
		setToggleState(fToggleState);
	}
	
	public String getToggleTooltip() {
		return fToggleTooltip;
	}
	
	public String getUntoggleTooltip() {
		return fUntoggleTooltip;
	}
	
	/**
	 * Selects the visible button which causes a SelectionEvent
	 * to be fired.
	 */
	public void toggle() {
		widgetSelected(null);
	}

	/**
	 * Fires a SelectionEvent, toggling current state
	 */
	public void widgetSelected(MouseEvent e) {
		setToggleState(!fToggleState);
		
		Event event = new Event();
		if (fToggleState) {
			event.detail = SWT.LEFT;
		} else {
			event.detail = SWT.RIGHT;
		}
		event.widget = this;
		
		this.layout();
		fireWidgetSelectedEvent(new SelectionEvent(event));
	}

	@Override
	public void setToolTipText(String string) {
		setTooltips(string, string);
	}
	
	protected void fireWidgetSelectedEvent(SelectionEvent e) {
		for (SelectionListener listener : fListeners) {
			listener.widgetSelected(e);
		}
	}
	
	/**
	 * sets color behind the "rounded" corner edge
	 * @param background
	 */
	public void setCornerBackground(Color background) {
		fCornerbackground = background;
	}
	
	/**
	 * returns current toggle state
	 * @return boolean
	 */
	public boolean getToggleState() {
		return fToggleState;
	}
	
	/**
	 * sets toggle state
	 * @param state
	 */
	public void setToggleState(boolean state) {
		this.fToggleState =  state;
		super.setToolTipText( (state)?fToggleTooltip:fUntoggleTooltip );
	}

	public static void main(String[] args) {
		Display display = new Display();

		Shell shell = new Shell(display);
		
		GridLayout layout = new GridLayout(2,false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		shell.setLayout(layout);
		shell.setBackground(JFaceColors.getBannerBackground(display));

		Canvas filler = new Canvas(shell,SWT.NONE);
		filler.setBackground(JFaceColors.getBannerBackground(filler.getDisplay()));
		filler.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Canvas canvas = (Canvas) e.widget;
				Point  size = canvas.getSize();
				e.gc.setBackground(canvas.getBackground());
				e.gc.fillRectangle(0,0,size.x,size.y);
			}
		});
		
		GridData fillerData = new GridData(GridData.FILL_BOTH);
		filler.setLayoutData(fillerData);
		
		HandleToggle toggle = new HandleToggle(shell,SWT.VERTICAL);
		toggle.setCornerBackground(filler.getBackground());
		toggle.setTooltips("toggle on", "toggle off");
		toggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				HandleToggle toggle = (HandleToggle) e.widget;
				MessageDialog.openInformation(toggle.getShell(), "test", "you selected the toggle widget. toggle state=" + toggle.getToggleState());
			}
		});
		toggle.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridData data = new GridData(SWT.RIGHT, SWT.FILL, false, true);
		data.widthHint = 12;
		data.minimumWidth = 12;
		toggle.setLayoutData(data);

		shell.pack();
		
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}

}
