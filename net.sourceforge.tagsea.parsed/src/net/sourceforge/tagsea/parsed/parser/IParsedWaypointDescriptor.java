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
 * A descriptor of text in a document that will be used to generate a waypoint.
 * @author Del Myers
 *
 */
public interface IParsedWaypointDescriptor {

	/**
	 * 
	 * @return the index of the starting character for the waypoint. 0 indexed and inclusive.
	 */
	public abstract int getCharStart();

	/**
	 * 
	 * @return the index for the last character for the waypoint. 0 indexed and exclusive.
	 */
	public abstract int getCharEnd();

	/**
	 * 
	 * @return the line at which the waypoint starts in the document.
	 */
	public abstract int getLine();
	/**
	 * 
	 * @return the message to be displayed with the waypoint
	 */
	public abstract String getMessage();

	/**
	 * 
	 * @return the creation/modification date of the waypoint.
	 */
	public abstract Date getDate();

	/**
	 * 
	 * @return domain-specific detail information for the waypoint.
	 */
	public abstract String getDetail();

	/**
	 * @return the author(s) of the waypoint.
	 */
	public abstract String getAuthor();
	
	/**
	 * The tags in this waypoint descriptor.
	 * @return
	 */
	public abstract String[] getTags();

}