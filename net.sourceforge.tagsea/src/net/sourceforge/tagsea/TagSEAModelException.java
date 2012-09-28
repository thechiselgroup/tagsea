/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Exception indicating that an error occurred withing the tags or waypoint model.
 * @author Del Myers
 */

public class TagSEAModelException extends CoreException {

	/**
	 * Default constructor for this exception.
	 *
	 */
	public TagSEAModelException() {
		this(new Status(IStatus.ERROR, TagSEAPlugin.PLUGIN_ID, IStatus.ERROR, "", null));
	}
	
	/**
	 * @param status
	 */
	public TagSEAModelException(IStatus status) {
		super(status);
	}

	/**
	 * The version number for serialization.
	 */
	private static final long serialVersionUID = 0L;

}
