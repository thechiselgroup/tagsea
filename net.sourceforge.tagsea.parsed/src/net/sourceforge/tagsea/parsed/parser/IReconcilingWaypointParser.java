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

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IRegion;

/**
 * Represents a waypoint parser that can react to and reconcile document changes. If
 * an IWaypointParser is an instance of IReconcilingWaypointParser, then the
 * IReconcilingWaypointParser will be asked to compute the "dirty" regions in the
 * document that needs to be re-parsed. Otherwise typically the entire document
 * will have to be re-parsed for every document update. It is recommended that
 * clients implement this interface rather than IWaypointParser.
 * @author Del Myers
 *
 */
public interface IReconcilingWaypointParser extends IWaypointParser {

	/**
	 * Checks the document for updates and calculates which regions will need to be parsed
	 * in order to update the waypoints.
	 * @param event the document event which contains the dirty portion of the document.
	 * @return the regions that need to be parsed to discover waypoint changes.
	 */
	public IRegion[] calculateDirtyRegions(DocumentEvent event);
	
}
