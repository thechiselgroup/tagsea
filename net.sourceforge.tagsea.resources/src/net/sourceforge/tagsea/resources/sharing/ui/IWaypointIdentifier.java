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
package net.sourceforge.tagsea.resources.sharing.ui;

import java.util.Map;
import java.util.SortedSet;

/**
 * @tag todo.remove : remove this type... just using it to fix compile errors due to deprecated code.
 * @author Del Myers
 *
 */
public interface IWaypointIdentifier {

	/**
	 * @return the attributes on this identifier.
	 */
	Map<String, Object> getAttributes();

	/**
	 * @return the tag names on this identifier.
	 */
	SortedSet<String> getTagNames();

}
