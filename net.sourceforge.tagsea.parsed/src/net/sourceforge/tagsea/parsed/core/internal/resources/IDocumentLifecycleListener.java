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

import org.eclipse.core.filebuffers.ITextFileBuffer;

/**
 * An interface that is used to notify clients of when documents are opened and closed in TagSEA.
 * @author Del Myers
 *
 */
public interface IDocumentLifecycleListener {
	/**
	 * A document has been created for the given file, and will be monitored by TagSEA.
	 * @param document the document.
	 * @param file the file.
	 */
	void documentCreated(ITextFileBuffer buffer);
	
	/**
	 * A document has been disposed for the given file, and will no longer be monitored by TagSEA.
	 * @param document the document.
	 * @param file the file.
	 */
	void documentDisposed(ITextFileBuffer buffer);
}
