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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.java.JavaTagsPlugin;
import net.sourceforge.tagsea.java.JavaWaypointUtils;
import net.sourceforge.tagsea.java.documents.internal.DocumentWriteSession;
import net.sourceforge.tagsea.java.resources.internal.FileWaypointRefreshJob;
import net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointParser;
import net.sourceforge.tagsea.java.waypoints.parser.IParsedJavaWaypointInfo;
import net.sourceforge.tagsea.java.waypoints.parser.JavaWaypointParserFactory;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditProcessor;

/**
 * An operation that responds to waypoint changes to refactor source code.
 * @author Del Myers
 */

//note: this class is assumed to be run internally within the java waypoint delegate, so it does its own updating of waypoints.
public class ModelChangeOperation implements IRunnableWithProgress {
	
	private HashMap<IFile, List<IWaypointChangeEvent>> fileWaypointMap;
	private IWaypointChangeEvent[] changes;
	
	private class EventComparator implements Comparator<IWaypointChangeEvent> {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(IWaypointChangeEvent o1, IWaypointChangeEvent o2) {
			int off1 = JavaWaypointUtils.getOffset(o1.getWaypoint());
			int off2 = JavaWaypointUtils.getOffset(o2.getWaypoint());
			return off1 - off2;
		}
	}
	
	/**
	 * Compares java waypoint infos to sort them by offset. This is so that they can
	 * consistently changed.
	 * @author Del Myers
	 */
	private class InfoComparator implements Comparator<MutableJavaWaypointInfo> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(MutableJavaWaypointInfo o1, MutableJavaWaypointInfo o2) {
			return o1.getOffset() - o2.getOffset();
		}
		
	}

	public ModelChangeOperation(IWaypointChangeEvent[] events) {
		this.changes = events;
		
		fileWaypointMap = new HashMap<IFile, List<IWaypointChangeEvent>>();
	}
	
	public void run(IProgressMonitor monitor) {
//		sort waypoints by the files that they are on.
		for (IWaypointChangeEvent event : changes) {
			IFile file = JavaWaypointUtils.getFile(event.getWaypoint());
			if (file != null && file.exists()) {
				List<IWaypointChangeEvent> list = fileWaypointMap.get(file);
				if (list == null) {
					list = new LinkedList<IWaypointChangeEvent>();
					fileWaypointMap.put(file, list);
				}
				list.add(event);
			}
		}
		monitor.beginTask(Messages.getString("ModelChangeOperation.refactoring"), fileWaypointMap.size()*2); //$NON-NLS-1$
		//sort the events by offset so that it will be easier to adjust the location
		//of the waypoints afterwards. Note that the collections sorter is stable
		//which is important for maintaining order of operations on a single waypoint.
		//Also note that waypoints cannot overlap. This is very important for the sorting.
		for (List<IWaypointChangeEvent> list : fileWaypointMap.values()) {
			Collections.sort(list, new EventComparator());
		}
		LinkedList<IFile> filesToRefresh = new LinkedList<IFile>();
		for (IFile file : fileWaypointMap.keySet()) {
			monitor.subTask(file.getName());
			try {
				if(refactorFile(file));
				filesToRefresh.add(file);
			} catch (CoreException e) {
				//@tag tagsea.todo : we should do something smart with this.
				e.printStackTrace();
			} catch (IllegalStateException e) {
				//quit
				monitor.done();
				return;
			}
			monitor.worked(1);
		}
		if (filesToRefresh.size() > 0) {
			FileWaypointRefreshJob refresher = new FileWaypointRefreshJob(filesToRefresh);
			IStatus status = TagSEAPlugin.syncRun(refresher, new SubProgressMonitor(monitor, filesToRefresh.size()));
			if (!status.isOK()) {
				JavaTagsPlugin.getDefault().getLog().log(status);
			}
		}
		monitor.done();
	}

