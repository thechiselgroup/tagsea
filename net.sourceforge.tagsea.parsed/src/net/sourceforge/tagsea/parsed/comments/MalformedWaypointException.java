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
package net.sourceforge.tagsea.parsed.comments;



/**
 * Exception for malformed waypoints.
 * @author Del Myers
 */

public class MalformedWaypointException extends Exception {
	enum ErrorType {
		ExpectedQuote,
		MalformedTagName,
		ExpectedEquals,
		ExpectedSpace,
		UnmatchedParen,
		IllegalCharacter,
		ExpectedLValue,
		ExpectedRValue,
		BadTag,
		MisplacedParens,
		ReadError,
		ExpectedWord;
		
		public String toString() {
			switch (this) {
			case ExpectedQuote:
				return Messages.getString("MalformedWaypointException.endOfQuote"); //$NON-NLS-1$
			case MalformedTagName:
				return Messages.getString("MalformedWaypointException.malformedTag"); //$NON-NLS-1$
			case ExpectedEquals:
				return Messages.getString("MalformedWaypointException.expectedEquals"); //$NON-NLS-1$
			case ExpectedSpace:
				return Messages.getString("MalformedWaypointException.expectedWhitespace"); //$NON-NLS-1$
			case UnmatchedParen:
				return Messages.getString("MalformedWaypointException.unmatchedParenthesis"); //$NON-NLS-1$
			case IllegalCharacter:
				return Messages.getString("MalformedWaypointException.illegalCharacter"); //$NON-NLS-1$
			case ExpectedLValue:
				return Messages.getString("MalformedWaypointException.expectedLValue"); //$NON-NLS-1$
			case ExpectedRValue:
				return Messages.getString("MalformedWaypointException.expectedRValue"); //$NON-NLS-1$
			case BadTag:
				return Messages.getString("MalformedWaypointException.badSyntax"); //$NON-NLS-1$
			case MisplacedParens:
				return Messages.getString("MalformedWaypointException.misplacedParentheses"); //$NON-NLS-1$
			case ReadError:
				return Messages.getString("MalformedWaypointException.readError"); //$NON-NLS-1$
			case ExpectedWord:
				return Messages.getString("MalformedWaypointException.expectedTagName"); //$NON-NLS-1$
			}
			return "";
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -697597904511706486L;
	private int offset;
	private int length;
	private ErrorType type;
	
	public MalformedWaypointException(ErrorType type, String message, int offset, int length) {
		super(message);
		this.offset = offset;
		this.length = length;
		this.type = type;
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
	/**
	 * @return the type
	 */
	public ErrorType getType() {
		return type;
	}
	
}
