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

import org.eclipse.core.runtime.ListenerList;

/**
 * Default implementation of a mutable parsed waypoint defintion.
 * @author Del Myers
 *
 */
public abstract class MutableParsedWaypointDefinition extends DefaultParsedWaypointDefinition {

	private ListenerList listeners;
	private boolean canContentChange;
	private boolean canAssociationsChange;
		
	public MutableParsedWaypointDefinition(String kind, String name, String[] fileAssociations, String contentType, IWaypointParser parser, boolean canContentChange, boolean canAssociationsChange) {
		super(kind, name, fileAssociations, contentType, parser);
		this.canContentChange = canContentChange;
		this.canAssociationsChange = canAssociationsChange;
		this.listeners = new ListenerList(ListenerList.EQUALITY);		
	}
	
	
	/**
	 * Creates a new definition with no initial file assocations and no content type.
	 * @param kind the kind.
	 * @param parser the parser.
	 * @param canContentChange
	 * @param canAssociationsChange
	 */
	public MutableParsedWaypointDefinition(String kind, String name, IWaypointParser parser, boolean canContentChange, boolean canAssociationsChange) {
		super(kind, name, new String[0], null, parser);
		this.canContentChange = canContentChange;
		this.canAssociationsChange = canAssociationsChange;
		this.listeners = new ListenerList(ListenerList.EQUALITY);		
	}
	
	public void addDefinitionListener(
			IParsedWaypointDefinitionChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeDefinitionListener(IParsedWaypointDefinitionChangeListener listener) {
		listeners.remove(listener);
	}

	public final boolean canContentTypeChange() {
		return canContentChange;
	}

	public final boolean canFileAssociationsChange() {
		return canAssociationsChange;
	}
	
	public void setFileAssociations(String[] fileAssociations) {
		if (!canFileAssociationsChange()) return;
		String[] oldAssociations = this.fileAssociations;
		Arrays.sort(fileAssociations);
		for (int i = 0; i < fileAssociations.length; i++) {
			fileAssociations[i] = fileAssociations[i].trim();
		}
		if (!Arrays.equals(oldAssociations, fileAssociations)) {
			this.fileAssociations = fileAssociations;
			for (Object o : listeners.getListeners()) {
				((IParsedWaypointDefinitionChangeListener)o).fileAssociationsChanged(this, oldAssociations);
			}
		}
	}
	
	public void setContentType(String contentType) {
		if (!canContentTypeChange()) return;
		contentType = contentType.trim();
		if (!this.contentType.equals(contentType)) {
			String oldContent = this.contentType;
			this.contentType = contentType;
			for (Object o : listeners.getListeners()) {
				((IParsedWaypointDefinitionChangeListener)o).contentTypeChanged(this, oldContent);
			}
		}
	}

}
