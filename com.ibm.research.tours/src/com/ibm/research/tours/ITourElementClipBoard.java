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
package com.ibm.research.tours;

/**
 * 
 * @author mdesmond
 *
 *	This framework can be replaced by LocalSelectionTransfer
 */
public interface ITourElementClipBoard 
{
	/**
	 * Adds a tour element to the clip board and return a unique id
	 * @param element
	 * @return
	 */
	public String putTourElement(ITourElement element);
	
	/**
	 * Retrieves a tour element from the clip board with the given id
	 * @param id
	 * @return
	 */
	public ITourElement getTourElement(String id);
	
	/**
	 * Clear the clip board
	 */
	public void clear();
}
