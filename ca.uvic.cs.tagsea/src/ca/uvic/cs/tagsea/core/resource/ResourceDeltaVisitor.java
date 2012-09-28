/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *     IBM Corporation 
 *******************************************************************************/
package ca.uvic.cs.tagsea.core.resource;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * @author mdesmond
 */
public class ResourceDeltaVisitor implements IResourceDeltaVisitor 
{
	private List<IResourceDelta> resourcesToUpdate;
	
	public ResourceDeltaVisitor(List<IResourceDelta> resourcesToUpdate) 
	{
		this.resourcesToUpdate = resourcesToUpdate; 
	}
	
	public List<IResourceDelta> getResourceDeltas() 
	{
		return this.resourcesToUpdate;
	}

	public boolean visit(IResourceDelta delta) throws CoreException 
	{
		IResource resource = delta.getResource();

//		if (resource.getType() == IResource.FILE && "java".equalsIgnoreCase(resource.getFileExtension()))
//		{
//			System.out.println(resource.getName());
//			System.out.println("CONTENT " + (delta.getFlags() & IResourceDelta.CONTENT));
//			System.out.println("REPLACED " + (delta.getFlags() & IResourceDelta.REPLACED));
//			System.out.println("DESCRIPTION " + (delta.getFlags() & IResourceDelta.DESCRIPTION));
//			System.out.println("ENCODING " + (delta.getFlags() & IResourceDelta.ENCODING));
//			System.out.println("OPEN " + (delta.getFlags() & IResourceDelta.OPEN));
//			System.out.println("MOVED_TO " + (delta.getFlags() & IResourceDelta.MOVED_TO));
//			System.out.println("MOVED_FROM " + (delta.getFlags() & IResourceDelta.MOVED_FROM));
//			System.out.println("TYPE " + (delta.getFlags() & IResourceDelta.TYPE));
//			System.out.println("SYNC " + (delta.getFlags() & IResourceDelta.SYNC));
//			System.out.println("MARKERS " + (delta.getFlags() & IResourceDelta.MARKERS));
//
//			System.out.println("ADDED " + (delta.getKind() == IResourceDelta.ADDED));
//			System.out.println("REMOVED " + (delta.getKind() == IResourceDelta.REMOVED));
//			System.out.println("CHANGED " + (delta.getKind() == IResourceDelta.CHANGED));
//			System.out.println("ADDED_PHANTOM " + (delta.getKind() == IResourceDelta.ADDED_PHANTOM));
//			System.out.println("REMOVED_PHANTOM " + (delta.getKind() == IResourceDelta.REMOVED_PHANTOM));
//			System.out.println("-");
//		}

		if (resource.getType() == IResource.FILE && "java".equalsIgnoreCase(resource.getFileExtension()))
		{
			// k-k-k-ontent changes, what a wonderful audience!
			if((delta.getFlags() & IResourceDelta.CONTENT)!=0)
				if(delta.getKind() == IResourceDelta.CHANGED)
					this.resourcesToUpdate.add(delta);	
			
			// Addition and removal of resources
			if(delta.getKind() == IResourceDelta.REMOVED || delta.getKind() == IResourceDelta.ADDED)
				this.resourcesToUpdate.add(delta);	
		}
		
		return true;
	}
}
