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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * A generic interface for declaring how to parse waypoints in a text file. It is recommended
 * that clients implement IReconcilingWaypointParser rather than IWaypointParser.
 * @author Del Myers
 * @see IReconcilingWaypointParser.
 *
 */
public interface IWaypointParser {

	/**
	 * Parses the given document for waypoints. Returns a descriptor for each waypoint
	 * found within the given regions. The collector is used to gather any problems/resolutions
	 * or proposals found in the region. The parser is responsible for populating the collector.
	 * @param document the document to parse.
	 * @param regions the regions to consider.
	 * @param collector a collector for problems or proposals. 
	 * @return the descriptors for the found waypoints/errors.
	 */
	public IParsedWaypointDescriptor[] parse(IDocument document, IRegion[] regions, IWaypointParseProblemCollector collector);
	
}
