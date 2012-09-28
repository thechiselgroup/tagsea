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
package net.sourceforge.tagsea.core.ui.internal.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;

/**
 * Horizontal Handle Toggle
 * 
 * A simple handle toggle with horizontal gripper marks.
 * 
 * @author Li-Te Cheng
 * @hax Michael Desmond
 * 
 * CUE, IBM Research 2006
 */
public class HorizontalHandleToggle extends Composite {

	private final static int DOT_DISTANCE = 5; 
	private final static int DOT_NUMBER = 7;
	
	private List<SelectionListener> fListeners;
	
	private String  fToggleTooltip;
	private String  fUntoggleTooltip;
	private boolean fToggleState = false;
	
	private Cursor  fHandCursor;
	private Color   fLineColor;
	private Color fDotColor;
	
	/**
	 * constructor
	 * @param style the style for the composite  - only vertical layout is currently supported
	 * @see SWT#VERTICAL
	 */
	public HorizontalHandleToggle(Composite parent, int style) 
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
		addPaintListener(new PaintListener() 
		{		
			public void paintControl(PaintEvent e) 
			{
				GC gc = e.gc;		
				
				Color background = getBackground();
				Color dotColor  = getDotColor();
				Color oldForegroundColor = gc.getForeground();
				Color oldBackgroundColor = gc.getBackground();
				
				Rectangle bounds = getBounds();
				gc.setBackground(getBackground());
				
				int yOffset = bounds.height/4; 
				int startDrawing = (bounds.width / 2) - ((DOT_NUMBER / 2) * DOT_DISTANCE) - DOT_DISTANCE;
				
				for (int i = 0; i < DOT_NUMBER; i++)
				{
					gc.setBackground(background);	
					gc.fillRectangle(startDrawing + 1 + (i * DOT_DISTANCE),yOffset, 2, 2);	
					gc.setBackground(dotColor);
					gc.fillRectangle(startDrawing + (i * DOT_DISTANCE), yOffset,  2, 2);
				}
				
				gc.setForeground(fLineColor);
				gc.setForeground(oldForegroundColor);
				gc.setBackground(oldBackgroundColor);
			}
		
		});
	}
	
	public Color getDotColor() 
	{
		if(fDotColor == null)
			fDotColor = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		return fDotColor;
	}
	
	public void setDotColor(Color dotColor) 
	{
		fDotColor = dotColor;
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
			event.detail = SWT.CLOSE;
		} else {
			event.detail = SWT.OPEN;
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
}
