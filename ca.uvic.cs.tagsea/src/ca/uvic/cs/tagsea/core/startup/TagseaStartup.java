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
package ca.uvic.cs.tagsea.core.startup;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.ui.IStartup;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.resource.RoutesRestoringJob;
import ca.uvic.cs.tagsea.core.resource.TagResourceChangeListener;
import ca.uvic.cs.tagsea.core.resource.TagRestoringJob;
import ca.uvic.cs.tagsea.preferences.TagSEAPreferences;

public class TagseaStartup implements IStartup 
{
	TagResourceChangeListener tagChangedListener = new TagResourceChangeListener();

	/**
	 * @tag loadTags loadRoutes
	 * @author mdesmond
	 */
	public void earlyStartup() 
	{
		if (!TagSEAPreferences.askedForID()) {
			TagSEAPlugin.getDefault().askForUserID();
		}
		
		TagRestoringJob tagRestoringJob = new TagRestoringJob();
		final RoutesRestoringJob routesRestoringJob = new RoutesRestoringJob();
		routesRestoringJob.setSystem(true);
		tagRestoringJob.setSystem(true);
		
		tagRestoringJob.schedule();
		tagRestoringJob.addJobChangeListener(new IJobChangeListener() {
		
			public void sleeping(IJobChangeEvent event){}
			public void scheduled(IJobChangeEvent event){}
			public void running(IJobChangeEvent event){}
		
			// The route state loader relies on the waypoints being loaded
			public void done(IJobChangeEvent event) 
			{
				routesRestoringJob.schedule();
			}
			public void awake(IJobChangeEvent event){}
			public void aboutToRun(IJobChangeEvent event){}
		});
		
		//@tag resourceManagment
		try 
		{
			ResourcesPlugin.getWorkspace().addResourceChangeListener(tagChangedListener);
		} 
		catch (IllegalStateException e) 
		{
			TagSEAPlugin.log("Can not add resource change listener - workspace is null: " + e.getMessage(), e);
		}
		
	}
	
	
}