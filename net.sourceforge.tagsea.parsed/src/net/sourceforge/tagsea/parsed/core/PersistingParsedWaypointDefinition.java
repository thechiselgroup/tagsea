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
package net.sourceforge.tagsea.parsed.core;

import java.util.Arrays;

import net.sourceforge.tagsea.parsed.parser.IWaypointParser;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

/**
 * A parsed waypoint definition that persists the changes in file assocations and content types to
 * local preferences.
 * @author Del Myers
 *
 */
public class PersistingParsedWaypointDefinition extends
		MutableParsedWaypointDefinition {
	
	private static final String SUFFIX = ".prefs";
	private IEclipsePreferences preferenceNode;
	private static final String ASSOCIATIONS = "associations";
	private static final String CONTENT  = "content";

	public PersistingParsedWaypointDefinition(String kind, String name,
			String[] defaultAssociations, String defaultContent,
			IWaypointParser parser, boolean canContentChange,
			boolean canAssociationsChange) {
		super(kind, name, defaultAssociations, defaultContent, parser, canContentChange, canAssociationsChange);
		this.preferenceNode = new InstanceScope().getNode(getKind()+SUFFIX);
		loadSettings();
	}
	
		
	/**
	 * 
	 */
	private void loadSettings() {
		String value = preferenceNode.get(ASSOCIATIONS, "");
		if ("".equals(value)) {
			//save the defaults
			saveFileAssociations();
		} else {
			String[] associations = value.split(",");
			this.fileAssociations = associations;
		}
		
		value = preferenceNode.get(CONTENT, "");
		if ("".equals(value)) {
			saveContentType();
		} else {
			this.contentType = value;
		}
	}

	@Override
	public void setFileAssociations(String[] fileAssociations) {
		String[] oldAssociations = getFileAssociations();
		super.setFileAssociations(fileAssociations);
		if (!Arrays.equals(oldAssociations, fileAssociations)) {
			saveFileAssociations();
		}
	}
	
	/**
	 * 
	 */
	private void saveFileAssociations() {
		if (getFileAssociations() == null) {
			preferenceNode.remove(ASSOCIATIONS);

		} else {
			String value = "";
			for (String a : getFileAssociations()) {
				value += a + ",";
			}
			preferenceNode.put(ASSOCIATIONS, value);
		}
		try {
			preferenceNode.flush();
		} catch (BackingStoreException e) {
		}
	}


	@Override
	public void setContentType(String contentType) {
		String oldContent = getContentType();
		super.setContentType(contentType);
		if (oldContent == null && contentType == null) {
			saveContentType();
		} else if (oldContent != null && oldContent.equals(contentType)) {
			saveContentType();
		}
	}


	/**
	 * 
	 */
	private void saveContentType() {
		if (getContentType() == null) {
			preferenceNode.remove(CONTENT);
		} else {
			preferenceNode.put(CONTENT, getContentType());
		}
		try {
			preferenceNode.flush();
		} catch (BackingStoreException e) {
		}
	}

	

}
