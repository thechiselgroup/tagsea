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
 * A simple collection of IWaypointChangeEvents.
 * @author Del Myers
 */

public class WaypointDelta {
	/**
	 * The changes in this delta.
	 */
	public final IWaypointChangeEvent[] changes;
	/**
	 * Basic constructor for the WaypointDelta. Not intended to be used outside of the TagSEA platform.
	 * @param changes
	 */
	public WaypointDelta(IWaypointChangeEvent[] changes) {
		this.changes = changes;
	}
	
	public final IWaypointChangeEvent[] getChanges() {
		return changes;
	}
}
