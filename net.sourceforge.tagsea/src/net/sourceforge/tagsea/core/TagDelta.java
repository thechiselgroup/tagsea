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
package net.sourceforge.tagsea.core;

/**
 * An object representing a list of tag change events.
 * @author Del Myers
 */

public class TagDelta {
	/**
	 * The list of change events on this delta.
	 */
	public final ITagChangeEvent[] events;
	
	/**
	 * Creates a new TagDelta. For internal use only.
	 * @param events
	 */
	public TagDelta(ITagChangeEvent[] events) {
		this.events = events;
	}
	
	/**
	 * @return the events
	 */
	public ITagChangeEvent[] getEvents() {
		return events;
	}
}
