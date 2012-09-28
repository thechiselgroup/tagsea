/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.resources.waypoints;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ResourceWaypointUtils;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * Synchronizes waypoints to markers.
 * @author Del Myers
 */

public class ResourceWaypointMarkerHelp {
	protected static HashMap<IWaypoint, IMarker> waypointMarkerMap = new HashMap<IWaypoint, IMarker>();
	protected static HashMap<IMarker, IWaypoint> markerWaypointMap = new HashMap<IMarker, IWaypoint>();
	private static ResourceListener resourceListener = new ResourceListener();
	private static boolean ignoreChange = false;
	protected static class ResourceListener implements IResourceChangeListener {
		private static class MarkerDeltaVisitor implements IResourceDeltaVisitor {
			/* (non-Javadoc)
			 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
			 */
			//@tag tagsea.bug.171 : this visitor interferes with the class FileAccessSession when it is used inside a TagSEAOperation. 
			public boolean visit(IResourceDelta delta) throws CoreException {
				IMarkerDelta[] mdeltas = delta.getMarkerDeltas();
				for (IMarkerDelta mdelta : mdeltas) {
					if (mdelta.isSubtypeOf(ResourceWaypointPlugin.MARKER_ID)) {
						//check the line
						IMarker marker = mdelta.getMarker();
						IWaypoint waypoint = markerWaypointMap.get(marker);
						if (waypoint != null) 
						{
							int deltaLine = (Integer) mdelta.getMarker().getAttribute(IResourceWaypointAttributes.ATTR_LINE);
							int waypointLine = waypoint.getIntValue(IResourceWaypointAttributes.ATTR_LINE, -1); 
							
							int deltaCharStart = (Integer) mdelta.getMarker().getAttribute(IResourceWaypointAttributes.ATTR_CHAR_START);
							int deltaCharEnd = (Integer) mdelta.getMarker().getAttribute(IResourceWaypointAttributes.ATTR_CHAR_END);
							
							int wayCharStart = waypoint.getIntValue(IResourceWaypointAttributes.ATTR_CHAR_START,-1);
							int wayCharEnd = waypoint.getIntValue(IResourceWaypointAttributes.ATTR_CHAR_END,-1);
							
							if (deltaLine != waypointLine)
							{
								//update the line
								ignoreChange = true;
								waypoint.setIntValue(IResourceWaypointAttributes.ATTR_LINE, deltaLine);
								ignoreChange = false;
							}
							
							if(deltaCharStart!=wayCharStart || deltaCharEnd!=wayCharEnd)
							{
								//update the range
								ignoreChange = true;
								waypoint.setIntValue(IResourceWaypointAttributes.ATTR_CHAR_START, deltaCharStart);
								waypoint.setIntValue(IResourceWaypointAttributes.ATTR_CHAR_END, deltaCharEnd);
								ignoreChange = false;
							}
						}
					}
				}
				return true;
			}
			
		}
		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
		 */
		public void resourceChanged(final IResourceChangeEvent event) {
			//@tag tagsea.bug.171 tour.TourTest2.1194628865421.1 : temporary fix.
//			TagSEAPlugin.run(new TagSEAOperation("Updating resource waypoints"){
//				@Override
//				public IStatus run(IProgressMonitor monitor)
//				throws InvocationTargetException {
//					try {
						try {
							event.getDelta().accept(new MarkerDeltaVisitor());
						} catch (CoreException e) {
							ResourceWaypointPlugin.getDefault().log(e);
						}
//						return Status.OK_STATUS;
//					} catch (CoreException e) {
//						return e.getStatus();
//					}
//				}}, false);
				
			
		}
	}
	
