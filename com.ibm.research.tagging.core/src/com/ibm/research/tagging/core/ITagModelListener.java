/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/

package com.ibm.research.tagging.core;

/**
 * 
 * @author mdesmond
 *
 */
public interface ITagModelListener 
{
	public void tagAdded(ITag tag);
	
	/**
	 * The given tag has been removed 
	 * @param tag the removed
	 * @param waypoints the affected waypoints
	 */
	public void tagRemoved(ITag tag, IWaypoint[] waypoints);
	
	public void tagRenamed(ITag tag,String oldName);
}
