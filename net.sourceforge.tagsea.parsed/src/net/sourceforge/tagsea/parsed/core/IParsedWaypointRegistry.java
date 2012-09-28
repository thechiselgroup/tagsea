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
package net.sourceforge.tagsea.parsed.core;

import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;

/**
 * Registry for parsed waypoint definitions.
 * @author Del Myers
 *
 */
public interface IParsedWaypointRegistry {

	/**
	 * Registers the given definition. Note that this will cause a clean of the parsed waypoints in
	 * the workspace. If more than one definition should be registered at the same time, use 
	 * {@link #register(IParsedWaypointDefinition[])}. The definition must have been contributed via
	 * the <code>net.sourceforge.tagsea.parsed.parsedWaypoint</code> or 
	 * <code>net.sourceforge.tagsea.parsed.standardComment</code> extension points.
	 * @param definitionKind the kind id for the definition to register.
	 */
	void register(String definitionKind);
	
	/**
	 * Registers the given definitions. The definitions must have been contributed via
	 * the <code>net.sourceforge.tagsea.parsed.parsedWaypoint</code> or 
	 * <code>net.sourceforge.tagsea.parsed.standardComment</code> extension points.
	 * @param definitionKinds the kind ids for the definitions to register.
	 */
	void register(String[] definitionKinds);
	
	
	/**
	 * Sets the registered definition kinds to the given kinds. Unregisters all kinds that are not
	 * included in the list. The definitions must have been contributed via
	 * the <code>net.sourceforge.tagsea.parsed.parsedWaypoint</code> or 
	 * <code>net.sourceforge.tagsea.parsed.standardComment</code> extension points.
	 * @param definitionKinds the kinds to set the registration to.
	 */
	void setRegistered(String[] definitionKinds);
	
	/**
	 * Returns the waypoint definition of the specified kind if available.
	 * @param kind the kind.
	 * @return waypoint definition of the specified kind if available.
	 */
	public IParsedWaypointDefinition getDefinition(String kind);
	
	/**
	 * Unregisters the given definition from the registry. Note that this will cause a clean of the parsed waypoints in
	 * the workspace. If more than one definition should be unregistered at once, use 
	 * {@link #unregister(IParsedWaypointDefinition[])}. The definition must have been contributed via
	 * the <code>net.sourceforge.tagsea.parsed.parsedWaypoint</code> or 
	 * <code>net.sourceforge.tagsea.parsed.standardComment</code> extension points.
	 * @param definitionKind the kind id for the definition to unregister.
	 */
	void unregister(String definitionKind);
	
	/**
	 * Unregisters the given definitions. This will cause a clean of the waypoints in the workspace.
	 * The definition must have been contributed via
	 * the <code>net.sourceforge.tagsea.parsed.parsedWaypoint</code> or 
	 * <code>net.sourceforge.tagsea.parsed.standardComment</code> extension points.
	 * @param definitionKinds the kind ids for the definitions to unregister.
	 */
	void unregister(String[] definitionKinds);
	
	/**
	 * Gets the definitions that are registered.
	 * @return the definitions that are registered.
	 */
	public IParsedWaypointDefinition[] getRegisteredDefinitions();
	
	/**
	 * Forces a clean of all the parsed waypoints in the workspace. This is accomplished by clearing
	 * all of the waypoints in the workspace and re-parsing the files. Convenience method for
	 * <source>clean(ResourcesPlugin.getWorkspace(), IResource.DEPTH_INFINITE)</source>.
	 * @param force forces a full clean regardless of whether or not the platform discovers a need for it.
	 */
	void clean(boolean force);
	
	/**
	 * Cleans the given resource at the given depth. This is accomplished by clearing all of the
	 * waypoints in the resource (and its children, if desired), and re-parsing the files.
	 * @param resource
	 * @param depth
	 */
	void clean(IResource resource, int depth);
	
	
	/**
	 * Returns the registered waypoint definitions that will operate on the given resource.
	 * @param resource the resource to check.
	 * @return the registered waypoint definitions that will operate on the given resource.
	 */
	IParsedWaypointDefinition[] getMatchingDefinitions(IFile resource);
	
	
	/**
	 * Returns the parsed waypoints on the given file.
	 * @param resource the resource to check.
	 * @return the parsed waypoints on that file.
	 */
	IWaypoint[] getWaypointsForFile(IFile resource);
	
	/**
	 * Returns the parsed waypoints on the given file that are of the given kind.
	 * @param resource the resource to check.
	 * @param kind the waypoint kind.
	 * @return the parsed waypoints on that file.
	 */
	IWaypoint[] getWaypointsForFile(IFile resource, String kind);
	
	
	/**
	 * Retrieves the waypoints for the given resource (and its children). The
	 * depth for which to search for the waypoints is one of {@link IResource#DEPTH_ZERO} 
	 * (for this resource only), {@link IResource#DEPTH_ONE} (for this resource and
	 * its immediate children), or {@link IResource#DEPTH_INFINITE} (for this resource and all
	 * of its children).
	 * @param resource
	 * @param depth
	 * @return
	 */
	IWaypoint[] findWaypoints(IResource resource, int depth);

	/**
	 * Returns the file that the given waypoint is on. 
	 * @param wp
	 */
	IFile getFileForWaypoint(IWaypoint wp);

	/**
	 * Returns the marker that is used to mark the waypoint in the workspace.
	 * @param wp the waypoint to get the marker for.
	 * @return the marker, or null if none could be found.
	 */
	public IMarker getMarkerForWaypoint(IWaypoint wp);

	/**
	 * Returns the waypoint for the given marker.
	 * @param marker the marker
	 * @return the waypoint found.
	 */
	IWaypoint getWaypointForMarker(IMarker marker);

	/**
	 * @return the parsed waypoint definitions that have been contributed to the platform via the 
	 * <code>net.sourceforge.tagsea.parsed.parsedWaypoint</code> or 
	 * <code>net.sourceforge.tagsea.parsed.standardComment</code> extension points.
	 */
	IParsedWaypointDefinition[] getContributedDefinitions();

	/**
	 * @param kind
	 * @return
	 */
	boolean isRegistered(String kind);
	
}
