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
package net.sourceforge.tagsea.parsed.core.internal.operations;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.IParsedWaypointDefinition;
import net.sourceforge.tagsea.parsed.core.ParsedWaypointUtils;
import net.sourceforge.tagsea.parsed.core.internal.KindedParsedWaypointDescriptor;
import net.sourceforge.tagsea.parsed.core.internal.ParsedWaypointRegistry;
import net.sourceforge.tagsea.parsed.core.internal.WaypointParsingTools;
import net.sourceforge.tagsea.parsed.core.internal.WaypointParsingTools.WaypointDescriptorMatch;
import net.sourceforge.tagsea.parsed.core.internal.resources.DocumentRegistry;
import net.sourceforge.tagsea.parsed.parser.IParsedWaypointDescriptor;
import net.sourceforge.tagsea.parsed.parser.IWaypointParseProblemCollector;
import net.sourceforge.tagsea.parsed.parser.IWaypointParser;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

/**
 * Parses a given file for waypoints. Does a clean parse. 
 * ONLY MEANT TO BE RUN FROM WITHIN AN AbstractWaypointUpdateOperation
 * @author Del Myers
 *
 */
public class ParseFileOperation extends TagSEAOperation {

	private IFile file;
	

	public ParseFileOperation(IFile file) {
		this.file = file;
	}


	public IStatus run(IProgressMonitor monitor) throws InvocationTargetException{
		ParsedWaypointRegistry wpRegistry =	((ParsedWaypointRegistry)ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry());
		wpRegistry.touch(file);
		if (!file.isAccessible()) {
			//remove the waypoints.
			IWaypoint[] wps = wpRegistry.findWaypoints(file, IResource.DEPTH_ZERO);
			for (IWaypoint wp : wps) {
				if (wp.exists()) {
					wp.delete();
				}
			}
			return Status.OK_STATUS;
		}
		monitor.beginTask("Parsing file " + file.getFullPath(), 103);
		IParsedWaypointDefinition[] defs = 
			ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getMatchingDefinitions(file);
		monitor.worked(1);
		if (defs == null || defs.length == 0) {
			//don't waste time or memory.
			monitor.done();
			return Status.OK_STATUS;
		}
		boolean disconnect = false;
		ITextFileBuffer buffer = 
			FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
		if (buffer == null) {
			try {
				FileBuffers.getTextFileBufferManager().connect(
						file.getFullPath(), 
						LocationKind.IFILE, new SubProgressMonitor(
								monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK
						)
				);
			} catch (CoreException e) {
				monitor.done();
				throw new InvocationTargetException(e);
			}
			buffer = 
				FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
			disconnect = true;
		}
		if (buffer != null) {
			IDocument document = buffer.getDocument();
			ISchedulingRule rule = file.getWorkspace().getRuleFactory().modifyRule(file);
			ArrayList<KindedParsedWaypointDescriptor> allDescriptors = new ArrayList<KindedParsedWaypointDescriptor>();
			IWaypointParseProblemCollector collector = DocumentRegistry.INSTANCE;
			try {
				Job.getJobManager().beginRule(rule, new SubProgressMonitor(monitor, 25));
				if (document != null) {
					((DocumentRegistry)collector).clearProblems(document);
					for (IParsedWaypointDefinition def : defs) {
						IWaypointParser parser = def.getParser();
						IParsedWaypointDescriptor[] descriptors = 
							parser.parse(document, new IRegion[]{new Region(0, document.getLength())}, collector);
						for (IParsedWaypointDescriptor desc : descriptors) {
							allDescriptors.add(new KindedParsedWaypointDescriptor(desc, def.getKind()));
						}
					}
					List<IWaypoint> waypoints = resolveWaypoints(allDescriptors, collector, document);
					if (!buffer.isDirty()) {
						//backup the waypoints.
						wpRegistry.backup(waypoints.toArray(new IWaypoint[waypoints.size()]));
					}
				}
			} finally {
				Job.getJobManager().endRule(rule);
			}
		}
		if (disconnect) {
			try {
				FileBuffers.getTextFileBufferManager().disconnect(
						file.getFullPath(), 
						LocationKind.IFILE, new SubProgressMonitor(
								monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK
						)		
				);
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			}
		}
		monitor.done();
		return Status.OK_STATUS;
	}


