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
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sourceforge.tagsea.core.ITagChangeListener;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.IWaypointChangeListener;
import net.sourceforge.tagsea.core.IWaypointLocator;
import net.sourceforge.tagsea.core.StrictWaypointLocator;
import net.sourceforge.tagsea.core.TagDelta;
import net.sourceforge.tagsea.core.TagSEAChangeStatus;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.model.internal.Waypoint;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Status;

/**
 * Class to be extended by implementors of a waypoint plugin. This delegate will handle
 * navigation to a waypoint in the workbench as well as answering questions about waypoints
 * such as whether or not the current state of the plugin will allow a waypoint to be
 * deleted by a UI element.
 * @author Del Myers
 */

public abstract class AbstractWaypointDelegate implements IWaypointType  {
	//for use in configuration
	private static final String TYPE_ATTRIBUTE ="type"; //$NON-NLS-1$
	private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
	private static final String ATTRIBUTE_ATTRIBUTE = "attribute"; //$NON-NLS-1$
	private static final String INT_TYPE = "integer"; //$NON-NLS-1$
	private static final String STRING_TYPE = "string"; //$NON-NLS-1$
	private static final String BOOL_TYPE = "boolean"; //$NON-NLS-1$
	private static final String DATE_TYPE = "date"; //$NON-NLS-1$
	private static final String DEFAULT_ATTRIBUTE = "default"; //$NON-NLS-1$
	private static final String PARENT_FIELD = "parent";
	private String name;
	private String type;
	private TreeSet<String> parentList;
	private boolean parentsResolved;
	private Map<String, Object> attributeDefaultMap;
	private LocalWaypointChangeListener waypointListener;
	private LocalTagListener tagListener;
	private StrictWaypointLocator locator;
	
	
	private class LocalTagListener implements ITagChangeListener {
		public void tagsChanged(TagDelta delta) {
			AbstractWaypointDelegate.this.tagsChanged(delta);
		}

	}
	
	private class LocalWaypointChangeListener implements IWaypointChangeListener {
		public void waypointsChanged(WaypointDelta delta) {
			AbstractWaypointDelegate.this.waypointsChanged(delta);
		}
	}
	
	/**
	 * 
	 */
	protected AbstractWaypointDelegate() {
		parentsResolved = false;
		name = null;
		type = null;
		parentList = null;
	}
	

	/**
	 * Asks the delegate to navigate to this waypoint in the workbench. This could include
	 * tasks such as opening up a web browser, or navigating to a line in an editor. Implementors
	 * should not expect this call to be made from the UI thread.
	 * @param waypoint the waypoint to navigate to.
	 */
	protected abstract void navigate(IWaypoint waypoint);
	
