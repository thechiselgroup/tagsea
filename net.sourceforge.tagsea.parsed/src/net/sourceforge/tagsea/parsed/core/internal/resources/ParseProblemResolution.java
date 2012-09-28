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

import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;
import net.sourceforge.tagsea.parsed.parser.IReplacementProposal;
import net.sourceforge.tagsea.parsed.parser.WaypointParseProblem;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMarkerResolution;

/**
 * 
 * @author Del Myers
 *
 */
public class ParseProblemResolution implements IMarkerResolution {

	private IMarker marker;
	private WaypointParseProblem problem;
	private int proposalIndex;

	public ParseProblemResolution(IMarker marker, int proposalIndex) throws IllegalMarkerProblemException {
		try {
			if (marker == null || !marker.getType().equals(IProblemConstants.PROBLEM_MARKER)) {
				throw new IllegalMarkerProblemException("Illegal Marker type");
			}
		} catch (CoreException e) {
			throw new IllegalMarkerProblemException("Illegal Marker type");
		}
		WaypointParseProblem problem = DocumentRegistry.INSTANCE.getProblemForMarker(marker);
		if (problem == null) {
			throw new IllegalMarkerProblemException("No problem for marker");
		}
		this.marker = marker;
		this.problem = problem;
		this.proposalIndex = proposalIndex;
		if (problem.getProposals() == null || proposalIndex < 0 || proposalIndex > problem.getProposals().length-1) {
			throw new IllegalMarkerProblemException("Proposal not found");
		}
	}
	
	public String getLabel() {
		return problem.getProposals()[proposalIndex].getDisplayString();
	}

	public void run(IMarker marker) {
		if (marker != this.marker || !marker.exists()) {
			return;
		}
		if (Display.getCurrent() != null) {
			IReplacementProposal proposal = this.problem.getProposals()[proposalIndex];
			try {
				proposal.apply(problem.getDocument());
			} catch (BadLocationException e) {
				ParsedWaypointPlugin.getDefault().log(e);
			}
		}
		else {
			Display.getDefault().syncExec(new Runnable(){
				public void run() {
					IReplacementProposal proposal = problem.getProposals()[proposalIndex];
					try {
						proposal.apply(problem.getDocument());
					} catch (BadLocationException e) {
						ParsedWaypointPlugin.getDefault().log(e);
					}
				};
			});
		}
	}

}
