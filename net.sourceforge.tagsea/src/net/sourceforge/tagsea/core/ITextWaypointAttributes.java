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
package net.sourceforge.tagsea.core;

/**
 * Lists the attributes available on text waypoints.
 * @author Del Myers
 *
 */
public interface ITextWaypointAttributes extends IBaseWaypointAttributes {
	/**
	 * The character start of the waypoint. The start character is 0 indexed and should be considered
	 * <i>inclusive</i> so that a waypoint on the String "peanut-butter" where [charStart,charEnd) =
	 * [0, 6) would represent the String "peanut".  This way 0-lengthed waypoints span no text.
	 */
	static final String ATTR_CHAR_START = "charStart";
	/**
	 * The character end of the waypoint. The end character is 0 indexed and should be considered
	 * <i>exclusive</i> so that a waypoint on the String "peanut-butter" where [charStart,charEnd) =
	 * [0, 6) would represent the String "peanut". This way 0-lengthed waypoints span no text.
	 */
	static final String ATTR_CHAR_END = "charEnd";
	/**
	 * The line number of the waypoint.
	 */
	static final String ATTR_LINE = "lineNumber";

}
