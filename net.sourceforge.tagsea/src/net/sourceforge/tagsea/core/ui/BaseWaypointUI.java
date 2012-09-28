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
package net.sourceforge.tagsea.core.ui;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;

/**
 * Basic implementation of the waypoint ui.
 * @author Del Myers
 */

public class BaseWaypointUI implements IWaypointUIExtension {

	private String type;
	private static final String TYPE_ATTRIBUTE ="type"; //$NON-NLS-1$
	private static final String ATTRIBUTE_ATTRIBUTE = "attribute"; //$NON-NLS-1$
	private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
	private static final String PARENT_FIELD = "parent";
	
	private HashMap<String, String> attributeLabels;
	
	/**
	 * Returns a human-readable label for the given attribute name which is not dependant on
	 * a waypoint.
	 * @param attribute
	 * @return
	 */
	public String getAttributeLabel(String attribute) {
		if (attributeLabels == null) {
			loadAttributeLabels();
		}
		String label = attributeLabels.get(attribute);
		if (label == null) {
			label = attribute;
		}
		return label;
	}

	/**
	 * 
	 */
	private void loadAttributeLabels() {
		attributeLabels = new HashMap<String, String>();
		IConfigurationElement[] elements = 
			Platform.getExtensionRegistry().getConfigurationElementsFor("net.sourceforge.tagsea.waypoint"); //$NON-NLS-1$
		HashSet<IConfigurationElement> elementSet = new HashSet<IConfigurationElement>();
		IConfigurationElement thisElement = null;
		for (IConfigurationElement e : elements) {
			if ("interface".equals(e.getName()) || "waypoint".equals(e.getName())) { //$NON-NLS-1$
				String localType = e.getContributor().getName() + "." + e.getAttribute(TYPE_ATTRIBUTE);
				if (localType.equals(getType())) {
					thisElement = e;
				} else if (IWaypoint.BASE_WAYPOINT.equals(localType)) {
					for (IConfigurationElement ec : e.getChildren(ATTRIBUTE_ATTRIBUTE)) {
						String name = ec.getAttribute(NAME_ATTRIBUTE);
						String label = ec.getAttribute("label");
						if (label != null) {
							attributeLabels.put(name, label);
						}
					}
				} else {
					elementSet.add(e);
				}
			}
		}
		if (thisElement != null) {
			//gather the parents and add local labels
			for (IConfigurationElement e: thisElement.getChildren()) {
				if (ATTRIBUTE_ATTRIBUTE.equals(e.getName())) {
					String name = e.getAttribute(NAME_ATTRIBUTE);
					String label = e.getAttribute("label");
					if (label != null) {
						attributeLabels.put(name, label);
					}
				} else if (PARENT_FIELD.equals(e.getName())) {
					//grab the values for the parent.
					String type = e.getAttribute("type");
					for (IConfigurationElement p : elementSet) {
						String pType = p.getContributor().getName() + "." + p.getAttribute(TYPE_ATTRIBUTE);
						if (pType.equals(type)) {
							for (IConfigurationElement pc : p.getChildren(ATTRIBUTE_ATTRIBUTE)) {
								String name = pc.getAttribute(NAME_ATTRIBUTE);
								String label = pc.getAttribute("label");
								if (label != null) {
									attributeLabels.put(name, label);
								}
							}
						}
					}
				}
			}
		}
		
	}

	/**
	 * Returns true by default.
	 */
	public boolean canUIChange(IWaypoint waypoint, String attribute) {
		return true;
	}

	/**
	 * Returns true by default.
	 */
	public boolean canUIDelete(IWaypoint waypoint) {
		return true;
	}

	/**
	 * Returns true by default.
	 */
	public boolean canUIMove(IWaypoint waypoint) {
		return true;
	}
	
	/**
	 * Returns true by default, because it is an expected feature of most waypoint types. 
	 * Clients should override.
	 */
	public boolean canUIAddTag(IWaypoint waypoint) {
		return true;
	}
	
	/**
	 * Returns true by default, because it is an expected feature of most waypoint types. 
	 * Clients should override.
	 */
	public boolean canUIDeleteTag(IWaypoint waypoint) {
		return true;
	}
	

	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.IWaypointUIExtension#getImage(net.sourceforge.tagsea.core.IWaypoint)
	 */
	public Image getImage(IWaypoint waypoint) {
		return TagSEAPlugin.getDefault().getImageRegistry().get(ITagSEAImageConstants.IMG_WAYPOINT);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.IWaypointUIExtension#getLabel(net.sourceforge.tagsea.core.IWaypoint)
	 */
	public String getLabel(IWaypoint waypoint) {
		String message = waypoint.getText();
		String location = getLocationString(waypoint);
		String result = message;
		if (result == null) {
			result = "";
		}
		if (location != null && !"".equals(location)) {
			if (!"".equals(result)) {
				result = result + " (" + location + ")";
			} else {
				result = location;
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.IWaypointUIExtension#getLabel(net.sourceforge.tagsea.core.IWaypoint, int)
	 */
	public String getLabel(IWaypoint waypoint, int column) {
		return waypoint.getText();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.IWaypointUIExtension#getSmallImage(net.sourceforge.tagsea.core.IWaypoint)
	 */
	public Image getSmallImage(IWaypoint waypoint) {
		return TagSEAPlugin.getDefault().getImageRegistry().get(ITagSEAImageConstants.IMG_WAYPOINT);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.IWaypointUIExtension#getToolTipText(net.sourceforge.tagsea.core.IWaypoint)
	 */
	public String getToolTipText(IWaypoint waypoint) {
		return waypoint.getText();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.IWaypointUIExtension#getVisibleAttributes()
	 */
	public String[] getVisibleAttributes() {
		return TagSEAPlugin.getDefault().getWaypointDelegate(getType()).getDeclaredAttributes();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.IWaypointUIExtension#getType()
	 */
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.IWaypointUIExtension#getAttributeLabel(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	public String getAttributeLabel(IWaypoint waypoint, String attribute) {
		return getAttributeLabel(attribute);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.IWaypointUIExtension#getValueImage(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	public Image getValueImage(IWaypoint waypoint, String attribute) {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.IWaypointUIExtension#getValueLabel(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	public String getValueLabel(IWaypoint waypoint, String attribute, Object value) {
		if (value == null) {
			value = waypoint.getValue(attribute);
		}
		if (IWaypoint.ATTR_DATE.equals(attribute)) {
			Date date = (Date)value;
			
			if (date == null) {
				value = "";
			} else {
				value = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
			}
		} 
		if (value != null) return value.toString();
		return "";
	}
	
	public String getLocationString(IWaypoint waypoint) {
		return "";
	}

}
