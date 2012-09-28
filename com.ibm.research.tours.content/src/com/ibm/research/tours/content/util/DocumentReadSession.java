/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tours.content.util;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;

public class DocumentReadSession extends FileAccessSession
{
	protected IDocument fDocument;
	
	public DocumentReadSession(IFile file) 
	{
		super(file);
		fDocument = getDocumentForRead(getFile());
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
	}
	
	/**
	 * Get the document associated with an IFile instance
	 * @param file
	 * @author mdesmond
	 * @return the document associated with the given IFile instance
	 */
	public IDocument getDocumentForRead(IFile file) 
	{
		if(file == null)
			return null;
		
		ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
		
		if(manager != null)
		{
			IPath path= file.getFullPath();

			try 
			{
				try 
				{
					manager.connect(path, new NullProgressMonitor());
					ITextFileBuffer buffer = manager.getTextFileBuffer(path);

					if (buffer != null) 
						return buffer.getDocument();
				}
				finally 
				{
					manager.disconnect(path, new NullProgressMonitor());
				}
			}
			catch (CoreException e) 
			{
				e.printStackTrace();
			}
		}
		return null; 
	}
}
