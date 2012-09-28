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
package net.sourceforge.tagsea.core.ui;

import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.swt.graphics.Image;

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
	 * Returns a human-readable interpretation of the location of the waypoint.
	 * @param waypoint
	 * @return
	 */
	public String getLocationString(IWaypoint waypoint);

	/**
	 * Get the label associated with this waypoint for column layouts 
	 * @param waypoint
	 * @param column
	 * @return String
	 */
	//public String getLabel(IWaypoint waypoint, int column);
	
	/**
	 * Get the tool tip associated with this waypoint
	 * @param waypoint
	 * @return tooltip
	 */
	public String getToolTipText(IWaypoint waypoint);
		
	
	/**
	 * Asks permission from this delegate to find out whether or not TagSEA UI elements are
	 * allowed to request a delete of the given waypoint.
	 * @param waypoint the waypoint in question.
	 * @return true iff TagSEA UI elements are allowed to request a delete of the given waypoint.
	 */
	public abstract boolean canUIDelete(IWaypoint waypoint);
	
	/**
	 * Asks permission from this delegate to find out whether or not the TagSEA UI elements are
	 * allowed to request a change of the given named attribute.
	 * @param waypoint the waypoint in question.
	 * @param attribute the attribute in question.
	 * @return true iff TagSEA UI elements are allowed to request a change on the given waypoint.
	 */
	public abstract boolean canUIChange(IWaypoint waypoint, String attribute);
	
	/**
	 * Asks permision from this delegate to find out whether or not the TagSEA UI elements are
	 * allowed to request a change to the tag names of the given waypoint.
	 * @param waypoint the waypoint in question.
	 * @return true iff TagSEA UI elements are allowed to request a change to the tags on the waypoint.
	 */
	public abstract boolean canUIMove(IWaypoint waypoint);
	
	
	/**
	 * Asks permission from this delegate to find out whether or not TagSEA UI elements are
	 * allowed to add a tag to this waypoint.
	 * @param waypoint the waypoint in question.
	 * @return true iff TagSEA UI elements are allowed to add a tag to this waypoint.
	 */
	public abstract boolean canUIAddTag(IWaypoint waypoint);
	
	/**
	 * Asks permission from this delegate to find out whether or not TagSEA UI elements are
	 * allowed to delete a tag to this waypoint.
	 * @param waypoint the waypoint in question.
	 * @return true iff TagSEA UI elements are allowed to delete a tag to this waypoint.
	 */
	public abstract boolean canUIDeleteTag(IWaypoint waypoint);

	/**
	 * Performs translation and presentation for the given attribute <u>name</u> for use in
	 * the TagSEA ui.
	 * @param waypoint the waypoint that is going to be displayed.
	 * @param attribute the attribute that is going to be displayed.
	 * @return text representation of the attribute name given.
	 */
	public abstract String getAttributeLabel(IWaypoint waypoint, String attribute);
	
	/**
	 * Returns the image that will be used to present the given attribute value, or null if
	 * none.
	 * @param waypoint the waypoint that is going to be displayed.
	 * @param attribute the attribute displayed on the waypoint.
	 * @return
	 */
	public abstract Image getValueImage(IWaypoint waypoint, String attribute);
	
	/**
	 * Gets the label for the attribute in the given waypoint.
	 * @param waypoint
	 * @param attribute
	 * @param value the value to use for the label, or null if just the value in the attribute is desired.
	 * @return
	 */
	public abstract String getValueLabel(IWaypoint waypoint, String attribute, Object value);
		
	/**
	 * Returns the attributes for this type of waypoint that should be visible in UI elements.
	 * @return the attributes for this type of waypoint that should be visible in UI elements.
	 */
	public abstract String[] getVisibleAttributes();
	
	/**
	 * Returns the waypoint type used for this ui contributer.
	 * @return the waypoint type used for this ui contributer.
	 */
	public abstract String getType();
	
	/**
	 * Returns a human-readable label for the given attribute name which is not dependant on
	 * a waypoint.
	 * @param attribute the attribute
	 * @return a human-readable label for the given attribute name which is not dependant on
	 * a waypoint.
	 */
	public String getAttributeLabel(String attribute);

}
