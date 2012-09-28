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
package net.sourceforge.tagsea.parsed.javabang;

import net.sourceforge.tagsea.parsed.core.IParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.core.PersistingParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.parser.IWaypointRefactoring;

/**
 * @tag todo.comment : Comment this type.
 * @author Del Myers
 *
 */
public class BangWaypointDefinition extends PersistingParsedWaypointDefinition
		implements IParsedWaypointDefinition {

	private BangRefactoring refactoring;

	/**
	 * @param kind
	 * @param defaultAssociations
	 * @param defaultContent
	 * @param parser
	 * @param canContentChange
	 * @param canAssociationsChange
	 */
	public BangWaypointDefinition() {
		super(JavaBangPlugin.WAYPOINT_KIND, "Simple Java Tagging",
				new String[] {"*.java"}, null, new BangCommentParser("net.sourceforge.tagsea.parsed.javabang"), 
				false, false);
		this.refactoring = new BangRefactoring();
		
	}
	
	@Override
	public boolean submitToCloserMatches() {
		return false;
	}
	
	@Override
	public IWaypointRefactoring getRefactoringMethod() {
		return this.refactoring;
	}

}
