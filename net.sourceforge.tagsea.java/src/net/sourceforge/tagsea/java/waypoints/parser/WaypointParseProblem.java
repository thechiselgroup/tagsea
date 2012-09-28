/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.java.waypoints.parser;

import org.eclipse.jface.text.IRegion;

/**
 * A class that represents a problem that occurred while parsing a waypoint.
 * @author Del Myers
 */

public class WaypointParseProblem {
	/**
	 * The message accompaning the problem.
	 */
	public final String message;
	/**
	 * The region relative to the actual waypoint text... not the document.
	 */
	public final IRegion region;
	
	public WaypointParseProblem(String message, IRegion region) {
		this.message = message;
		this.region = region;
	}

}
