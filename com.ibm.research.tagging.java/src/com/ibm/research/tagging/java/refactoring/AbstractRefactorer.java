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

import org.eclipse.core.resources.IFile;

public abstract class AbstractRefactorer 
{
	private IFile fFile;
	private MarkerUpdatingDocumentWriteSession fSession;
	
	public AbstractRefactorer(IFile file)
	{
		fFile = file;
		fSession = new MarkerUpdatingDocumentWriteSession(fFile);
	}
	
	public void execute()
	{
		fSession.begin();
		
		try
		{
			performRefactoring();
		}
		finally
		{
			fSession.end();
		}
	}

	public abstract void performRefactoring();

	public IFile getFile() {
		return fFile;
	}

	public MarkerUpdatingDocumentWriteSession getSession() {
		return fSession;
	}
	
	
	
}
