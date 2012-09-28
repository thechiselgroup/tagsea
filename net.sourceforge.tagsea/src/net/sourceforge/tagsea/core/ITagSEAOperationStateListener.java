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
 * An {@link ITagSEAOperationStateListener} is notified whenever the state of a TagSEAOperation
 * changes in the platform. The state is recorded in the operation itself.
 * @author Del Myers
 */
public interface ITagSEAOperationStateListener {
	/**
	 * Notification that the state of the given operation has changed. The state is stored in the
	 * operation itself.
	 * @param operation
	 * @see TagSEAOperation
	 */
	public void stateChanged(TagSEAOperation operation);
}