	public static void listenToMarkers(boolean listen) {
		if (listen) {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener, IResourceChangeEvent.POST_CHANGE);
		} else {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);
		}
	}
	
	public static IMarker getMarker(IWaypoint waypoint)
	{
		return waypointMarkerMap.get(waypoint);
	}
	
	public static IWaypoint getWaypoint(IMarker marker) {
		return markerWaypointMap.get(marker);
	}
	
	public static synchronized void synchronize(WaypointDelta delta) {
		if (ignoreChange) return;
		for (IWaypointChangeEvent event : delta.changes) {
			try {
				switch(event.getType()) {
				case IWaypointChangeEvent.NEW: handleNew(event); break;
				case IWaypointChangeEvent.DELETE: handleDelete(event); break;
				case IWaypointChangeEvent.CHANGE:
				case IWaypointChangeEvent.TAG_NAME_CHANGED: 
				case IWaypointChangeEvent.TAGS_CHANGED:	
					handleChange(event); 
					break;
				}
			} catch (CoreException e) {

			}
		}
	}


	/**
	 * @param event
	 * @throws CoreException 
	 */
	private synchronized static void handleDelete(IWaypointChangeEvent event) throws CoreException {
		IMarker marker = waypointMarkerMap.get(event.getWaypoint());
		if (marker != null) {
			marker.delete();
			waypointMarkerMap.remove(event.getWaypoint());
			markerWaypointMap.remove(marker);
		}
	}

	/**
	 * @param event
	 * @throws CoreException 
	 */
	private  synchronized static void handleNew(IWaypointChangeEvent event) throws CoreException {
		IMarker marker = waypointMarkerMap.get(event.getWaypoint());
		if (marker == null) {
			ResourcesPlugin.getWorkspace().
				run(
					getCreateRunnable(event.getWaypoint()), 
					getRule(event.getWaypoint()),
					IWorkspace.AVOID_UPDATE,
					new NullProgressMonitor()
				);
		}
	}

	/**
	 * @param event
	 * @throws CoreException 
	 */
	private  synchronized static void handleChange(IWaypointChangeEvent event) throws CoreException {
		IMarker marker = waypointMarkerMap.get(event.getWaypoint());
		if (marker == null) {
			handleNew(event);
		} else {
			if (!ResourcesPlugin.getWorkspace().isTreeLocked())
				ResourcesPlugin.getWorkspace().
				run(
					getChangeRunnable(event.getWaypoint(), marker), 
					getRule(event.getWaypoint()),
					IWorkspace.AVOID_UPDATE,
					new NullProgressMonitor()
				);
		}
	}

	
	
	/**
	 * @param waypoint
	 * @param marker
	 * @return
	 */
	private static IWorkspaceRunnable getChangeRunnable(final IWaypoint waypoint, final IMarker marker) {
		IWorkspaceRunnable r= new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IResource resource = ResourceWaypointUtils.getResource(waypoint);
				if (resource != null) {
					setMarkerAttributesToWaypoint(marker, waypoint);
				}
				
			}
		};
		return r;
	}


	private static IWorkspaceRunnable getCreateRunnable(final IWaypoint waypoint) {
		IWorkspaceRunnable r= new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IResource resource = ResourceWaypointUtils.getResource(waypoint);
				if (resource != null) {
					IMarker marker= resource.createMarker(ResourceWaypointPlugin.MARKER_ID);
					setMarkerAttributesToWaypoint(marker, waypoint);
					waypointMarkerMap.put(waypoint, marker);
					markerWaypointMap.put(marker, waypoint);
				}
			}
		};
		return r;
	}
	
	protected static void setMarkerAttributesToWaypoint(IMarker marker, IWaypoint waypoint) throws CoreException {

		String[] attrs = waypoint.getAttributes();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		for (String attr : attrs) {
			if (IResourceWaypointAttributes.ATTR_RESOURCE.equals(attr)) continue;
			Object value = waypoint.getValue(attr);
			if (value != null) {
				if (value instanceof Integer || value instanceof String || value instanceof Boolean) {
					attributes.put(attr, value);
				} else if (value instanceof Date) {
					DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
					attributes.put(attr, format.format((Date)value));
				} else {
					attributes.put(attr, value.toString());
				}
			}
		}
		attributes.put("waypointType", ResourceWaypointPlugin.WAYPOINT_ID);
		marker.setAttributes(attributes);
		waypointMarkerMap.put(waypoint, marker);
		markerWaypointMap.put(marker, waypoint);
	}
	
	private static ISchedulingRule getRule(IWaypoint waypoint) {
		IResource resource = ResourceWaypointUtils.getResource(waypoint);
		if (resource == null) {
			return ResourcesPlugin.
				getWorkspace().
				getRuleFactory().markerRule(
					ResourcesPlugin.getWorkspace().getRoot()
				);
		}
		return resource.getWorkspace().getRuleFactory().markerRule(resource);
	}
}
