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

package net.sourceforge.tagsea.powerpoint.waypoints;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.powerpoint.PowerpointWaypointPlugin;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

public class PowerpointResourceChangeListener implements IResourceChangeListener {

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
								if(markerDelta.getType().equals(PowerpointWaypointPlugin.MARKER_ID))
								{
									IWaypoint[] waypoints = TagSEAPlugin.getWaypointsModel().getWaypoints(PowerpointWaypointPlugin.WAYPOINT_ID);

									for(IWaypoint waypoint: waypoints)
									{
										try 
										{
											String markerIdString = waypoint.getStringValue(PowerpointWaypointDelegate.MARKER_ID_ATTR, "");
											Long markerId = Long.parseLong(markerIdString.trim()); // throws NFE

											if(markerId == markerDelta.getId())
												TagSEAPlugin.getWaypointsModel().removeWaypoint(waypoint);
										} 
										catch (NumberFormatException e) 
										{
											e.printStackTrace();
										}
									}
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
