package net.sourceforge.tagsea.resources.ui;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.waypoints.ResourceWaypointMarkerHelp;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;
import org.eclipse.ui.texteditor.MarkerAnnotation;


public class WaypointAnnotationImageProvider implements IAnnotationImageProvider {

	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getImageDescriptor(java.lang.String)
	 */
	public ImageDescriptor getImageDescriptor(String imageDescritporId) {
		return ResourceWaypointPlugin.getDefault().getImageRegistry().getDescriptor(imageDescritporId);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getImageDescriptorId(org.eclipse.jface.text.source.Annotation)
	 */
	public String getImageDescriptorId(Annotation annotation) {
		if (annotation instanceof MarkerAnnotation) {
			IMarker marker = ((MarkerAnnotation)annotation).getMarker();
			try {
				if (marker.isSubtypeOf(TagSEAPlugin.MARKER_ID)) {
					return ITagSEAImageConstants.IMG_WAYPOINT;
				}
			} catch (CoreException e) {
			}
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getManagedImage(org.eclipse.jface.text.source.Annotation)
	 */
	public Image getManagedImage(Annotation annotation) {
		if (annotation instanceof MarkerAnnotation) {
			IMarker marker = ((MarkerAnnotation)annotation).getMarker();
			IWaypoint waypoint = ResourceWaypointMarkerHelp.getWaypoint(marker);
			if (waypoint != null) {
				return TagSEAPlugin.getDefault().getUI().getImage(waypoint);
			}
		}
		String id = getImageDescriptorId(annotation);
		return ResourceWaypointPlugin.getDefault().getImageRegistry().get(id);
	}

}
