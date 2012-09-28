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
package net.sourceforge.tagsea.parsed.core.internal.resources;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.parsed.IParsedWaypointAttributes;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.core.ParsedWaypointUtils;
import net.sourceforge.tagsea.parsed.core.internal.WaypointParsingTools;
import net.sourceforge.tagsea.parsed.core.internal.WaypointParsingTools.RegionGraph;
import net.sourceforge.tagsea.parsed.core.internal.WaypointParsingTools.WaypointDescriptorMatch;
import net.sourceforge.tagsea.parsed.core.internal.operations.AbstractWaypointUpdateOperation;
import net.sourceforge.tagsea.parsed.core.internal.operations.CleanFilesOperation;
import net.sourceforge.tagsea.parsed.core.internal.operations.WaypointOverlapProblem;
import net.sourceforge.tagsea.parsed.parser.IParsedWaypointDescriptor;
import net.sourceforge.tagsea.parsed.parser.IReconcilingWaypointParser;
import net.sourceforge.tagsea.parsed.parser.IWaypointParseProblemCollector;
import net.sourceforge.tagsea.parsed.parser.IWaypointParser;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

/**
 * Document listener that actually does all of the updating for changes in the documents.
 * @author Del Myers
 *
 */
public class DocumentWaypointUpdater implements IDocumentListener {

	private DocumentRegistry registry;
	private FullParseTimer timer;
	
	private class FullParseTimer {
		private HashMap<IFile, TimerTask> timerMap;
		private Timer timer;
		/**
		 * 
		 */
		public FullParseTimer() {
			timerMap = new HashMap<IFile, TimerTask>();
			timer = new Timer();
		}
		public void schedule(final IFile file) {
			synchronized (timerMap) {
				TimerTask task = timerMap.get(file);
				if (task != null) {
					task.cancel();
				}
				task = new TimerTask() {
					@Override
					public void run() {
						synchronized (timerMap) {
							ArrayList<IFile> list = new ArrayList<IFile>(1);
							list.add(file);
							TagSEAPlugin.run(new CleanFilesOperation(list), false);
							timerMap.remove(file);
						}
					}
				};
				timerMap.put(file, task);
				timer.schedule(task, 1000);
			}
		}
	}

	/**
	 * @param fileBufferRegistry
	 */
	public DocumentWaypointUpdater(DocumentRegistry fileBufferRegistry) {
		this.registry = fileBufferRegistry;
		this.timer = new FullParseTimer();
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
	}

