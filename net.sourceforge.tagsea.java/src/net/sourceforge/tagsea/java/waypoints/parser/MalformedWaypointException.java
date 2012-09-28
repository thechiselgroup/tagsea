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

/**
 * Exception for malfomed tags.
 * @author Del Myers
 */

public class MalformedWaypointException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -697597904511706486L;
	public static final String EXPECTED_OF_QUOTE = Messages.getString("MalformedWaypointException.endOfQuote"); //$NON-NLS-1$
	public static final String MALFORMED_TAG_NAME = Messages.getString("MalformedWaypointException.malformedTag"); //$NON-NLS-1$
	public static final String EXPECTED_EQUALS = Messages.getString("MalformedWaypointException.expectedEquals"); //$NON-NLS-1$
	public static final String EXPECTED_SPACE = Messages.getString("MalformedWaypointException.expectedWhitespace"); //$NON-NLS-1$
	public static final String UNMATCHED_PAREN = Messages.getString("MalformedWaypointException.unmatchedParenthesis"); //$NON-NLS-1$
	public static final String ILLEGAL_CHARACTER = Messages.getString("MalformedWaypointException.illegalCharacter"); //$NON-NLS-1$
	public static final String EXPECTED_LVALUE = Messages.getString("MalformedWaypointException.expectedLValue"); //$NON-NLS-1$
	public static final String EXPECTED_RVALUE = Messages.getString("MalformedWaypointException.expectedRValue"); //$NON-NLS-1$
	public static final String BAD_TAG_SYNTAX = Messages.getString("MalformedWaypointException.badSyntax"); //$NON-NLS-1$
	public static final String MISPLACED_PARENS = Messages.getString("MalformedWaypointException.misplacedParentheses"); //$NON-NLS-1$
	public static final String READ_ERROR = Messages.getString("MalformedWaypointException.readError"); //$NON-NLS-1$
	public static final String EXPECTED_WORD = Messages.getString("MalformedWaypointException.expectedTagName"); //$NON-NLS-1$
	private int offset;
	private int length;
	
	public MalformedWaypointException(String message, int offset, int length) {
		super(message);
		this.offset = offset;
		this.length = length;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}
	
	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
}