	/**
	 * @param allDescriptors
	 * @param collector
	 */
	private List<IWaypoint> resolveWaypoints(
			ArrayList<KindedParsedWaypointDescriptor> allDescriptors,
			IWaypointParseProblemCollector problems,
			IDocument document) {
		//delete the unused waypoints.
		TreeSet<String> unregisteredSet = new TreeSet<String>();
		for (IParsedWaypointDefinition def : ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getContributedDefinitions()) {
			unregisteredSet.add(def.getKind());
		}
		for (IParsedWaypointDefinition def : ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getRegisteredDefinitions()) {
			unregisteredSet.remove(def.getKind());
		}
		
		for (IWaypoint wp : ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getWaypointsForFile(file)) {
			if (unregisteredSet.contains(ParsedWaypointUtils.getKind(wp))) {
				if (wp.exists()) {
					WaypointParsingTools.deleteWaypoint(wp);
				}
			}
		}
		
		WaypointParsingTools.RegionGraph<IParsedWaypointDescriptor> overlaps = 
			WaypointParsingTools.calculateOverlap(allDescriptors);
		LinkedList<KindedParsedWaypointDescriptor> goodWaypoints = new LinkedList<KindedParsedWaypointDescriptor>();
		LinkedList<KindedParsedWaypointDescriptor> overlappingDescriptors = new LinkedList<KindedParsedWaypointDescriptor>();
		for (KindedParsedWaypointDescriptor desc : allDescriptors) {
			int overlapCount = overlaps.getOverlapCount(desc);
			if (overlapCount > 0) {
				overlappingDescriptors.add(desc);
				problems.accept(new WaypointOverlapProblem(desc.getCharStart(), desc.getCharEnd()-desc.getCharStart(), document));
			} else {
				goodWaypoints.add(desc);
			}
		}
		//delete the overlapping waypoints
		deleteOverlaps(overlappingDescriptors);
		return generateWaypoints(goodWaypoints);
	}


	/**
	 * @param overlappingDescriptors
	 */
	private void deleteOverlaps(
			LinkedList<KindedParsedWaypointDescriptor> overlappingDescriptors) {
		HashMap<String, List<IParsedWaypointDescriptor>> waypointKinds = 
			new HashMap<String, List<IParsedWaypointDescriptor>>();
		for (KindedParsedWaypointDescriptor desc : overlappingDescriptors) {
			List<IParsedWaypointDescriptor> kindDescriptors = waypointKinds.get(desc.getKind());
			if (kindDescriptors == null) {
				kindDescriptors = new ArrayList<IParsedWaypointDescriptor>();
				waypointKinds.put(desc.getKind(), kindDescriptors);
			}
			kindDescriptors.add(desc);
		}
		for (String kind : waypointKinds.keySet()) {
			List<IParsedWaypointDescriptor> descriptorsList = waypointKinds.get(kind);
			IParsedWaypointDescriptor[] descriptors = 
				descriptorsList.toArray(new IParsedWaypointDescriptor[descriptorsList.size()]);
			List<WaypointDescriptorMatch> matchings = WaypointParsingTools.organizeWaypoints(
					file,
					ParsedWaypointPlugin.
						getDefault().getParsedWaypointRegistry().getWaypointsForFile(
							file, 
							kind
						), 
					descriptors, 
					kind
				);
			//delete the waypoint
			for (WaypointDescriptorMatch match : matchings) {
				if (match.waypoint != null) {
					match.waypoint.delete();
				}
			}
		}
	}


	/**
	 * @param goodWaypoints
	 */
	private List<IWaypoint> generateWaypoints(
			LinkedList<KindedParsedWaypointDescriptor> goodWaypoints) {
		HashMap<String, List<IParsedWaypointDescriptor>> waypointKinds = 
			new HashMap<String, List<IParsedWaypointDescriptor>>();
		for (KindedParsedWaypointDescriptor desc : goodWaypoints) {
			List<IParsedWaypointDescriptor> kindDescriptors = waypointKinds.get(desc.getKind());
			if (kindDescriptors == null) {
				kindDescriptors = new ArrayList<IParsedWaypointDescriptor>();
				waypointKinds.put(desc.getKind(), kindDescriptors);
			}
			kindDescriptors.add(desc);
		}
		//Make sure that all of the kinds that did exist are in the set of kinds.
		for (IWaypoint wp : ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getWaypointsForFile(file)) {
			if (wp.exists()) {
				String kind = ParsedWaypointUtils.getKind(wp);
				if (!waypointKinds.containsKey(kind)) {
					//add an empty list.
					waypointKinds.put(kind, new LinkedList<IParsedWaypointDescriptor>());
				}
			}
		}
		List<IWaypoint> generatedList = new LinkedList<IWaypoint>();
		for (String kind : waypointKinds.keySet()) {
			List<IParsedWaypointDescriptor> descriptorsList = waypointKinds.get(kind);
			IParsedWaypointDescriptor[] descriptors = 
				descriptorsList.toArray(new IParsedWaypointDescriptor[descriptorsList.size()]);
			List<WaypointDescriptorMatch> matchings = WaypointParsingTools.organizeWaypoints(
					file,
					ParsedWaypointPlugin.
						getDefault().getParsedWaypointRegistry().getWaypointsForFile(
							file, 
							kind
						), 
					descriptors, 
					kind
				);
				generatedList.addAll(WaypointParsingTools.generateWaypoints(matchings, file, kind));
		}
		return generatedList;
	}

	/**
	 * @return the file
	 */
	public IFile getFile() {
		return file;
	}
	
}