	public void documentChanged(final DocumentEvent event) {
		final IFile file = registry.getFileForDocument(event.getDocument());
		if (file == null) 
			return;
		if (TagSEAPlugin.isBlocked()) {
			//just schedule a full parse and return.
			timer.schedule(file);
			return;
		}
		final long time = System.currentTimeMillis();
		TagSEAPlugin.run(new AbstractWaypointUpdateOperation("Repairing Waypoints"){
			@Override
			public IStatus run(IProgressMonitor monitor)
					throws InvocationTargetException {
				if (System.currentTimeMillis() - time > 1000) {
					//don't update if the full parse has already run.
					return Status.OK_STATUS;
				}
				IParsedWaypointDefinition[] defs = ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getMatchingDefinitions(file);
				MultiStatus status = new MultiStatus(ParsedWaypointPlugin.PLUGIN_ID, 0, "Synchronizing document change to waypoints.", null);
				List<IWaypoint> generated = new ArrayList<IWaypoint>();
				for (IParsedWaypointDefinition def : defs) {
					IWaypointParser parser = def.getParser();
					IRegion[] regionsToConsider = new IRegion[] {new Region(0, event.getDocument().getLength())}; 
					if (parser instanceof IReconcilingWaypointParser) {
						regionsToConsider = ((IReconcilingWaypointParser)parser).calculateDirtyRegions(event);
					}
					int minStart = -1;
					int maxEnd = -1;
					boolean doParse = true;
					//calculate the full spanning region so that we can get all of the waypoints in that region which
					//will need replacing.
					if (regionsToConsider.length == 0) {
						doParse = false;
					} else {
						for (IRegion region : regionsToConsider) {
							if (minStart == -1 || region.getOffset() < minStart) {
								minStart = region.getOffset();
							}
							if (maxEnd == -1 || maxEnd < region.getOffset() + region.getLength()) {
								maxEnd = region.getOffset() + region.getLength();
							}
						}
					}
					int textLength = (event.getText() == null) ? 0 : event.getText().length();
					
					//adjust the region to fit the state of the document _before_ the document change
					if (doParse && (event.getOffset() < minStart || 
						event.getOffset() > maxEnd || 
						event.getOffset() + textLength > maxEnd)) {
						//the returned region was outside of the actual document change try to gracefully
						//reparse the whole document.
						minStart = 0;
						maxEnd = event.getDocument().getLength();
						regionsToConsider = new IRegion[] {new Region(0, event.getDocument().getLength())}; 
					}
					int textDiff = event.getLength()- textLength;
					int textAdjustment = -textDiff;
					maxEnd += textDiff;
					//find the old waypoints and adjust their locations to fit the document change
					//so that they can be properly synchronized.
					IWaypoint[] oldArray = 
						ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getWaypointsForFile(file, def.getKind());
					LinkedList<IWaypoint> waypointsToConsider = new LinkedList<IWaypoint>();
					for (IWaypoint wp : oldArray) {
						int wpStart = wp.getIntValue(IParsedWaypointAttributes.ATTR_CHAR_START, -1);
						int wpEnd = wp.getIntValue(IParsedWaypointAttributes.ATTR_CHAR_END, -1);
						if (doParse && ((wpStart >= minStart && wpStart <= maxEnd) || (wpEnd >= minStart && wpEnd <= maxEnd))) {
							//at least one end is in the region: add it to the list to consider.
							waypointsToConsider.add(wp);
						}
						
						if (wpStart > event.getOffset() + event.getLength() && wpEnd > event.getOffset()) {
							//if it comes after the changed region, update both of it's indexes
							wp.setIntValue(IParsedWaypointAttributes.ATTR_CHAR_START, wpStart + textAdjustment);
							wp.setIntValue(IParsedWaypointAttributes.ATTR_CHAR_END, wpEnd + textAdjustment);
						} else if (wpStart <= event.getOffset() && wpEnd >= event.getOffset()+event.getLength()) {
							//if it is inside, adjust the end of the waypoint only
							int newEnd = wpEnd+textAdjustment;
							if (newEnd == wpStart) {
								//0-lengthed waypoints must be deleted.
								waypointsToConsider.add(wp);
							} else {
								wp.setIntValue(IParsedWaypointAttributes.ATTR_CHAR_END, newEnd);
								if (!doParse) {
									Status s = new Status(Status.WARNING, ParsedWaypointPlugin.PLUGIN_ID, "Unsynchronized edit in " + def.getName() + "waypoint");
									status.merge(s);
								}
							}
						} else {
							//check to make sure that no error has occurred in the gathering of dirty areas.
							//if it has, log an error.
							
							if (!doParse) {
							if ((wpStart >= event.getOffset() && wpStart <= event.getOffset() + event.getLength()) ||
								(wpEnd >= event.getOffset() && wpEnd <= event.getOffset() + event.getLength())) {
									//if we aren't parsing, but it intersects with a dead region, asume that
									//it should be deleted.
									waypointsToConsider.add(wp);
								}
							}
							
							//otherwise, do nothing: it will either be before the change in which case it doesn't matter,
							//or it will intersect with the change in which case it has to be replaced.
						}
					}
					//clear the document in the given regions
					for (IRegion r : regionsToConsider) {
						registry.clearProblems(event.getDocument(), r);
					}
					IParsedWaypointDescriptor[] descriptors = parser.parse(event.getDocument(), regionsToConsider, registry);
					List<WaypointDescriptorMatch> matchings =WaypointParsingTools.organizeWaypoints(
							file, waypointsToConsider.toArray(new IWaypoint[waypointsToConsider.size()]), 
							descriptors, 
							def.getKind()
					);
					generated.addAll(WaypointParsingTools.generateWaypoints(matchings, file, def.getKind()));
				}
				removeOverlaps(generated, registry, event.getDocument());
				return status;
			}
			/**
			 * Removes the overlaps for the given waypoints.
			 * @param generated
			 * @param collector 
			 * @param document 
			 */
			private void removeOverlaps(List<IWaypoint> generated, IWaypointParseProblemCollector collector, IDocument document) {
				RegionGraph<IWaypoint> overlaps = WaypointParsingTools.calculateOverlappingWaypoints(generated);
				for (IWaypoint wp : generated) {
					if (overlaps.getOverlapCount(wp) > 0) {
						int start = ParsedWaypointUtils.getCharStart(wp);
						int end = ParsedWaypointUtils.getCharEnd(wp);
						WaypointParsingTools.deleteWaypoint(wp);
						collector.accept(new WaypointOverlapProblem(start, end-start, document));
					}
				}
			}
			
		}, false);
		//schedule a full clean
		timer.schedule(file);
	}

}
