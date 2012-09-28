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
package net.sourceforge.tagsea.parsed.parser;

import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.TextEdit;

/**
 * Default implementation of IWaypointRefactoring which does not support any changes
 * to the documents.
 * @author Del Myers
 *
 */
public class DefaultWaypointRefactoring implements IWaypointRefactoring {

	/**
	 * Default implementation always returns false.
	 */
	public boolean canAddTags(IWaypoint waypoint) {
		return false;
	}

	/**
	 * Default implementation always returns false.
	 */
	public boolean canDelete(IWaypoint waypoint) {
		return false;
	}

	/**
	 * Default implementation always returns false.
	 */
	public boolean canRemoveTags(IWaypoint waypoint) {
		return false;
	}

	/**
	 * Default implementation always returns false.
	 */
	public boolean canReplaceTags(IWaypoint waypoint) {
		return false;
	}

	/**
	 * Default implementation always returns false.
	 */
	public boolean canSetAuthor(IWaypoint waypoint) {
		return false;
	}

	/**
	 * Default implementation always returns false.
	 */
	public boolean canSetDate(IWaypoint waypoint) {
		return false;
	}

	/**
	 * Default implementation always returns false.
	 */
	public boolean canSetMessage(IWaypoint waypoint) {
		return false;
	}

	/**
	 * Default implementation unsupported.
	 */
	public TextEdit delete(IWaypoint waypoint, IDocument document)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Default implementation returns a descriptor that does not allow any changes, and returns
	 * null values.
	 * @see DefaultMutableParsedWaypointDescripor
	 */
	public IMutableParsedWaypointDescriptor getMutable(IWaypoint waypoint,
			IRegion waypointRegion, IDocument document, IWaypointParseProblemCollector collector) {
		return new DefaultMutableParsedWaypointDescripor();
	}

}
