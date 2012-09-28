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
package net.sourceforge.tagsea.java.resources.internal;

import java.util.Date;
import java.util.HashMap;

import net.sourceforge.tagsea.TagSEAModelException;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.java.IJavaWaypointAttributes;
import net.sourceforge.tagsea.java.IJavaWaypointsConstants;
import net.sourceforge.tagsea.java.JavaWaypointUtils;
import net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo;
import net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo;
import net.sourceforge.tagsea.java.waypoints.parser.WaypointParseProblem;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
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
	public static IWaypoint createWaypointForInfo(IJavaWaypointInfo info, IFile file) {
		if (info instanceof IParsedJavaWaypointInfo) {
			IParsedJavaWaypointInfo javaInfo = (IParsedJavaWaypointInfo) info;
			if (javaInfo.getProblems().length != 0) {
				createProblemsForInfo(javaInfo, file);
				return null;
			}
		}
		
		IWaypoint wp = TagSEAPlugin.getWaypointsModel().createWaypoint(IJavaWaypointsConstants.JAVA_WAYPOINT, info.getTags());
		if (wp != null) {
			int offset = info.getOffset();
			int end = info.getLength() + offset;
			String filePath = file.getFullPath().toPortableString();
			String author = info.getAuthor();
			Date date = info.getDate();
			String comment = info.getDescription();
			String javaElement = null;
			ITextFileBuffer buffer = FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
			if (buffer != null) {
				IDocument doc = buffer.getDocument();
				if (doc != null) {
					int line = -1;
					try {
						line = doc.getLineOfOffset(offset);
					} catch (BadLocationException e) {
					}
					wp.setIntValue(IJavaWaypointAttributes.ATTR_LINE, line);
				}
			}
			if (author != null) {
				wp.setAuthor(author);
			}
			if (date != null) {
				wp.setDate(date);
			}
			try {
				javaElement = findJavaElement(offset, end-offset, file);
			} catch (Exception e) {
				javaElement = "";
			}
			wp.setStringValue(IJavaWaypointAttributes.ATTR_JAVA_ELEMENT, javaElement);
			if (comment != null) {
				wp.setText(comment);
			}
			wp.setIntValue(IJavaWaypointAttributes.ATTR_CHAR_START, offset);
			wp.setIntValue(IJavaWaypointAttributes.ATTR_CHAR_END, end);
			wp.setStringValue(IJavaWaypointAttributes.ATTR_RESOURCE, filePath);
		}
		return wp;
		
	}
	
	/**
	 * Gets a string representation of the java element.
	 * @param offset
	 * @param length
	 * @param file
	 * @return
	 * @throws CoreException 
	 * @throws BadLocationException 
	 */
	private static String findJavaElement(int offset, int length, IFile file) throws CoreException, BadLocationException {
		ICompilationUnit unit = JavaCore.createCompilationUnitFrom(file);
		if (!unit.isOpen()) {
			unit.open(new NullProgressMonitor());
		}
		if (unit.isOpen()) {
			IJavaElement element =  unit.getElementAt(offset);
			if (element == null) {
				element = unit.getTypes()[0];
			}
			return JavaWaypointUtils.getStringForElement(element);
		}
		return "";
	}


	@SuppressWarnings({"unchecked"}) //$NON-NLS-1$
	public static void createProblemsForInfo(IParsedJavaWaypointInfo info, IFile file) {
		for (WaypointParseProblem problem : info.getProblems()) {
			int offset = problem.region.getOffset() + info.getOffset();
			int length = problem.region.getLength();
			HashMap attributes = new HashMap();
			attributes.put(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			attributes.put(IMarker.CHAR_START, offset);
			attributes.put(IMarker.CHAR_END, offset+length);
			attributes.put(IMarker.MESSAGE, problem.message);
			try {
				MarkerUtilities.createMarker(file, attributes, IJavaWaypointsConstants.WAYPOINT_PROBLEM_MARKER);
				file.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
