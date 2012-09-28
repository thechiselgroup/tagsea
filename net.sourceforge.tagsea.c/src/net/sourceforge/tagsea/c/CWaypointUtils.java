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
package net.sourceforge.tagsea.c;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.sourceforge.tagsea.c.waypoints.CWaypointDelegate;
import net.sourceforge.tagsea.core.ITextWaypointAttributes;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAChangeStatus;
import net.sourceforge.tagsea.resources.IResourceInterfaceAttributes;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * Utility methods for java waypoints.
 * @author Del Myers
 */

public class CWaypointUtils {
	
	/**
	 * The character offset in the file that the waypoint is on.
	 * @param waypoint the waypoint.
	 * @return character offset in the file that the waypoint is on.
	 */
	public static int getOffset(IWaypoint waypoint) {
		return waypoint.getIntValue(ITextWaypointAttributes.ATTR_CHAR_START, -1);
	}
	
	/**
	 * Returns the formatted date String for the given date as it would appear
	 * inside a C/C++ waypoint in source code. Uses the default locale.
	 * @param date the date to format.
	 * @return the formatted date.
	 */
	public static String getFormattedDate(Date date) {
		return getFormattedDate(date, Locale.getDefault());
	}
	
	
	/**
	 * Returns the formatted date String for the given date as it would appear
	 * inside a C/C++ waypoint in source code. Uses the given locale.
	 * @param date the date to format.
	 * @return the formatted date.
	 */
	public static String getFormattedDate(Date date, Locale loc) {
		String dateString = DateFormat.getDateInstance(DateFormat.SHORT, loc).format(date);
		dateString = loc.getLanguage()+loc.getCountry().toUpperCase()+':'+dateString;
		return dateString;
	}
	/**
	 * The character length in the file that the waypoint is on.
	 * @param waypoint the waypoint.
	 * @return character length in the file that the waypoint is on.
	 */
	public static int getLength(IWaypoint waypoint) {
		int start = getOffset(waypoint);
		int end = getEnd(waypoint);
		if (start >= 0 && end >= 0) {
			return end - start;
		}
		return -1;
	}
	
	public static int getEnd(IWaypoint waypoint) {
		return waypoint.getIntValue(ITextWaypointAttributes.ATTR_CHAR_END, -1);
	}
	 
