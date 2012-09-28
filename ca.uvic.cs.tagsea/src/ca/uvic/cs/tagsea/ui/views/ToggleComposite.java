/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.ui.views;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;


/**
 * This class is similar to a splitter without the drag resize capabilities.
 * It is simply a vertical or horizontal bar which contains two buttons, only one
 * of which is shown.  The buttons will have either be left and right arrows,
 * or up and down arrows.  When a button is clicked, the other arrow button
 * is shown. 
 * 
 * @author Chris Callendar
 */
public class ToggleComposite extends Composite implements SelectionListener {

	private Button right;
	private Button left;
	private boolean horizontal = true;
	
	private ArrayList<SelectionListener> listeners;
	
	/**
	 * 
	 * @param style the style for the composite 
	 * @see SWT#HORIZONTAL
	 * @see SWT#VERTICAL
	 */
	public ToggleComposite(Composite parent, int style) {
		super(parent, checkStyle(style));
		this.horizontal = ((style & SWT.HORIZONTAL) != 0);
		this.listeners = new ArrayList<SelectionListener>(1);
		
		createControl();
	}
	
	static int checkStyle (int style) {
		/* Remove the vertical and horizontal styles. */
		return style & ~(SWT.VERTICAL | SWT.HORIZONTAL);
	}	
	
	protected void createControl() {
		StackLayout layout = new StackLayout();
		// create the right & left arrow buttons to hide and show the routes
		left = new Button(this, SWT.ARROW | (horizontal ? SWT.UP : SWT.LEFT) | SWT.FLAT);
		right = new Button(this, SWT.ARROW | (horizontal ? SWT.DOWN : SWT.RIGHT) | SWT.FLAT);
		left.addSelectionListener(this);
		right.addSelectionListener(this);

		layout.topControl = right;
		super.setLayout(layout);
	}
	
	/**
	 * Does nothing.  Must use a stack layout.
	 */
	@Override
	public void setLayout(Layout layout) {
		// does nothing
	}
	
	/**
	 * Add a listener to be notified when one of the buttons is clicked.
	 * @param listener
	 */
	public void addSelectionListener(SelectionListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public boolean removeSelectionListener(SelectionListener listener) {
		return listeners.remove(listener);
	}
	
	/**
	 * Sets the tooltips for the two buttons.
	 * @param leftOrUpToolTip the tooltip for the left (or up) arrow button
	 * @param rightOrDownToolTip the tooltip for the right (or down) arrow button
	 */
	public void setTooltips(String leftOrUpToolTip, String rightOrDownToolTip) {
		left.setToolTipText(leftOrUpToolTip);
		right.setToolTipText(rightOrDownToolTip);
	}
	
	/**
	 * Selects the visible button which causes a SelectionEvent
	 * to be fired.
	 */
	public void toggle() {
		widgetSelected(null);
	}

	/**
	 * Fires a SelectionEvent indicating which button was clicked.
	 * The {@link Event#detail} variable will be set to one of 
	 * {@link SWT#LEFT}, {@link SWT#RIGHT}, {@link SWT#UP}, {@link SWT#DOWN}
	 * depending which button was selected.
	 * @see SWT#LEFT
	 * @see SWT#RIGHT
	 * @see SWT#UP
	 * @see SWT#DOWN
	 */
	public void widgetSelected(SelectionEvent e) {
		StackLayout layout = (StackLayout)getLayout();

		Event event = new Event();
		if (layout.topControl == left) {
			event.detail = (horizontal ? SWT.UP : SWT.LEFT);
			layout.topControl = right;
		} else {
			event.detail = (horizontal ? SWT.DOWN : SWT.RIGHT);
			layout.topControl = left;
		}
		this.layout();
		
		event.item = layout.topControl;
		event.widget = this;
		fireWidgetSelectedEvent(new SelectionEvent(event));
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}
	
	protected void fireWidgetSelectedEvent(SelectionEvent e) {
		for (SelectionListener listener : listeners) {
			listener.widgetSelected(e);
		}
	}


}
