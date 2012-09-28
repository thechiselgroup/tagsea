package net.sourceforge.tagsea.java.resources.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.LinkedList;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.TagSEAOperation;
import net.sourceforge.tagsea.java.JavaTagsPlugin;
import net.sourceforge.tagsea.java.JavaWaypointUtils;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;

//@tag quick-fix tagsea : example of how to do a quick-fix.
public class DateAuthorQuickFix implements IMarkerResolutionGenerator, IMarkerResolutionGenerator2 {
	private class Resolution implements IMarkerResolution, IMarkerResolution2 {
		
		private boolean date;
		private boolean author;
		private IWaypoint waypoint;

		public Resolution(boolean date, boolean author, IWaypoint waypoint) {
			this.date = date;
			this.author = author;
			this.waypoint = waypoint;
		}
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IMarkerResolution#getLabel()
		 */
		public String getLabel() {
			if (author && date) {
				return "Set the author and date";
			} else if (author) {
				return "Set the author";
			} else if (date) {
				return "Set the date";
			}
			return "";
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IMarkerResolution#run(org.eclipse.core.resources.IMarker)
		 */
		public void run(IMarker marker) {
			TagSEAPlugin.run(getOperation(), false);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IMarkerResolution2#getDescription()
		 */
		public String getDescription() {
			String uname = System.getProperty("user.name");
			if (author && date) {
				return "Set the author to " + uname + " and the date to today";
			} else if (author) {
				return "Set the author to " + uname;
			} else if (date) {
				return "Set the date to today";
			}
			return "";
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IMarkerResolution2#getImage()
		 */
		public Image getImage() {
			return null;
		}
		public TagSEAOperation getOperation() {
			return new TagSEAOperation("Fixing Java Waypoint...") {

				@Override
				public IStatus run(IProgressMonitor monitor) throws InvocationTargetException {
					MultiStatus status = new MultiStatus(JavaTagsPlugin.PLUGIN_ID, 0, "", null);
					monitor.beginTask("", 1);
					if (date) {
						status.merge(waypoint.setDate(new Date()).getStatus());
					}
					if (author) {
						String authorName = System.getProperty("user.name");
						if (authorName != null)
							status.merge(waypoint.setAuthor(authorName).getStatus());
					}
					monitor.done();
					return status;
				}
				
			};
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IMarkerResolutionGenerator#getResolutions(org.eclipse.core.resources.IMarker)
	 */
	public IMarkerResolution[] getResolutions(IMarker marker) {
		IWaypoint wp = JavaWaypointUtils.getWaypointForMarker(marker);
		if (wp == null) return new IMarkerResolution[0];
		AbstractWaypointDelegate delegate = JavaTagsPlugin.getJavaWaypointDelegate();
		Date date = wp.getDate();
		String author = wp.getAuthor();
		LinkedList<IMarkerResolution> resolutions = new LinkedList<IMarkerResolution>();
		if (date == null || date.equals(delegate.getDefaultValue(IWaypoint.ATTR_DATE)))
			resolutions.add(new Resolution(true, false, wp));
		if (author == null || author.equals(delegate.getDefaultValue(IWaypoint.ATTR_AUTHOR)))
			resolutions.add(new Resolution(false, true, wp));
		if (resolutions.size() == 2) {
			resolutions.addFirst(new Resolution(true, true, wp));
		}
		return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IMarkerResolutionGenerator2#hasResolutions(org.eclipse.core.resources.IMarker)
	 */
	public boolean hasResolutions(IMarker marker) {
		IWaypoint wp = JavaWaypointUtils.getWaypointForMarker(marker);
		AbstractWaypointDelegate delegate = JavaTagsPlugin.getJavaWaypointDelegate();
		Date date = wp.getDate();
		String author = wp.getAuthor();
		if (date == null || date.equals(delegate.getDefaultValue(IWaypoint.ATTR_DATE)))
			return true;
		if (author == null || author.equals(delegate.getDefaultValue(IWaypoint.ATTR_AUTHOR)))
			return true;
		return false;
	}

}
