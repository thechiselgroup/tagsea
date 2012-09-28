package net.sourceforge.tagsea.parsed.core.internal;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;
import org.eclipse.ui.texteditor.MarkerAnnotation;

public class ParsedWaypointAnnotationImageProvider implements IAnnotationImageProvider {

	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getImageDescriptor(java.lang.String)
	 */
	public ImageDescriptor getImageDescriptor(String imageDescritporId) {
		return TagSEAPlugin.getDefault().getImageRegistry().getDescriptor(ITagSEAImageConstants.IMG_WAYPOINT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getImageDescriptorId(org.eclipse.jface.text.source.Annotation)
	 */
	public String getImageDescriptorId(Annotation annotation) {
		return ITagSEAImageConstants.IMG_WAYPOINT;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getManagedImage(org.eclipse.jface.text.source.Annotation)
	 */
	public Image getManagedImage(Annotation annotation) {
		if (annotation instanceof MarkerAnnotation) {
			IWaypoint wp = ParsedWaypointPlugin.getDefault().getParsedWaypointRegistry().getWaypointForMarker(((MarkerAnnotation)annotation).getMarker());
			if (wp != null) {
				return TagSEAPlugin.getDefault().getUI().getImage(wp);
			}
		}
		return TagSEAPlugin.getDefault().getImageRegistry().get(ITagSEAImageConstants.IMG_WAYPOINT);
	}
}
