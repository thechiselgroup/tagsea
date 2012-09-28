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

import java.util.Date;

/**
 * Default implementation of IMutableParsedWaypointDescriptor which does not support any
 * changes and returns no information.
 * @author Del Myers
 *
 */
public class DefaultMutableParsedWaypointDescripor implements
		IMutableParsedWaypointDescriptor {

	public void addTag(String tag) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public String getText() {
		throw new UnsupportedOperationException();
	}

	public void removeTag(String tag) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public void replaceTag(String oldTag, String newTag)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public void setAuthor(String author) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public void setDate(Date date) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public void setDetail(String detail) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public void setMessage(String message) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Default implementation always returns null.
	 */
	public String getAuthor() {
		return null;
	}

	/**
	 * Default implementation always returns -1.
	 */
	public int getCharEnd() {
		return -1;
	}

	/**
	 * Default implementation always returns -1.
	 */
	public int getCharStart() {
		return -1;
	}
	
	/**
	 * Default implementation always returns -1;
	 */
	public int getLine() {
		return -1;
	}

	/**
	 * Default implementation always returns null.
	 */
	public Date getDate() {
		return null;
	}

	/**
	 * Default implementation always returns null.
	 */
	public String getDetail() {
		return null;
	}

	/**
	 * Default implementation always returns null.
	 */
	public String getMessage() {
		return null;
	}

	/**
	 * Default implementation always returns null.
	 */
	public String[] getTags() {
		return null;
	}

}
