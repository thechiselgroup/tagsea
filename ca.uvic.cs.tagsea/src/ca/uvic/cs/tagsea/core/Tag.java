/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;

import ca.uvic.cs.tagsea.monitoring.TagSEATagEvent;
import ca.uvic.cs.tagsea.monitoring.TagSEAWaypointEvent;
import ca.uvic.cs.tagsea.monitoring.internal.Monitoring;

/**
 * Class for a tag node.
 * Contains child tags and waypoint information.
 * @author Sean Falconer
 * @author Peggy Storey
 */
public class Tag {
	// this tag's name, always the single tag name, not the hierarchy
	private String name = ""; 
	
	// unique identifier for this tag
	private String id = "";
	
	// a pointer to this tag's parent, every tag has a parent but the root
	private Tag parent = null;
	
	// hashtable to child tags, the key is the tag's name
	private Hashtable<String, Tag> children;
	
	// list of waypoints for this tag
	private ArrayList<Waypoint> waypoints;
	
	public Tag() {
		this.id = "";
		this.name = "";
		
		init();
	}
	
	/**
	 * Initialies this tag.
	 * Sets the parent, and sets this to be a child of the parent (if not null).
	 * Computes the unique tag id.
	 */
	public Tag(Tag parent, String name) {
		this.parent = parent;
		this.name = name;
		
		if (parent != null) {
			parent.addChild(this);
		}
		
		computeTagId();
		init();
	}
	
	private void init() {
		this.children = new Hashtable<String, Tag>();
		this.waypoints = new ArrayList<Waypoint>();
	}
	
	/**
	 * Sets the name - no updates are done on the children or waypoints, or the id.
	 * Call rename to properly update these values.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public String getId() {
		return id;
	}
	
	private void computeTagId() {
		if(parent != null) {
			id = parent.getId() + "/";
		}
		id += name;
	}
	
	public Tag[] getChildren() {
		Collection<Tag> collectionOfChildren = children.values();
		return (Tag[])collectionOfChildren.toArray(new Tag[collectionOfChildren.size()]);
	}
	
	/**
	 * Returns the number of children this tag has.
	 * @return int the number of children
	 */
	public int getChildrenCount() {
		return children.size();
	}
	
	public boolean hasChildren() {
		return (children.size() > 0);
	}
	
	public Tag getParent() {
		return this.parent;
	}
	
	public void setParent(Tag parent) {
		this.parent = parent;
	}
	
	public void removeWaypoint( Waypoint waypoint ) {
		waypoints.remove(waypoint);
		Monitoring.getDefault().fireEvent(new TagSEAWaypointEvent(TagSEAWaypointEvent.Waypointing.Removed, waypoint));
		if ( waypoints.size() == 0 && children.size() == 0 && parent != null) {
			parent.removeChild(this);
		}
	}
	
	public void removeChild(Tag tag) {
		children.remove(tag.getName());
		Monitoring.getDefault().fireEvent(new TagSEATagEvent(TagSEATagEvent.Tagging.Removed, tag));
		if ( waypoints.size() == 0 && children.size() == 0 && parent != null) {
			parent.removeChild(this);
		}
	}

	public Waypoint[] getWaypoints( ) {
		Waypoint[] waypointsArray = (Waypoint[])waypoints.toArray(new Waypoint[waypoints.size()]);
		return waypointsArray;
	}
	
	/**
	 * Gets all waypoints for this tag and its children.
	 * @return A static array of all waypoints and children waypoints.
	 */
	public Waypoint[] getAllWaypoints() {
		LinkedList<Waypoint> allWaypoints = new LinkedList<Waypoint>();
		allWaypoints.addAll(waypoints);
		
		Collection<Tag> collectionOfChildren = children.values();
		for(Tag child : collectionOfChildren) {
			Waypoint[] wayPoints = child.getAllWaypoints();
			for(int i = 0; i < wayPoints.length; i++) {
				allWaypoints.add(wayPoints[i]);
			}
		}
		
		return (Waypoint[])allWaypoints.toArray(new Waypoint[allWaypoints.size()]);
	}
	
	/**
	 * Searches all waypoints under this tag and returns the found one 
	 * or null.
	 * @param keyword case sensitive
	 * @return Waypoint or null if not found
	 */
	public Waypoint findWaypoint(String keyword) {
		Waypoint found = null;
		Waypoint[] all = getAllWaypoints();
		for (Waypoint wp : all) {
			if (wp.getKeyword().equals(keyword)) {
				found = wp;
				break;
			}
		}
		return found;
	}