	/**
	 * Initializes the delegate and loads all the previously saved waypoints.
	 *
	 */
	public final void initialize() {
		if (this.waypointListener == null) {
			this.waypointListener = new LocalWaypointChangeListener();
			this.tagListener = new LocalTagListener();
		}
		TagSEAPlugin.addTagChangeListener(this.tagListener);
		TagSEAPlugin.addWaypointChangeListener(this.waypointListener, getType());
		load();
	}
	
	
	/**
	 * Loads all the previously saved waypoints and contributes them to the model. This happens
	 * at start up only once for each delegate. Delegates will be notified of new or changed
	 * waypoint/tag events that occur during this process. It is recommended that plugins register
	 * themselves as save participants so that this process will have the most up-to-date 
	 * information possible.
	 */
	protected abstract void load();
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointType#getType()
	 */
	public String getType() {
		return this.type;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointType#getName()
	 */
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointType#getDeclaredAttributes()
	 */
	public final String[] getDeclaredAttributes() {
		resolveParents();
		return attributeDefaultMap.keySet().toArray(new String[attributeDefaultMap.size()]);
	}
	
	/**
	 * Resolves all the attributes for the parent types of this waypoint.
	 */
	private void resolveParents() {
		if (parentsResolved) return;
		//first make sure that all parent types are in the list of parents.
		String[] parents = parentList.toArray(new String[parentList.size()]);
		for (String parent : parents) {
			IWaypointType parentType = 
				TagSEAPlugin.getDefault().getWaypointType(parent);
			if (getType().equals(parent)) continue;
			if (parentType instanceof AbstractWaypointDelegate) {
				((AbstractWaypointDelegate)parentType).resolveParents();
				parentList.addAll(((AbstractWaypointDelegate)parentType).parentList);
			}
		}
		//get all the attributes for the parents
		for (String parent : parentList) {
			IWaypointType parentType = 
				TagSEAPlugin.getDefault().getWaypointType(parent);
			if (getType().equals(parent)) continue;
			if (parentType != null) {
				String[] parentAttrs = parentType.getDeclaredAttributes();
				for (String attr : parentAttrs) {
					if (!attributeDefaultMap.containsKey(attr)) {
						attributeDefaultMap.put(attr, parentType.getDefaultValue(attr));
					}
				}
			}
		}
		parentsResolved = true;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointType#isDeclaredAttribute(java.lang.String)
	 */
	public final boolean isDeclaredAttribute(String attribute) {
		resolveParents();
		return attributeDefaultMap.containsKey(attribute);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointType#getAttributeType(java.lang.String)
	 */
	public final Class<?> getAttributeType(String attribute) {
		if (!isDeclaredAttribute(attribute)) return null;
		return attributeDefaultMap.get(attribute).getClass();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointType#getDefaultValue(java.lang.String)
	 */
	public final Object getDefaultValue(String attribute) {
		resolveParents();
		return attributeDefaultMap.get(attribute);
	}
	
	/**
	 * Configures this delegate according to the given configuration element.
	 * @param e
	 */
	final boolean configure(IConfigurationElement e) {
		this.name = e.getAttribute(NAME_ATTRIBUTE);
		this.type = e.getContributor().getName() + "." + e.getAttribute(TYPE_ATTRIBUTE);
		attributeDefaultMap = new TreeMap<String, Object>();
		//add the default attributes
		attributeDefaultMap.put(IWaypoint.ATTR_AUTHOR, "");
		attributeDefaultMap.put(IWaypoint.ATTR_MESSAGE, "");
		attributeDefaultMap.put(IWaypoint.ATTR_DATE, new Date(0));
		TreeSet<String> parents = new TreeSet<String>();
		parents.add(IWaypoint.BASE_WAYPOINT);
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
			} else if (PARENT_FIELD.equals(child.getName())) {
				parents.add(child.getAttribute("type"));
			}
		}
		parentList = parents;
		return true;
	}
	
	/**
	 * Local indication for when tags have been changed in the model.
	 * @param delta the tag delta.
	 */
	protected abstract void tagsChanged(TagDelta delta);
	
	
	/**
	 * Local indication for when waypoints of the type defined by this delegate have
	 * been changed.
	 * @param delta the waypoint delta.
	 */
	protected abstract void waypointsChanged(WaypointDelta delta);
	
			
	/**
	 * A convenience method to check for equality between waypoints. This method does not
	 * replace the equals() method on the waypoint itself, but may be used for UI functionality
	 * that may need to find equality between waypoints for different criteria. For example:
	 * to check for equality based on attribute values. The default functionality is to
	 * check for identity of the waypoints in the workspace. Overriders can be assured
	 * that the two waypoints are of the type defined by this delegate.
	 * @param wp0 a waypoint.
	 * @param wp1 a waypoint.
	 * @return whether the waypoints are equal.
	 */
	protected boolean waypointsEqual(IWaypoint wp0, IWaypoint wp1) {
		return wp0.equals(wp1);
	}
	
	
	/**
	 * 
	 * @param waypoint
	 * @return
	 */
	public int waypointHashCode(IWaypoint waypoint) {
		return (int)((Waypoint)waypoint).getWorkbenchId();
	}


	/**
	 * Called by the platform before a change occurs on the waypoint for the given event to
	 * allow the plugin to refuse to allow the platform to perform the change. By default,
	 * TagSEAChangeStatus.SUCCESS_STATUS is returned for all changes. 
	 * @param event the event to process.
	 * @return the status.
	 */
	public TagSEAChangeStatus processChange(IWaypointChangeEvent event) {
		return TagSEAChangeStatus.SUCCESS_STATUS;
	}
	
	/**
	 * Checks to see if the given waypoint type is a parent of this type.
	 * @param parentType the type to check. If this waypoint type is a subtype of the given type,
	 * it can be guaranteed that this type has all of the attributes of the parent.
	 * @return whether or not the given waypoint type is a parent of this type.
	 */
	public boolean isSubtypeOf(String parentType) {
		if (parentType == null) return false;
		for (String parent : parentList) {
			if (parent.equals(parentType)) return true;
		}
		return false;
	}
	
	/**
	 * Returns the waypoint locator for this waypoint type. Clients may override to provide a different locator.
	 * Returns an instance of {@link StrictWaypointLocator} by default.
	 * @return the waypoint locator for this waypoint type.
	 */
	public IWaypointLocator getLocator() {
		if (this.locator == null) {
			this.locator = new StrictWaypointLocator(getType());
		}
		return this.locator;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointType#isDelegate()
	 */
	public final boolean isDelegate() {
		return true;
	}
}
