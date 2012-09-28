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
package net.sourceforge.tagsea.ui.internal;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author Del Myers
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sourceforge.tagsea.ui.internal.messages"; //$NON-NLS-1$

	public static String TagSEAPreferencePage_info;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
