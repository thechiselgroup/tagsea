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

/**
 * A waypoint descriptor that can have its attributes set.
 * @author Del Myers
 *
 */
public interface IMutableResourceWaypointDescriptor extends
		IResourceWaypointDescriptor {
	
	public abstract void setResource(String path);

	public abstract void setTags(SortedSet<String> tags);

	/**
	 * @return the line
	 */
	public abstract void setLine(int line);

	/**
	 * @return the author
	 */
	public abstract void setAuthor(String author);

	/**
	 * @return the date
	 */
	public abstract void setDate(Date date);

	public abstract void setCharEnd(int end);

	public abstract void setCharStart(int start);

	public abstract void setStamp(String stamp);

	/**
	 * @return the revision
	 */
	public abstract void setRevision(String revision);
	
	public abstract void setText(String text);
	
	/**
	 * Sets the value of the given key if it is a valid key.
	 * @param attr
	 * @param value
	 */
	public abstract void setValue(String attr, Object value);

}
