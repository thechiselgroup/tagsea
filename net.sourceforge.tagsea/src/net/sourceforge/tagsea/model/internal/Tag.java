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

import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.TagSEAModelException;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.TagSEAUtils;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.runtime.Status;

/**
 * Concrete implementation of ITag
 * @author Del Myers
 */

public class Tag implements ITag {
	private List<IWaypoint> fWaypoints;
	private String fName;
	//for convenience, so that we don't have to query the tags model just to find out whether
	//the tag exists.
	private boolean exists;

	Tag(String name) throws TagSEAModelException {
		int validation = TagSEAUtils.isValidTagName(name);
		if (validation != TagSEAUtils.TAG_NAME_VALID) {
			String message = "";
			switch (validation) {
			case TagSEAUtils.TAG_NAME_SYNTAX_ERROR:
				message = "Incorrect syntax for tag name.";
				break;
			case TagSEAUtils.TAG_NAME_BAD_CHARACTER:
				message = "Tag name contains an illegal or misplaced character";
				break;
			}
			TagSEAModelException exception = 
				new TagSEAModelException(
						new Status(Status.ERROR, TagSEAPlugin.PLUGIN_ID, Status.ERROR, message, null)
				);
			throw exception;
		}
		fWaypoints = new LinkedList<IWaypoint>();
		this.fName = name;
		fWaypoints.add(Waypoint.DUMMY);
	}
	 
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.ITag#getName()
	 */
	public String getName() {
		return fName;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.ITag#getWaypointCount()
	 */
	public int getWaypointCount() {
		synchronized (fWaypoints) {
			if (fWaypoints.contains(Waypoint.DUMMY)) return 0;
			return fWaypoints.size();
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.ITag#getWaypoints()
	 */
	public IWaypoint[] getWaypoints() {
		synchronized (fWaypoints) {
			if (fWaypoints.contains(Waypoint.DUMMY)) return new IWaypoint[0];
			return fWaypoints.toArray(new IWaypoint[fWaypoints.size()]);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ITag that) {
		return this.getName().compareTo(that.getName());
	}
	
	/**
	 * Removes the given waypoint from the list of waypoints, if it exists.
	 * @param waypoint removes the given waypoint from the list of waypoints, if it exists.
	 */
	protected boolean removeWaypoint(IWaypoint waypoint) {
		if (waypoint == null) return false;
		synchronized (getBlock()) {
			if (!exists()) return false;
			IWaypoint[] old = getWaypoints();
			fWaypoints.remove(waypoint);
			TagSEAChangeSupport.INSTANCE.postTagChange(TagChangeEvent.createWaypointEvent(this, old));
			if (getWaypointCount() == 0) {
				//remove this tag from the model.
				((TagsModel)TagSEAPlugin.getTagsModel()).removeFromModel(this);
			}
		}
		return true;
	}
	
	protected boolean addWaypoint(IWaypoint waypoint) {
		if (waypoint == null) return false;
		synchronized (getBlock()) {
			if (!waypoint.exists()) return false;
			//get rid of the dummy waypoint
			fWaypoints.remove(Waypoint.DUMMY);
			IWaypoint[] old = getWaypoints();
			if (old.length == 0) {
				//add the tag to the model
				TagsModel.INSTANCE.addToModel(this);
			}
			fWaypoints.add(waypoint);
			TagSEAChangeSupport.INSTANCE.postTagChange(TagChangeEvent.createWaypointEvent(this, old));
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.ITag#setName(java.lang.String)
	 */
	public boolean setName(String name) {
		if (TagSEAUtils.isValidTagName(name) != TagSEAUtils.TAG_NAME_VALID) return false;
		synchronized (getBlock()) {
			if (!exists()) return false;
			//can't change the default tag.
			if ("default".equals(getName())) return false;
			//can't have an empty name.
			if ("".equals(name)) return false;
			//scan to make sure that the tag name has sufficient
			//differences between the periods.
			int diff = 0;
			for (int i = 0; i < name.length(); i++) {
				char c = name.charAt(i);
				if (c == '.') {
					if (diff == 0)
						return false;
					diff = 0;
				} else {
					diff++;
				}
			}
			//ended with a period.
			if (diff == 0) return false;
			if (TagSEAPlugin.getTagsModel().getTag(name) != null) {
				return false;
			} else {
				String oldName = getName();
				this.fName = name;
				TagsModel.INSTANCE.remap(oldName, this);
				TagSEAChangeSupport.INSTANCE.postTagChange(TagChangeEvent.createNameEvent(this, oldName));
				return true;
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.ITag#exists()
	 */
	public boolean exists() {
		return exists;
	}
	
	private Object getBlock() {
		return TagSEAChangeSupport.INSTANCE.getOperationBlocker();
	}
	
	/**
	 * Sets local flag on this tag as deleted. Should only be called from the tags model.
	 */
	void delete() {
		this.exists = false;
	}
	
	/**
	 * Sets local flag on this tag as created. Should only be called from the tags model.
	 *
	 */
	void create() {
		this.exists = true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

}
