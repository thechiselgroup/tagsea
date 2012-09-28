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
package net.sourceforge.tagsea.resources.ui;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;

/**
 * Image descriptor that takes a base image and an overlay. The size will be computed
 * as the maximum width and height of the two images. The base image is always drawn
 * centered and the overlay position is determined by the alignment values passed into 
 * the constructor.
 * @author Del Myers
 *
 */
public class OverlayImageDescriptor extends CompositeImageDescriptor {
	/**
	 * Vertical alignment constant for "top".
	 */
	public static final int TOP = 0;
	/**
	 * Vertical alignment constant for "bottom"
	 */
	public static final int BOTTOM = 1;
	/**
	 * Horizontal alignment constant for "left"
	 */
	public static final int LEFT = 0;
	/**
	 * Horizontal alignment constant for "right"
	 */
	public static final int RIGHT = 1;
	/**
	 * Horizontal and vertical alginment constant for "center"
	 */
	public static final int CENTER = 2;
	
	private ImageDescriptor base;
	private ImageDescriptor overlay;
	private int halign;
	private int valign;
	private Point size;

	
	/**
	 * Creates an image descriptor with centered alignment.
	 * @param base
	 * @param overlay
	 */
	public OverlayImageDescriptor(ImageDescriptor base, ImageDescriptor overlay) {
		this(base, overlay, CENTER, CENTER);
	}
	
	/**
	 * Creates an image descriptor with the given base and overlay using the given alignments.
	 * @param base
	 * @param overlay
	 * @param horizontal one of LEFT, RIGHT, or CENTER.
	 * @param vertical one of TOP, BOTTOM, or CENTER.
	 */
	public OverlayImageDescriptor(ImageDescriptor base, ImageDescriptor overlay, int horizontal, int vertical) {
		this.overlay = overlay;
		this.base = base;
		this.halign = horizontal;
		this.valign = vertical;
	}
	
	/**
	 * Creates an image descriptor with the given base and overlay using the given alignments. Uses the
	 * given size as an override to the default size calculation.
	 * @param base
	 * @param overlay
	 * @param horizontal one of LEFT, RIGHT, or CENTER.
	 * @param vertical one of TOP, BOTTOM, or CENTER.
	 */
	public OverlayImageDescriptor(ImageDescriptor base, ImageDescriptor overlay, int horizontal, int vertical, Point size) {
		this.overlay = overlay;
		this.base = base;
		this.halign = horizontal;
		this.valign = vertical;
		this.size = size;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.resource.CompositeImageDescriptor#drawCompositeImage(int, int)
	 */
	@Override
	protected void drawCompositeImage(int width, int height) {
		//draw the base
		int x = 0;//(width - base.getImageData().width)/2;
		int y = 0;//(height - base.getImageData().height)/2;
		drawImage(base.getImageData(), x, y);
		
		switch (halign) {
		case CENTER:
			x = (width-overlay.getImageData().width)/2;
			break;
		case LEFT:
			x = 0;
			break;
		case RIGHT:
			x = width-overlay.getImageData().width;
		}
		switch (valign) {
		case CENTER:
			y = (height-overlay.getImageData().height)/2;
			break;
		case TOP:
			y=0;
			break;
		case BOTTOM:
			y = height - overlay.getImageData().height; 
		}
		drawImage(overlay.getImageData(), x, y);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.resource.CompositeImageDescriptor#getSize()
	 */
	@Override
	protected Point getSize() {
		//return new Point(32, 32);
		if (size != null)
			return size;
		int x = Math.max(overlay.getImageData().width, base.getImageData().width);
		int y = Math.max(overlay.getImageData().height, base.getImageData().height);
		return new Point(x, y);
	}

}
