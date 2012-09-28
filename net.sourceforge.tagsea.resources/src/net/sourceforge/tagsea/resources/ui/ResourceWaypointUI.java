package net.sourceforge.tagsea.resources.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.core.ui.BaseWaypointUI;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.resources.IResourceWaypointAttributes;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ResourceWaypointUtils;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ResourceWaypointUI extends BaseWaypointUI {
	/**
	 * An image registry that uses a base image as the key to the registry. This is used so that
	 * we don't have to create multiple overlay images for a single base. Only the target images
	 * get disposed when displays are disposed. It is expeceted that the base images get disposed
	 * by another mechanism.
	 * @author Del Myers
	 *
	 */
	private class ImageKeyedImageRegistry implements Listener {
		private HashMap<Image, Image> map;
		private HashSet<Display> displays;
		/**
		 * 
		 */
		public ImageKeyedImageRegistry() {
			this.map = new HashMap<Image, Image>();
			displays = new HashSet<Display>();
		}
		
		public void put(Image base, Image target) {
			Display baseDisp = (Display)base.getDevice();
			if (!displays.contains(baseDisp)) {
				baseDisp.addListener(SWT.Dispose, this);
				displays.add(baseDisp);
			}
			Display targetDisp = (Display)target.getDevice();
			if (!displays.contains(targetDisp)) {
				targetDisp.addListener(SWT.Dispose, this);
				displays.add(targetDisp);
			}
			this.map.put(base, target);
			
		}
		
		public Image get(Image base) {
			return map.get(base);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		public void handleEvent(Event event) {
			if (displays.contains(event.display)) {
				for (Iterator<Image> it = map.keySet().iterator(); it.hasNext();) {
					Image key = it.next();
					Image value =  map.get(key);
					if (!value.isDisposed()) {
						if (key.isDisposed()) {
							value.dispose();
						} else if ((key.getDevice().equals(event.display) || value.getDevice().equals(event.display))) {
							value.dispose();
						}
						it.remove();
					}
				}
				displays.remove(event.display);
			}
		}
	}
	private WorkbenchLabelProvider labelProvider;
	private ImageKeyedImageRegistry imageRegistry;
	private Image missingResourceImage;

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#canUIChange(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	@Override
	public boolean canUIChange(IWaypoint waypoint, String attribute) {
		return !(
			IResourceWaypointAttributes.ATTR_RESOURCE.equals(attribute) ||
			IResourceWaypointAttributes.ATTR_STAMP.equals(attribute) ||
			IResourceWaypointAttributes.ATTR_REVISION.equals(attribute)
		);
	}

	@Override
	public String[] getVisibleAttributes() 
	{
		String[] visible =  super.getVisibleAttributes();
		List<String> list = new ArrayList<String>();

		for(String attr : visible)
			list.add(attr);

		list.remove(IResourceWaypointAttributes.ATTR_CHAR_START);
		list.remove(IResourceWaypointAttributes.ATTR_CHAR_END);
		list.remove(IResourceWaypointAttributes.ATTR_HANDLE_IDENTIFIER);
		list.remove(IResourceWaypointAttributes.ATTR_STAMP);
		list.remove(IResourceWaypointAttributes.ATTR_REVISION);

		return list.toArray(new String[0]);
	}


	@Override
	public Image getImage(IWaypoint waypoint) 
	{
		if(waypoint.getIntValue(IResourceWaypointAttributes.ATTR_CHAR_START, -1) > 0)
			return ResourceWaypointPlugin.getDefault().getImageRegistry().get(ISharedImages.IMG_TEXT);
		
		IResource resource = ResourceWaypointUtils.getResource(waypoint);
		if (resource != null) {
			if (imageRegistry == null) {
				imageRegistry = new ImageKeyedImageRegistry();
			}
			
			if (labelProvider == null) {
				labelProvider = new WorkbenchLabelProvider();
				PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener(){
					public void postShutdown(IWorkbench workbench) {
					}

					public boolean preShutdown(IWorkbench workbench, boolean forced) {
						labelProvider.dispose();
						return true;
					}
				});
			}
			Image providerImage = labelProvider.getImage(resource);
			if (providerImage != null) {
				Image image = imageRegistry.get(providerImage);
				if (image == null) {
					Image base = labelProvider.getImage(resource);
					if (base != null) {
						ImageDescriptor overlay = ResourceWaypointPlugin.getDefault().getImageRegistry().getDescriptor(ISharedImages.IMG_OVERLAY);
						OverlayImageDescriptor desc = new OverlayImageDescriptor(ImageDescriptor.createFromImage(base), overlay, OverlayImageDescriptor.LEFT, OverlayImageDescriptor.TOP);
						imageRegistry.put(providerImage, desc.createImage());
						image =  imageRegistry.get(providerImage);
					}
				}
				if (image != null) {
					return image;
				}
			}
		} else {
			if (missingResourceImage == null) {
				ImageDescriptor base = ResourceWaypointPlugin.
					getDefault().
					getImageRegistry().
					getDescriptor(ITagSEAImageConstants.IMG_WAYPOINT);
				ImageDescriptor overlay = 
					ResourceWaypointPlugin.getImageDescriptor("icons/questionoverlay.gif");
				OverlayImageDescriptor desc = new OverlayImageDescriptor(base, overlay, OverlayImageDescriptor.BOTTOM, OverlayImageDescriptor.RIGHT);
				missingResourceImage = desc.createImage();
				Display.getCurrent().addListener(SWT.Dispose, new Listener(){
					public void handleEvent(Event event) {
						if (missingResourceImage != null && !missingResourceImage.isDisposed()) {
							missingResourceImage.dispose();
						}
					}
				});
			}
			return missingResourceImage;
		}
		return ResourceWaypointPlugin.
			getDefault().
			getImageRegistry().
			get(ITagSEAImageConstants.IMG_WAYPOINT);
			
	}

	@Override
	public String getLabel(IWaypoint waypoint) 
	{
		String handleIdentifier =waypoint.getStringValue(IResourceWaypointAttributes.ATTR_HANDLE_IDENTIFIER, null);
		int charStart = waypoint.getIntValue(IResourceWaypointAttributes.ATTR_CHAR_START, -1);
		int charEnd = waypoint.getIntValue(IResourceWaypointAttributes.ATTR_CHAR_END, -1);

		if(charStart > 0 && charEnd > 0)
		{
			if(handleIdentifier != null && handleIdentifier.trim().length() > 0)
			{
				IJavaElement element = JavaCore.create(handleIdentifier);

				if(element!=null)
				{
					return element.getElementName() + " [offset=" + charStart  +  ",length=" + (charEnd-charStart) + "]" + (waypoint.getText().trim().length()>0?" - " + waypoint.getText():"");
				}
			}
			else
				return ResourceWaypointUtils.getResourcePath(waypoint).lastSegment() + " [offset=" + charStart  +  ",length=" + (charEnd-charStart) + "]" + (waypoint.getText().trim().length()>0?" - " + waypoint.getText():"");
	
		}

		return ResourceWaypointUtils.getResourcePath(waypoint).lastSegment() + ": " + waypoint.getText();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.BaseWaypointUI#getValueLabel(net.sourceforge.tagsea.core.IWaypoint, java.lang.String)
	 */
	@Override
	public String getValueLabel(IWaypoint waypoint, String attribute, Object value) {
		String label = super.getValueLabel(waypoint, attribute, value);
		if (IResourceWaypointAttributes.ATTR_RESOURCE.equals(attribute)) {
			if (value instanceof String) {
				IResource resource = ResourceWaypointUtils.getResource(waypoint);
				if (resource == null || !resource.exists()) {
					label = "!!!" + label + "!!!";
				}
			} else if (value == null) {
				IResource resource = ResourceWaypointUtils.getResource(waypoint);
				if (resource == null || !resource.exists()) {
					label = "!!!" + label + "!!!";
				}
			}
		}
		return label;
	}
	
	public String getLocationString(IWaypoint waypoint) {
		int line = waypoint.getIntValue(IResourceWaypointAttributes.ATTR_LINE, -1);
		int start = waypoint.getIntValue(IResourceWaypointAttributes.ATTR_CHAR_START, -1);
		int end = waypoint.getIntValue(IResourceWaypointAttributes.ATTR_CHAR_END, -1);
		String resourceString = waypoint.getStringValue(IResourceWaypointAttributes.ATTR_RESOURCE, "");
		if (!"".equals(resourceString)) {
			int slash = resourceString.lastIndexOf('/');
			if (slash != -1 && slash < resourceString.length()-1) {
				resourceString = resourceString.substring(slash+1);
			}
		}
		String result = resourceString;
		if (!"".equals(result)) {
			String locationString = "";
			if (line != -1) {
				locationString = "" + line;
			} else if (start != -1 && end != -1) {
				locationString = start + "-" + end;
			}
			if (!"".equals(locationString)) {
				result = result + " (" +locationString +")";
			}
		}
		return result;
	}

}
