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

package com.ibm.research.tagging.java.refactoring;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;

public class DocumentWriteSession extends FileAccessSession
{
	private IDocument fDocument;
	
	public DocumentWriteSession(IFile file) 
	{
		super(file);
		fDocument = getDocumentForWrite(getFile());
	}

	public IDocument getDocument() 
	{
		return fDocument;
	}
	
	@Override
	public void begin() 
	{
		super.begin();
	}
	
	@Override
	public void end()
	{
		super.end();
		commit(getFile());
	}
	
	
	private IDocument getDocumentForWrite(IFile file) 
	{
		if(file == null)
			return null;

		ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();

		if(manager != null)
		{
			IPath path= file.getFullPath();

			try 
			{
				manager.connect(path, new NullProgressMonitor());
				ITextFileBuffer buffer = manager.getTextFileBuffer(path);

				if (buffer != null) 
					return buffer.getDocument();
			}
			catch (CoreException e) 
			{
				e.printStackTrace();
			}
		}
		return null; 
	}
	
	private void commit(IFile file) 
	{
		if (file == null) return;
		ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
		try {
			try {
				ITextFileBuffer buffer = manager.getTextFileBuffer(file.getFullPath());
				if (buffer != null && buffer.isDirty()) {
					buffer.commit(new NullProgressMonitor(), true);
				}
			}  finally {
				manager.disconnect(file.getFullPath(), new NullProgressMonitor());
			}
		} catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}
}
