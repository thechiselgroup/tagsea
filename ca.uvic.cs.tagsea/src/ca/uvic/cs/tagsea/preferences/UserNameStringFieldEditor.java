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
package ca.uvic.cs.tagsea.preferences;

import java.util.regex.Pattern;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;


/**
 * Special text field editor which only allows letters,
 * numbers, spaces, underscores, hyphens, and single quotes in the name.
 * 
 * @author Chris Callendar
 */
public class UserNameStringFieldEditor extends StringFieldEditor {

	private final Pattern P;
	
	public UserNameStringFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		setEmptyStringAllowed(false);
		P = Pattern.compile("[^a-zA-Z0-9 _\\-\']");
	}

	@Override
	protected boolean doCheckState() {
		String txt = getStringValue().trim();
		boolean ok = !P.matcher(txt).find();
		return ok;
	}
	
}
