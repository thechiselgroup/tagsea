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

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;

/**
 * Abstract implementation of IMutableWaypointDescriptor that only allows changes on valid 
 * resource waypoint attributes.
 * @author Del Myers
 *
 */
public abstract  class AbstractMutableWaypointDescriptor implements
		IMutableResourceWaypointDescriptor {

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getValue(java.lang.String)
	 */
	public Object getValue(String attr) {
		if (IResourceWaypointAttributes.ATTR_AUTHOR.equals(attr)) {
			return getAuthor();
		} else if (IResourceWaypointAttributes.ATTR_CHAR_END.equals(attr)) {
			return getCharEnd();
		} else if (IResourceWaypointAttributes.ATTR_CHAR_START.equals(attr)) {
			return getCharStart();
		} else if (IResourceWaypointAttributes.ATTR_DATE.equals(attr)) {
			return getDate();
		} else if (IResourceWaypointAttributes.ATTR_LINE.equals(attr)) {
			return getLine();
		} else if (IResourceWaypointAttributes.ATTR_MESSAGE.equals(attr)) {
			return getText();
		} else if (IResourceWaypointAttributes.ATTR_RESOURCE.equals(attr)) {
			return getResource();
		} else if (IResourceWaypointAttributes.ATTR_REVISION.equals(attr)) {
			return getRevision();
		} else if (IResourceWaypointAttributes.ATTR_STAMP.equals(attr)) {
			return getStamp();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IMutableResourceWaypointDescriptor#setValue(java.lang.String, java.lang.Object)
	 */
	public void setValue(String attr, Object value) {
		if (IResourceWaypointAttributes.ATTR_AUTHOR.equals(attr)) {
			setAuthor(value.toString());
		} else if (IResourceWaypointAttributes.ATTR_CHAR_END.equals(attr)) {
			setCharEnd((Integer)value);
		} else if (IResourceWaypointAttributes.ATTR_CHAR_START.equals(attr)) {
			setCharStart((Integer)value);
		} else if (IResourceWaypointAttributes.ATTR_DATE.equals(attr)) {
			setDate((Date)value);
		} else if (IResourceWaypointAttributes.ATTR_LINE.equals(attr)) {
			setLine((Integer)value);
		} else if (IResourceWaypointAttributes.ATTR_MESSAGE.equals(attr)) {
			setText(value.toString());
		} else if (IResourceWaypointAttributes.ATTR_RESOURCE.equals(attr)) {
			setResource(value.toString());
		} else if (IResourceWaypointAttributes.ATTR_REVISION.equals(attr)) {
			setRevision(value.toString());
		} else if (IResourceWaypointAttributes.ATTR_STAMP.equals(attr)) {
			setStamp(value.toString());
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor#getAttributes()
	 */
	public String[] getAttributes() {
		//@tag tagsea.bug.minor : doesn't take care of the HANDLE_IDENTIFIER attribute. But that isn't saved anyway. 
		return TagSEAPlugin.
			getDefault().
			getWaypointType(ResourceWaypointPlugin.WAYPOINT_ID).
			getDeclaredAttributes();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter.isAssignableFrom(IResourceWaypointDescriptor.class)) {
			return this;
		}
		return null;
	}

}
