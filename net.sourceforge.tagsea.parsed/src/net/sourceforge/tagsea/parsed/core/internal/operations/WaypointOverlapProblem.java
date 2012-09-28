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

import net.sourceforge.tagsea.parsed.parser.IReplacementProposal;
import net.sourceforge.tagsea.parsed.parser.WaypointParseProblem;

import org.eclipse.jface.text.IDocument;

/**
 * Indicates that the waypoint that would have been created cannot be created
 * due to an overlap.
 * @author Del Myers
 *
 */
public class WaypointOverlapProblem extends WaypointParseProblem {

	/**
	 * @param start
	 * @param length
	 * @param document
	 */
	public WaypointOverlapProblem(int start, int length, IDocument document) {
		super(start, length, document);
	}

	@Override
	public String getMessage() {
		return "Could not parse waypoint due to an overlap with another waypoint area.";
	}

	@Override
	public IReplacementProposal[] getProposals() {
		return new IReplacementProposal[0];
	}

	@Override
	public int getSeverity() {
		return SEVERITY_ERROR;
	}

}
