package net.sourceforge.tagsea.java;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.BaseWaypointUI;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;

public class JavaWaypointUI extends BaseWaypointUI {
	
	public static final String IMAGE_JAVA_WAYPOINT_QUICKFIX = "java.quickfix";
	/**
	 * The descriptor key for a java waypoint image.
	 */
	public static String IMAGE_JAVA_WAYPOINT = "java.waypoint";
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#canUIChange(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	@Override
	public boolean canUIChange(IWaypoint waypoint, String attribute) {
		return !(
			IJavaWaypointAttributes.ATTR_RESOURCE.equals(attribute) ||
			IJavaWaypointAttributes.ATTR_JAVA_ELEMENT.equals(attribute) ||
			IJavaWaypointAttributes.ATTR_CHAR_START.equals(attribute) ||
			IJavaWaypointAttributes.ATTR_CHAR_END.equals(attribute) ||
			IJavaWaypointAttributes.ATTR_LINE.equals(attribute)
		);
	}
	
	@Override
	public String[] getVisibleAttributes() {
		return new String[] {
			IJavaWaypointAttributes.ATTR_AUTHOR,
			IJavaWaypointAttributes.ATTR_DATE,
			IJavaWaypointAttributes.ATTR_MESSAGE,
			IJavaWaypointAttributes.ATTR_JAVA_ELEMENT,
			IJavaWaypointAttributes.ATTR_RESOURCE,
			IJavaWaypointAttributes.ATTR_CHAR_START,
			IJavaWaypointAttributes.ATTR_CHAR_END
		};
	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#getValueLabel(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	@Override
	public String getValueLabel(IWaypoint waypoint, String attribute) {
		if (IJavaWaypointAttributes.ATTR_JAVA_ELEMENT.equals(attribute)) {
			String value = waypoint.getStringValue(attribute, "");
			int colon = value.indexOf(':');
			if (colon >= 0) {
				return value.substring(colon+1);
			}
		}
		return super.getValueLabel(waypoint, attribute);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#getValueImage(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	@Override
	public Image getValueImage(IWaypoint waypoint, String attribute) {
		if (IJavaWaypointAttributes.ATTR_JAVA_ELEMENT.equals(attribute)) {
			IJavaElement element = JavaWaypointUtils.getJavaElement(waypoint);
			if (element != null) {
				JavaElementLabelProvider provider = new JavaElementLabelProvider();
				Image image = provider.getImage(element);
				if (image != null && !image.isDisposed()) {
					return image;
				}
				//return (ImageDescriptor) element.getAdapter(ImageDescriptor.class);
			}
		}
		//return super.getValueImage(waypoint, attribute);
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#getImage(net.sourceforge.tagsea.core.IWaypoint)
	 */
	@Override
	public Image getImage(IWaypoint waypoint) {
		return JavaTagsPlugin.getDefault().getImageRegistry().get(IMAGE_JAVA_WAYPOINT);
	}
	
	
	public String getLocationString(IWaypoint waypoint) {
		String element = getValueLabel(waypoint, IJavaWaypointAttributes.ATTR_JAVA_ELEMENT);
		int start = JavaWaypointUtils.getOffset(waypoint);
		int end = JavaWaypointUtils.getEnd(waypoint);
		String result = element;
		if (start != -1 && end != -1) {
			result = result + " [" +start+"-"+end+"]";
		}
		return result;
	}
	
}
