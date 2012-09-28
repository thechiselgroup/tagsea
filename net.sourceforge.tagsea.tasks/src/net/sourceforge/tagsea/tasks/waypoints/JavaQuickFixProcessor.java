package net.sourceforge.tagsea.tasks.waypoints;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.tasks.TaskWaypointPlugin;
import net.sourceforge.tagsea.tasks.TaskWaypointUtils;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class JavaQuickFixProcessor implements IQuickFixProcessor {

	private static class WaypointCompletionProposal implements IJavaCompletionProposal {
		private IWaypoint waypoint;

		/**
		 * @param waypoint
		 */
		public WaypointCompletionProposal(IWaypoint waypoint) {
			this.waypoint = waypoint;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.ui.text.java.IJavaCompletionProposal#getRelevance()
		 */
		public int getRelevance() {
			return 0;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
		 */
		public void apply(IDocument document) {
			try {
				document.replace(TaskWaypointUtils.getOffset(waypoint), TaskWaypointUtils.getLength(waypoint), getReplacementString());
			} catch (BadLocationException e) {
				TaskWaypointPlugin.getDefault().log(e);
			}			
		}

		private String getReplacementString() {
			ITag[] tags = waypoint.getTags();
			ITag tagOfInterest = null;
			for (ITag tag : tags) {
				if (!tag.getName().equals(ITag.DEFAULT)) {
					tagOfInterest = tag;
					break;
				}
			}
			return "@tag " + tagOfInterest + ": " + waypoint.getText(); 
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
		 */
		public String getAdditionalProposalInfo() {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
		 */
		public IContextInformation getContextInformation() {
			return new WaypointContextInformation();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
		 */
		public String getDisplayString() {
			return "Convert to a Java waypoint";
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
		 */
		public Image getImage() {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
		 */
		public Point getSelection(IDocument document) {
			return null;
		}
		
	}
	
	private static class WaypointContextInformation implements IContextInformation {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.contentassist.IContextInformation#getContextDisplayString()
		 */
		public String getContextDisplayString() {
			return "Convert to a Java waypoint";
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.contentassist.IContextInformation#getImage()
		 */
		public Image getImage() {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.contentassist.IContextInformation#getInformationDisplayString()
		 */
		public String getInformationDisplayString() {
			return "Convert to a Java waypoint";
		}
		
	}
	
	
	public IJavaCompletionProposal[] getCorrections(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {
		ArrayList<IJavaCompletionProposal> proposals = new ArrayList<IJavaCompletionProposal>();
		List<IWaypoint> waypoints = findWaypointsForLocations(context, locations);
		for (IWaypoint waypoint : waypoints) {
			proposals.add(new WaypointCompletionProposal(waypoint));
		}
		return proposals.toArray(new IJavaCompletionProposal[proposals.size()]);
	}

	/**
	 * @param context
	 * @param locations
	 * @throws CoreException 
	 */
	private List<IWaypoint> findWaypointsForLocations(IInvocationContext context, IProblemLocation[] locations) throws CoreException {
		IResource resource = context.getCompilationUnit().getUnderlyingResource();
		IMarker[] markers = resource.findMarkers(null, false, IResource.DEPTH_ZERO);
		ArrayList<IWaypoint> waypoints = new ArrayList<IWaypoint>();
		for (IProblemLocation location : locations) {
			for (IMarker marker : markers) {
				if (location.getMarkerType().equals(marker.getType())) {
					int charStart = marker.getAttribute(IMarker.CHAR_START, -1);
					int charEnd = marker.getAttribute(IMarker.CHAR_END, -1);
					if (location.getOffset() == charStart && location.getLength() == (charEnd - charStart)) {
						IWaypoint wp = TaskWaypointUtils.getWaypointForTask(marker);
						if (wp != null) {
							waypoints.add(wp);
						}
					}
				}
			}
		}
		return waypoints;
	}

	public boolean hasCorrections(ICompilationUnit unit, int problemId) {
		return problemId == IProblem.Task;
	}

}
