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

public class DocumentWriteSession extends DocumentReadSession
{
		
	public DocumentWriteSession(IFile file) 
	{
		super(file);
	}

	@Override
	public void closeDocument() {
		commit();
		super.closeDocument();
	}
	
	private void commit() 
	{
		if (!isConnected()) return;
		IFile file = getFile();
		if (file == null) return;
		ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
		ITextFileBuffer buffer = manager.getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
		if (buffer != null && buffer.isDirty()) {
			try {
				buffer.commit(new NullProgressMonitor(), true);
			} catch (CoreException e) {
				JavaTagsPlugin.getDefault().log(e);
			}
		}
	}
}
