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
package ca.uvic.cs.tagsea.core;

import java.util.Vector;

import ca.uvic.cs.tagsea.monitoring.TagSEARoutingEvent;
import ca.uvic.cs.tagsea.monitoring.internal.Monitoring;

/**
 * A route is a list of ordered waypoints that can be navigated.
 * 
 * @author Jie Zhang 
 * @author Suzanne Thompson
 * @author mdesmond
 *
 */
public class Route 
{
	private String fName;
	private Vector<Waypoint> fWaypoints;
	
	/**
	 * @author mdesmond
	 * @param name
	 */
	public Route(String name) 
	{
		setName(name);
	}
	
	/**
	 * @author mdesmond
	 * @param waypoint
	 */
	public void addWaypoint(Waypoint waypoint)
	{
		_getWaypoints().addElement(waypoint);
		Monitoring.getDefault().fireEvent(new TagSEARoutingEvent(TagSEARoutingEvent.Routing.NewWaypoint, this, waypoint));
	}
	
	/**
	 * @author mdesmond
	 * @param index
	 * @param waypoint
	 */
	public void addWaypoint(int index, Waypoint waypoint) 
	{
		_getWaypoints().add(index, waypoint);
		Monitoring.getDefault().fireEvent(new TagSEARoutingEvent(TagSEARoutingEvent.Routing.NewWaypoint, this, waypoint));
	}	

	/**
	 * @author mdesmond
	 * @param index
	 */
	public void removeWaypoint(int index)
	{
		Waypoint w = _getWaypoints().remove(index);
		Monitoring.getDefault().fireEvent(new TagSEARoutingEvent(TagSEARoutingEvent.Routing.DeleteWaypoint, this, w));
	}

	/**
	 * @author mdesmond
	 */
	public Vector<Waypoint> getWaypoints() 
	{
		return _getWaypoints();
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return fName;
	}
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name) 
	{
		String oldName = fName;
		this.fName = (name != null ? name : "");
		Monitoring.getDefault().fireEvent(new TagSEARoutingEvent(this, oldName));
	}
	
	/**
	 * 
	 */
	public String toString() 
	{
		return fName + " [" + _getWaypoints().size() + "]";
	}

	/**
	 * @author mdesmond
	 * @return
	 */
	private Vector<Waypoint> _getWaypoints()
	{
		if(fWaypoints == null)
			fWaypoints = new Vector<Waypoint>();
		
		return fWaypoints;
	}
}