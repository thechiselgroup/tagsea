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
package net.sourceforge.tagsea.java.waypoints;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.java.IJavaWaypointsConstants;
import net.sourceforge.tagsea.java.JavaTagsPlugin;
import net.sourceforge.tagsea.java.JavaWaypointUtils;
import net.sourceforge.tagsea.java.documents.internal.NewWaypointDefinitionExtractor;
import net.sourceforge.tagsea.java.resources.internal.WaypointResourceUtil;
import net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointParser;
import net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo;
import net.sourceforge.tagsea.java.waypoints.parser.JavaWaypointParserFactory;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

class DocumentChangeOperation extends JavaWaypointDelegate.InternalOperation {

	private DocumentEvent event;
	private ITextFileBuffer filebuffer;

	public DocumentChangeOperation(DocumentEvent event, ITextFileBuffer fileBuffer) {
		super("Synchronizing java waypoints to documents...");
		this.event = new DocumentEvent(event.getDocument(), event.getOffset(), event.getLength(), event.getText());
		//@tag tagsea.hack.eclipse -author="Del Myers" -date="enCA:16/01/07" : hack for a bug in Eclipse 3.2.x. Fixed in 3.3 stream.
		if (this.event.fText == null)
			this.event.fText = "";
		this.filebuffer = fileBuffer;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ITagSEAOperation#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
		IFile file = FileBuffers.getWorkspaceFileAtLocation(filebuffer.getLocation());
		MultiStatus status = new MultiStatus(JavaTagsPlugin.PLUGIN_ID, 0, "", null);
		if (file != null && file.exists()) {
			IWaypoint[] waypoints = JavaTagsPlugin.getJavaWaypointsForFile(file);
			boolean refreshRegion = true;
			List<IWaypoint> intersectingWaypoints = new LinkedList<IWaypoint>();

			for (IWaypoint waypoint : waypoints) {
				int offset = JavaWaypointUtils.getOffset(waypoint);
				int length = JavaWaypointUtils.getLength(waypoint);
				int end = offset + length;
				//adjust the end to the end-of line. This way we can get all the changes.
				int offsetOfLine;
				int lineLength;
				int line;
				boolean startsLine = false;
				try {
					line = event.fDocument.getLineOfOffset(offset);
					offsetOfLine = event.fDocument.getLineOffset(line);
					lineLength = event.fDocument.getLineLength(line);
					String[] delims = event.fDocument.getLegalLineDelimiters();
					String delim = "";
					for (int i = 0; i < delims.length && !startsLine ; i++) {
						startsLine = event.fText.startsWith(delims[i]);
						if (startsLine)
							delim = delims[i];
					}
					end = offsetOfLine + lineLength - delim.length();
				} catch (BadLocationException e) {
					//@tag tagsea.bug.14.fix -author="Del Myers" -date="enCA:18/01/07" : at least one waypoint is now missing in the file. Update all waypoints.
					return updateForNew(file);	
				}
				
				if (event.fText == null)
					event.fText = "";
				if (event.fOffset > end || (end == event.fOffset && startsLine)) {
					if (end == event.fOffset)
						intersectingWaypoints.add(waypoint);
					continue;
				} else if (event.fOffset + event.fLength < offset) {
					offset += event.fText.length()-event.fLength;
					int oldLength = JavaWaypointUtils.getLength(waypoint);
					JavaWaypointUtils.setOffset(waypoint, offset);
					JavaWaypointUtils.setEnd(waypoint, offset+oldLength);
					
				} else if ((event.fOffset >= offset && event.fLength+event.fOffset <= end)){
					//the change is contained in a single waypoint. Change only that one.
					refreshRegion = false;
					//inside = true;
					IRegion[] rs = NewWaypointDefinitionExtractor.internalGetWaypointRegions(event.fDocument, offset, end-offset);
					if (rs == null || rs.length != 1) {
						
//						delete the waypoint
						TagSEAPlugin.getWaypointsModel().removeWaypoint(waypoint);
						continue;
					}
					try {
						removeProblems(rs[0], file);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
					try {
						String text = event.fDocument.get(rs[0].getOffset(), rs[0].getLength());
						IJavaWaypointParser parser = JavaWaypointParserFactory.createParser();
						IParsedJavaWaypointInfo info = parser.parse(text, rs[0].getOffset());
						status.merge(updateWaypoint(waypoint, info, file));
					} catch (BadLocationException e) {
						throw new InvocationTargetException(e);
					}
				} else {
					if ((event.fOffset >= offset && event.fOffset <= end) || 
						(event.fOffset+event.fLength >= offset && event.fOffset+event.fLength <= end) ||
						(event.fOffset <= offset && event.fOffset+event.fLength >= end)) {
						//add an intersection
						intersectingWaypoints.add(waypoint);
					}
				}
			}
			if (refreshRegion) {
				//if (!"".equals(event.getText().trim())) //don't care about empty space
					//refreshForRegion(event.fDocument, event.fOffset, event.fText.length(), event.fLength, file, intersectingWaypoints);
				status.add(updateForNew(file));
			}
		}
		return status;
	}

	
	private IStatus updateForNew(IFile file) {
		class RegionComparator implements Comparator<IRegion> {
			public int compare(IRegion o1, IRegion o2) {
				int diff = o1.getOffset() - o2.getOffset();
				if (diff == 0) {
					diff = o1.getLength() - o2.getLength();
				}
				return diff;
			}
		}
		class WaypointComparator implements Comparator<IWaypoint> {
			public int compare(IWaypoint o1, IWaypoint o2) {
				int off1 = JavaWaypointUtils.getOffset(o1);
				int off2 = JavaWaypointUtils.getOffset(o2);
				int diff = off1-off2;
				if (diff == 0) {
					int l1 = JavaWaypointUtils.getLength(o1);
					int l2 = JavaWaypointUtils.getLength(o2);
					diff = l1-l2;
				}
				return diff;
			}
			
		}
		MultiStatus status = new MultiStatus(JavaTagsPlugin.PLUGIN_ID, IStatus.OK, "", null);
		RegionComparator comparator = new RegionComparator();
		IRegion[] newRegions = NewWaypointDefinitionExtractor.getWaypointRegions(event.fDocument);
		Arrays.sort(newRegions, comparator);
		ArrayList<IRegion> waypointRegions = new ArrayList<IRegion>();
		IWaypoint[] waypoints = JavaTagsPlugin.getJavaWaypointsForFile(file);
		for (IWaypoint waypoint : waypoints) {
			waypointRegions.add(new Region(JavaWaypointUtils.getOffset(waypoint), JavaWaypointUtils.getLength(waypoint)));
		}
		Collections.sort(waypointRegions, comparator);
		Arrays.sort(waypoints, new WaypointComparator());
		int i = 0;
		int j = 0;
		IJavaWaypointParser parser = JavaWaypointParserFactory.createParser();
		while ((i < newRegions.length) && (j < waypointRegions.size())) {
			IRegion nr = newRegions[i];
			IRegion wr = waypointRegions.get(j);
			int diff = comparator.compare(nr, wr);
			if (diff == 0) {
				i++;
				j++;
			} else if (diff < 0){
				status.merge(createWaypointForRegion(parser, nr, file));
				i++;
			} else if (diff > 0) {
				status.merge(waypoints[j].delete().getStatus());
				j++;
			}
		}
		while (i < newRegions.length) {
			IRegion nr = newRegions[i];
			status.add(createWaypointForRegion(parser, nr, file));
			i++;
		}
		while (j < waypoints.length) {
			status.add(waypoints[j].delete().getStatus());
			j++;
		}
		return status;
	}
	
	private IStatus createWaypointForRegion(IJavaWaypointParser parser, IRegion region, IFile file) {
		try {
			removeProblems(region, file);
			String text = event.fDocument.get(region.getOffset(), region.getLength());
			WaypointResourceUtil.createWaypointForInfo(parser.parse(text, region.getOffset()), file);
		} catch (CoreException e) {
			return e.getStatus();
		} catch (BadLocationException e) {
			return new Status(IStatus.WARNING, JavaTagsPlugin.PLUGIN_ID,IStatus.WARNING, e.getMessage(), e);
		}
		return Status.OK_STATUS;
	}
	

	/**
	 * @param waypoint
	 * @param info
	 */
	private IStatus updateWaypoint(IWaypoint waypoint, IParsedJavaWaypointInfo info, IFile file) {
		if (info.getProblems().length != 0) {
			WaypointResourceUtil.createProblemsForInfo(info, file);
			TagSEAPlugin.getWaypointsModel().removeWaypoint(waypoint);
			return Status.OK_STATUS;
		}
		ITag[] tags = waypoint.getTags();
		String[] newTags = info.getTags();
		Arrays.sort(tags, new Comparator<ITag>(){
			public int compare(ITag o1, ITag o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		int i = 0;
		int j = 0;
		LinkedList<ITag> oldTagsList = new LinkedList<ITag>();
		LinkedList<String> newTagsList = new LinkedList<String>();
		for (i = 0; i < tags.length && newTags.length != 0; i++) {
			if (newTags[j].compareTo(tags[i].getName()) < 0) {
				newTagsList.add(newTags[j]);
				i--;
				j++;
			} else if (newTags[j].compareTo(tags[i].getName()) == 0) {
				j++;
			} else {
				oldTagsList.add(tags[i]);
			}
			if (j >= newTags.length) {i++; break;}
		}
		for (; i < tags.length; i++) {
			oldTagsList.add(tags[i]);
		}
		for (; j < newTags.length; j++) {
			newTagsList.add(newTags[j]);
		}
		//make sure that the default tag stays in the list
		MultiStatus status = new MultiStatus(JavaTagsPlugin.PLUGIN_ID, IStatus.OK, "", null);
		ITag defaultTag = TagSEAPlugin.getTagsModel().getTag(ITag.DEFAULT);
		oldTagsList.remove(defaultTag);
		for (ITag old : oldTagsList) {
			status.add(waypoint.removeTag(old).getStatus());
		}
		for (String newT : newTagsList) {
			ITag tag = waypoint.addTag(newT);
			if (tag == null) {
				status.merge(new Status(IStatus.WARNING, JavaTagsPlugin.PLUGIN_ID, IStatus.WARNING, "Could not add tag " + newT, null));
			}
		}
		
		status.merge(JavaWaypointUtils.setOffset(waypoint, info.getOffset()).getStatus());
		status.merge(JavaWaypointUtils.setEnd(waypoint, info.getOffset() + info.getWaypointData().length()).getStatus());
		String author = info.getAuthor();
		Date date = info.getDate();
		String javaElement = null;

		if (author != null) {
			status.merge(waypoint.setAuthor(author).getStatus());
		}
		if (date != null) {
			status.merge(waypoint.setDate(date).getStatus());
		}
		if (javaElement != null) {
			//find a fancy way to put in the java element.
		}
		status.merge(waypoint.setText(info.getDescription()).getStatus());
		return status;
	}

	
	/**
	 * @param rs
	 * @throws CoreException 
	 */
	private void removeProblems(IRegion region, IFile file) throws CoreException {
		IMarker[] markers = file.findMarkers(IJavaWaypointsConstants.WAYPOINT_PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
		int regionEnd = region.getOffset() + region.getLength();
		for (IMarker marker : markers) {
			int start = marker.getAttribute(IMarker.CHAR_START, -1);
			int end = marker.getAttribute(IMarker.CHAR_END, -1);
			if ((start <= region.getOffset() && end <= regionEnd && end >= region.getOffset()) || 
				(start >= region.getOffset() && start <= regionEnd && end >= region.getOffset())) {
				marker.delete();
			}
		}
	}
}