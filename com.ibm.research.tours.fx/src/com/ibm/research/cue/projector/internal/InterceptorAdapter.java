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
 * see InterceptorListener for details
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */

public class InterceptorAdapter implements InterceptorListener {

	public boolean KeyDown(int keyCode, int scanCode) {
		return false;
	}

	public boolean KeyUp(int keyCode, int scanCode) {
		return false;
	}

	public boolean LeftButtonDown(int x, int y) {
		return false;
	}

	public boolean LeftButtonUp(int x, int y) {
		return false;
	}

	public boolean MouseMove(int x, int y) {
		return false;
	}

	public boolean RightButtonDown(int x, int y) {
		return false;
	}

	public boolean RightButtonUp(int x, int y) {
		return false;
	}

	public boolean SystemKeyDown(int keyCode, int scanCode) {
		return false;
	}

	public boolean SystemKeyUp(int keyCode, int scanCode) {
		return false;
	}

}
