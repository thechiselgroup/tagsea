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

import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.ui.adapters.StructuredContentProviderAdapter;

/**
 * 
 * @author mdesmond
 *
 */
public class TagTableContentProvider extends StructuredContentProviderAdapter
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) 
	{
		return TagCorePlugin.getDefault().getTagCore().getTagModel().getTags();
	}
}
