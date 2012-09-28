/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.java;

import org.eclipse.osgi.util.NLS;

/**
 * @tag todo.comment
 * @author Del Myers
 */

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sourceforge.tagsea.java.messages"; //$NON-NLS-1$

	public static String JavaWaypointUI_line;

	public static String JavaWaypointUI_file;

	public static String JavaWaypointUI_javaelement;

	public static String JavaWaypointUI_end;

	public static String JavaWaypointUI_offset;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
