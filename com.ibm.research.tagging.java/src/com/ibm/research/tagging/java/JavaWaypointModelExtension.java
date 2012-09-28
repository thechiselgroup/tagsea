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
package com.ibm.research.tagging.java;

import org.eclipse.core.resources.ResourcesPlugin;

import com.ibm.research.tagging.core.ITagCore;
import com.ibm.research.tagging.core.IWaypointModelExtension;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.java.jobs.LoadWaypointsJob;
import com.ibm.research.tagging.java.resources.JavaFileResourceChangeListener;

public class JavaWaypointModelExtension implements IWaypointModelExtension {

	private JavaFileResourceChangeListener resourceChangeListener = new JavaFileResourceChangeListener();
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModelExtension#initialize(com.ibm.research.tagging.core.ITagCore)
	 */
	public void initialize(ITagCore core)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModelExtension#loadWaypoints()
	 */
	public void loadWaypoints()
	{
		LoadWaypointsJob job = new LoadWaypointsJob("Loading java waypoints");
		job.setUser(true);
		job.schedule();
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener);
		TagCorePlugin.getDefault().getTagCore().getTagModel().addTagModelListener(new TagModelListener());
		TagCorePlugin.getDefault().getTagCore().getWaypointModel().addWaypointModelListener(new WaypointModelListener());
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointModelExtension#saveWaypoints()
	 */
	public void saveWaypoints()
	{
	}
}
