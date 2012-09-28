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
package net.sourceforge.tagsea.c.waypoints;

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

import net.sourceforge.tagsea.c.CWaypointUtils;
import net.sourceforge.tagsea.c.CWaypointsPlugin;
import net.sourceforge.tagsea.c.documents.internal.DocumentWriteSession;
import net.sourceforge.tagsea.c.resources.internal.FileWaypointRefreshJob;
import net.sourceforge.tagsea.c.waypoints.parser.CWaypointParserFactory;
import net.sourceforge.tagsea.c.waypoints.parser.ICWaypointParser;
import net.sourceforge.tagsea.c.waypoints.parser.IParsedCWaypointInfo;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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
			int off1 = CWaypointUtils.getOffset(o1.getWaypoint());
			int off2 = CWaypointUtils.getOffset(o2.getWaypoint());
			return off1 - off2;
		}
	}
	
	/**
	 * Compares java waypoint infos to sort them by offset. This is so that they can
	 * consistently changed.
	 * @author Del Myers
	 */
	private class InfoComparator implements Comparator<MutableCWaypointInfo> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(MutableCWaypointInfo o1, MutableCWaypointInfo o2) {
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
			IFile file = CWaypointUtils.getFile(event.getWaypoint());
			if (file != null && file.exists()) {
				List<IWaypointChangeEvent> list = fileWaypointMap.get(file);
				if (list == null) {
					list = new LinkedList<IWaypointChangeEvent>();
					fileWaypointMap.put(file, list);
				}
				list.add(event);
			}
		}
		monitor.beginTask(Messages.getString("ModelChangeOperation.refactoring"), fileWaypointMap.size()); //$NON-NLS-1$
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
			CWaypointDelegate delegate = 
				(CWaypointDelegate) CWaypointsPlugin.getCWaypointDelegate();
			delegate.internalRun(refresher, true);
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
				FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getLocation());
			if (buffer != null && buffer.isDirty()) {
				//have to quit, we can't refactor on a dirty file.
				return false;
			}
			HashMap<IWaypoint, MutableCWaypointInfo> waypointInfoMap = 
				new HashMap<IWaypoint, MutableCWaypointInfo>();
			HashMap<MutableCWaypointInfo, IWaypoint> infoWaypointMap =
				new HashMap<MutableCWaypointInfo, IWaypoint>();
			for (IWaypointChangeEvent event : events) {
				IWaypoint waypoint = event.getWaypoint();
				MutableCWaypointInfo info = waypointInfoMap.get(waypoint);
				if (info == null) {
					info = createInfoForWaypoint(waypoint);
					waypointInfoMap.put(waypoint, info);
					infoWaypointMap.put(info, waypoint);
				}
				applyChange(info, event);
			}
			
			List<MutableCWaypointInfo> infos = new ArrayList<MutableCWaypointInfo>(waypointInfoMap.values());
			Collections.sort(infos, new InfoComparator());
			DocumentWriteSession session = new DocumentWriteSession(file);
			try {
				session.begin();
				for (int i = 0; i < infos.size(); i++) {
					MutableCWaypointInfo info = infos.get(i);
					try {
						int diff = applyToDocument(info, infoWaypointMap.get(info), session.getDocument());
						if (diff != 0) {
							for (int j = i+1; j < infos.size(); j++) {
								MutableCWaypointInfo nextInfo = infos.get(j);
								int offsetInDocument = nextInfo.getOffset();
								nextInfo.setOffsetInText(offsetInDocument+diff);
							}
						}
					} catch (MalformedTreeException e) {
						e.printStackTrace();
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}

				//update the file.
			} catch (IllegalStateException e){
				throw e;
			} finally {
				session.end();
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
	private int applyToDocument(MutableCWaypointInfo info, IWaypoint waypoint, IDocument document) throws MalformedTreeException, BadLocationException {
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
	private int applyDelete(MutableCWaypointInfo info, IWaypoint waypoint, IDocument document) {
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
	private int simpleReplace(MutableCWaypointInfo info, IWaypoint waypoint, IDocument document) {
		int oldLength = CWaypointUtils.getLength(waypoint);
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
	private void applyChange(MutableCWaypointInfo info, IWaypointChangeEvent event) {
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
	private void handleApplyTagsChange(MutableCWaypointInfo info, IWaypointChangeEvent event) {
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
	private void handleApplyTagNameChange(MutableCWaypointInfo info, IWaypointChangeEvent event) {
		info.changeTag(event.getOldTagName(), event.getNewTagName());		
	}

	/**
	 * @param info
	 * @param event
	 */
	private void handleApplyDelete(MutableCWaypointInfo info, IWaypointChangeEvent event) {
		info.delete();
	}

	/**
	 * @param info
	 * @param event
	 */
	private void handleApplyAttributeChanges(MutableCWaypointInfo info, IWaypointChangeEvent event) {
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
	private MutableCWaypointInfo createInfoForWaypoint(IWaypoint waypoint) {
		String text = CWaypointUtils.getWaypointText(waypoint);
		ICWaypointParser parser = CWaypointParserFactory.createParser();
		IParsedCWaypointInfo parsedInfo = parser.parse(text, CWaypointUtils.getOffset(waypoint));
		//System.out.println(text);
		return new MutableCWaypointInfo(parsedInfo, MutableCWaypointInfo.TagStyle.DOT);
	}

}
