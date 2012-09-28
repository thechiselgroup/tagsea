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
package net.sourceforge.tagsea.parsed.core;

import net.sourceforge.tagsea.parsed.core.internal.resources.IDocumentLifecycleListener;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;

/**
 * Platform registry linking documents to files. Hooks into the ITextFileBuffer support
 * that is standard in the Eclipse platform. The methods in this interface are only here
 * to be a faster, more convenient way of getting the same information that the ITextFileBuffer
 * and the ITextFileBufferManager support could also get you.
 * @author Del Myers
 *
 */
public interface IDocumentRegistry {

	/**
	 * Returns the IFile associated with the given document, only if the document and
	 * file are connected in the ITextFileBufferManager. Null otherwise.
	 * @param document the document to check.
	 * @return the file for the given document.
	 */
	public IFile getFileForDocument(IDocument document);
	
	/**
	 * A convenient, quick method for finding the text file buffer of the given document
	 * if it exists. Null otherwise.
	 * {@link ITextFileBufferManager}
	 * @param document
	 * @return
	 */
	public ITextFileBuffer getBufferForDocument(IDocument document);
	
	/**
	 * Adds the given listener to listen for when documents/file buffers are being 
	 * monitored by the parsed waypoint platform.
	 * @param listener the listener to add.
	 */
	public void addDocumentLifecycleListener(IDocumentLifecycleListener listener);
	
	/**
	 * Removes the given listener to stop listening to when documents/file buffers are
	 * being monitored by the parsed waypoint platform.
	 * @param listener the listener to rmove.
	 */
	public void removeDocumentLifecycleListener(IDocumentLifecycleListener listener);
	
}
