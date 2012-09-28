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
 * Listener for changes to the file associations and content types for a waypoint definition.
 * @author Del Myers
 *
 */
public interface IParsedWaypointDefinitionChangeListener {
	/**
	 * Event indicating that the file associations for the given definition have changed.
	 * @param definition the definition with the new associations.
	 * @param oldAssociations the old associations.
	 */
	public void fileAssociationsChanged(IParsedWaypointDefinition definition, String[] oldAssociations);
	
	/**
	 * Event indicating that the content type has changed for the given definition has changed.
	 * @param definition the definition with the new content type.
	 * @param oldContentType the old content type.
	 */
	public void contentTypeChanged(IParsedWaypointDefinition definition, String oldContentType);
}
