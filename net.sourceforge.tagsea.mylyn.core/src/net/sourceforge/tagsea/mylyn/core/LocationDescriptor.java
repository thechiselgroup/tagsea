/*******************************************************************************
 * 
 *   Copyright 2007, 2008, CHISEL Group, University of Victoria, Victoria, BC, Canada. 
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *       The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.mylyn.core;

public class LocationDescriptor {

	public static final String LOCATON_DELIMINTER = ":";

	private final String description;
	private final String location;

	public LocationDescriptor(String description, String location) {
		this.description = description;
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public String getLocation() {
		return location;
	}

	public static LocationDescriptor createFromText(String tag,
			String tagDelimitor, String text) {
		if (text.startsWith(tag)) {
			text = text.replace(tag, "");
		}
		int delimitor = text.lastIndexOf(LOCATON_DELIMINTER);
		int tagsDelimitorIndex = -1;
		if (tagDelimitor != null)
			tagsDelimitorIndex = text.lastIndexOf(tagDelimitor);
		if (tagsDelimitorIndex == -1)
			tagsDelimitorIndex = text.length();

		if (delimitor != -1) {
			String description = text.substring(0, delimitor).trim();
			String location = text.substring(delimitor + 1, tagsDelimitorIndex)
					.trim();
			return new LocationDescriptor(description, location);
		} else {
			return new LocationDescriptor(text, null);
		}

	}
}
