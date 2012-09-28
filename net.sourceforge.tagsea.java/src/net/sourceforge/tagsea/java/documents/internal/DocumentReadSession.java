/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/

package net.sourceforge.tagsea.java.documents.internal;

import net.sourceforge.tagsea.java.JavaTagsPlugin;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;

public class DocumentReadSession extends FileAccessSession
{
	
	private boolean connected;

	public DocumentReadSession(IFile file) 
	{
		super(file);
		connected = false;
	}

	/**
	 * @return the connected
	 */
	protected boolean isConnected() {
		return connected;
	}
	
	public IDocument openDocument() {
		begin();
		ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
		if (!connected) {
			try {
				manager.connect(getFile().getFullPath(), LocationKind.IFILE, new NullProgressMonitor());
				connected = true;
			} catch (CoreException e) {
				JavaTagsPlugin.getDefault().log(e);
			}
		}
		ITextFileBuffer buffer = manager.getTextFileBuffer(getFile().getFullPath(), LocationKind.IFILE);
		return buffer.getDocument();
	}
	
	public void closeDocument() {
		ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
		if (connected) {
			try {
				manager.disconnect(getFile().getFullPath(), LocationKind.IFILE, new NullProgressMonitor());
				connected = false;
			} catch (CoreException e) {
				JavaTagsPlugin.getDefault().log(e);
			}
		}
		end();
	}
}
