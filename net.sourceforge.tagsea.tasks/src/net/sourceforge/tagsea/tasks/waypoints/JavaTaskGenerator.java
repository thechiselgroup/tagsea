package net.sourceforge.tagsea.tasks.waypoints;

import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.tasks.TaskWaypointPlugin;
import net.sourceforge.tagsea.tasks.TaskWaypointUtils;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;

public class JavaTaskGenerator implements IMarkerResolutionGenerator, IMarkerResolutionGenerator2 {
	private class JavaTaskResolution implements IMarkerResolution, IMarkerResolution2 {
		
		private IWaypoint waypoint;

		public JavaTaskResolution(IWaypoint waypoint) {
			this.waypoint = waypoint;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IMarkerResolution#getLabel()
		 */
		public String getLabel() {
			return "Convert to a Java waypoint";
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IMarkerResolution#run(org.eclipse.core.resources.IMarker)
		 */
		public void run(IMarker marker) {
			IMarker task = TaskWaypointUtils.getTaskForWaypoint(waypoint);
			if (task != null && task.exists()) {
				ITextFileBuffer buffer = 
					FileBuffers.getTextFileBufferManager().getTextFileBuffer(task.getResource().getFullPath(), LocationKind.IFILE);
				if (buffer != null) {
					int charStart = task.getAttribute(IMarker.CHAR_START, -1);
					int charEnd = task.getAttribute(IMarker.CHAR_END, -1);
					if (charStart >=0 && charEnd >= 0) {
						try {
							buffer.getDocument().replace(charStart, charEnd-charStart, getReplacementString());
							//waypoint.delete();
						} catch (BadLocationException e) {
						}
					}
				}
			}
		}
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IMarkerResolution2#getDescription()
		 */
		public String getDescription() {
			return getReplacementString();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IMarkerResolution2#getImage()
		 */
		public Image getImage() {
			return TaskWaypointPlugin.getDefault().getImageRegistry().get("TOJAVA");
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
		
	}
	
	/**
	 * 
	 */
	public JavaTaskGenerator() {
		
	}
	
	public IMarkerResolution[] getResolutions(IMarker marker) {
		if (hasResolutions(marker)) {
			return new IMarkerResolution[] {new JavaTaskResolution(TaskWaypointUtils.getWaypointForTask(marker))};
		}
		return new IMarkerResolution[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IMarkerResolutionGenerator2#hasResolutions(org.eclipse.core.resources.IMarker)
	 */
	public boolean hasResolutions(IMarker marker) {
		try {
			if (marker.exists() && marker.isSubtypeOf(IMarker.TASK)) {
				return TaskWaypointUtils.getWaypointForTask(marker) != null;
			}
		} catch (CoreException e) {
		}
		return false;
	}

}
