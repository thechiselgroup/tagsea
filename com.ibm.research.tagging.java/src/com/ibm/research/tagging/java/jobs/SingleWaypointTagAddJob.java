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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.java.JavaWaypoint;
import com.ibm.research.tagging.java.refactoring.TagRefactorer;

public class SingleWaypointTagAddJob extends Job {

	private ITag fTag;
	private JavaWaypoint fWaypoint;
	
	public SingleWaypointTagAddJob(ITag tag,JavaWaypoint waypoint) 
	{
		super("Tag Remove");
		fTag = tag;
		fWaypoint = waypoint;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		JavaWaypoint javaWaypoint = (JavaWaypoint)fWaypoint;
		List<JavaWaypoint> waypoints = new ArrayList<JavaWaypoint>();
		waypoints.add(javaWaypoint);
		
			TagRefactorer refactorer = new TagRefactorer((IFile)fWaypoint.getMarker().getResource(),waypoints)
			{
				@Override
				public String refactorTag(String tagDefintion) 
				{
					String[] tags = tagDefintion.split("\\s+");

					StringBuffer buffer = new StringBuffer();

					for(String tagName : tags)
					{
						// Already exists
						if(tagName.trim().equals(fTag.getName()))
							return null;

						buffer.append(" " + tagName);
					}

					String newTagDefinition = buffer.toString().trim();
					newTagDefinition = " " + newTagDefinition + " " + fTag.getName() + " ";

					return newTagDefinition;
				}
			};
			
			refactorer.execute();

		return Status.OK_STATUS;
	}
}
