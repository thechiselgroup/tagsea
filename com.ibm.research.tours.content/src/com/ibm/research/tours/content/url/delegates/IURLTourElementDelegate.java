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
package com.ibm.research.tours.content.url.delegates;

import org.eclipse.swt.graphics.Image;

import com.ibm.research.tours.ITourElement;
import com.ibm.research.tours.content.url.IURL;

public interface IURLTourElementDelegate 
{
	public abstract ITourElement createClone();
	public abstract Image getImage();
	public abstract String getText();
	public abstract String getShortText(); 
	public abstract void start();
	public abstract void stop();
	public abstract void transition();
	public abstract IURL getUrl(); 
}
