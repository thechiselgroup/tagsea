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
package com.ibm.research.tagging.core.ui.adapters;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * @author mdesmond
 *
 */
public class StructuredContentProviderAdapter implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) 
	{
		return null;
	}

	public void dispose() 
	{

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) 
	{

	}
}
