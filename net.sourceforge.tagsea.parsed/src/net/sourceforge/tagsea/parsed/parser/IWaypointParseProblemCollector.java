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
 * An interface that represents a collection of parse problems.
 * @author Del Myers
 * @see DefaultParsedWaypointProblemCollector
 */
public interface IWaypointParseProblemCollector {
	
	/**
	 * Adds the given problem to the collection of problems in this collector.
	 * @param problem the problem to collect.
	 */
	public void accept(WaypointParseProblem problem);
	
	/**
	 * Returns the problems collected by this collector.
	 * @return the problems collected by this collector.
	 */
	public WaypointParseProblem[] problems();

}
