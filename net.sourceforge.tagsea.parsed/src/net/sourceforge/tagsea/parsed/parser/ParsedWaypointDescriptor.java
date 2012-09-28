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
 * A waypoint descriptor. Contains all of the information necessary to create a waypoint, or a 
 * parse error.
 * @author Del Myers
 *
 */
public final class ParsedWaypointDescriptor implements IParsedWaypointDescriptor {
	private int charStart;
	private int charEnd;
	private String message;
	private Date date;
	private String detail;
	private String author;
	private String[] tags;
	private int line;

	/**
	 * Creates a new descriptor for the waypoint.
	 * @param charStart the start of the waypoint in the document.
	 * @param charEnd the end of the waypoint in the document.
	 * @param line the line at which the waypoint starts in the document.
	 * @param message the message that will be displayed with the waypoint.
	 * @param author the author(s) of the waypoint.
	 * @param date the date that the waypoint was created/modified.
	 * @param tags the tags that this waypoint contains.
	 * @param detail domain-specific detail information for the waypoint.
	 */
	public ParsedWaypointDescriptor(int charStart, int charEnd, int line, String message, String author, Date date, String[] tags, String detail) {
		this.charStart = charStart;
		this.charEnd = charEnd;
		this.line = line;
		this.message = message;
		this.date = date;
		this.detail = detail;
		this.author = author;
		this.tags = tags;
	}
	
	


	/**
	 * 
	 * @return the index of the starting character for the waypoint.
	 */
	public int getCharStart() {
		return charStart;
	}

	/**
	 * 
	 * @return the index for the last character for the waypoint.
	 */
	public int getCharEnd() {
		return charEnd;
	}

	/**
	 * 
	 * @return the message to be displayed with the waypoint
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * 
	 * @return the creation/modification date of the waypoint.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * 
	 * @return domain-specific detail information for the waypoint.
	 */
	public String getDetail() {
		return detail;
	}
	
	/**
	 * @return the author(s) of the waypoint.
	 */
	public String getAuthor() {
		return author;
	}




	public String[] getTags() {
		return tags;
	}




	public int getLine() {
		return line;
	}
	
}
