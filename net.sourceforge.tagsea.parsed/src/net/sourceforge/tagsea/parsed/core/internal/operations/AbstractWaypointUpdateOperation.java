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
package net.sourceforge.tagsea.parsed.core.internal.operations;

import net.sourceforge.tagsea.core.TagSEAOperation;

/**
 * Operation type used to indicate that the current operation running is an internal operation
 * that will update waypoints. Forces the parsed waypoint delegate to allow waypoint changes to
 * occur.
 * @author Del Myers
 *
 */
public abstract class AbstractWaypointUpdateOperation extends TagSEAOperation {
	public AbstractWaypointUpdateOperation(String name) {
		super(name);
	}
}