/**
	 * Refactors a file for the given changes.
	 * @param file
	 * @throws CoreException
	 * @return true if the file was refactored. 
	 */
	private boolean refactorFile(IFile file) throws CoreException {
		if (file.exists()) {
			List<IWaypointChangeEvent> events = fileWaypointMap.get(file);
			//get a document for the file.
			ITextFileBuffer buffer = 
				FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
			if (buffer != null && buffer.isDirty()) {
				//have to quit, we can't refactor on a dirty file.
				return false;
			}
			HashMap<IWaypoint, MutableJavaWaypointInfo> waypointInfoMap = 
				new HashMap<IWaypoint, MutableJavaWaypointInfo>();
			HashMap<MutableJavaWaypointInfo, IWaypoint> infoWaypointMap =
				new HashMap<MutableJavaWaypointInfo, IWaypoint>();
			for (IWaypointChangeEvent event : events) {
				IWaypoint waypoint = event.getWaypoint();
				MutableJavaWaypointInfo info = waypointInfoMap.get(waypoint);
				if (info == null) {
					info = createInfoForWaypoint(waypoint);
					waypointInfoMap.put(waypoint, info);
					infoWaypointMap.put(info, waypoint);
				}
				applyChange(info, event);
			}
			
			List<MutableJavaWaypointInfo> infos = new ArrayList<MutableJavaWaypointInfo>(waypointInfoMap.values());
			Collections.sort(infos, new InfoComparator());
			DocumentWriteSession session = new DocumentWriteSession(file);
			try {
				IDocument document = session.openDocument();
				for (int i = 0; i < infos.size(); i++) {
					//@tag tagsea.java.todo : if we want to try to keep consistency with the current waypoints, we should update all waypoints here.
					MutableJavaWaypointInfo info = infos.get(i);
					try {
						int diff = applyToDocument(info, infoWaypointMap.get(info), document);
						if (diff != 0) {
							for (int j = i+1; j < infos.size(); j++) {
								MutableJavaWaypointInfo nextInfo = infos.get(j);
								int offsetInDocument = nextInfo.getOffset();
								nextInfo.setOffsetInText(offsetInDocument+diff);
							}
						}
					} catch (MalformedTreeException e) {
						//@tag tagsea.todo : do some sort of smart processing with this.
						e.printStackTrace();
					} catch (BadLocationException e) {
//						@tag tagsea.todo : do some sort of smart processing with this.
						e.printStackTrace();
					}
				}

				//update the file.
			} catch (IllegalStateException e){
				throw e;
			} finally {
				session.closeDocument();
			}
			return true;
		}
		return false;
		
		
	}

	/**
	 * Applies the changes to the info to the given document.
	 * @param info
	 * @param waypoint 
	 * @param document
	 * @return the difference in lenght of the entire region after the edit occurs.
	 * @throws BadLocationException 
	 * @throws MalformedTreeException 
	 */
	private int applyToDocument(MutableJavaWaypointInfo info, IWaypoint waypoint, IDocument document) throws MalformedTreeException, BadLocationException {
//		MultiTextEdit multiEdit = new MultiTextEdit();
//		Iterator<TextReplacement> replacements = info.getReplacementIterator();
//		int diff = 0;
//		while (replacements.hasNext()) {
//			TextReplacement replacement = replacements.next();
//			diff += (replacement.text.length() - replacement.length);
//			multiEdit.addChild(new ReplaceEdit(replacement.offset+info.getOffset(), replacement.length, replacement.text));
//		}
		if (info.isDeleted()) {
			return applyDelete(info, waypoint, document);
		}

		return simpleReplace(info, waypoint, document);
	}

	/**
	 * @param info
	 * @param waypoint
	 * @param document
	 */
	//@tag tagsea.bug.42.fix -author="Del Myers" -date="enCA:07/02/07"
	private int applyDelete(MutableJavaWaypointInfo info, IWaypoint waypoint, IDocument document) {
		//scan the area around the waypoint to see if only the waypoint should be deleted, or the entire line.
		int length;
		try {
			char lastChar = 0;
			int offset = info.getOffset() - 1;
			int line = document.getLineOfOffset(offset);
			int currentLine = line;
			while (offset >= 1 && currentLine == line) {
				char c = document.getChar(offset);
				if (c == '/') {
					//if the last character was a '*', we have to be careful to make sure that we don't 
					//erase the beginning of a multi-line comment.
					if (lastChar == '*') {
						//check the character just before the current character to see if it is a '/' 
						//if so, we can continue.
						if (offset != 1) {
							char nextChar = document.getChar(offset-1);
							if (nextChar != '/') {
								return simpleReplace(info, waypoint, document);
							}
						} else {
							return simpleReplace(info, waypoint, document);
						}
					}
				}
				if (c != '*' && c != '/' && !Character.isWhitespace(c)) {
					return simpleReplace(info, waypoint, document);
				}
				offset--;
				lastChar = c;
				if (offset > 0) {
					currentLine = document.getLineOfOffset(offset);
				}
			}
			//we've gotten to the beginning of the line, erase.
			IRegion lineRegion = document.getLineInformationOfOffset(info.getOffset());
			String delim = document.getLineDelimiter(line);
			length = lineRegion.getLength()+delim.length();
			ReplaceEdit edit = new ReplaceEdit(lineRegion.getOffset(), length, info.getWaypointData());
			final TextEditProcessor processor = new TextEditProcessor(document, edit, TextEdit.UPDATE_REGIONS);
			//multiEdit.apply(document, TextEdit.UPDATE_REGIONS);
			Display.getDefault().syncExec(new Runnable(){
				public void run() {
					try {
						processor.performEdits();
					} catch (MalformedTreeException e) {
						e.printStackTrace();
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (BadLocationException e) {
			return simpleReplace(info, waypoint, document);
		}		
		return -length;
	}

	/**
	 * @param info
	 * @param waypoint
	 * @param document
	 * @return
	 */
	private int simpleReplace(MutableJavaWaypointInfo info, IWaypoint waypoint, IDocument document) {
		int oldLength = JavaWaypointUtils.getLength(waypoint);
		int diff = info.getWaypointData().length() - oldLength;
		ReplaceEdit edit = new ReplaceEdit(info.getOffset(), oldLength, info.getWaypointData());
		final TextEditProcessor processor = new TextEditProcessor(document, edit, TextEdit.UPDATE_REGIONS);
		//multiEdit.apply(document, TextEdit.UPDATE_REGIONS);
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				try {
					processor.performEdits();
				} catch (MalformedTreeException e) {
					e.printStackTrace();
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
		return diff;
	}

	/**
	 * Applys the change described in the event to the mutable waypoint info. 
	 * @param info
	 * @param event
	 * @param buffer
	 */
	private void applyChange(MutableJavaWaypointInfo info, IWaypointChangeEvent event) {
		switch (event.getType()) {
		case IWaypointChangeEvent.CHANGE:
			handleApplyAttributeChanges(info, event);
			break;
		case IWaypointChangeEvent.DELETE:
			handleApplyDelete(info, event);
			break;
		case IWaypointChangeEvent.TAG_NAME_CHANGED:
			handleApplyTagNameChange(info, event);
			break;
		case IWaypointChangeEvent.TAGS_CHANGED:
			handleApplyTagsChange(info, event);
			break;
		}
	}

	/**
	 * @param info
	 * @param event
	 */
	private void handleApplyTagsChange(MutableJavaWaypointInfo info, IWaypointChangeEvent event) {
		TreeSet<String> oldTags = new TreeSet<String>(Arrays.asList(event.getOldTags()));
		TreeSet<String> newTags = new TreeSet<String>(Arrays.asList(event.getNewTags()));
		List<String> toDelete = new LinkedList<String>();
		List<String> toAdd = new LinkedList<String>();
		Iterator<String> oldIt = oldTags.iterator();
		Iterator<String> newIt = newTags.iterator();
		boolean done = !(oldIt.hasNext() && newIt.hasNext());
		String newT=null, oldT=null;
		if (!done) {
			newT = newIt.next();
			oldT = oldIt.next();
		}
		while (!done) {
			while (!done && (newT.compareTo(oldT) == 0)) {
				if (oldIt.hasNext()) {
					oldT = oldIt.next();
				} else {
					oldT = null;
				}
				if (newIt.hasNext()) {
					newT = newIt.next();
				} else {
					newT = null;
				}
				done = (newT == null || oldT == null);
			}
			while (!done && (newT.compareTo(oldT)< 0)) {
				toAdd.add(newT);
				if (newIt.hasNext()) {
					newT = newIt.next();
				} else {
					newT = null;
					done = true;
					break;
				}
			}
			while (!done && (newT.compareTo(oldT) > 0)) {
				toDelete.add(oldT);
				if (oldIt.hasNext()) {
					oldT = oldIt.next();
				} else {
					oldT = null;
					done = true;
					break;
				}
			}
			
			
		}
		if (newT != null) {
			toAdd.add(newT);
		}
		if (oldT != null) {
			toDelete.add(oldT);
		}
		while (oldIt.hasNext()) {
			toDelete.add(oldIt.next());
		}
		while (newIt.hasNext()) {
			toAdd.add(newIt.next());
		}
		for (String tag : toDelete) {
			info.removeTag(tag);
		}
		for (String tag : toAdd) {
			info.addTag(tag);
		}
	}

	/**
	 * @param info
	 * @param event
	 */
	private void handleApplyTagNameChange(MutableJavaWaypointInfo info, IWaypointChangeEvent event) {
		info.changeTag(event.getOldTagName(), event.getNewTagName());		
	}

	/**
	 * @param info
	 * @param event
	 */
	private void handleApplyDelete(MutableJavaWaypointInfo info, IWaypointChangeEvent event) {
		info.delete();
	}

	/**
	 * @param info
	 * @param event
	 */
	private void handleApplyAttributeChanges(MutableJavaWaypointInfo info, IWaypointChangeEvent event) {
		String[] attrs = event.getChangedAttributes();
		for (String attr : attrs) {
			Object value = event.getNewValue(attr);
			if (IWaypoint.ATTR_DATE.equals(attr)) {
				info.setDate((Date)value);
			} else {
				//all of the attributes are simple types, toString should give a good string value.
				info.setAttribute(attr, value.toString());
			}
		}
	}

	/**
	 * @param waypoint
	 * @return
	 */
	private MutableJavaWaypointInfo createInfoForWaypoint(IWaypoint waypoint) {
		String text = JavaWaypointUtils.getWaypointText(waypoint);
		IJavaWaypointParser parser = JavaWaypointParserFactory.createParser();
		IParsedJavaWaypointInfo parsedInfo = parser.parse(text, JavaWaypointUtils.getOffset(waypoint));
		//System.out.println(text);
		//@tag tagsea.todo : get the preferred style from a preference.
		return new MutableJavaWaypointInfo(parsedInfo, MutableJavaWaypointInfo.TagStyle.DOT);
	}

}
