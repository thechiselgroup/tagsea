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

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * Simple image descriptor that adds padding in size to the given base image to make it have
 * a width of at least 22.
 * @author Del Myers
 *
 */
class PaddedImageDescriptor extends CompositeImageDescriptor {
	
	public static final int WIDTH = 22;
	private Image base;

	public PaddedImageDescriptor(Image base) {
		this.base = base;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.resource.CompositeImageDescriptor#drawCompositeImage(int, int)
	 */
	@Override
	protected void drawCompositeImage(int width, int height) {
		drawImage(base.getImageData(), 0, 0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.resource.CompositeImageDescriptor#getSize()
	 */
	@Override
	protected Point getSize() {
		int x = Math.max(WIDTH, base.getImageData().width);
		return new Point(x, base.getImageData().height);
	}

}
