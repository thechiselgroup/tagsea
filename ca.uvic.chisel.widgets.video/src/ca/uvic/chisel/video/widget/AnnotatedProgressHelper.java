/*******************************************************************************
 * Copyright (c) 2009 the CHISEL group and contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Del Myers - initial API and implementation
 *******************************************************************************/
package ca.uvic.chisel.video.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Del Myers
 *
 */
class AnnotatedProgressHelper implements MouseListener, MouseMoveListener {
	int armedLocation;
	private AnnotatedProgress slider;
	private boolean armedHigh;
	private boolean dragging;
	
	private class EventForwarder implements Listener {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		public void handleEvent(Event e) {
			if (e.type == SWT.MenuDetect) {
				return;
			}
			Event fe = new Event();
			fe.button = e.button;
			fe.character = e.character;
			fe.count = e.count;
			fe.data = e.data;
			fe.detail = e.detail;
			fe.display = e.display;
			fe.doit = e.doit;
			fe.gc = e.gc;
			fe.height = e.height;
			fe.index = e.index;
			fe.item = e.item;
			fe.keyCode = e.keyCode;
			fe.start = e.start;
			fe.stateMask = e.stateMask;
			fe.time = e.time;
			fe.type = e.type;
			fe.widget = slider;
			fe.width = e.width;
			fe.x = e.x;
			fe.y = e.y;
			fe.item = slider.itemAt(new Point(e.x, e.y));
			slider.notifyListeners(e.type, fe);
			
		}
		
	}
	
	AnnotatedProgressHelper(AnnotatedProgress slider) {
		this.slider = slider;
		armedLocation = -1;
		Canvas canvas = slider.getCanvas();
		canvas.addMouseListener(this);
		canvas.addMouseMoveListener(this);
		Listener listener = new EventForwarder();
		canvas.addListener(SWT.MouseMove, listener);
		canvas.addListener(SWT.MouseDown, listener);
		canvas.addListener(SWT.MouseEnter, listener);
		canvas.addListener(SWT.MouseUp, listener);
		canvas.addListener(SWT.MouseExit, listener);
		canvas.addListener(SWT.MouseHover, listener);
		canvas.addListener(SWT.MouseMove, listener);
		canvas.addListener(SWT.MouseWheel, listener);
		canvas.addListener(SWT.MouseDoubleClick, listener);
		canvas.addListener(SWT.KeyDown, listener);
		canvas.addListener(SWT.KeyUp, listener);
		canvas.addListener(SWT.MenuDetect, listener);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDoubleClick(MouseEvent e) {}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDown(MouseEvent e) {
		//if (!inHandle(e.x)) {
			slider.internalSetVisualMaximum(e.x);
		//}
		arm(e);
	}

	/**
	 * 
	 */
	private void arm(MouseEvent e) {
		if (e.button == 1) {
			int visualLow = 0;
			int visualHigh = slider.getVisualHigh();
			armedLocation = e.x;
			boolean aboveMin = e.x >= visualLow-AnnotatedProgress.HANDLE_SIZE;
			boolean belowMinHandle = e.x <= visualLow;
			boolean belowMax = e.x <= visualHigh + AnnotatedProgress.HANDLE_SIZE;
			boolean aboveMaxHandle = e.x >= visualHigh;
			if (belowMax && aboveMaxHandle) {
				armedHigh = true;
			}
		} else {
			armedLocation = -1;
			armedHigh = false;
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseUp(MouseEvent e) {
		if (armed() && armedLocation != e.x) {
			
		}
		armedHigh = false;
		armedLocation = -1;
		if (!dragging) {
			slider.updateSelection(e);
		}
		//notify listeners of the change
		dragging = false;
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseMove(MouseEvent e) {
		if (e.button != 0) {
			dragging = true;
		}
		if (inHandle(e.x) || armed()) {
			slider.getCanvas().setCursor(slider.getCanvas().getDisplay().getSystemCursor(SWT.CURSOR_SIZEW));
		} else {
			slider.getCanvas().setCursor(slider.getCursor());
		}
		if (armedHigh) {
			slider.internalSetVisualMaximum(e.x);
		}
		
	}

	/**
	 * @return
	 */
	public boolean armed() {
		return armedHigh;
	}

	private boolean inHandle(int x) {
		int visualHigh = slider.getVisualHigh();
		boolean inLow = (x >= visualHigh-AnnotatedProgress.HANDLE_SIZE);
		boolean inHigh = (x <= visualHigh + AnnotatedProgress.HANDLE_SIZE);
		return (inLow && inHigh);
	}

}
