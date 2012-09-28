/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.model.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.IWaypointsModel;
import net.sourceforge.tagsea.core.TagSEAChangeStatus;

/**
 * 
 * @author Del Myers
 */

public class WaypointsModel implements IWaypointsModel {

	public static final WaypointsModel INSTANCE = new WaypointsModel();
	private long nextWaypointId;
	/**
	 * A mapping of waypoint types to waypoints.
	 */
	private TreeMap<String, TreeMap<Long, IWaypoint>> waypoints;
	private WaypointsModel() {
		waypoints = new TreeMap<String, TreeMap<Long, IWaypoint>>();
		nextWaypointId = ((Waypoint)Waypoint.DUMMY).getWorkbenchId()+1;
	};
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointsModel#getAllWaypoints()
	 */
	public IWaypoint[] getAllWaypoints() {
		LinkedList<IWaypoint> waypointsList = new LinkedList<IWaypoint>();
		for (String type : waypoints.keySet()) {
			TreeMap<Long, IWaypoint> typeMap = waypoints.get(type);
			waypointsList.addAll(typeMap.values());
		}
		return waypointsList.toArray(new IWaypoint[waypointsList.size()]);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointsModel#getWaypoints(java.lang.String)
	 */
	public IWaypoint[] getWaypoints(String type) {
		TreeMap<Long, IWaypoint> typeMap = waypoints.get(type);
		if (typeMap == null) {
			return new IWaypoint[0];
		}
		Collection<IWaypoint> waypointsList = typeMap.values();
		return waypointsList.toArray(new IWaypoint[waypointsList.size()]);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.IWaypointsModel#createWaypoint(java.lang.String, java.lang.String[])
	 */
	public IWaypoint createWaypoint(String type, String[] tags) {
		List<String> tagsList = new LinkedList<String>();
		tagsList.addAll(Arrays.asList(tags));
		if (tagsList.isEmpty()) {
			tagsList.add(ITag.DEFAULT);
		}
		AbstractWaypointDelegate delegate  =
			TagSEAPlugin.getDefault().getWaypointDelegate(type);
		if (delegate == null) return null;
		String[] attrs = delegate.getDeclaredAttributes();
		TreeMap<String, Object> attrMap = new TreeMap<String, Object>();
		for (String attr : attrs) {
			attrMap.put(attr, delegate.getDefaultValue(attr));
		}
		//create the waypoint 
		IWaypoint waypoint = new Waypoint(type, attrMap, nextWaypointId++);
		//post the new event.
		synchronized (waypoints) {
			TreeMap<Long, IWaypoint> waypointsList = waypoints.get(type);
			if (waypointsList == null) {
				waypointsList = new TreeMap<Long, IWaypoint>();
				waypoints.put(type, waypointsList);
			}
			synchronized (getBlock()) {
				waypointsList.put(((Waypoint)waypoint).getWorkbenchId(), waypoint);
				IWaypointChangeEvent event  = WaypointChangeEvent.createNewEvent(waypoint);
				TagSEAChangeStatus status = delegate.processChange(event);
				if (status.changePerformed) {
					TagSEAChangeSupport.INSTANCE.postWaypointChange(event);
					//create the tags
					for (String tag : tagsList) {
						waypoint.addTag(tag);
					}
				} else {
					waypointsList.remove(((Waypoint)waypoint).getWorkbenchId());
					if (waypointsList.size() == 0) {
						waypoints.remove(type);
					}
					waypoint = null;
				}
			}
		}
		return waypoint;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.IWaypointsModel#removeWaypoint(net.sourceforge.tagsea.core.IWaypoint)
	 */
	public TagSEAChangeStatus removeWaypoint(IWaypoint waypoint) {
		synchronized (waypoints) {
			synchronized (getBlock()) {
				TreeMap<Long, IWaypoint> waypointsList = waypoints.get(waypoint.getType());
				if (waypointsList != null) {
					IWaypointChangeEvent event = WaypointChangeEvent.createDeleteEvent(waypoint);
					AbstractWaypointDelegate delegate = TagSEAPlugin.getDefault().getWaypointDelegate(waypoint.getType());
					TagSEAChangeStatus status = delegate.processChange(event);
					if (!status.changePerformed) return status;
					if (waypointsList.remove(((Waypoint)waypoint).getWorkbenchId())!=null) {
						//tell the waypoint that it has been deleted.
						if (waypoint.exists()) waypoint.delete(); 
						//post the change
						TagSEAChangeSupport.
							INSTANCE.
							postWaypointChange(event);
						//update the tags model with the remove.
						for (ITag tag : waypoint.getTags()) {
							((Tag)tag).removeWaypoint(waypoint);
						}
					}
					return status;
				}
			}
		}
		return TagSEAChangeStatus.SUCCESS_STATUS;
	}
	
	private Object getBlock() {
		return TagSEAChangeSupport.INSTANCE.getOperationBlocker();
	}
	
	/**
	 * Returns the waypoint for the given workbench id if it exists for the given type.
	 * If the type is already known, this will run faster than getWaypointById(long).
	 * @param type the type to check.
	 * @param id the id to find.
	 * @return the waypoint, or null if it couldn't be found.
	 */
	public IWaypoint getWaypointById(String type, long id) {
		IWaypoint waypoint = null;
		TreeMap<Long, IWaypoint> map = waypoints.get(type);
		if (map != null) {
			waypoint = map.get(id);
		}
		return waypoint;
	}
	
	/**
	 * Searches for the waypoint with the given workbench id and returns it if it can be found.
	 * Returns null otherwise.
	 * @param id the id to find.
	 * @return the waypoint  with the given workbench id if it can be found.
	 * Returns null otherwise.
	 */
	public IWaypoint getWaypointById(long id) {
		IWaypoint waypoint = null;
		for (String type : waypoints.keySet()) {
			waypoint = getWaypointById(type, id);
			if (waypoint != null)
				return waypoint;
		}
		return waypoint;
	}

}