	/**
	 * Returns the file that the given waypoint references if it can be found.
	 * @param waypoint the waypoint.
	 * @return the file that the given waypoint references if it can be found.
	 */
	public static IFile getFile(IWaypoint waypoint) {
		String fileName;
		fileName = waypoint.getStringValue(IResourceInterfaceAttributes.ATTR_RESOURCE, ""); //$NON-NLS-1$
		if (!"".equals(fileName)) { //$NON-NLS-1$
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileName));
			return file;
		}
		return null;
	}
	
	/**
	 * Sets the file that the given waypoint references. 
	 * @param waypoint the waypoint to change.
	 * @param file the file to set.
	 * @return the status indicating whether or not the set operation occurred.
	 */
	
	public static TagSEAChangeStatus setFile(IWaypoint waypoint, IFile file) {
		return waypoint.setStringValue(IResourceInterfaceAttributes.ATTR_RESOURCE, file.getFullPath().toPortableString());
	}
	
	/**
	 * Sets the offset in the file that the given waypoint references. NOTE: this method
	 * is not for use outside of the Java Waypoints platform. It will always fail to apply
	 * the change when used outside the Java Waypoints platform.
	 * @param waypoint the waypoint to change.
	 * @param offset the offset to set.
	 * @return the status of whether or not the change could occurr.
	 */
	public static TagSEAChangeStatus setOffset(IWaypoint waypoint, int offset) {
		return waypoint.setIntValue(ITextWaypointAttributes.ATTR_CHAR_START, offset);
		
	}
	
	/**
	 * Sets the character in the file that the given waypoint references. NOTE: this method
	 * is not for use outside of the Java Waypoints platform. It will always fail to apply
	 * the change when used outside the Java Waypoints platform.
	 * @param waypoint the waypoint to change.
	 * @param end the end to set.
	 * @return the status of whether or not the change could occurr.
	 */
	public static TagSEAChangeStatus setEnd(IWaypoint waypoint, int end) {
		return waypoint.setIntValue(ITextWaypointAttributes.ATTR_CHAR_END, end);
	}
	
	/**
	 * Returns a document referece for the underlying file reference by the waypoint.
	 * @param waypoint the waypoint.
	 * @return a document referece for the underlying file reference by the waypoint.
	 */
	public static IDocument getDocument(IWaypoint waypoint) {
		IFile file = getFile(waypoint);
		IDocument document = null;
		if (file != null) {
			ITextFileBuffer buffer  =
				FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getLocation());
			if (buffer != null) {
				document = buffer.getDocument();
			} else {
				try {
					FileBuffers.getTextFileBufferManager().connect(file.getLocation(), new NullProgressMonitor());
					buffer = FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getLocation());
					if (buffer != null) {
						document = buffer.getDocument();
					}
					FileBuffers.getTextFileBufferManager().disconnect(file.getLocation(), new NullProgressMonitor());
				} catch (CoreException e) {
				}
				
			}
		}
		return document;
	}
	
	/**
	 * Returns the full text within the source code for the given waypoint.
	 * @param waypoint the waypoint.
	 * @return the full text within the source code for the given waypoint.
	 */
	public static String getWaypointText(IWaypoint waypoint) {
		IDocument document = getDocument(waypoint);
		if (document != null) {
			int offset = getOffset(waypoint);
			int length = getLength(waypoint);
			if (offset != -1 && length != -1) {
				try {
					return document.get(offset, length);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		} else {
			//System.out.println("document null");
		}
		return null;
	}
	



	/**
	 * Returns the waypoint for the given marker if it exists. Null otherwise.
	 * @param marker the marker to check.
	 * @return the waypoint for the given marker if it exists. Null otherwise.
	 */
	public static IWaypoint getWaypointForMarker(IMarker marker) {
		return ((CWaypointDelegate)CWaypointsPlugin.getCWaypointDelegate()).getWaypointForMarker(marker);
	}
	
	/**
	 * Returns the marker for the given waypoint if it exists. Null otherwise.
	 * @param waypoint the waypoint to check.
	 * @return the marker for the given waypoint if it exists. Null otherwise.
	 */
	public static IMarker getMarkerForWaypoint(IWaypoint waypoint) {
		return ((CWaypointDelegate)CWaypointsPlugin.getCWaypointDelegate()).getMarkerForWaypoint(waypoint);
	}


	/**
	 * Resturns the waypoints that currently exist on the given file.
	 * @param javaFile
	 * @return
	 */
	public static IWaypoint[] getWaypointsForFile(IFile javaFile) {
		IMarker[] markers;
		try {
			markers = javaFile.findMarkers(ICWaypointsConstants.WAYPOINT_MARKER, false, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			return new IWaypoint[0];
		}
		List<IWaypoint> waypoints = new LinkedList<IWaypoint>();
		for (IMarker marker : markers) {
			IWaypoint waypoint = getWaypointForMarker(marker);
			if (waypoint != null) {
				waypoints.add(waypoint);
			}
		}
		return waypoints.toArray(new IWaypoint[waypoints.size()]);
	}
	
	/**
	 * Checks the extension of the given file to see if it is an acceptable file to parse.
	 * @param file
	 * @return
	 */
	public static boolean isCFile(IFile file) {
		//check source files and headers.
		String[] extensions = new String[] {"c", "c++", "cpp", "cp", "h", "hpp", "h++", "hp"};
		for (String s : extensions) {
			if (s.equals(file.getFileExtension())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks the extension of the given file to see if it is an acceptable file to parse.
	 * @param file
	 * @return
	 */
	public static boolean isCFile(IPath file) {
		//check source files and headers.
		String[] extensions = new String[] {"c", "c++", "cpp", "cp", "h", "hpp", "h++", "hp"};
		for (String s : extensions) {
			if (s.equals(file.getFileExtension())) {
				return true;
			}
		}
		return false;
	}

}
