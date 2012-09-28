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
package com.ibm.research.cue.tourist.internal.win32.ppt;

public interface IPowerpointApplicationListener {
	
	public void slideShowEnded();		// end of slide show via advancing past last slide
	public void endSlideShowCalled();	// PowerpointApplication.endSlideShow() is called - could be via powerpoint or via tour runner
}
