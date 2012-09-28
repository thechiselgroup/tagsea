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
package net.sourceforge.tagsea;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;

import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Status;

/**
 * Defines an access point for all of the information about a waypoint interface defined by the
 * net.sourceforge.tagsea.waypointInterface extension point.
 * @author Del Myers
 */
public final class WaypointInterface implements IWaypointType {
	//for use in configuration
	private static final String TYPE_ATTRIBUTE ="type"; //$NON-NLS-1$
	private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
	private static final String ATTRIBUTE_ATTRIBUTE = "attribute"; //$NON-NLS-1$
	private static final String INT_TYPE = "integer"; //$NON-NLS-1$
	private static final String STRING_TYPE = "string"; //$NON-NLS-1$
	private static final String BOOL_TYPE = "boolean"; //$NON-NLS-1$
	private static final String DATE_TYPE = "date"; //$NON-NLS-1$
	private static final String DEFAULT_ATTRIBUTE = "default"; //$NON-NLS-1$
	private String name;
	private String type;
	private TreeMap<String, Object> attributeDefaultMap;
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointType#getAttributeType(java.lang.String)
	 */
	public Class<?> getAttributeType(String attribute) {
		if (isDeclaredAttribute(attribute)) {
			return getDefaultValue(attribute).getClass();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointType#getDeclaredAttributes()
	 */
	public String[] getDeclaredAttributes() {
		return attributeDefaultMap.keySet().toArray(new String[attributeDefaultMap.size()]);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointType#getDefaultValue(java.lang.String)
	 */
	public Object getDefaultValue(String attribute) {
		return attributeDefaultMap.get(attribute);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointType#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointType#getType()
	 */
	public String getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointType#isDeclaredAttribute(java.lang.String)
	 */
	public boolean isDeclaredAttribute(String attribute) {
		return attributeDefaultMap.containsKey(attribute);
	}
	
	final boolean configure(IConfigurationElement e) {
		this.name = e.getAttribute(NAME_ATTRIBUTE);
		this.type = e.getContributor().getName() + "." + e.getAttribute(TYPE_ATTRIBUTE);
		attributeDefaultMap = new TreeMap<String, Object>();
		//add the default attributes
		attributeDefaultMap.put(IWaypoint.ATTR_AUTHOR, "");
		attributeDefaultMap.put(IWaypoint.ATTR_MESSAGE, "");
		attributeDefaultMap.put(IWaypoint.ATTR_DATE, new Date(0));
		IConfigurationElement[] children = e.getChildren();
		for (IConfigurationElement child : children) {
			if (ATTRIBUTE_ATTRIBUTE.equals(child.getName())) {
				//configure the attributes.
				String name = child.getAttribute(NAME_ATTRIBUTE);
				String type = child.getAttribute(TYPE_ATTRIBUTE);
				String def = child.getAttribute(DEFAULT_ATTRIBUTE);
				//check the types
				if (INT_TYPE.equals(type)) {
					int value = 0;
					if (def != null) {
						try {
							value = Integer.parseInt(def);
						} catch (NumberFormatException ex) {
							Status status = new Status(
								Status.ERROR,
								TagSEAPlugin.PLUGIN_ID,
								Status.ERROR,
								"Bad value for attribute " + name,
								ex
							);
							TagSEAPlugin.getDefault().getLog().log(status);
						}
					}
					attributeDefaultMap.put(name, new Integer(value));
				} else if (STRING_TYPE.equals(type)) {
					String value = "";
					if (def != null) value = def;
					attributeDefaultMap.put(name, value);
				} else if (BOOL_TYPE.equals(type)) {
					boolean value = false;
					if (def != null) {
						value = Boolean.parseBoolean(def);
					}
					attributeDefaultMap.put(name, new Boolean(value));
				} else if (DATE_TYPE.equals(type)) {
					Date value = new Date(0);
					if (def != null) {
						int colon = def.indexOf(':');
						if (colon == 4) {
							String localeString = def.substring(0, 2);
							String countryString = def.substring(2, 4);
							String dateString = def.substring(5);
							Locale locale = new Locale(localeString, countryString);
							DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
							try {
								value = df.parse(dateString);
							} catch (ParseException ex) {
								TagSEAPlugin.getDefault().log("Could not parse date " + dateString, ex);
							}
						}
						attributeDefaultMap.put(name, value);
					}
				} else {
					Status status = new Status(
						Status.ERROR,
						TagSEAPlugin.PLUGIN_ID,
						Status.ERROR,
						"Bad type value for attribute " + name,
						null
					);
					TagSEAPlugin.getDefault().getLog().log(status);
					return false;
				}
			} 
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointType#isDelegate()
	 */
	public final boolean isDelegate() {
		return false;
	}

}
