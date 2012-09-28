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
import net.sourceforge.tagsea.parsed.IParsedWaypointAttributes;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.IFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;

/**
 * Utility methods for getting the individual attributes for a parsed waypoint.
 * @author Del Myers
 *
 */
public class ParsedWaypointUtils {
	
	public static String getKind(IWaypoint waypoint) {
		return waypoint.getStringValue(IParsedWaypointAttributes.ATTR_KIND, null);
	}
	
	public static String getDomainDetail(IWaypoint waypoint) {
		return waypoint.getStringValue(IParsedWaypointAttributes.ATTR_DOMAIN, null);
	}
	
	public static int getCharStart(IWaypoint waypoint) {
		return waypoint.getIntValue(IParsedWaypointAttributes.ATTR_CHAR_START, -1);
	}
	
	public static int getCharEnd(IWaypoint waypoint) {
		return waypoint.getIntValue(IParsedWaypointAttributes.ATTR_CHAR_END, -1);
	}
	
	public static String getResourceName(IWaypoint waypoint) {
		return waypoint.getStringValue(IParsedWaypointAttributes.ATTR_RESOURCE, null);
	}
	
	public static IResource getResource(IWaypoint waypoint) {
		String resourceName = getResourceName(waypoint);
		if (resourceName != null) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(resourceName));
			if (resource != null && resource.exists()) {
				return resource;
			}
		}
		return null;
	}

	
	/**
	 * Returns the document that the waypoint exists on iff one is currently opened for it using the
	 * {@link FileBuffers} facility. Returns null otherwise.
	 * @param wp the waypoint to check.
	 * @return document that the waypoint exists on iff one is currently opened for it using the
	 * {@link FileBuffers} facility. Null otherwise.
	 * @see FileBuffers
	 */
	public static IDocument getDocument(IWaypoint wp) {
		IResource resource = getResource(wp);
		if (resource != null) {
			IFileBuffer buffer = 
				FileBuffers.getTextFileBufferManager().getFileBuffer(resource.getFullPath(), LocationKind.IFILE);
			if (buffer instanceof ITextFileBuffer) {
				return ((ITextFileBuffer)buffer).getDocument();
			}
		}
		return null;
	}
	
	
	public static IParsedWaypointDefinition getWaypointDefinition(IWaypoint wp) {
		String kind = getKind(wp);
		if (kind != null) {
			return ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getDefinition(kind);
		}
		return null;
	}


	/**
	 * @param wp
	 * @return
	 */
	public static int getLength(IWaypoint wp) {
		int start = getCharStart(wp);
		int end = getCharEnd(wp);
		return end-start;
	}

}
