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

import net.sourceforge.tagsea.core.IWaypointLocator;
import net.sourceforge.tagsea.parsed.parser.DefaultWaypointRefactoring;
import net.sourceforge.tagsea.parsed.parser.IWaypointParser;
import net.sourceforge.tagsea.parsed.parser.IWaypointRefactoring;

/**
 * Default implementation of IParsedWaypointDefinition.
 * @author Del Myers
 *
 */
public class DefaultParsedWaypointDefinition implements
		IParsedWaypointDefinition {
	
	
	private String kind;
	private String name;
	protected String[] fileAssociations;
	protected String contentType;
	private IWaypointParser parser;
	private IParsedWaypointPresentation presentation;

	public DefaultParsedWaypointDefinition(String kind, String name, String[] fileAssociations, String contentType, IWaypointParser parser) {
		this.kind = kind;
		this.name = name;
		if (fileAssociations != null) {
			for (int i = 0; i < fileAssociations.length; i++) {
				fileAssociations[i] = fileAssociations[i].trim();
			}
		} else {
			fileAssociations = new String[0];
		}
		this.fileAssociations = fileAssociations;
		if (contentType != null) {
			contentType = contentType.trim();
		}
		this.contentType = contentType;
		this.parser = parser;
	}

	public final String getContentType() {
		return contentType;
	}

	public final String[] getFileAssociations() {
		return fileAssociations;
	}

	public final String getKind() {
		return kind;
	}

	public final IWaypointParser getParser() {
		return parser;
	}

	/**
	 * Returns true by default. Extenders may override.
	 * @see IParsedWaypointDefinition#matchSubContentTypes()
	 */
	public boolean matchSubContentTypes() {
		return true;
	}

	/**
	 * Returns true by default. Extenders may override.
	 * @see IParsedWaypointDefinition#matchSubContentTypes()
	 */
	public boolean submitToCloserMatches() {
		return true;
	}

	/**
	 * Default implementation returns null until the presentation has been set.
	 */
	public IParsedWaypointPresentation getPresentation() {
		return presentation;
	}
	
	public void setPresentation(IParsedWaypointPresentation presentation) {
		this.presentation = presentation;
	}

	public String getName() {
		return name;
	}

	
	/**
	 * Default implementation returns a refactoring method that doesn't support any changes. Extenders should
	 * override this method if they can, by any means, support refactoring.
	 * @see DefaultWaypointRefactoring
	 */
	public IWaypointRefactoring getRefactoringMethod() {
		return new DefaultWaypointRefactoring();
	}

	public IWaypointLocator getLocator() {
		return null;
	}

}
