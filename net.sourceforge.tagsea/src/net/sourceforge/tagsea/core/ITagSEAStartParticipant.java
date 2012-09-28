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
package net.sourceforge.tagsea.core;

/**
 * Interface for plugins that would like to participate in TagSEA's startup process. Clients
 * are notified of when the TagSEA plugin begins to load its waypoint delegates, and after
 * it has finished. Note that not all waypoints may have necessarily been loaded when the
 * TagSEA startup process has finished as the loading of waypoints may be scheduled in a
 * TagSEA operation. It is recommended that any processing that needs to have access to the
 * tags or waypoints model also queue itself in a TagSEAOperation to gain confidence that an
 * up-to-date state of the waypoints and tags is available.
 * @author Del Myers
 *
 */
public interface ITagSEAStartParticipant {
	
	/**
	 * Notifies listeners that TagSEA has begun its loading process. Occurrs before delegates
	 * have been asked to begin their load process.
	 */
	public void tagSEAStarting();
	
	/**
	 * Notifies listeners that TagSEA has finished its loading process. Occurrs after delegates
	 * have been asked to begin their load process.
	 *
	 */
	public void tagSEAStarted();

}
