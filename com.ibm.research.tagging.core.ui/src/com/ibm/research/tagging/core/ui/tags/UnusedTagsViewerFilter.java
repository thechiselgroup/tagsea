/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tagging.core.ui.tags;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.ibm.research.tagging.core.ITag;

/**
 * 
 * @author mdesmond
 *
 */
public class UnusedTagsViewerFilter extends ViewerFilter {

	private boolean fEnabled = false;
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) 
	{
		if(fEnabled)
		{
			if(element instanceof ITag)
			{
				ITag tag = (ITag)element;

				if(tag.getWaypointCount() > 0)
					return true;
				else
					return false;
			}
		}
		return true;
	}
	
	public void enable(boolean enabled)
	{
		fEnabled = enabled;
	}
}
