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
package net.sourceforge.tagsea.resources.waypoints;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;


public class ResourceWaypointDescriptor extends AbstractMutableWaypointDescriptor {
	String message;
	private SortedSet<String> tags;
	String author;
	private int line;
	Date date;
	private String resource;
	private int charStart;
	private int charEnd;
	String stamp;
	private String revision;

	/**
	 * 
	 */
	public ResourceWaypointDescriptor() {
		message = "";
		author = "";
		stamp = "";
		revision = "";
		line = -1;
		date = null;
		resource = null;
		tags = new TreeSet<String>();
	}
	

	/**
	 * @param resource the resource to set
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getResource()
	 */
	public String getResource() {
		return resource;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getTags()
	 */
	public SortedSet<String> getTags() {
		return tags;
	}

	public void addTag(String tag) {
		tags.add(tag);
	}

	/**
	 * @param line the line to set
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getLine()
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getAuthor()
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getDate()
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param currentString
	 */
	public void setText(String message) {
		this.message = message;		
	}

	/**
	 * @return the message
	 */
	public String getText() {
		return message;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getCharEnd()
	 */
	public int getCharEnd() {
		return charEnd;
	}

	public void setCharEnd(int charEnd) {
		this.charEnd = charEnd;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getCharStart()
	 */
	public int getCharStart() {
		return charStart;
	}

	public void setCharStart(int charStart) {
		this.charStart = charStart;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getStamp()
	 */
	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	/**
	 * @param revision
	 */
	public void setRevision(String revision) {
		this.revision = revision;
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getRevision()
	 */
	public String getRevision() {
		return revision;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor#setTags(java.util.SortedSet)
	 */
	public void setTags(SortedSet<String> tags) {
		this.tags = new TreeSet<String>(tags);		
	}
}