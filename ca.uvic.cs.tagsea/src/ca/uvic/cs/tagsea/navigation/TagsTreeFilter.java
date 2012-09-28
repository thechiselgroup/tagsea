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
package ca.uvic.cs.tagsea.navigation;

import ca.uvic.cs.tagsea.core.Tag;


/**
 * Filters the Tags TreeViewer based on the tag name.
 * 
 * @author Chris Callendar
 */
public class TagsTreeFilter extends TreeFilter {

	public TagsTreeFilter() {
	}

	/**
	 * Returns the tag name.
	 * @return String the tag name.
	 */
	@Override
	public String getElementString(Object element) {
		if (element instanceof Tag) {
			Tag tag = (Tag) element;
			//@tag bug(sourceforge(1528033)) : now uses the id, so that all parents are traced.
			return tag.getId();
		}
		return super.getElementString(element);
	}
	
}
