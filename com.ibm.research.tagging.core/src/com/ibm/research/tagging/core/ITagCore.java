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

public interface ITagCore 
{
	/**
	 * Get the tag model
	 * @return
	 */
	public ITagModel getTagModel();
	
	/**
	 * Get the waypoint model
	 * @return
	 */
	public IWaypointModel getWaypointModel();
	
	/**
	 * Add a WaypointModelExtensionr
	 * @param listener
	 */
	public void addWaypointModelExtension(IWaypointModelExtension extension); 
	
	/**
	 * Remove a WaypointModelExtensionr
	 * @param listener
	 */
	public void removeWaypointModelExtension(IWaypointModelExtension extension); 
	
	public void start();
	
	public void stop();
	
}