	/**
	 * Adds a child to this Tag.  Also calls setParent on the child and sets it to this.
	 * @param child the child to add
	 * @return Tag the child
	 */
	public Tag addChild(String childName) {
		Tag t = children.get(childName);
		
		// check if we already have this tag
		if (t == null) {
			t = new Tag(this, childName);
			children.put(childName, t);
		}
		t.setParent(this);
		Monitoring.getDefault().fireEvent(new TagSEATagEvent(TagSEATagEvent.Tagging.New, t));
		return t;
	}
	
	/**
	 * Adds childTag to this Tag's list of children.  Also calls setParent on the child and sets it to this.
	 * <b>CAREFUL</b> - if this tag already has a child with the same name as childTag, then 
	 * <b>that</b> Tag is returned, not childTag.  The children of childTag will be added to this
	 * already existing tag.   
	 * @param childTag the child to add
	 * @return Tag either childTag if this Tag didn't contain a child with that name,
	 * or the existing child with the same name.
	 */
	public Tag addChild(Tag childTag) {
		Tag t = children.get(childTag.getName());
		
		// check if we already have this tag
		if (t == null) {
			children.put(childTag.getName(), childTag);
			t = childTag;
		} else {
			// ensure that all the children exist too
			for (Tag subchild : childTag.getChildren()) {
				t.addChild(subchild);
			}
		}
		// if the parent is different we need to update the tag id 
		if (t.getParent() != this) {
			t.setParent(this);
			if (t.getParent() != null)
				t.getParent().removeChild(t);
			// need to update the tag id
			t.computeTagId();
		}
		Monitoring.getDefault().fireEvent(new TagSEATagEvent(TagSEATagEvent.Tagging.New, t));
		return t;
	}
	
	public void addWaypoint(Waypoint wp) {
		Monitoring.getDefault().fireEvent(new TagSEAWaypointEvent(TagSEAWaypointEvent.Waypointing.New, wp));
		waypoints.add(wp);
	}

	/**
	 * Renames this tag, and all it's children.
	 * Also updates the unique IDs for each descendent (and this).
	 * Also updates the waypoints keywords.
	 * @param newName
	 */
	public void rename(String newName) {
		String oldName = getName();
		if (!oldName.equals(newName)) {
			setName(newName);
			// we have to update the parent's hashtable of children (the key is the tag name)
			if (parent != null) {
				parent.childRenamed(this, oldName);
			}
			// update all the descendent ids
			updateAllDescendentTagIDs(this);
			// now update all the keywords on each waypoint
			Waypoint[] allWaypoints = getAllWaypoints();
			for (Waypoint wp : allWaypoints) {
				wp.setKeywordFromTag();
			}
		}
	}
	
	/**
	 * Updates the hashtable of children.  The entry in the table
	 * for the oldName is removed, and the new tag name is added 
	 * (if it doesn't already exist in the hashtable).
	 * @param child the child (who has been renamed already)
	 * @param oldName the old name of the child
	 */
	protected void childRenamed(Tag child, String oldName) {
		this.children.remove(oldName);
		if (!children.containsKey(child.getName())) {
			this.children.put(child.getName(), child);
		}
	}

	/**
	 * Updates the ids for all the descendents of the given tag recursively.
	 * @param tag
	 */
	private static void updateAllDescendentTagIDs(Tag tag) {
		tag.computeTagId();
		for (Tag child : tag.children.values()) {
			updateAllDescendentTagIDs(child);
		}
	}
	
	@Override
	public String toString() {
		return getId();
	}

	/**
	 * Generates the keyword from the Tag hierarchy from the root down to this tag.
	 * This is only useful for tags that have waypoints directly under them.
	 * The keyword is a combination of all the tag names with round brackets
	 * like this:  root(child(subchild))
	 * @param tag the tag to start from
	 * @return String the keyword - won't be null.  Empty string is returned if tag is null or if tag name is null.
	 */
	public static String generateKeyword(Tag tag) {
		String keyword = "";
		while ((tag != null) && (tag.getName() != null) && (tag.getName().length() > 0)) {
			if (keyword.length() > 0) {
				keyword = "(" + keyword + ")";
			}
			keyword = tag.getName() + keyword;
			tag = tag.getParent();
		}
		return keyword;
	}
	
}