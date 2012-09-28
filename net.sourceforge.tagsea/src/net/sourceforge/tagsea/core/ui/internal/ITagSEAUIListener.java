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
package net.sourceforge.tagsea.core.ui.internal;

/**
 * Listener for various events in the TagSEA UI. For internal use only. Not intended to
 * be implemented by clients.
 * @author Del Myers
 *
 */
public interface ITagSEAUIListener {

	/**
	 * Indicates that an event has occurred in the TagSEAUI
	 * @param event
	 */
	public void eventPerformed(TagSEAUIEvent event);
}
