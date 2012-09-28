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

import net.sourceforge.tagsea.resources.ResourceWaypointPlugin;
import net.sourceforge.tagsea.resources.ui.OverlayImageDescriptor;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * Decorates elements that adapt to IResource. If the resource returned by the
 * adapter doesn't exist, a small ? is presented in the lower-right corner.
 * @author Del Myers
 *
 */
public class MissingResourceLabelDecorator implements ILabelDecorator {
	private HashMap<Image, Image> cachedImages;
	private ImageDescriptor overlay;
	public MissingResourceLabelDecorator() {
		cachedImages = new HashMap<Image, Image>();
		overlay = ResourceWaypointPlugin.getImageDescriptor("icons/questionoverlay.gif");
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object)
	 */
	public Image decorateImage(Image image, Object element) {
		IResource resource = null;
		if (image == null) return null;
		if (element instanceof IAdaptable) {
			resource = (IResource) ((IAdaptable)element).getAdapter(IResource.class);
		}
		if (resource == null) return image;
		if (resource.exists()) return image;
		Image result = cachedImages.get(image);
		if (result == null) {
			OverlayImageDescriptor desc = new OverlayImageDescriptor(ImageDescriptor.createFromImage(image), overlay, OverlayImageDescriptor.RIGHT, OverlayImageDescriptor.BOTTOM);
			result = desc.createImage();
			cachedImages.put(image, result);
		}
		return result;
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
		for (Image i : cachedImages.values()) {
			if (!i.isDisposed()) {
				i.dispose();
			}
		}
		cachedImages.clear();
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
