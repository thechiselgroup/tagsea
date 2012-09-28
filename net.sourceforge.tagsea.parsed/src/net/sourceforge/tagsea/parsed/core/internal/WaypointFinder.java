/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.parsed.core.internal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.IWaypointChangeListener;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.parsed.IParsedWaypointAttributes;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Utility class for registering all waypoints and parse problems.
 * @author Del Myers
 *
 */
class WaypointFinder {
	
	/**
	 * A map used to measure the last time that a file was accessed so that we can remove them from
	 * memory when the haven't been used.
	 */
	private final HashMap<String, Long> DECAY_MAP = new HashMap<String, Long>();
	
	private final int MAX_SIZE = 1000; 
	
	/**
	 * A map of files to waypoints. The waypoints are not sorted.
	 */
	private final HashMap<String, List<IWaypoint>> FILE_WAYPOINT_MAP = new HashMap<String, List<IWaypoint>>();
	
	/**
	 * Five minute decay.
	 */
	private static final long DECAY_TIME = 1000 * 60 * 5;

	private final IWaypointChangeListener WAYPOINT_LISTENER = new IWaypointChangeListener() {
		public void waypointsChanged(WaypointDelta delta) {
			synchronized (DECAY_MAP) {
				for (IWaypointChangeEvent event : delta.getChanges()) {
					IWaypoint wp = event.getWaypoint();
					String fileName = wp.getStringValue(IParsedWaypointAttributes.ATTR_RESOURCE, null);
					List<IWaypoint> currentWaypoints = FILE_WAYPOINT_MAP.get(fileName);
					if (currentWaypoints != null) {
						switch (event.getType()) {
						case IWaypointChangeEvent.DELETE:
							currentWaypoints.remove(wp);
							DECAY_MAP.put(fileName, System.currentTimeMillis());
							break;
						case IWaypointChangeEvent.NEW:
							currentWaypoints.add(wp);
							DECAY_MAP.put(fileName, System.currentTimeMillis());
							break;
						}
					}
				}
			}
		}
	};
	
	
	public IWaypoint[] getWaypointsForFile(IFile file) {
		TagSEAPlugin.addWaypointChangeListener(WAYPOINT_LISTENER, ParsedWaypointPlugin.WAYPOINT_TYPE);
		List<IWaypoint> currentWaypoints = new LinkedList<IWaypoint>();
		String filePath = file.getFullPath().toPortableString();
		synchronized (DECAY_MAP) {
			if (FILE_WAYPOINT_MAP.containsKey(file)) {
				currentWaypoints = FILE_WAYPOINT_MAP.get(filePath);
			} else {
				//@tag tagsea.todo tour.TourTest2.1194628865421.2 : this, in rare occasions might not be synched with the waypoint state.
				IWaypoint[] allWaypoints = TagSEAPlugin.getWaypointsModel().getWaypoints(ParsedWaypointPlugin.WAYPOINT_TYPE);
				//make sure to log that the file doesn't contain any waypoints if none are discovered.
				FILE_WAYPOINT_MAP.put(filePath, currentWaypoints);
				DECAY_MAP.put(filePath, System.currentTimeMillis());
				//get all of the waypoints that are currently available.
				for (IWaypoint wp : allWaypoints) {
					if (wp.exists()) {
						String wpFile = wp.getStringValue(IParsedWaypointAttributes.ATTR_RESOURCE, null);
						//may as well build the whole registry while we are at it.
						if (wpFile != null) {
							List<IWaypoint> waypointList = FILE_WAYPOINT_MAP.get(wpFile);
							if (waypointList == null && (FILE_WAYPOINT_MAP.size() < MAX_SIZE || filePath.equals(wpFile))) {
								waypointList = new LinkedList<IWaypoint>();
								FILE_WAYPOINT_MAP.put(wpFile, waypointList);
							}
							if (filePath.equals(wpFile)) {
								currentWaypoints = waypointList;
							}
							DECAY_MAP.put(wpFile, System.currentTimeMillis());
							waypointList.add(wp);
						}
					}
				}
			}
			//if the size of the list has gotten too large, start deleting entries
			Object[] keys = FILE_WAYPOINT_MAP.keySet().toArray();
			if (keys.length > MAX_SIZE) {
				int i = 0;
				while (FILE_WAYPOINT_MAP.size() > MAX_SIZE - 100) {
					if (keys[i] != file) {
						FILE_WAYPOINT_MAP.remove(keys[i]);
						DECAY_MAP.remove(keys[i]);
					}
					i++;
				}
			}
			DECAY_MAP.put(filePath, System.currentTimeMillis());
			//delete all of the out-of-date files.
			
			long timestamp = System.currentTimeMillis();
			for (Object k : keys) {
				Long time = DECAY_MAP.get(k);
				if (time == null || timestamp - time > DECAY_TIME) {
					DECAY_MAP.remove(k);
					FILE_WAYPOINT_MAP.remove(k);
				}
			}
		}
		return currentWaypoints.toArray(new IWaypoint[currentWaypoints.size()]);
	}
	
	
	private static IResource getResourceForWaypoint(IWaypoint wp) {
		String fileName = wp.getStringValue(IParsedWaypointAttributes.ATTR_RESOURCE, null);
		if (fileName != null) {
			IResource resource = 
				ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(fileName), true);
			return resource;
		}
		return null;
	}
	
	public IWaypoint[] getWaypointsForFile(IFile file, String kind) {
		IWaypoint[] allWaypoints = getWaypointsForFile(file);
		LinkedList<IWaypoint> waypointList = new LinkedList<IWaypoint>();
		for (IWaypoint wp : allWaypoints) {
			if (kind.equals(wp.getStringValue(IParsedWaypointAttributes.ATTR_KIND, null))) {
				waypointList.add(wp);
			}
		}
		return waypointList.toArray(new IWaypoint[waypointList.size()]);
	}


	/**
	 * @param resource
	 * @param depth
	 * @return
	 */
	public IWaypoint[] findWaypoints(IResource resource, int depth) {
		if (resource instanceof IFile) {
			return getWaypointsForFile((IFile)resource);
		} else if (resource instanceof IContainer && depth == IResource.DEPTH_ZERO) {
			return new IWaypoint[0];
		}
		IWaypoint[] allWaypoints = TagSEAPlugin.getWaypointsModel().getWaypoints(ParsedWaypointPlugin.WAYPOINT_TYPE);
		IPath resourcePath = resource.getFullPath();
		int resourceSegments = resourcePath.segmentCount();
		if (resource instanceof IWorkspaceRoot && depth == IResource.DEPTH_INFINITE) {
			//just return all of them.
			return allWaypoints;
		}
		LinkedList<IWaypoint> result = new LinkedList<IWaypoint>();
		for (IWaypoint wp : allWaypoints) {
			if (!wp.exists()) continue;
			IResource wpResource = getResourceForWaypoint(wp);
			if (resourcePath.isPrefixOf(wpResource.getFullPath())) {
				if (depth == IResource.DEPTH_INFINITE || (wpResource.getFullPath().segmentCount()-resourceSegments)==1) {
					result.add(wp);
				}
			}
		}
		return result.toArray(new IWaypoint[result.size()]);
	}

}
