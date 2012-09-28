package net.sourceforge.tagsea.c.resources.internal;

import java.util.Date;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.c.CWaypointUI;
import net.sourceforge.tagsea.c.CWaypointUtils;
import net.sourceforge.tagsea.c.CWaypointsPlugin;
import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;
import org.eclipse.ui.texteditor.MarkerAnnotation;

public class CWaypointAnnotationImageProvider implements IAnnotationImageProvider {

	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getImageDescriptor(java.lang.String)
	 */
	public ImageDescriptor getImageDescriptor(String imageDescritporId) {
		return CWaypointsPlugin.getDefault().getImageRegistry().getDescriptor(imageDescritporId);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getImageDescriptorId(org.eclipse.jface.text.source.Annotation)
	 */
	public String getImageDescriptorId(Annotation annotation) {
		if (annotation instanceof MarkerAnnotation) {
			IWaypoint wp = CWaypointUtils.getWaypointForMarker(((MarkerAnnotation)annotation).getMarker());
			if (wp != null) {
				AbstractWaypointDelegate delegate = CWaypointsPlugin.getCWaypointDelegate();
				Date date = wp.getDate();
				String author = wp.getAuthor();
				if (date == null || date.equals(delegate.getDefaultValue(IWaypoint.ATTR_DATE)))
					return CWaypointUI.IMAGE_C_WAYPOINT_QUICKFIX;
				if (author == null || author.equals(delegate.getDefaultValue(IWaypoint.ATTR_AUTHOR)))
					return CWaypointUI.IMAGE_C_WAYPOINT_QUICKFIX;
				return CWaypointUI.IMAGE_C_WAYPOINT;
			}
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getManagedImage(org.eclipse.jface.text.source.Annotation)
	 */
	public Image getManagedImage(Annotation annotation) {
		return CWaypointsPlugin.getDefault().getImageRegistry().get(getImageDescriptorId(annotation));
	}

}
