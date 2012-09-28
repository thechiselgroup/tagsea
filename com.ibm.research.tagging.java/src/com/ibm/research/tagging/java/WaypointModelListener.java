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

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.IWaypointListener;
import com.ibm.research.tagging.core.IWaypointModelListener;
import com.ibm.research.tagging.java.jobs.SingleWaypointTagAddJob;
import com.ibm.research.tagging.java.jobs.SingleWaypointTagRemoveJob;

public class WaypointModelListener implements IWaypointModelListener, IWaypointListener 
{	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointCollectionListener#waypointAdded(com.ibm.research.tagging.core.IWaypoint)
	 */
	public void waypointAdded(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(JavaWaypoint.TYPE))
			waypoint.addWaypointListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointCollectionListener#waypointRemoved(com.ibm.research.tagging.core.IWaypoint)
	 */
	public void waypointRemoved(IWaypoint waypoint) 
	{
		if(waypoint.getType().equals(JavaWaypoint.TYPE))
			waypoint.removeWaypointListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointListener#tagAdded(com.ibm.research.tagging.core.IWaypoint, com.ibm.research.tagging.core.ITag)
	 */
	public void tagAdded(final IWaypoint waypoint, final ITag tag) 
	{
		SingleWaypointTagAddJob job = new SingleWaypointTagAddJob(tag,(JavaWaypoint)waypoint);
		job.setSystem(true);
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointListener#tagRemoved(com.ibm.research.tagging.core.IWaypoint, com.ibm.research.tagging.core.ITag)
	 */
	public void tagRemoved(final IWaypoint waypoint, final ITag tag) 
	{
		SingleWaypointTagRemoveJob job = new SingleWaypointTagRemoveJob(tag,(JavaWaypoint)waypoint);
		job.setSystem(true);
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.core.IWaypointListener#waypointChanged(com.ibm.research.tagging.core.IWaypoint)
	 */
	public void waypointChanged(IWaypoint waypoint) 
	{
		// not implemented yet
	}
}
