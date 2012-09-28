/*******************************************************************************
 * Copyright 2005, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.editing;

import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IInputValidator;


/**
 * Validates the tag name.
 * It must be at least 1 character long, and can only contain letters, numbers, 
 * underscores, hyphens, exclamation marks, and plus or equals characters.
 * 
 * @author Chris Callendar
 */
public class TagNameValidator implements IInputValidator {

	/** This regex is used to find any illegal characters	 */
	private static Pattern p = Pattern.compile("[^a-zA-Z0-9_\\-\']");
	
	/** This regex is used to match a double quoted string with spaces. */
	private static Pattern p2 = Pattern.compile("\"[a-zA-Z0-9_\\-\']+(\\s+[a-zA-Z0-9_\\-\']+)*\"");

	private static final String ERROR = "Invalid name (only letters, numbers, hyphens, and underscores are allowed).";
	private static final String ERROR_BLANK = "Please enter at least one character.";
	
	public TagNameValidator() {
	}

	/**
	 * Checks if the text is valid.
	 * It must be at least 1 character long, and can only contain
	 * letters, numbers, underscores, and hyphens.
	 * @param text the string to validate
	 * @return String the error message or null if the text is valid
	 */
	public String isValid(String text) {
		if ((text == null) || (text.length() == 0))
			return ERROR_BLANK;
		
		// return the error message or null if no error
		return (isAllowed(text) ? null : ERROR);
	}

	/**
	 * Checks if the given string is contains any illegal
	 * characters.  Also checks to make sure the text isn't just spaces.
	 * @param text
	 * @return boolean if the text is a valid name
	 */
	public static boolean isAllowed(String text) {
		boolean allowed = false;
		if ((text != null) && (text.length() > 0)) {
			if (text.startsWith("\"") && text.endsWith("\"")) {
				allowed = p2.matcher(text).matches();	// match quoted string
			} else {
				// Tries to find any illegal characters
				allowed = !p.matcher(text).find();
			}
			
			// check if the text is not just spaces
			if (allowed) {
				allowed = (text.trim().length() > 0); 
			}
		}
		return allowed;
	}
	
}
