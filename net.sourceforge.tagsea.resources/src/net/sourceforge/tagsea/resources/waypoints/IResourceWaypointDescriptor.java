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

import org.eclipse.core.runtime.IAdaptable;

/**
 * Interface for describing a resource waypoint
 * @author Del Myers
 *
 */
public interface IResourceWaypointDescriptor extends IAdaptable {

	/**
	 * Returns the workspace-relative path to the resource.
	 * @return the resource
	 */
	public abstract String getResource();

	/**
	 * @return the tags
	 */
	public abstract SortedSet<String> getTags();

	/**
	 * @return the line
	 */
	public abstract int getLine();

	/**
	 * @return the author
	 */
	public abstract String getAuthor();

	/**
	 * @return the date
	 */
	public abstract Date getDate();

	public abstract int getCharEnd();

	public abstract int getCharStart();

	public abstract String getStamp();

	/**
	 * @return the revision
	 */
	public abstract String getRevision();
	
	public abstract String getText();
	
	/**
	 * returns the value for the given key, or null if it doesn't exist.
	 * @param attr
	 * @return
	 */
	public abstract Object getValue(String attr);
	
	/**
	 * Returns the valid attributes on this waypoint descriptor.
	 * @return the valid attributes on this waypoint descriptor.
	 */
	public String[] getAttributes();
	
}