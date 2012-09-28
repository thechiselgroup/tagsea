/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.parsed.parser;

import java.util.Date;

/**
 * A parsed waypoint descriptor that can change its fields.
 * @author Del Myers
 *
 */
public interface IMutableParsedWaypointDescriptor extends
		IParsedWaypointDescriptor {
	
	/**
	 * set the message to be displayed with the waypoint
	 * @throws UnsupportedOperationException if the message can't be set.
	 */
	public abstract void setMessage(String message) throws UnsupportedOperationException;

	/**
	 * set the creation/modification date of the waypoint.
	 * @throws UnsupportedOperationException if dates can't be set
	 */
	public abstract void setDate(Date date) throws UnsupportedOperationException;

	/**
	 * set domain-specific detail information for the waypoint.
	 * @throws UnsupportedOperationException if the detail informationcan't be set.
	 */
	public abstract void setDetail(String detail) throws UnsupportedOperationException;

	/**
	 * set the author(s) of the waypoint.
	 * @throws UnsupportedOperationException if authors can't be changed.
	 */
	public abstract void setAuthor(String author) throws UnsupportedOperationException;
	
	/**
	 * 
	 * @return the new text representation of the waypoint after changes have been applied.
	 */
	public abstract String getText();
	
	/**
	 * Adds the given tag to the list of tags.
	 * @param tag the tag to add.
	 * @throws UnsupportedOperationException if tags can't be added.
	 */
	public abstract void addTag(String tag) throws UnsupportedOperationException;
	
	/**
	 * Removes all instances of the given tag from the list of tags.
	 * @throws UnsupportedOperationException if tags can't be removed.
	 */
	public abstract void removeTag(String tag) throws UnsupportedOperationException;
	
	/**
	 * Replaces all instances of the given old tag with the new tag.
	 * @param oldTag
	 * @param newTag
	 * @param throws UnsupportedOperationException if tags can't be replaced
	 */
	public abstract void replaceTag(String oldTag, String newTag) throws UnsupportedOperationException;

}
