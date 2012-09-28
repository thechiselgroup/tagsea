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
package net.sourceforge.tagsea.c.resources.internal;

import java.util.Date;
import java.util.HashMap;

import net.sourceforge.tagsea.TagSEAModelException;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.c.ICWaypointsConstants;
import net.sourceforge.tagsea.c.waypoints.parser.ICWaypointInfo;
import net.sourceforge.tagsea.c.waypoints.parser.IParsedCWaypointInfo;
import net.sourceforge.tagsea.c.waypoints.parser.WaypointParseProblem;
import net.sourceforge.tagsea.core.ITextWaypointAttributes;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.resources.IResourceInterfaceAttributes;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 * Creates a waypoint for the given java waypoint info.
 * @author Del Myers
 */

public class WaypointResourceUtil {
	
	/**
	 * Creates a waypoint for the given info and returns it, or null if it couldn't
	 * be created.
	 * @param info
	 * @param file
	 * @return the created waypoint.
	 * @throws TagSEAModelException
	 */
	public static IWaypoint createWaypointForInfo(ICWaypointInfo info, IFile file) {
		if (info instanceof IParsedCWaypointInfo) {
			IParsedCWaypointInfo javaInfo = (IParsedCWaypointInfo) info;
			if (javaInfo.getProblems().length != 0) {
				createProblemsForInfo(javaInfo, file);
				return null;
			}
		}
		
		IWaypoint wp = TagSEAPlugin.getWaypointsModel().createWaypoint(ICWaypointsConstants.C_WAYPOINT, info.getTags());
		if (wp != null) {
			int offset = info.getOffset();
			int end = info.getLength() + offset;
			String filePath = file.getFullPath().toPortableString();
			String author = info.getAuthor();
			Date date = info.getDate();
			String comment = info.getDescription();
			ITextFileBuffer buffer = FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getLocation());
			if (buffer != null) {
				IDocument doc = buffer.getDocument();
				if (doc != null) {
					int line = -1;
					try {
						line = doc.getLineOfOffset(offset);
					} catch (BadLocationException e) {
					}
					wp.setIntValue(ITextWaypointAttributes.ATTR_LINE, line);
				}
			}
			if (author != null) {
				wp.setAuthor(author);
			}
			if (date != null) {
				wp.setDate(date);
			}
	
			if (comment != null) {
				wp.setText(comment);
			}
			wp.setIntValue(ITextWaypointAttributes.ATTR_CHAR_START, offset);
			wp.setIntValue(ITextWaypointAttributes.ATTR_CHAR_END, end);
			wp.setStringValue(IResourceInterfaceAttributes.ATTR_RESOURCE, filePath);
		}
		return wp;
		
	}
	



	@SuppressWarnings({"unchecked"}) //$NON-NLS-1$
	public static void createProblemsForInfo(IParsedCWaypointInfo info, IFile file) {
		for (WaypointParseProblem problem : info.getProblems()) {
			int offset = problem.region.getOffset() + info.getOffset();
			int length = problem.region.getLength();
			HashMap attributes = new HashMap();
			attributes.put(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			attributes.put(IMarker.CHAR_START, offset);
			attributes.put(IMarker.CHAR_END, offset+length);
			attributes.put(IMarker.MESSAGE, problem.message);
			try {
				MarkerUtilities.createMarker(file, attributes, ICWaypointsConstants.WAYPOINT_PROBLEM_MARKER);
				file.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
