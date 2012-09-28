package net.sourceforge.tagsea.parsed.core.internal.resources;

import java.util.LinkedList;

import net.sourceforge.tagsea.parsed.parser.IReplacementProposal;
import net.sourceforge.tagsea.parsed.parser.WaypointParseProblem;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

public class ProblemMarkerResolutionGenerator implements
		IMarkerResolutionGenerator2 {

	public boolean hasResolutions(IMarker marker) {
		WaypointParseProblem problem = DocumentRegistry.INSTANCE.getProblemForMarker(marker);
		return (problem != null && problem.getProposals() != null && problem.getProposals().length > 0);
	}

	public IMarkerResolution[] getResolutions(IMarker marker) {
		WaypointParseProblem problem = DocumentRegistry.INSTANCE.getProblemForMarker(marker);
		if (problem == null) {
			return new IMarkerResolution[0];
		}
		IReplacementProposal[] proposals = problem.getProposals();
		LinkedList<IMarkerResolution> resolutions = new LinkedList<IMarkerResolution>();;
		for (int i = 0; i < proposals.length; i++) {
			try {
				resolutions.add(new ParseProblemResolution(marker, i));
			} catch (IllegalMarkerProblemException e) {
				//just don't add the resolution.
			}
		}

		return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
	}

}
