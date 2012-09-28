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
 * Description of a problem that occurred during the parsing of a waypoint. May indicate an error
 * that does not allow the waypoint to be created, or it may indicate a possible fix (a preferred
 * syntax, for example). In the second case, the waypoint will be created, but a quick fix will
 * be provided if the problem supplies one or more completion proposals. 
 * @author Del Myers
 * 
 */
public abstract class WaypointParseProblem {
	
	/**
	 * This problem represents a fatal error that will stop the waypoint from being created.
	 */
	public static final int SEVERITY_ERROR = 1;
	
	/**
	 * This problem represents an error that does not prevent the waypoint from being created
	 * but will be represented as a warning.
	 */
	public static final int SEVERITY_WARNING = 2;
	/**
	 * This problem represents information that may also supply one or more completion proposals.
	 */
	public static final int SEVERITY_INFO = 0;
	private int start;
	private int length;
	private IDocument document;
	

	/**
	 * Creates a new waypoint parse problem with the given initial start offset, and length. The platform will update these
	 * values to stay up-to-date with text changes.
	 * @param start the initial start of the problem in the document.
	 * @param length the initial length of the problem in the document.
	 */
	public WaypointParseProblem(int start, int length, IDocument document) {
		this.start = start;
		this.length = length;
		this.document = document;
	}
	
	
	/**
	 * Returns the document that the problem is associated with. Null if the document could not be found.
	 * @return the document that the problem is associated with. Null if the document could not be found.
	 */
	public final IDocument getDocument() {
		return document;
	}
	
	/**
	 * Returns the offset into the document that the problem is set on. -1 if an error occurred.
	 * @return the offset into the document that the problem is set on.
	 */
	public final int getOffset() {
		return start;
	}
	
	/**
	 * Returns the length in the document that the problem is set on. -1 if an error occurred.
	 * @return the length in the document that the problem is set on.
	 */
	public final int getLength() {
		return length;
	}
	
	/**
	 * Returns the offset of the last character in the document that the problem is set on. -1 if an error occurred.
	 * @return the offset of the last character in the document that the problem is set on. -1 if an error occurred.
	 */
	public final int getEnd() {
		return start + length;
	}
	
	/**
	 * Returns a list of proposals to resolve this problem. May return null or an empty array.
	 * @return a list of proposals to resolve this problem.
	 */
	public abstract IReplacementProposal[] getProposals();
	
	/**
	 * Returns a human-readable message for this problem.
	 * @return a human-readable message for this problem.
	 */
	public abstract String getMessage();
	
	/**
	 * Returns one of SEVERITY_ERROR, or SEVERITY_INFO.
	 * @return one of SEVERITY_ERROR, or SEVERITY_INFO.
	 */
	public abstract int getSeverity();
	
}
