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


import java.util.HashMap;
import java.util.Iterator;

import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.core.internal.ThreadQueue;
import net.sourceforge.tagsea.parsed.parser.WaypointParseProblem;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.IFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.MarkerAnnotation;

/**
 * A class that connects to documents and annotates them with parse problems.
 * @author Del Myers
 *
 */
public class ProblemAnnotator  {
	
	
	private HashMap<IMarker, WaypointParseProblem> markerProblemMap;
	

	
	
	public ProblemAnnotator() {
		markerProblemMap = new HashMap<IMarker, WaypointParseProblem>();
	}

	/**
	 * Cleans the given document of all parse problems.
	 * @param document
	 * @param file
	 */
	void cleanDocument(final IDocument document, final IFile file) {
		if (file == null) return;
		spawnAnnotator(new Runnable(){
			public void run() {
				ISchedulingRule rule = file.getWorkspace().getRuleFactory().markerRule(file);
				try {
					Job.getJobManager().beginRule(rule, new NullProgressMonitor());
					try {
						IMarker[] markers = file.findMarkers(IProblemConstants.PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
						for (IMarker marker : markers) {
							if (marker != null && marker.exists()) {
								markerProblemMap.remove(marker);
								marker.delete();
							}
						}
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} finally {
					Job.getJobManager().endRule(rule);
				}
			}
		});
	}
	
	/**
	 * Cleans all of the parse problems intersecting with the given region.
	 * @param document
	 * @param file
	 * @param region
	 */
	void cleanDocument(final IDocument document, final IFile file, final IRegion region) {
		spawnAnnotator(new Runnable(){
			public void run() {
				IAnnotationModel annotations = getAnnotationModel(file);
				if (annotations == null) return;

				ISchedulingRule rule = file.getWorkspace().getRuleFactory().markerRule(file);
				try {
					Job.getJobManager().beginRule(rule, new NullProgressMonitor());
					for (Iterator<?> i=annotations.getAnnotationIterator(); i.hasNext();) {
						Annotation a = (Annotation) i.next();
						if (a instanceof MarkerAnnotation) {
							MarkerAnnotation ppa = (MarkerAnnotation) a;
							try {
								if (!ppa.getMarker().exists()) {
									markerProblemMap.remove(ppa.getMarker());
								} else {
									if (ppa.getMarker().getType().equals(IProblemConstants.PROBLEM_MARKER)) {
										Position p = annotations.getPosition(ppa);
										if (intersects(region, p)) {
											ppa.getMarker().delete();
											synchronized (markerProblemMap) {
												markerProblemMap.remove(ppa.getMarker());
											}
										}
									}
								}
							} catch (CoreException e) {
								ParsedWaypointPlugin.getDefault().log(e);
							}
						}
					}
				} finally {
					Job.getJobManager().endRule(rule);
				}
			}
			
		});
		
		
	}
	
	/**
	 * @param region
	 * @param p
	 * @return
	 */
	private boolean intersects(IRegion region, Position p) {
		int pStart = p.getOffset();
		int pEnd = p.getOffset()+p.getLength();
		int rStart = region.getOffset();
		int rEnd = region.getOffset()+region.getLength();
		return (
				(pStart == rStart || pEnd == rEnd) ||
				(pStart >= rStart && pStart <= rEnd) ||
				(rStart >= pStart && rStart <= rEnd)
			);
	}

	/**
	 * Gets the annotation model for the given file if it can be found (i.e. a file buffer is opened for it)
	 * or null if it cannot.
	 * @param file
	 * @return
	 */
	private IAnnotationModel getAnnotationModel(IFile file) {
		if (file == null) return null;
		IFileBuffer buffer = 
			FileBuffers.getTextFileBufferManager().getFileBuffer(file.getFullPath(), LocationKind.IFILE);
		if (buffer instanceof ITextFileBuffer) {
			return ((ITextFileBuffer)buffer).getAnnotationModel();
		}
		return null;
	}

	/**
	 * @param marker
	 * @return
	 */
	public WaypointParseProblem getProblemForMarker(IMarker marker) {
		synchronized (markerProblemMap) {
			return markerProblemMap.get(marker);
		}
	}

	/**
	 * @param problem
	 * @param file
	 */
	public void createAnnotation(final WaypointParseProblem problem, final IFile file) {
		spawnAnnotator(new Runnable(){
			public void run() {
				int start = problem.getOffset();
				int end = problem.getEnd();
				if (start != -1 && end != -1) {
					final HashMap<String, Object> attributes = new HashMap<String, Object>();
					attributes.put(IMarker.CHAR_START, start);
					attributes.put(IMarker.CHAR_END, end);
					attributes.put(IMarker.MESSAGE, problem.getMessage());
					try {
						int line = problem.getDocument().getLineOfOffset(start);
						attributes.put(IMarker.LINE_NUMBER, line);
					} catch (BadLocationException e1) {
						//return with no annotation
						return;
					}
					attributes.put(IProblemConstants.ATTR_PROBLEM_SEVERITY, problem.getSeverity());
					ISchedulingRule rule = file.getWorkspace().getRuleFactory().markerRule(file);
					try {
						Job.getJobManager().beginRule(rule, new NullProgressMonitor());
						IMarker marker= file.createMarker(IProblemConstants.PROBLEM_MARKER);
						marker.setAttributes(attributes);
						synchronized (markerProblemMap) {
							markerProblemMap.put(marker, problem);
						}
					} catch (CoreException e) {
						ParsedWaypointPlugin.getDefault().log(e);
					} finally {
						Job.getJobManager().endRule(rule);
					}
				}
			}
		});
	}
	
	private void spawnAnnotator(Runnable r) {
		ThreadQueue.queue(r);
	}
	
	
	
	
		

}
