/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.model.internal;

import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ITagChangeEvent;
import net.sourceforge.tagsea.core.IWaypoint;

/**
 * The concrete implementation of a ITagChangeEvent
 * @author Del Myers
 */

public class TagChangeEvent implements ITagChangeEvent {

	private final ITag tag;
	private final int type;
	private final String oldName;
	private final String newName;
	private final IWaypoint[] oldWaypoints;
	private final IWaypoint[] newWaypoints;
	
	private TagChangeEvent(ITag tag, int type, String oldName, String newName, IWaypoint[] oldWaypoints, IWaypoint[] newWaypoints) {
		this.tag = tag;
		this.type = type;
		this.oldName = oldName;
		this.newName = newName;
		this.oldWaypoints = oldWaypoints;
		this.newWaypoints = newWaypoints;
	}
	
	public static ITagChangeEvent createNewEvent(ITag tag) {
		return new TagChangeEvent(tag, NEW, tag.getName(), tag.getName(), tag.getWaypoints(), tag.getWaypoints());
	}
	
	public static ITagChangeEvent createNameEvent(ITag tag, String oldName) {
		return new TagChangeEvent(tag, NAME, oldName, tag.getName(), tag.getWaypoints(), tag.getWaypoints());
	}
	
	public static ITagChangeEvent createDeleteEvent(ITag tag) {
		return new TagChangeEvent(tag, DELETED, tag.getName(), tag.getName(), tag.getWaypoints(), tag.getWaypoints());
	}
	
	public static ITagChangeEvent createWaypointEvent(ITag tag, IWaypoint[] oldWaypoints) {
		return new TagChangeEvent(tag, WAYPOINTS, tag.getName(), tag.getName(), oldWaypoints, tag.getWaypoints());
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.ITagChangeEvent#getNewName()
	 */
	public String getNewName() {
		return newName;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.ITagChangeEvent#getNewWaypoints()
	 */
	public IWaypoint[] getNewWaypoints() {
		return newWaypoints;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.ITagChangeEvent#getOldName()
	 */
	public String getOldName() {
		return oldName;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.ITagChangeEvent#getOldWaypoints()
	 */
	public IWaypoint[] getOldWaypoints() {
		return oldWaypoints;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.ITagChangeEvent#getTag()
	 */
	public ITag getTag() {
		return tag;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.ITagChangeEvent#getType()
	 */
	public int getType() {
		return type;
	}

}
