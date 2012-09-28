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
package net.sourceforge.tagsea.resources.sharing.ui;

import java.util.HashMap;

import net.sourceforge.tagsea.core.IWaypoint;
import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ui.ISharedImages;
import net.sourceforge.tagsea.resources.ui.OverlayImageDescriptor;
import net.sourceforge.tagsea.resources.waypoints.IResourceWaypointDescriptor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * Decorates an image to add a waypoint flag.
 * @author Del Myers
 *
 */
public class ResourceWaypointLabelDecorator implements ILabelDecorator {
	HashMap<Image, Image> imageRegistry;
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object)
	 */
	public Image decorateImage(Image image, Object element) {
		if (!(element instanceof IResourceWaypointDescriptor)) {
			Object adapted = null;
			if (element instanceof IAdaptable) {
				adapted = ((IAdaptable)element).getAdapter(IWaypoint.class);
			}
			if (adapted == null) return image;
		}
		if (imageRegistry == null) {
			imageRegistry = new HashMap<Image, Image>();
		}
		if (image != null) {
			Image result = imageRegistry.get(image);
			if (result == null) {
				ImageDescriptor overlay = ResourceWaypointPlugin.getDefault().getImageRegistry().getDescriptor(ISharedImages.IMG_OVERLAY);
				OverlayImageDescriptor desc = new OverlayImageDescriptor(ImageDescriptor.createFromImage(image), overlay, OverlayImageDescriptor.LEFT, OverlayImageDescriptor.TOP);
				result = desc.createImage();
				imageRegistry.put(image, result);
			}
			if (result != null) {
				return result;
			}
		}
		return image;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String, java.lang.Object)
	 */
	public String decorateText(String text, Object element) {
		return text;
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
		if (imageRegistry == null) return;
		for (Image value : imageRegistry.values()) {
			if (!value.isDisposed()) {
				value.dispose();
			}
		}
		imageRegistry = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}

}
