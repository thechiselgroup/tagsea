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

import com.ibm.research.tagging.core.IWaypoint;

/**
 * 
 * @author mdesmond
 *
 */
public interface IWaypointUIExtension 
{
	/**
	 * Get the image associated with this waypoint
	 * @param waypoint
	 * @return Image
	 */
	public Image getImage(IWaypoint waypoint);
	
	/**
	 * Get the image associated with this waypoint
	 * @param waypoint
	 * @return Image
	 */
	public Image getSmallImage(IWaypoint waypoint);
	
	/**
	 * Get the label associated with this waypoint
	 * @param waypoint
	 * @return String
	 */
	public String getLabel(IWaypoint waypoint);

	/**
	 * Get the label associated with this waypoint for column layouts 
	 * @param waypoint
	 * @param column
	 * @return String
	 */
	public String getLabel(IWaypoint waypoint, int column);
	
	/**
	 * Get the tool tip associated with this waypoint
	 * @param waypoint
	 * @return tooltip
	 */
	public FormText getToolTipText(IWaypoint waypoint);
	
	/**
	 * Get the properties associated with this waypoint
	 * @param waypoint
	 * @return FormText
	 */
	public FormText getProperties(Composite parent, IWaypoint waypoint);
	
	/**
	 * Edit the properties associated with this waypoint
	 * @param waypoint
	 * @return boolean, if this operation is supported
	 */
	public boolean editProperties(IWaypoint waypoint);

}
