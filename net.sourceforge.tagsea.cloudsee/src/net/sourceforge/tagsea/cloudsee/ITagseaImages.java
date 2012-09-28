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
import org.eclipse.swt.graphics.Image;


/**
 * Interface containing the TagSEA image paths and keys for the ImageRegistry.
 * If images are added here, they must also be added to the {@link TagseaImages#loadImages()} method.
 * 
 * @author Chris Callendar
 * @see ca.uvic.cs.tagsea.TagseaImages
 */
public interface ITagseaImages {
	
	/** The key for the up arrow image. */
	public static final String IMG_UP_ARROW = "IMG_UP_ARROW";
	/** The path for the up arrow image. */
	public static final String PATH_UP_ARROW = "icons/up.gif";
	/** The key for the disabled up arrow image. */
	public static final String IMG_UP_ARROW_DISABLED = "IMG_UP_ARROW_DISABLED";
	/** The path for the disabled up arrow image. */
	public static final String PATH_UP_ARROW_DISABLED = "icons/up_disabled.gif";
	
	/** The key for the down arrow image. */
	public static final String IMG_DOWN_ARROW = "IMG_DOWN_ARROW";
	/** The path for the down arrow image. */
	public static final String PATH_DOWN_ARROW = "icons/down.gif";
	/** The key for the disabled down arrow image. */
	public static final String IMG_DOWN_ARROW_DISABLED = "IMG_DOWN_ARROW_DISABLED";
	/** The path for the disabled down arrow image. */
	public static final String PATH_DOWN_ARROW_DISABLED = "icons/down_disabled.gif";

	

	/**
	 * Loads all the TagSEA images into the ImageRegistry.
	 * All image constants in this file should be loaded in this method.
	 */
	public void loadImages();
	
    /**
     * Retrieves the specified image from the workbench plugin's image registry.
     * Note: The returned <code>Image</code> is managed by the workbench; clients
     * must <b>not</b> dispose of the returned image.
     *
     * @param key the symbolic name of the image; there are constants
     * declared in this interface for build-in images that come with the workbench
     * @return the image, or <code>null</code> if not found
     */
    public Image getImage(String key);

    /**
     * Retrieves the image descriptor for specified image from the workbench's
     * image registry. Unlike <code>Image</code>s, image descriptors themselves do
     * not need to be disposed.
     *
     * @param key the symbolic name of the image; there are constants
     * declared in this interface for build-in images that come with the workbench
     * @return the image descriptor, or <code>null</code> if not found
     */
    public ImageDescriptor getDescriptor(String key);	
}
