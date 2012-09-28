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
package net.sourceforge.tagsea.parsed.core.internal.resources;

/**
 * Exception thrown when a resolution has an illegal marker associated with it.
 * @author Del Myers
 *
 */
public class IllegalMarkerProblemException extends Exception {
	private static final long serialVersionUID = 754932552541997288L;
	public IllegalMarkerProblemException(String message) {
		super(message);
	}
	public IllegalMarkerProblemException(String message, Exception parent) {
		super(message, parent);
	}
}
