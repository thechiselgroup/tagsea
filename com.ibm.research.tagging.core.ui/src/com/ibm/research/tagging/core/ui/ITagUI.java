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
package com.ibm.research.tagging.core.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.IWaypoint;

/**
 * 
 * @author mdesmond
 *
 */
public interface ITagUI 
{
	/**
	 * Add the given waypoint UI extension 
	 * @param extension
	 */
	public void addWaypointUIExtension(IWaypointUIExtension extension);
	
	/**
	 * Remove the given waypoint UI extension 
	 * @param extension
	 */
	public void removeWaypointUIExtension(IWaypointUIExtension extension);
	
	/**
	 * Get the image associated with this waypoint
	 * @param waypoint
	 * @return the image
	 */
	public Image getImage(IWaypoint waypoint);
	
	/**
	 * Get the small image associated with this waypoint
	 * @param waypoint
	 * @return the image
	 */
	public Image getSmallImage(IWaypoint waypoint);
	
	/**
	 * Get the label associated with this waypoint
	 * @param waypoint
	 * @return the label
	 */
	public String getLabel(IWaypoint waypoint);

	/**
	 * Get the label associated with this waypoint for table layouts
	 * @param waypoint
	 * @param column
	 * @return the label
	 */
	public String getLabel(IWaypoint waypoint, int column);
	
	/**
	 * Add a tag selection listener
	 * @param listener
	 */
	public void addTagSelectionListener(ITagSelectionListener listener);
	
	/**
	 * Add a tag selection listener
	 * @param listener
	 */
	public void removeTagSelectionListener(ITagSelectionListener listener);

	/**
	 * Fire a tag selection changed event
	 * @param tags
	 */
	public void tagSelectionChanged(ITag[] tags);
	
	/**
	 * Get the properties associated with this waypoint
	 * @param the properties ui widget
	 */
	public FormText getProperties(Composite parent, IWaypoint waypoint);
	
	/**
	 * Edit the properties associated with this waypoint
	 * @param waypoint
	 * @return true if properties were changed, false otherwise
	 */
	public boolean editProperties(IWaypoint waypoint);
	
	public void addWaypointViewListener(IWaypointViewListener listener);
	public void removeWaypointViewListener(IWaypointViewListener listener);
	public void addTagViewListener(ITagViewListener listener);
	public void removeTagViewListener(ITagViewListener listener);
}
