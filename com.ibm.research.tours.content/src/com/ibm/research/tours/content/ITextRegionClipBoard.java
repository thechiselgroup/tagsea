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
package com.ibm.research.tours.content;

/**
 * 
 * @author mdesmond
 *
 */
public interface ITextRegionClipBoard 
{
	/**
	 * Adds a text region to the clip board
	 * @param element
	 * @return
	 */
	public void putTextRegion(TextRegion region);
	
	/**
	 * Gets the text region from the clip board
	 * @param element
	 * @return
	 */
	public TextRegion getTextRegion();
	
	
	/**
	 * Clear the clip board
	 * @param element
	 * @return
	 */
	public void clear();
}
