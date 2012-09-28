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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.monitoring.TagSEAJobEvent;
import ca.uvic.cs.tagsea.monitoring.internal.Monitoring;

/**
 * @author mdesmond
 */
public class RoutesRestoringJob extends Job 
{
	public RoutesRestoringJob() 
	{
		super("Retrieving @tag routes from workspace");
	}
	 
	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
        monitor.beginTask("Loading routes", 1);
        Monitoring.getDefault().fireEvent(new TagSEAJobEvent(TagSEAJobEvent.JobType.StartLoadingRoutes));
		TagSEAPlugin.getDefault().getRouteCollection().load();
		monitor.worked(1);
		monitor.done();
		refreshRoutesView();
		Monitoring.getDefault().fireEvent(new TagSEAJobEvent(TagSEAJobEvent.JobType.EndLoadingRoutes));
		return Status.OK_STATUS;
	}
	
    /**
     * Refresh the tag UI part
     */
    private void refreshRoutesView() 
    {
        Display.getDefault().asyncExec(new Runnable()
        {
			public void run()
			{
            	TagSEAPlugin.getDefault().getRouteCollection().updateView();
			}
		});
    }
}