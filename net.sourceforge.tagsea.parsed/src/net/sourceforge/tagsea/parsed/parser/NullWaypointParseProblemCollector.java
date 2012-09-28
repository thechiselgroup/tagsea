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
package net.sourceforge.tagsea.parsed.parser;

/**
 * A null problem collector that does nothing with the problems that are reported. Useful for when
 * extenders would like to use their own parsers, but do not want to report problems that arrise.
 * @author Del Myers
 *
 */
public class NullWaypointParseProblemCollector implements
		IWaypointParseProblemCollector {

	public void accept(WaypointParseProblem problem) {
		//do nothing
	}

	public WaypointParseProblem[] problems() {
		return new WaypointParseProblem[0];
	}

}
