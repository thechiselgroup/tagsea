/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.resources.synchronize.ui;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.synchronize.WaypointSynchronizeObject;
import net.sourceforge.tagsea.resources.ui.ISharedImages;
import net.sourceforge.tagsea.resources.ui.OverlayImageDescriptor;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Label privder for the synchronizing view for resource waypoints.
 * @author Del Myers
 *
 */
public class WaypointSynchronizeLabelProvider implements ILabelProvider {
	private HashMap<Object, Image> cachedImages;
	private WorkbenchLabelProvider workbenchProvider;
	
	WaypointSynchronizeLabelProvider() {
		cachedImages = new HashMap<Object, Image>();
		workbenchProvider = new WorkbenchLabelProvider();
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof WaypointSynchronizeObject) {
			String primaryImageKey = ITagSEAImageConstants.IMG_WAYPOINT;
			String secondaryImageKey = null;
			WaypointSynchronizeObject sync = (WaypointSynchronizeObject) element;
			switch (sync.getKind()) {
			case WaypointSynchronizeObject.NEW_IN:
				secondaryImageKey = ISharedImages.IMG_NEW_IN;
				break;
			case WaypointSynchronizeObject.NEW_OUT:
				secondaryImageKey = ISharedImages.IMG_NEW_OUT;
				break;
			case WaypointSynchronizeObject.REMOVE_IN:
				secondaryImageKey = ISharedImages.IMG_REMOVE_IN;
				break;
			case WaypointSynchronizeObject.REMOVE_OUT:
				secondaryImageKey = ISharedImages.IMG_REMOVE_OUT;
				break;
			case WaypointSynchronizeObject.SYNCH_IN:
				secondaryImageKey = ISharedImages.IMG_SYNCH_IN;
				break;
			case WaypointSynchronizeObject.SYNCH_OUT:
				secondaryImageKey = ISharedImages.IMG_SYNCH_OUT;
				break;
			case WaypointSynchronizeObject.CONFLICT:
				secondaryImageKey = ISharedImages.IMG_CONFLICT;
				break;
			}
			if (secondaryImageKey == null) {
				return ResourceWaypointPlugin.getDefault().getImageRegistry().get(primaryImageKey);
			} else {
				String key = primaryImageKey + ":" + secondaryImageKey;
				Image image = cachedImages.get(key);
				if (image == null) {
					ImageDescriptor primary = ResourceWaypointPlugin.getDefault().getImageRegistry().getDescriptor(primaryImageKey);
					ImageDescriptor secondary = ResourceWaypointPlugin.getDefault().getImageRegistry().getDescriptor(secondaryImageKey);
					OverlayImageDescriptor desc = new OverlayImageDescriptor(primary, secondary, OverlayImageDescriptor.RIGHT, OverlayImageDescriptor.BOTTOM, new Point(22,16));
					image = desc.createImage();
					cachedImages.put(key, image);
				}
				return image;
			}
		} else if (element instanceof IPath) {
			IPath path = (IPath) element;
			IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
			if (r != null) {
				element = r;
			}
		}
		Image i = workbenchProvider.getImage(element);
		Image result;
		if (i.getBounds().width < 22) {
			result = cachedImages.get(i);
			if (result == null) {
				ImageDescriptor desc = new PaddedImageDescriptor(i);
				//desc.getImageData().width = 22;
				result = desc.createImage();
				cachedImages.put(i, result);
			}
		} else {
			result = i;
		}
		return result;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof WaypointSynchronizeObject) {
			WaypointSynchronizeObject sync = (WaypointSynchronizeObject) element;
			if (sync.getLocal() != null) {
				Path resourcePath = new Path(sync.getLocal().getResource());
				return sync.getLocal().getText() + " (" + resourcePath.lastSegment() + ")";
			} else {
				Path resourcePath = new Path(sync.getRemote().getResource());
				return sync.getRemote().getText() + " (" + resourcePath.lastSegment() + ")";
			}
		} else if (element instanceof IPath) {
			return ((IPath)element).removeFirstSegments(1).toPortableString();
		}
		return workbenchProvider.getText(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		for (Image i : cachedImages.values()) {
			if (!i.isDisposed()) {
				i.dispose();
			}
		}
		cachedImages.clear();
		workbenchProvider.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}

}
