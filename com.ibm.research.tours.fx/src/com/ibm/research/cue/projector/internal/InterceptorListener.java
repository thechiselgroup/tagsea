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

/**
 * used by MouseKeyboardInterceptor.  first listener that returns true will override all other listeners, and override default system handling of events
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */


public interface InterceptorListener {

	/**
	 * fired when left button is pushed down
	 * 
	 * @param x
	 * @param y
	 * @return true=override all other listeners, and all left mouse button handling for this event
	 */
	public boolean LeftButtonDown(int x, int y);

	/**
	 * fired when left button is pushed up
	 * 
	 * @param x
	 * @param y
	 * @return true=override all other listeners, and all left mouse button handling for this event
	 */
	public boolean LeftButtonUp(int x, int y);

	/**
	 * fired when right button is pushed up
	 * 
	 * @param x
	 * @param y
	 * @return true=override all other listeners, and all right mouse button handling for this event
	 */
	public boolean RightButtonDown(int x, int y);

	/**
	 * fired when right button is pushed down
	 * 
	 * @param x
	 * @param y
	 * @return true=override all other listeners, and all right mouse button handling for this event
	 */
	public boolean RightButtonUp(int x, int y);

	/**
	 * fired when key is pushed down
	 * 
	 * @param keyCode
	 * @param scanCode
	 * @return true=override all other listeners, and all keyboard handling for this event
	 */
	public boolean KeyDown(int keyCode, int scanCode);

	/**
	 * fired when key is pushed up
	 * 
	 * @param keyCode
	 * @param scanCode
	 * @return true=override all other listeners, and all keyboard handling for this event
	 */
	public boolean KeyUp(int keyCode, int scanCode);

	/**
	 * fired when system key (e.g. alt) is pushed down
	 * 
	 * @param keyCode
	 * @param scanCode
	 * @return true=override all other listeners, and all keyboard handling for this event
	 */
	public boolean SystemKeyDown(int keyCode, int scanCode);

	/**
	 * fired when system key (e.g. alt) is pushed up
	 * 
	 * @param keyCode
	 * @param scanCode
	 * @return true=override all other listeners, and all keyboard handling for this event
	 */
	public boolean SystemKeyUp(int keyCode, int scanCode);

	/**
	 * fired when mouse moves without any button presses
	 * @param x
	 * @param y
	 * @return true=override all other listeners, and all mouse motion handling for this event
	 */
	public boolean MouseMove(int x, int y);

}
