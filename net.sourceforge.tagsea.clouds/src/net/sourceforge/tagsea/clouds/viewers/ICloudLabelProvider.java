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
package net.sourceforge.tagsea.clouds.viewers;

import org.eclipse.jface.viewers.ILabelProvider;

/**
 * A label provider that provides a "priority" for items in a tag cloud to adjust the size in the
 * layout.
 * @author Del Myers
 *
 */
public interface ICloudLabelProvider extends ILabelProvider {
	/**
	 * Returns a relative priority for the given element. Larger priorities will have larger sizes in the
	 * layout.
	 * @param element the element to prioritise.
	 * @return the element priority.
	 */
	public int getPriority(Object element);
}
