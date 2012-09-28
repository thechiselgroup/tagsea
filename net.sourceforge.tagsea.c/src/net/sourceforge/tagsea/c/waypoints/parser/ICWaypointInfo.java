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

package net.sourceforge.tagsea.c.waypoints.parser;

import java.util.Date;
import java.util.Map;

public interface ICWaypointInfo 
{
	/**
	 * Get the tags associated with this waypoint
	 * @return the tags or an empty array if none exist
	 */
	public String[] getTags();
	
	/**
	 * Returns the author attribute, or null if it does not exist.
	 * @return the author attribute, or null if it does not exist.
	 */
	public String getAuthor();
	
	/**
	 * Returns the date attribute, or null if it does not exist.
	 * @return the date attribute, or null if it does not exist.
	 */
	public Date getDate();
	
	/**
	 * Get the description associated with this waypoint
	 * @return the description or null if none exists
	 */
	public String getDescription();
	
	/**
	 * Get the attributes associated with this waypoint
	 * @return the attributes or an empty array if none exists
	 */
	public Map<String,String> getAttributes();
	
	/**
	 * The offset of the waypoint.
	 * @return
	 */
	public int getOffset();
	
	/**
	 * The length of the waypoint.
	 * @return
	 */
	public int getLength();
	
}
