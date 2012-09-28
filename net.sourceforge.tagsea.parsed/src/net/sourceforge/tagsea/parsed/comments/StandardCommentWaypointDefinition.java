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
package net.sourceforge.tagsea.parsed.comments;

import net.sourceforge.tagsea.parsed.core.PersistingParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.parser.IWaypointParser;
import net.sourceforge.tagsea.parsed.parser.IWaypointRefactoring;

/**
 * A waypoint definition that includes functionality to parse and refactor standard waypoints.
 * @author Del Myers
 *
 */
public class StandardCommentWaypointDefinition extends
		PersistingParsedWaypointDefinition {
	private StandardCommentRefactoring refactoringMethod;

	public StandardCommentWaypointDefinition(String kind, String name,
			String[] defaultAssociations, String defaultContent,
			IWaypointParser parser, boolean canContentChange,
			boolean canAssociationsChange) {
		super(kind, name, defaultAssociations, defaultContent, parser, canContentChange, canAssociationsChange);
		this.refactoringMethod = new StandardCommentRefactoring();
	}
	
	@Override
	public IWaypointRefactoring getRefactoringMethod() {
		return refactoringMethod;
	}
}
