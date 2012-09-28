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

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.operations.IInternalUpdateOperation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

/**
 * A descriptor that delegates to a waypoint to get its information. If new values are set in the descriptor,
 * those values won't be sent to the waypoint unless commit() is called.
 * @author Del Myers
 *
 */
public class ResourceWaypointProxyDescriptor extends AbstractMutableWaypointDescriptor {

	/**
	 * Used to commit changes to the proxy
	 * @author Del Myers
	 *
	 */
	private final class CommitOperation extends TagSEAOperation implements IInternalUpdateOperation {
		/**
		 * @param name
		 */
		private CommitOperation() {
			super("Committing Waypoint Changes...");
		}

		@Override
		public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
			MultiStatus status = new MultiStatus(ResourceWaypointPlugin.PLUGIN_ID, 0, "", null);
			status.merge(waypoint.setAuthor(getAuthor()).getStatus());
			status.merge(waypoint.setDate(getDate()).getStatus());
			status.merge(waypoint.setText(getText()).getStatus());
			status.merge(waypoint.setStringValue(IResourceWaypointAttributes.ATTR_REVISION, getRevision()).getStatus());
			status.merge(waypoint.setStringValue(IResourceWaypointAttributes.ATTR_STAMP, getStamp()).getStatus());
			status.merge(waypoint.setIntValue(IResourceWaypointAttributes.ATTR_LINE, getLine()).getStatus());
			status.merge(waypoint.setIntValue(IResourceWaypointAttributes.ATTR_CHAR_END, getCharEnd()).getStatus());
			status.merge(waypoint.setIntValue(IResourceWaypointAttributes.ATTR_CHAR_START, getCharStart()).getStatus());
			SortedSet<String> newTags = getTags();
			for (ITag tag : waypoint.getTags()) {
				if (!newTags.contains(tag.getName())) {
					status.merge(waypoint.removeTag(tag).getStatus());
				}
			}
			for (String newT : newTags) {
				waypoint.addTag(newT);
			}
			return status;
		}
	}

	private IWaypoint waypoint;
	private String author;
	private Integer charEnd;
	private Integer charStart;
	private Date date;
	private Integer line;
	private String resourceString;
	private SortedSet<String> tagList;
	private String text;
	private String revision;
	private String stamp;

	public ResourceWaypointProxyDescriptor(IWaypoint waypoint) {
		this.waypoint = waypoint;
	}
	
	public void commit() {
		
		
		TagSEAPlugin.run(new CommitOperation(), true);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getAuthor()
	 */
	public String getAuthor() {
		if (this.author == null)
			return waypoint.getAuthor();
		return author;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getCharEnd()
	 */
	public int getCharEnd() {
		if (this.charEnd == null)
			return waypoint.getIntValue(IResourceWaypointAttributes.ATTR_CHAR_END, -1);
		return charEnd;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getCharStart()
	 */
	public int getCharStart() {
		if (this.charStart == null)
			return waypoint.getIntValue(IResourceWaypointAttributes.ATTR_CHAR_START, -1);
		return charStart;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getDate()
	 */
	public Date getDate() {
		if (this.date == null)
			return waypoint.getDate();
		return date;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getLine()
	 */
	public int getLine() {
		if (this.line == null)
			return waypoint.getIntValue(IResourceWaypointAttributes.ATTR_LINE, -1);
		return line;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getResource()
	 */
	public String getResource() {
		if (this.resourceString == null) {
			return waypoint.getStringValue(IResourceWaypointAttributes.ATTR_RESOURCE, "");
		}
		return resourceString;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getRevision()
	 */
	public String getRevision() {
		if (this.revision == null) {
			return waypoint.getStringValue(IResourceWaypointAttributes.ATTR_REVISION, "");
		}
		return revision;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getStamp()
	 */
	public String getStamp() {
		if (this.stamp == null)
			return waypoint.getStringValue(IResourceWaypointAttributes.ATTR_STAMP, "");
		return stamp;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getTags()
	 */
	public SortedSet<String> getTags() {
		if (this.tagList == null) {
			ITag[] tags = waypoint.getTags();
			SortedSet<String> list = new TreeSet<String>();
			for (ITag tag : tags) {
				list.add(tag.getName());
			}
			return list;
		}
		return tagList;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getText()
	 */
	public String getText() {
		if (this.text == null)
			return waypoint.getText();
		return text;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor#getText(java.lang.String)
	 */
	public void setText(String text) {
		this.text = text;		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor#setAuthor(java.lang.String)
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor#setCharEnd(int)
	 */
	public void setCharEnd(int end) {
		this.charEnd = end;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor#setCharStart(int)
	 */
	public void setCharStart(int start) {
		this.charStart = start;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor#setDate(java.util.Date)
	 */
	public void setDate(Date date) {
		this.date = date;		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor#setLine(int)
	 */
	public void setLine(int line) {
		this.line = line;		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor#setResource(java.lang.String)
	 */
	public void setResource(String path) {
		this.resourceString = path;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor#setRevision(java.lang.String)
	 */
	public void setRevision(String revision) {
		this.revision = revision;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor#setStamp(java.lang.String)
	 */
	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor#setTags(java.util.List)
	 */
	public void setTags(SortedSet<String> tags) {
		this.tagList = new TreeSet<String>(tags);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().isAssignableFrom(getClass())) return false;
		ResourceWaypointProxyDescriptor that = (ResourceWaypointProxyDescriptor) obj;
		if (this == that) return true;
		if (waypoint.equals(that.waypoint)) {
			//make sure all the values match.
			String[] attributes = getAttributes();
			for (String attribute : attributes) {
				Object thisValue = getValue(attribute);
				Object thatValue = that.getValue(attribute);
				if ((thisValue == null && thatValue != null) || (thisValue != null && thatValue == null))
					return false;
				if (thisValue == thatValue || thisValue.equals(thatValue)) continue;
				else return false;
			}
		} else {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int code = 0;
		String[] attributes = getAttributes();
		for (String attribute : attributes) {
			Object thisValue = getValue(attribute);
			if (thisValue != null) {
				code += thisValue.hashCode();
			}
		}
		return code;
	}

	/**
	 * @return
	 */
	public IWaypoint getWaypoint() {
		return waypoint;
	}

}
