/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core.ui.internal.views;

import org.eclipse.jface.text.IRegion;

/**
 * Simple interface that finds a series of regions within text.
 * @author Del Myers
 */

public interface IStringFinder {
	
	/**
	 * Returns the regions found in the order that they were found. An
	 * empty array is returned if no regions were found.
	 * @param text
	 * @return
	 */
	public IRegion[] findRegions(String text);

}
