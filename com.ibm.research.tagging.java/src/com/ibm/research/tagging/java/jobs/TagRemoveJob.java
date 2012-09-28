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
package com.ibm.research.tagging.java.jobs;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.java.JavaWaypoint;
import com.ibm.research.tagging.java.refactoring.TagRefactorer;
import com.ibm.research.tagging.java.util.SortingUtilities;

public class TagRemoveJob extends Job {

	private ITag fTag;
	private IWaypoint[] fWaypoints;
	
	public TagRemoveJob(ITag tag,IWaypoint[] waypoints) 
	{
		super("Tag Remove");
		fTag = tag;
		fWaypoints = waypoints;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		// sort waypoints by resource
		Map<IResource, List<JavaWaypoint>> map = SortingUtilities.sortWaypointsByResource(fWaypoints);	
		
		for(IResource resource : map.keySet())
		{
			TagRefactorer refactorer = new TagRefactorer((IFile)resource,map.get(resource))
			{
				@Override
				public String refactorTag(String tagDefintion) 
				{
					String[] tags = tagDefintion.split("\\s+");

					StringBuffer buffer = new StringBuffer();

					boolean tagExists = false;
					
					for(String tagName : tags)
					{
						if(!tagName.trim().equals(fTag.getName()))
							buffer.append(" " + tagName);
						else
							tagExists = true;	
					}

					if(!tagExists)
						return null;
					
					String newTagDefinition = buffer.toString().trim();
					newTagDefinition = " " + newTagDefinition + " ";
					
					return newTagDefinition;
				}
			};
			
			refactorer.execute();
		}
		
		return Status.OK_STATUS;
	}
}
