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

package com.ibm.research.tagging.java.resources;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * @author mdesmond
 */
public class JavaFileResourceVisitor implements IResourceVisitor
{
	private List<IFile> fFiles;
	
	public List<IFile> getFiles() 
	{
		if(fFiles == null)
			fFiles = new ArrayList<IFile>();
		
		return fFiles;
	}
	
	public boolean visit(IResource resource) throws CoreException
	{
		if (resource.getType() == IResource.FILE && "java".equalsIgnoreCase(resource.getFileExtension()))
		{
			getFiles().add((IFile)resource);
			return false;
		}
		return true;
	}
}
