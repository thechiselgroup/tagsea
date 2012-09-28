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
 * Implements methods for refactoring waypoints in documents.
 * @author Del Myers
 *
 */
public interface IWaypointRefactoring {
	
	/**
	 * Returns true if the IMutableParsedWaypointDescriptor supplies a method for replacing
	 * the tags in the given waypoint.
	 * @param waypoint the waypoint to check.
	 * @return true if the IMutableParsedWaypointDescriptor supplies a method for replacing
	 * the tags in the given waypoint.
	 */
	boolean canReplaceTags(IWaypoint waypoint);
	
	/**
	 * Returns true if the IMutableWaypointDescriptor supplies a method for adding tags
	 * to the given waypoint.
	 * @param waypoint the waypoint to check.
	 * @return true if the IMutableParsedWaypointDescriptor supplies a method for adding tags
	 * to the given waypoint.
	 */
	boolean canAddTags(IWaypoint waypoint);
	
	/**
	 * Returns true if the IMutableWaypointDescriptor supplies a method for removing tags
	 * from the given waypoint.
	 * @param waypoint the waypoint to check.
	 * @return true if the IMutableWaypointDescriptor supplies a method for removing tags
	 * from the given waypoint.
	 */
	boolean canRemoveTags(IWaypoint waypoint);
	
	/**
	 * Returns true if the IMutableWaypointDescriptor supplies a method for setting the
	 * date of the given waypoint.
	 * @param waypoint the waypoint to check.
	 * @return true if the IMutableWaypointDescriptor supplies a method for setting the
	 * date of the given waypoint.
	 */
	boolean canSetDate(IWaypoint waypoint);
	
	
	/**
	 * Returns true if the IMutableWaypointDescriptor supplies a method for setting the
	 * author of the given waypoint.
	 * @param waypoint the waypoint to check.
	 * @return true if the IMutableWaypointDescriptor supplies a method for setting the
	 * author of the given waypoint.
	 */
	boolean canSetAuthor(IWaypoint waypoint);
	
	/**
	 * Returns true if the IMutableWaypointDescriptor supplies a method for setting the
	 * message of the given waypoint.
	 * @param waypoint the waypoint to check.
	 * @return true if the IMutableWaypointDescriptor supplies a method for setting the
	 * message of the given waypoint.
	 */
	boolean canSetMessage(IWaypoint waypoint);
	
	/**
	 * Returns true if the {@link #delete(IWaypoint, IDocument)} method is supported in this
	 * refactoring interface.
	 * @param waypoint the waypoint to check.
	 * @return true if the {@link #delete(IWaypoint, IDocument)} method is supported in this
	 * refactoring interface.
	 * @see #delete(IWaypoint, IDocument)
	 */
	boolean canDelete(IWaypoint waypoint);
	
	/**
	 * Generates a TextEdit that can be used to delete the textual representation of the given
	 * waypoint in the given document, or throws
	 * an UnsupportedOperationException if it can't be done for the given waypoint. Implementors
	 * must be careful when implementing this method to be sure that they don't delete more than
	 * should be for the waypoint. No guarantees can be made that the edit won't interfere with
	 * other waypoints, but care must be taken in the implementation of this method.
	 * @param waypoint the waypoint to delete.
	 * @param document the document in which the waypoint appears.
	 * @return status indicating the success of the deletion.
	 * @throws UnsupportedOperationException if this method is unsupported.
	 */
	TextEdit delete(IWaypoint waypoint, IDocument document) throws UnsupportedOperationException;
	
	/**
	 * Generates an IMutableParsedWaypointDescriptor for the given waypoint. This descriptor will
	 * be used to change the textual representation of the waypoint which will result in or are
	 * results of model changes to the waypoint. It is recommended that implementors use the
	 * region of text in the given document to generate the descriptor rather than relying on
	 * the waypoint itself as this method is used to create a descriptor that will be used both
	 * for applying and reacting to waypoint and tag model changes (hence the current state
	 * of the waypoint may not reflect the current state of the text).
	 * @param waypoint the waypoint that has/will be changed.
	 * @param region the region in the document at which the waypoint is represented.
	 * @param document the document in which the waypoint appears.
	 * @param collector the collector for parse problems that may occur while generating the descriptor.
	 * @return the mutable representation of the waypoint.
	 */
	IMutableParsedWaypointDescriptor getMutable(IWaypoint waypoint, IRegion waypointRegion, IDocument document, IWaypointParseProblemCollector collector);

}
