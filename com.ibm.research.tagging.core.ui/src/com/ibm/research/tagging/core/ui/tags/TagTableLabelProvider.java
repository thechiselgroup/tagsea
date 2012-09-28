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

import org.eclipse.swt.graphics.Image;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.ui.TagUIPlugin;
import com.ibm.research.tagging.core.ui.adapters.TableLabelProviderAdapter;

/**
 * 
 * @author mdesmond
 *
 */
public class TagTableLabelProvider extends TableLabelProviderAdapter
{
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.adapters.TableLabelProviderAdapter#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) 
	{
		return TagUIPlugin.getDefault().getImageRegistry().get(TagUIPlugin.IMG_TAG);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) 
	{
		ITag tag = (ITag)element;
		return tag.getName() + "(" + tag.getWaypointCount() + ")";
	}
}
