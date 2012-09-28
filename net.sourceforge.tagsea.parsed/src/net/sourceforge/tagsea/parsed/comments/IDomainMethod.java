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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * Method for discovering what the domain-specific element is for a region of text. Domain-specific
 * elements can be things like java methods in a java file, or xml tags, or paragraph numbers. This
 * interface only meant to be called by the StandardCommentParser logic. Clients should implement this
 * class if they wish to supply domain-specific information in their standard comment waypoints.
 * @author Del Myers
 * @see StandardCommentParser
 */
public interface IDomainMethod {

	/**
	 * Does the processing to discover the domain-specific object for the given document and region.
	 * @param document the document to check.
	 * @param region the region in which the waypoint can be found.
	 * @return a string-representation of the domain-specific object.
	 */
	String getDomainObject(IDocument document, IRegion region);

}
