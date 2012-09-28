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

/**
 * IWaypointType is the generic interface for WaypointInterface and AbstractWaypointDelegate. All waypoint
 * types contributed using 
 * @author Del Myers
 */
public interface IWaypointType {

	/**
	 * Returns the fully-qualified identifier for the waypoint type.
	 * @return the fully-qualified identifier for the waypoint type.
	 */
	String getType();

	/**
	 * Returns the human-readable name for this waypoint type.
	 * @return the human-readable name for this waypoint type.
	 */
	String getName();

	/**
	 * Returns the attributes for this type of waypoint that were declared in the plugin extension.
	 * @return the attributes for this type of waypoint that were declared in the plugin extension.
	 */
	String[] getDeclaredAttributes();

	/**
	 * Returns true iff the attribute of the given name was declared in the plugin extension.
	 * @param name the attribute name.
	 * @return true iff the attribute of the given name was declared in the plugin extension.
	 */
	boolean isDeclaredAttribute(String attribute);

	/**
	 * Gets the java.lang Object class type for the given attribute name. For example, if the
	 * plugin declared the given attribute to be "string", this method will return 
	 * java.lang.String.class. If the given attribute was not declared by the plugin, this
	 * method will return null.
	 * @param attribute the attribute name.
	 * @return the java.lang Object class type for the given attribute name.
	 */
	Class<?> getAttributeType(String attribute);

	/**
	 * Returns the default value for the given attribute, or null if it was not declared
	 * for the waypoint type.
	 * @param attribute the attribute name.
	 * @return the default value for the given attribute, or null if it was not declared
	 * for the waypoint type.
	 */
	Object getDefaultValue(String attribute);
	
	/**
	 * Returns true if this type represents a user-defined concrete waypoint type defined in
	 * the extension point net.sourceforge.tagsea.waypoint. If true, then this object can be
	 * safely cast to an AbstractWaypontDelegate. Otherwise, the type was defined using the
	 * extension point net.sourceforge.tagsea.waypointInterface and this object can be safely
	 * cast to a WaypointInterface.
	 * @return true if this type represents a user-defined concrete waypoint type.
	 * @see AbstractWaypointDelegate
	 * @see WaypointInterface
	 */
	boolean isDelegate();

}