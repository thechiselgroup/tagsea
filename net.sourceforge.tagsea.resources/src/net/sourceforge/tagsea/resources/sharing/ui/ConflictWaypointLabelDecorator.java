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
package net.sourceforge.tagsea.resources.sharing.ui;

import java.util.HashMap;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.WaypointMatch;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;
import net.sourceforge.tagsea.resources.waypoints.ResourceWaypointProxyDescriptor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IColorDecorator;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Decorates elements that adapt to IResourceWaypointDescriptor or IResourceWaypointDescriptor[]
 * to see if there is a matching waypoint in the workspace. If the waypoint descriptor represents a
 * new waypoint, then a '&lt;' is prepended to the text. If it is not new, but there are different
 * values for the fields, then the foreground is coloured red.
 * @author Del Myers
 *
 */
public class ConflictWaypointLabelDecorator implements
		ILabelDecorator, IColorDecorator {
	
	private Color red;
	private boolean ignoreTextData;
	private boolean ignoreRevision;
	
	private int EXISTS = 0;
	private int NEW = 1;
	private int COLLISION = 2;

	private HashMap<IWaypointIdentifier, Integer> cache;
	public ConflictWaypointLabelDecorator() {
		this(false, false);
	}
	
	public ConflictWaypointLabelDecorator(boolean ignoreTextData, boolean ignoreRevision) {
		this.ignoreTextData = ignoreTextData;
		this.ignoreRevision = ignoreRevision;
		red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		cache = new HashMap<IWaypointIdentifier, Integer>();
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object)
	 */
	public Image decorateImage(Image image, Object element) {
		return image;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String, java.lang.Object)
	 */
	public String decorateText(String text, Object element) {
		if (element instanceof IAdaptable) {
			IResourceWaypointDescriptor desc =
				(IResourceWaypointDescriptor) 
				((IAdaptable)element).getAdapter(IResourceWaypointDescriptor.class);
			if (desc == null) {
				IResourceWaypointDescriptor[] descs =
					(IResourceWaypointDescriptor[])
					((IAdaptable)element).getAdapter(IResourceWaypointDescriptor[].class);
				if (descs != null) {
					int type = getDifferenceType(descs);
					if ((type & NEW) != 0) {
						return "<" + text;
					}
				}
			} else {
				int type = getDifferenceType(desc);
				if ((type & NEW) != 0) {
					return "<" + text;
				}
			}
		}
		return text;
	}

	/**
	 * @param desc
	 * @return
	 */
	private int getDifferenceType(IResourceWaypointDescriptor desc) {
		ResourceWaypointIdentifier identifier = new ResourceWaypointIdentifier(desc);
		Integer mask = cache.get(identifier);
		if (mask == null) {
			WaypointMatch[] matches = TagSEAPlugin.getDefault().locate(ResourceWaypointPlugin.WAYPOINT_ID, identifier.getAttributes(), identifier.getTagNames());
			if (matches == null || matches.length == 0) {
				mask = NEW;
			} else {
				IWaypoint wp = matches[0].getWaypoint();
				String[] atts = desc.getAttributes();
				mask = EXISTS;
				for (String att : atts) {
					if (ignoreRevision && att.equals(IResourceWaypointAttributes.ATTR_REVISION)) {
						continue;
					}
					if (ignoreTextData && (
						att.equals(IResourceWaypointAttributes.ATTR_LINE) ||
						att.equals(IResourceWaypointAttributes.ATTR_CHAR_END) ||
						att.equals(IResourceWaypointAttributes.ATTR_CHAR_START)
						)
					){
						continue;
					}
					if (att.equals(IResourceWaypointAttributes.ATTR_HANDLE_IDENTIFIER)) continue;
					Object thisValue = desc.getValue(att);
					Object thatValue = wp.getValue(att);
					if (thisValue == null && thatValue == null) {
						continue;
					} else if ((thisValue == null && thatValue != null) || (thisValue !=null && thatValue==null)) {
						mask = COLLISION;
					} else if (!thisValue.equals(thatValue)) {
						mask = COLLISION;
					}
				}
				if (mask != COLLISION) {
					//check the tags
					if (!desc.getTags().equals(new ResourceWaypointProxyDescriptor(wp).getTags())) {
						mask = COLLISION;
					}
				}
			}
			cache.put(identifier, mask);
		}
		return mask;
	}
	
	private int getDifferenceType(IResourceWaypointDescriptor[] descs) {
		int mask = 0;
		for (IResourceWaypointDescriptor desc : descs) {
			mask |= getDifferenceType(desc);
			if (mask == (COLLISION & EXISTS)) {
				//no need to do more work
				break;
			}
		}
		return mask;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		cache.clear();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorDecorator#decorateBackground(java.lang.Object)
	 */
	public Color decorateBackground(Object element) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorDecorator#decorateForeground(java.lang.Object)
	 */
	public Color decorateForeground(Object element) {
		if (element instanceof IAdaptable) {
			IResourceWaypointDescriptor desc =
				(IResourceWaypointDescriptor) 
				((IAdaptable)element).getAdapter(IResourceWaypointDescriptor.class);
			if (desc == null) {
				IResourceWaypointDescriptor[] descs =
					(IResourceWaypointDescriptor[])
					((IAdaptable)element).getAdapter(IResourceWaypointDescriptor[].class);
				if (descs != null) {
					int type = getDifferenceType(descs);
					if ((type & COLLISION) != 0) {
						return red;
					}
				}
			} else {
				int type = getDifferenceType(desc);
				if ((type & COLLISION) != 0) {
					return red;
				}
			}
		}
		return null;
	}

}
