/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * A convenience class for loading the tagsea images into the plugin ImageRegistry.
 * It also contains methods for getting the images and descriptors.
 * 
 * @author Chris Callendar
 */
public class TagseaImages implements ITagseaImages {

	private ImageRegistry getRegistry() {
		return TagSEAPlugin.getDefault().getImageRegistry();
	}
	
	/**
	 * Loads all the images into the registry.
	 */
	public void loadImages() {
		ImageRegistry reg = getRegistry();

		loadImageDescriptorIntoRegistry(reg, IMG_WAYPOINT, PATH_WAYPOINT);
		loadImageDescriptorIntoRegistry(reg, IMG_WAYPOINT24, PATH_WAYPOINT24);

		loadImageDescriptorIntoRegistry(reg, IMG_ROUTE, PATH_ROUTE);
		loadImageDescriptorIntoRegistry(reg, IMG_ROUTE_DISABLED, PATH_ROUTE_DSIABLED);
		
		loadImageDescriptorIntoRegistry(reg, IMG_UP_ARROW, PATH_UP_ARROW);
		loadImageDescriptorIntoRegistry(reg, IMG_UP_ARROW_DISABLED, PATH_UP_ARROW_DISABLED);
		loadImageDescriptorIntoRegistry(reg, IMG_DOWN_ARROW, PATH_DOWN_ARROW);
		loadImageDescriptorIntoRegistry(reg, IMG_DOWN_ARROW_DISABLED, PATH_DOWN_ARROW_DISABLED);
		
		loadImageDescriptorIntoRegistry(reg, IMG_CLEAR, PATH_CLEAR);
		loadImageDescriptorIntoRegistry(reg, IMG_CLEAR_DISABLED, PATH_CLEAR_DISABLED);

		// load the shared workbench images
		final ISharedImages workbenchImages = PlatformUI.getWorkbench().getSharedImages();
		reg.put(IMG_TOOL_DELETE, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		reg.put(IMG_TOOL_DELETE_DISABLED, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		reg.put(IMG_TOOL_BACK, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
		reg.put(IMG_TOOL_BACK_DISABLED, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED));
		reg.put(IMG_TOOL_FORWARD, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
		reg.put(IMG_TOOL_FORWARD_DISABLED, workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD_DISABLED));

		// load any additional more images here
		
	}
	
	
	/**
	 * Loads an image descriptor into the registry if it doesn't already exist.
	 * @param reg the image registry
	 * @param key the key for the image in the registry
	 * @param path the path to the image (relative to the plugin directory (e.g. /icons/icon.gif) 
	 */
	private void loadImageDescriptorIntoRegistry(ImageRegistry reg, String key, String path) {
		ImageDescriptor descriptor = reg.getDescriptor(key);
		if (descriptor == null) {
			descriptor = TagSEAPlugin.getImageDescriptor(path);
			if(descriptor != null) {
				reg.put(key, descriptor);
			}
		}
	}	
	
	/**
	 * Gets the image from the registry for the given key.
	 */
	public Image getImage(String key) {
		return getRegistry().get(key);
	}

	/**
	 * Gets the image descriptor from the registry for the give key.
	 */
	public ImageDescriptor getDescriptor(String key) {
		return getRegistry().getDescriptor(key);
	}

}
