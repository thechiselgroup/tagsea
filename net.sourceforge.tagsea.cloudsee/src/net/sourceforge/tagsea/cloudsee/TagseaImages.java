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
package net.sourceforge.tagsea.cloudsee;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;


/**
 * A convenience class for loading the tagsea images into the plugin ImageRegistry.
 * It also contains methods for getting the images and descriptors.
 * 
 * @author Chris Callendar
 */
public class TagseaImages implements ITagseaImages {

	private ImageRegistry getRegistry() {
		return CloudSeePlugin.getDefault().getImageRegistry();
	}
	
	/**
	 * Loads all the images into the registry.
	 */
	public void loadImages() {
		ImageRegistry reg = getRegistry();


		loadImageDescriptorIntoRegistry(reg, IMG_UP_ARROW, PATH_UP_ARROW);
		loadImageDescriptorIntoRegistry(reg, IMG_UP_ARROW_DISABLED, PATH_UP_ARROW_DISABLED);
		loadImageDescriptorIntoRegistry(reg, IMG_DOWN_ARROW, PATH_DOWN_ARROW);
		loadImageDescriptorIntoRegistry(reg, IMG_DOWN_ARROW_DISABLED, PATH_DOWN_ARROW_DISABLED);
		

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
			descriptor = CloudSeePlugin.imageDescriptorFromPlugin(CloudSeePlugin.PLUGIN_ID, path);
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
