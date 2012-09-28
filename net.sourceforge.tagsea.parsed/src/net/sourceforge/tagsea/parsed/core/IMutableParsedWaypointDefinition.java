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
package net.sourceforge.tagsea.parsed.core;

/**
 * An extension to IParsedWaypointDefinitions which allows the content type/and or file
 * associations to be changed.
 * @author Del Myers
 *
 */
public interface IMutableParsedWaypointDefinition extends
		IParsedWaypointDefinition {
	
	/**
	 * Adds the given listener to the list of listeners for this parsed waypoint definition.
	 * The listener is not added if an equal listener is already present.
	 * @param listener the listener to add.
	 */
	public void addDefinitionListener(IParsedWaypointDefinitionChangeListener listener);
	
	/**
	 * Removes the given listener if it exists in the list of listeners.
	 * @param listener the listener to remove.
	 */
	public void removeDefinitionListener(IParsedWaypointDefinitionChangeListener listener);
	
	/**
	 * Indicates whether or not file associations are mutable.
	 * @return whether or not file associations are mutable.
	 */
	public boolean canFileAssociationsChange();
	
	/**
	 * Sets the file associations. Implementors are required to notify listeners of the changes.
	 * @param fileAssociations the new associations.
	 */
	public void setFileAssociations(String[] fileAssociations);
	
	/**
	 * Indicates whether or not the content type for this definition is mutable.
	 * @return whether or not the content type for this definition is mutable.
	 */
	public boolean canContentTypeChange();
	
	/**
	 * Sets the content type. Implementors are required to notify listeners of the changes.
	 * @param contentType the new content type.
	 */
	public void setContentType(String contentType);

}
