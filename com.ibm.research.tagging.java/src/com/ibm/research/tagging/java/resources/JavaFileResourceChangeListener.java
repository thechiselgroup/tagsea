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

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;

import com.ibm.research.tagging.java.jobs.ResourceChangeJob;

public class JavaFileResourceChangeListener implements IResourceChangeListener 
{
	public void resourceChanged(IResourceChangeEvent event) 
	{
		IResourceDelta resourceDelta = event.getDelta();

		if (( event.getType() == IResourceChangeEvent.POST_CHANGE )) 
		{
			JavaFileResourceDeltaVisitor visitor = new JavaFileResourceDeltaVisitor();

			try 
			{
				// extract added, removed and changed java file resource deltas using the visitor
				resourceDelta.accept(visitor);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return;
			}

			// only exec if there is something in there
			if(visitor.getChangedResourceDeltas().size() > 0 || visitor.getAddedResourceDeltas().size() > 0 || visitor.getRemovedResourceDeltas().size() > 0)
			{
				ResourceChangeJob changeJob = new ResourceChangeJob(visitor.getAddedResourceDeltas(),
																	  visitor.getRemovedResourceDeltas(),
																	  visitor.getChangedResourceDeltas());
				changeJob.setSystem(true);
				changeJob.schedule();
			}
		}
	}
}
