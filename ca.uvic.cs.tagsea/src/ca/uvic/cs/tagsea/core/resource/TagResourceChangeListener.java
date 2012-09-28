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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;

import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * @author mdesmond
 */
public class TagResourceChangeListener implements IResourceChangeListener 
{
	/**
	 * @author mdesmond
	 * @tag tagUpdate resourceDelta : updates tags on resource change
	 */
	public void resourceChanged(IResourceChangeEvent event) 
	{
		IResourceDelta resourceDelta = event.getDelta();

		// We are only interested in post change
		if (( event.getType() != IResourceChangeEvent.POST_CHANGE )) 
			return;

		List<IResourceDelta> resouceDeltas = new ArrayList<IResourceDelta>();
		ResourceDeltaVisitor visitor = new ResourceDeltaVisitor(resouceDeltas);

		try 
		{
			// extract altered java file resource deltas using the visitor
			resourceDelta.accept(visitor);
		}
		catch (Exception e) 
		{
			// open error dialog with syncExec or print to plugin log file
			TagSEAPlugin.log(e.getMessage(), e);
			return;
		}

		// only exec if there is something in there
		if(resouceDeltas.size() > 0)
		{
			TagUpdateJob tagUpdateJob = new TagUpdateJob(resouceDeltas);
			tagUpdateJob.setSystem(true);
			tagUpdateJob.schedule();
		}
	}
}
