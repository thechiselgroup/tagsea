/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.util;

import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IInputValidator;

import ca.uvic.cs.tagsea.core.Route;
import ca.uvic.cs.tagsea.core.RouteCollection;


/**
 * Checks if a route name is valid.
 * 
 * @author Chris Callendar
 */
public class RouteNameValidator implements IInputValidator {

	private static final String ERROR_INVALID = "The name contains invalid characters.";
	private static final String ERROR_DUPLICATE = "Another route with that name already exists.";
	private static final String ERROR_BLANK = "Please enter at least one character.";
	
	private static Pattern p = Pattern.compile("[^a-zA-Z0-9_ \\-\\'#!\\+]"); // allowed characters

	private RouteCollection routes;
	
	public RouteNameValidator(RouteCollection routes) {
		this.routes = routes;
	}

	/**
	 * Checks if the new text is not blank, and then compares it with every 
	 * other route name to make sure there isn't already a route with that name.
	 * @param newText the new route name
	 * @return String the error message or null if valid
	 */
	public String isValid(String newText) {
		if ((newText == null) || (newText.length() == 0))
			return ERROR_BLANK;
		
		if (!isAllowed(newText))
			return ERROR_INVALID;
		
		for (Route route : routes.getRoutes()) {
			String name = route.getName();
			if (name.equals(newText)) {
				return ERROR_DUPLICATE;
			}
		}
		return null;
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
			// Tries to find any illegal characters
			allowed = !p.matcher(text).find();
			
			// check if the text is not just spaces
			if (allowed) {
				allowed = (text.trim().length() > 0); 
			}
		}
		return allowed;
	}	

}
