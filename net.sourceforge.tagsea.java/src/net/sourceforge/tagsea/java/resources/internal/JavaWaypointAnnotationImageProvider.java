package net.sourceforge.tagsea.java.resources.internal;

import java.util.Date;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.java.JavaTagsPlugin;
import net.sourceforge.tagsea.java.JavaWaypointUI;
import net.sourceforge.tagsea.java.JavaWaypointUtils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;
import org.eclipse.ui.texteditor.MarkerAnnotation;

public class JavaWaypointAnnotationImageProvider implements IAnnotationImageProvider {

	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getImageDescriptor(java.lang.String)
	 */
	public ImageDescriptor getImageDescriptor(String imageDescritporId) {
		return JavaTagsPlugin.getDefault().getImageRegistry().getDescriptor(imageDescritporId);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getImageDescriptorId(org.eclipse.jface.text.source.Annotation)
	 */
	public String getImageDescriptorId(Annotation annotation) {
		if (annotation instanceof MarkerAnnotation) {
			IWaypoint wp = JavaWaypointUtils.getWaypointForMarker(((MarkerAnnotation)annotation).getMarker());
			if (wp != null) {
				AbstractWaypointDelegate delegate = JavaTagsPlugin.getJavaWaypointDelegate();
				Date date = wp.getDate();
				String author = wp.getAuthor();
				if (date == null || date.equals(delegate.getDefaultValue(IWaypoint.ATTR_DATE)))
					return JavaWaypointUI.IMAGE_JAVA_WAYPOINT_QUICKFIX;
				if (author == null || author.equals(delegate.getDefaultValue(IWaypoint.ATTR_AUTHOR)))
					return JavaWaypointUI.IMAGE_JAVA_WAYPOINT_QUICKFIX;
				return JavaWaypointUI.IMAGE_JAVA_WAYPOINT;
			}
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getManagedImage(org.eclipse.jface.text.source.Annotation)
	 */
	public Image getManagedImage(Annotation annotation) {
		return JavaTagsPlugin.getDefault().getImageRegistry().get(getImageDescriptorId(annotation));
	}

}
