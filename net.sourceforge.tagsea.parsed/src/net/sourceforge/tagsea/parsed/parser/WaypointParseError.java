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

/**
 * A waypoint parse error that doesn't have a proposal. Indicates that a waypoint couldn't be parsed.
 * @author Del Myers
 *
 */
public class WaypointParseError extends WaypointParseProblem {

	private String message;

	/**
	 * Creates the new error.
	 * @param start
	 * @param length
	 * @param document
	 */
	public WaypointParseError(String message, int start, int length, IDocument document) {
		super(start, length, document);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
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