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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAModelException;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.TagSEAChangeStatus;

import org.eclipse.core.runtime.Platform;

/**
 * The concrete implementation of IWaypoint.
 * @author Del Myers
 */

public class Waypoint implements IWaypoint, Comparable<Waypoint> {
	/**
	 * A dummy place-holder waypoint that is used when tags are first created in order to 
	 * keep the integrity of the model.
	 */
	static final IWaypoint DUMMY = new Waypoint(IWaypoint.BASE_WAYPOINT, new HashMap<String, Object>(),-1);
	/**
	 * For convenience so that we don't have to query the waypoints model for existence.
	 */
	private boolean exists;
	private Map<String, Object> attributes;
	private String type;
	private TreeSet<ITag> tags;
	private AbstractWaypointDelegate delegate;
	
	/**
	 * An id that uniquely identifies this waypoint in this workbench only. It is used for drag-and-drop,
	 * and other methods that are needed to uniquely identify the waypoint.
	 */
	private long workbenchId;
	
	/**
	 * Creates a waypoint with the given default attributes.
	 * @param attributes
	 */
	Waypoint(String type, Map<String, Object> attributes, long id) {
		//the waypoint can't be created except for by the model.
		exists = true;
		this.attributes = attributes;
		this.type = type;
		this.tags = new TreeSet<ITag>();
		this.workbenchId = id;
		this.delegate = TagSEAPlugin.getDefault().getWaypointDelegate(type);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypoint#getAttributes()
	 */
	public String[] getAttributes() {
		return this.attributes.keySet().toArray(new String[this.attributes.size()]);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypoint#getAuthor()
	 */
	public String getAuthor() {
		return this.attributes.get(ATTR_AUTHOR).toString();
	}
	

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypoint#getBooleanValue(java.lang.String, boolean)
	 */
	public boolean getBooleanValue(String name, boolean defaultValue) {
		Object value = getValue(name);
		if (value == null) return defaultValue;
		if (!(value instanceof Boolean)) {
			return defaultValue;
		}
		return ((Boolean)value).booleanValue();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypoint#getIntValue(java.lang.String, int)
	 */
	public int getIntValue(String name, int defaultValue) {
		Object value = getValue(name);
		if (value == null) return defaultValue;
		if (!(value instanceof Integer)) {
			return defaultValue;
		}
		return ((Integer)value).intValue();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypoint#getStringValue(java.lang.String, java.lang.String)
	 */
	public String getStringValue(String name, String defaultValue) {
		Object value = getValue(name);
		if (value == null) return defaultValue;
		if (!(value instanceof String)) {
			return defaultValue;
		}
		return value.toString();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypoint#getType()
	 */
	public String getType() {
		return this.type;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypoint#getValue(java.lang.String)
	 */
	public Object getValue(String name) {
		return attributes.get(name);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypoint#exists()
	 */
	public boolean exists() {
		return exists;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypoint#getTags()
	 */
	public ITag[] getTags() {
		return this.tags.toArray(new ITag[tags.size()]);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypoint#setBooleanValue(java.lang.String, boolean)
	 */
	public TagSEAChangeStatus setBooleanValue(String name, boolean value) {
		return setObjectValue(name, new Boolean(value));
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypoint#setIntValue(java.lang.String, int)
	 */
	public TagSEAChangeStatus setIntValue(String name, int value) {
		return setObjectValue(name, new Integer(value));
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypoint#setObjectValue(java.lang.String, java.lang.Object)
	 */
	public TagSEAChangeStatus setObjectValue(String name, Object value) {
		Object old = getValue(name);
		if (old == null) return new TagSEAChangeStatus(TagSEAPlugin.PLUGIN_ID, false, TagSEAChangeStatus.NO_SUCH_ATTRIBUTE,"Attribute doesn't exist"); //can't set something that hasn't been declared.
		if (!delegate.getAttributeType(name).equals(value.getClass()))
			return new TagSEAChangeStatus(TagSEAPlugin.PLUGIN_ID, false, TagSEAChangeStatus.BAD_ATTRIBUTE_TYPE, "Invalid type for requested waypoint attribute.");
	
		synchronized (getBlock()) {
			if (old.equals(value)) return TagSEAChangeStatus.SUCCESS_STATUS;
			this.attributes.put(name, value);
			TreeMap<String, Object> oldValues = new TreeMap<String, Object>();
			oldValues.put(name, old);
			WaypointChangeEvent event = WaypointChangeEvent.createChangeEvent(this, oldValues);
			TagSEAChangeStatus okay = delegate.processChange(event);
			if (okay.changePerformed) {
				TagSEAChangeSupport.INSTANCE.postWaypointChange(event);
			} else {
				//back-out the change.
				this.attributes.put(name, old);
			}
			return okay;
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypoint#setStringValue(java.lang.String, java.lang.String)
	 */
	public TagSEAChangeStatus setStringValue(String name, String value) {
		return setObjectValue(name, value);
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypoint#addTag(java.lang.String)
	 */
	public ITag addTag(String tagName) {
		
		TagsModel model = (TagsModel) TagSEAPlugin.getTagsModel();
		ITag tag;
		try {
			tag = model.createOrGetTag(tagName);
		} catch (TagSEAModelException e) {
			return null;
		}
		synchronized (getBlock()) {
			if (tagName.equals(ITag.DEFAULT)) {
				if (tags.size() == 1) {
					//return the default tag that is already present.
					ITag firstTag = tags.first();
					if (firstTag.getName().equals(ITag.DEFAULT)) {
						return firstTag;
					} else {
						return null;
					}
				} else if (tags.size() != 0) {
					//can't add the default tag to 
					return null;
				}
			}
			ITag[] oldITags = getTags();
			String[] oldTags = new String[oldITags.length];
			for (int i = 0; i < oldITags.length; i++) {
				oldTags[i] = oldITags[i].getName();
			}
			if (tags.add(tag)) {
				//remove the default tag.
				if (tags.size() == 2) {
					ITag defaultTag = null;
					for (ITag t : tags) {
						if (t.getName().equals(ITag.DEFAULT)) {
							defaultTag = t;
						}
					}
					if (defaultTag != null) {
						tags.remove(defaultTag);
						((Tag)defaultTag).removeWaypoint(this);
					}
				}
				WaypointChangeEvent event = WaypointChangeEvent.createTagChangeEvent(this, oldTags);
				TagSEAChangeStatus okay = delegate.processChange(event);
				if (okay.changePerformed) {
					TagSEAChangeSupport.INSTANCE.postWaypointChange(WaypointChangeEvent.createTagChangeEvent(this, oldTags));
					((Tag)tag).addWaypoint(this);
				} else {
					//back-out
					tags.remove(tag);
					((Tag)tag).delete();
				}
			}
		}
		return tag;
	}
	
	/**
	 * Silently adds the given tag to this waypoint. This is useful for when a tag is renamed to an
	 * already existing tag.
	 * @param tag the tag to add.
	 */
	protected void internalAddTag(ITag tag) {
		tags.add(tag);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypoint#removeTag(net.sourceforge.tagsea.core.ITag)
	 */
	public TagSEAChangeStatus removeTag(ITag tag) {
		if (!tags.contains(tag)) return new TagSEAChangeStatus(TagSEAPlugin.PLUGIN_ID, false, TagSEAChangeStatus.TAG_DOES_NOT_EXITS, "Tag does not exist on waypoint.");
		if (ITag.DEFAULT.equals(tag.getName())) return new TagSEAChangeStatus(TagSEAPlugin.PLUGIN_ID, false, TagSEAChangeStatus.DEFAULT_TAG_CHANGE_ERROR, "Cannot remove the default tag from a waypoint.");
		synchronized (getBlock()) {
			ITag[] oldITags = getTags();
			String[] oldTags = new String[oldITags.length];
			for (int i = 0; i < oldITags.length; i++) {
				oldTags[i] = oldITags[i].getName();
			}
			tags.remove(tag);
			ITag defaultTag = null;
			if (tags.size() == 0) {
				//add the default tag.
				TagsModel model = (TagsModel) TagSEAPlugin.getTagsModel();
				try {
					defaultTag = model.createOrGetTag(ITag.DEFAULT);
					tags.add(defaultTag);
				} catch (TagSEAModelException e) {
					tags.add(tag);
					return new TagSEAChangeStatus(TagSEAPlugin.PLUGIN_ID, false, TagSEAChangeStatus.DEFAULT_TAG_CHANGE_ERROR, "Could not create default tag");
				}
				
			}
			IWaypointChangeEvent event = WaypointChangeEvent.createTagChangeEvent(this, oldTags);
			TagSEAChangeStatus status = delegate.processChange(event);
			if (status.changePerformed) {
				TagSEAChangeSupport.INSTANCE.postWaypointChange(event);
				((Tag)tag).removeWaypoint(this);
				if (defaultTag != null) {
					((Tag)defaultTag).addWaypoint(this);
				}
			} else {
				//roll-back
				tags.add(tag);
				if (defaultTag != null) {
					((Tag)defaultTag).removeWaypoint(this);
				}
			}
			return status;
		}
	}
	
	/**
	 * Silently removes the given tag from the list of tags.
	 * @param tag
	 */
	protected void internalRemoveTag(ITag tag) {
		tags.remove(tag);
	}
	
	public TagSEAChangeStatus delete() {
		//set the existence to false first, so that there will be no recursive call
		//from the model.
		this.exists = false;
		return TagSEAPlugin.getWaypointsModel().removeWaypoint(this);
	}
	
	private Object getBlock() {
		return TagSEAChangeSupport.INSTANCE.getOperationBlocker();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypoint#getText()
	 */
	public String getText() {
		return this.attributes.get(ATTR_MESSAGE).toString();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypoint#getDate()
	 */
	public Date getDate() {
		Date date = null;

		date = getDateValue(ATTR_DATE, null);
		if (date != null) {
			if (date.equals(delegate.getDefaultValue(IWaypoint.ATTR_DATE)))
				date = null;
		}
	
		return date;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Waypoint that) {
		long diff = this.getWorkbenchId() - that.getWorkbenchId();
		if (diff < 0) return -1;
		if (diff == 0) return 0;
		return 1;
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (int)getWorkbenchId();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Waypoint))
			return false;
		Waypoint wp = (Waypoint) obj;
		return this.getWorkbenchId() == wp.getWorkbenchId();
	}
	
	/**
	 * Returns the workbench id that uniquely identifies the given waypoint <b>in this workbench only</b>.
	 * This will be used for things like drag and drop.
	 * @return the workbenchId
	 */
	public long getWorkbenchId() {
		return workbenchId;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypoint#getDateValue(java.lang.String, java.util.Date)
	 */
	public Date getDateValue(String name, Date defaultValue) {
		Object value = getValue(name);
		if (value == null) return defaultValue;
		if (!(value instanceof Date)) {
			return defaultValue;
		}
		return (Date)value;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypoint#setAuthor(java.lang.String)
	 */
	public TagSEAChangeStatus setAuthor(String author) {
		return setStringValue(ATTR_AUTHOR, author);	
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypoint#setDate(java.util.Date)
	 */
	public TagSEAChangeStatus setDate(Date date) {
		return setDateValue(ATTR_DATE, date);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypoint#setDateValue(java.lang.String, java.lang.Object)
	 */
	public TagSEAChangeStatus setDateValue(String name, Date value) {
		return setObjectValue(name, value);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypoint#setText(java.lang.String)
	 */
	public TagSEAChangeStatus setText(String text) {
		return setStringValue(ATTR_MESSAGE, text);	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public boolean isSubtypeOf(String parentType) {
		return delegate.isSubtypeOf(parentType);
	}


}
