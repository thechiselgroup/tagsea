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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * @author mdesmond
 */
public class JavaFileResourceDeltaVisitor implements IResourceDeltaVisitor 
{
	private List<IResourceDelta> fChangedResourceDeltas;
	private List<IResourceDelta> fAddedResourceDeltas;
	private List<IResourceDelta> fRemovedResourceDeltas;
	
	public List<IResourceDelta> getChangedResourceDeltas() 
	{
		if(fChangedResourceDeltas == null)
			fChangedResourceDeltas = new ArrayList<IResourceDelta>();
		return fChangedResourceDeltas;
	}
	
	public List<IResourceDelta> getAddedResourceDeltas() 
	{
		if(fAddedResourceDeltas == null)
			fAddedResourceDeltas = new ArrayList<IResourceDelta>();
		return fAddedResourceDeltas;
	}
	
	public List<IResourceDelta> getRemovedResourceDeltas() 
	{
		if(fRemovedResourceDeltas == null)
			fRemovedResourceDeltas = new ArrayList<IResourceDelta>();
		return fRemovedResourceDeltas;
	}

	public boolean visit(IResourceDelta delta) throws CoreException 
	{
		IResource resource = delta.getResource();
		
		if (resource.getType() == IResource.FILE && "java".equalsIgnoreCase(resource.getFileExtension()))
		{
			if((delta.getFlags() & IResourceDelta.CONTENT)!=0)
				if(delta.getKind() == IResourceDelta.CHANGED)
					getChangedResourceDeltas().add(delta);	
			
			if(delta.getKind() == IResourceDelta.REMOVED)
				getRemovedResourceDeltas().add(delta);
			
			if(delta.getKind() == IResourceDelta.ADDED)
				getAddedResourceDeltas().add(delta);
		}
		
		return true;
	}
}
