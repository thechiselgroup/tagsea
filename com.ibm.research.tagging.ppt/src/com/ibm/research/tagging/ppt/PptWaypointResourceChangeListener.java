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

package com.ibm.research.tagging.ppt;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.resource.ResourceWaypointUtil;

public class PptWaypointResourceChangeListener implements
		IResourceChangeListener {

	public void resourceChanged(IResourceChangeEvent event) 
	{
		IResourceDelta resourceDelta = event.getDelta();

		// We are only interested in post change
		if (( event.getType() == IResourceChangeEvent.POST_CHANGE )) 
		{
			try
			{
				resourceDelta.accept(new IResourceDeltaVisitor() 
				{
					public boolean visit(IResourceDelta delta) throws CoreException 
					{
						if ((delta.getKind() == IResourceDelta.REMOVED)) 
						{
							IMarkerDelta[] markerDeltas = delta.getMarkerDeltas();
							
							for(IMarkerDelta markerDelta : markerDeltas)
							{
								if(markerDelta.getType().equals(PptWaypoint.MARKER_ID))
								{
									IWaypoint waypoint = ResourceWaypointUtil.getWaypointFromModel(Long.toString(markerDelta.getId()));

									if(waypoint != null)
										TagCorePlugin.getDefault().getTagCore().getWaypointModel().removeWaypoint(waypoint);
								}
							}
						}
						return true;
					}
				
				});
			} 
			catch (CoreException e)
			{
				e.printStackTrace();
			}
		}
	}
}
