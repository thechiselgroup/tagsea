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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.IWaypointChangeEvent;
import net.sourceforge.tagsea.core.WaypointDelta;
import net.sourceforge.tagsea.parsed.IParsedWaypointAttributes;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.core.internal.WaypointParsingTools.RegionGraph;
import net.sourceforge.tagsea.parsed.core.internal.operations.AbstractWaypointUpdateOperation;
import net.sourceforge.tagsea.parsed.core.internal.operations.CleanFilesOperation;
import net.sourceforge.tagsea.parsed.core.internal.resources.DocumentRegistry;
import net.sourceforge.tagsea.parsed.parser.IMutableParsedWaypointDescriptor;
import net.sourceforge.tagsea.parsed.parser.IWaypointParseProblemCollector;
import net.sourceforge.tagsea.parsed.parser.IWaypointRefactoring;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

/**
 * Generates text changes and applies them for refactoring.
 * @author Del Myers
 *
 */
public class RefactoringSupport {
	
	public static void applyWaypointChanges(WaypointDelta delta) {
		HashMap<IWaypoint, List<IWaypointChangeEvent>> waypointChanges = 
			new HashMap<IWaypoint, List<IWaypointChangeEvent>>();
		HashMap<IFile, IDocument> fileDocumentMap = new HashMap<IFile, IDocument>();
		//sort the changes by waypoints and by files.
		for (IWaypointChangeEvent event : delta.changes) {
			List<IWaypointChangeEvent> waypointEvents = waypointChanges.get(event.getWaypoint());
			if (waypointEvents == null) {
				waypointEvents = new ArrayList<IWaypointChangeEvent>();
				waypointChanges.put(event.getWaypoint(), waypointEvents);
			}
			waypointEvents.add(event);
		}
		//connect all the documents.
		final HashMap<IFile, ITextFileBuffer> connectedMap = new HashMap<IFile, ITextFileBuffer>();
		for (IWaypoint wp : waypointChanges.keySet()) {
			IFile file = ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getFileForWaypoint(wp);
			if (!fileDocumentMap.containsKey(file)) {
				try {
					ITextFileBuffer buffer = FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
					if (buffer == null) {
						FileBuffers.getTextFileBufferManager().connect(file.getFullPath(), LocationKind.IFILE, new NullProgressMonitor());
						buffer = FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
						connectedMap.put(file, buffer);
					}
					IDocument document = buffer.getDocument();
					fileDocumentMap.put(file, document);
				} catch (CoreException e) {
					ParsedWaypointPlugin.getDefault().log(e);
				}
			}
		}
		generateEdits(waypointChanges, fileDocumentMap);
		//create a refresh job for all the waypoint files that weren't saved.
		List<IFile> refreshList = new LinkedList<IFile>();
		for (IFile file: fileDocumentMap.keySet()) {
			if (!connectedMap.keySet().contains(file)) {
				refreshList.add(file);
			}
		}
		final CleanFilesOperation cleanOp = new CleanFilesOperation(refreshList);
		TagSEAPlugin.run(new AbstractWaypointUpdateOperation("Refreshing changed files..."){
			@Override
			public IStatus run(IProgressMonitor monitor)
					throws InvocationTargetException {
				IStatus status = TagSEAPlugin.syncRun(cleanOp, monitor);
				if (!status.isOK()) {
					//refresh the whole workspace to make sure that nothing is ugly.
					ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().clean(true);
				}
				return status;
			}
			@Override
			public ISchedulingRule getRule() {
				return cleanOp.getRule();
			}
			
		}, false);
		
		Job commitJob = new Job("Committing changes..."){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Committing waypoint refactorings....", connectedMap.keySet().size());
				MultiStatus status = new MultiStatus(ParsedWaypointPlugin.PLUGIN_ID, 0, "Committing waypoint changes", null);
				for (IFile file : connectedMap.keySet()) {
					ITextFileBuffer buffer = connectedMap.get(file);
					try {
						buffer.commit(monitor, false);
						file.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
						FileBuffers.getTextFileBufferManager().disconnect(file.getFullPath(), LocationKind.IFILE, new NullProgressMonitor());
					} catch (CoreException e) {
						status.merge(e.getStatus());
					}
					monitor.worked(1);
				}
				monitor.done();
				return status;
			}
		};
		ISchedulingRule[] schedules = new ISchedulingRule[connectedMap.keySet().size()];
		int index = 0;
		for (IFile file : connectedMap.keySet()) {
			schedules[index] = file.getWorkspace().getRuleFactory().modifyRule(file);
			index++;
		}
		MultiRule commitRule = new MultiRule(schedules);
		commitJob.setRule(commitRule);
		commitJob.schedule();
		
	}

	/**
	 * @param waypointChanges
	 * @param connectedMap 
	 */
	private static void generateEdits(
			HashMap<IWaypoint, List<IWaypointChangeEvent>> waypointChanges,
			HashMap<IFile, IDocument> fileDocumentMap) {
		HashMap<IFile, List<TextEdit>> edits = new HashMap<IFile, List<TextEdit>>();
		for (IWaypoint wp : waypointChanges.keySet()) {
			//see if any of the changes is a delete. If yes, get rid of the rest.
			List<IWaypointChangeEvent> events = waypointChanges.get(wp);
			for (int i = 0; i < events.size(); i++) {
				if (events.get(i).getType() == IWaypointChangeEvent.DELETE) {
					IWaypointChangeEvent e = events.get(i);
					events.clear();
					events.add(e);
				}
				
			}
			IFile file = ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getFileForWaypoint(wp);
			TextEdit edit = createEdit(wp, events, file, fileDocumentMap.get(file));
			if (edit != null) {
				List<TextEdit> fileEdits = edits.get(file);
				if (fileEdits == null) {
					fileEdits = new ArrayList<TextEdit>();
					edits.put(file, fileEdits);
				}
				fileEdits.add(edit);
			}
		}
		applyEdits(edits, fileDocumentMap);
	}



	/**
	 * Tries to apply as many edits as possible to the document.
	 * @param edits
	 * @param fileDocumentMap
	 * @param connectedMap 
	 */
	private static void applyEdits(HashMap<IFile, List<TextEdit>> edits,
			HashMap<IFile, IDocument> fileDocumentMap) {
		LinkedList<IFile> refreshList = new LinkedList<IFile>();
		for (IFile file : edits.keySet()) {
			List<TextEdit> fileEdits = edits.get(file);
			//sort the edits and generate a graph of overlapping regions
			//so that we can remove the minimal set of conflicts and do
			//as much work as possible.
			TextEdit[] editArray = fileEdits.toArray(new TextEdit[fileEdits.size()]);
			IRegion[] regionArray = new IRegion[editArray.length];
			for (int i = 0; i < editArray.length; i++) {
				TextEdit edit = editArray[i];
				regionArray[i] = new Region(edit.getOffset(), edit.getLength());
			}
			RegionGraph<TextEdit> overlaps = new RegionGraph<TextEdit>(regionArray, editArray);
			List<TextEdit> skippedEdits = new ArrayList<TextEdit>(); 
			while (overlaps.getOverlapCount(overlaps.peekObject()) > 0) {
				skippedEdits.add(overlaps.popObject());
			}
			final MultiTextEdit finalEdit = new MultiTextEdit();
			final IDocument document = fileDocumentMap.get(file);
			finalEdit.addChildren(overlaps.getNodeObjects());
			final Exception[] ex = new Exception[1];
			if (Display.getCurrent() == null) {
				Display.getDefault().syncExec(new Runnable(){
					public void run() {
						try {
							//don't notify of changes that will be saved later.

								((DocumentRegistry) ParsedWaypointPlugin
										.getDefault().getPlatformDocumentRegistry())
										.ignoreDocument(document);
							finalEdit.apply(document);
								((DocumentRegistry) ParsedWaypointPlugin
										.getDefault().getPlatformDocumentRegistry()).watchDocument(document);

						} catch (MalformedTreeException e) {
							ex[0] = e;
						} catch (BadLocationException e) {
							ex[0] = e;
						}
					}});
			} else {
				try {
					//don't notify of changes that will be saved later.
					((DocumentRegistry) ParsedWaypointPlugin
							.getDefault().getPlatformDocumentRegistry())
							.ignoreDocument(document);

					finalEdit.apply(document);
					((DocumentRegistry) ParsedWaypointPlugin
							.getDefault().getPlatformDocumentRegistry()).watchDocument(document);

				} catch (MalformedTreeException e) {
					ex[0] = e;
				} catch (BadLocationException e) {
					ex[0] = e;
				}
			}
			if (ex[0] != null) {
				refreshList.add(file);
			}
		}
		if (refreshList.size() > 0) {
			CleanFilesOperation cleanOp = new CleanFilesOperation(refreshList);
			TagSEAPlugin.run(cleanOp, false);
		}
	}

	/**
	 * @param events
	 * @return
	 */
	private static TextEdit createEdit(IWaypoint wp, List<IWaypointChangeEvent> events, IFile file, IDocument document) {
		IMutableParsedWaypointDescriptor descriptor = null;
		String kind = wp.getStringValue(IParsedWaypointAttributes.ATTR_KIND, null);
		if (kind == null) return null;
		IParsedWaypointDefinition def =
			ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getDefinition(kind);
		IWaypointRefactoring refactoring = def.getRefactoringMethod();
		int charStart = wp.getIntValue(IParsedWaypointAttributes.ATTR_CHAR_START, -1);
		int charEnd = wp.getIntValue(IParsedWaypointAttributes.ATTR_CHAR_END, -1);
		if (refactoring == null) return null;
		for (IWaypointChangeEvent event : events) {
			if (event.getType() == IWaypointChangeEvent.DELETE) {
				TextEdit edit = refactoring.delete(wp, document);
				WaypointParsingTools.deleteWaypoint(wp);
				return edit;
			} else {
				if (descriptor == null) {
					if (!event.getWaypoint().exists()) return null;
					if (charStart == -1 || charEnd == -1) return null;
					IWaypointParseProblemCollector collector = DocumentRegistry.INSTANCE;
					IRegion r = new Region(charStart, charEnd-charStart);
					((DocumentRegistry)collector).clearProblems(document, r);
					descriptor = refactoring.getMutable(event.getWaypoint(), r, document, collector);
					//maybe schedule the waypoint for deletion?
					if (descriptor == null) return null;
				}
				switch (event.getType()) {
				case IWaypointChangeEvent.CHANGE:
					handleChangeEvent(event, descriptor);
					break;
				case IWaypointChangeEvent.TAG_NAME_CHANGED:
					handleTagNameChangeEvent(event, descriptor);
					break;
				case IWaypointChangeEvent.TAGS_CHANGED:
					handleTagChangeEvent(event, descriptor);
					break;
				}
			}
		}
		if (descriptor != null) {
			return new ReplaceEdit(charStart, charEnd-charStart, descriptor.getText());
		}
		return null;
	}



	/**
	 * @param event
	 * @param descriptor
	 */
	private static void handleChangeEvent(IWaypointChangeEvent event,
			IMutableParsedWaypointDescriptor descriptor) {
		for (String attr : event.getChangedAttributes()) {
			try {
				if (IParsedWaypointAttributes.ATTR_AUTHOR.equals(attr)) {
					descriptor.setAuthor((String)event.getNewValue(attr));
				} else if (IParsedWaypointAttributes.ATTR_DATE.equals(attr)) {
					descriptor.setDate((Date)event.getNewValue(attr));
				} else if (IParsedWaypointAttributes.ATTR_MESSAGE.equals(attr)) {
					descriptor.setMessage((String)event.getNewValue(attr));
				}
			} catch (UnsupportedOperationException e) {
				ParsedWaypointPlugin.getDefault().log(e);
			}
		}
		
	}

	/**
	 * @param event
	 * @param descriptor
	 */
	private static void handleTagNameChangeEvent(IWaypointChangeEvent event,
			IMutableParsedWaypointDescriptor descriptor) {
		try {
			descriptor.replaceTag(event.getOldTagName(), event.getNewTagName());
		} catch (UnsupportedOperationException e) {
			ParsedWaypointPlugin.getDefault().log(e);
		}
		
	}

	/**
	 * @param event
	 * @param descriptor
	 */
	private static void handleTagChangeEvent(IWaypointChangeEvent event,
			IMutableParsedWaypointDescriptor descriptor) {
		TreeSet<String> oldTags = new TreeSet<String>(Arrays.asList(event.getOldTags()));
		TreeSet<String> newTags = new TreeSet<String>(Arrays.asList(event.getNewTags()));
		TreeSet<String> difference = new TreeSet<String>();
		difference.addAll(oldTags);
		difference.removeAll(newTags);
		//delete everything left in the difference
		for (String tagName : difference) {
			try {
				descriptor.removeTag(tagName);
			} catch (UnsupportedOperationException e) {
				ParsedWaypointPlugin.getDefault().log(e);
			}
		}
		difference.clear();
		difference.addAll(newTags);
		difference.removeAll(oldTags);
		//add the new ones
		for (String tagName : difference) {
			try {
				descriptor.addTag(tagName);
			} catch(UnsupportedOperationException e) {
				ParsedWaypointPlugin.getDefault().log(e);
			}
		}
	}

}
