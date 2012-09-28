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

import java.util.Timer;
import java.util.TimerTask;

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
public class TagRestoringJob extends Job 
{
	private static final long UI_REFRESH_DELAY = 1000;
	private static final long UI_REFRESH_PERIOD = 1000;
	
	public TagRestoringJob()
	{
		super("Retrieving @tags from workspace");
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		Timer t = new Timer();
		t.schedule(new TimerTask() 
		{
			@Override
			public void run() 
			{
				refreshTagUI();
			}				
		}, UI_REFRESH_DELAY, UI_REFRESH_PERIOD);
		
		Monitoring.getDefault().fireEvent(new TagSEAJobEvent(TagSEAJobEvent.JobType.StartLoadingTags));
		TagSEAPlugin.getDefault().getTagCollection().load();
		Monitoring.getDefault().fireEvent(new TagSEAJobEvent(TagSEAJobEvent.JobType.EndLoadingTags));
        monitor.done();
        t.cancel();
        refreshTagUI();
        refreshRouteUI();
        return Status.OK_STATUS;
	}
    
    /**
     * Refresh the tag UI part
     */
    private void refreshRouteUI() 
    {
        Display.getDefault().asyncExec(new Runnable()
        {
			public void run()
			{
	           	TagSEAPlugin.getDefault().getRouteCollection().updateView();
			}
		});
    }
    
    /**
     * Refresh the tag UI part
     */
    private void refreshTagUI() 
    {
        Display.getDefault().asyncExec(new Runnable()
        {
			public void run()
			{
            	TagSEAPlugin.getDefault().getTagCollection().updateView();
			}
		});
    }
}
