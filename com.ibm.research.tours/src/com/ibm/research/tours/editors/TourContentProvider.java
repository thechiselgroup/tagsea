/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package com.ibm.research.tours.editors;

import java.util.Vector;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TourContentProvider implements ITreeContentProvider
{
	private TourTreeAdapter fTourTreeAdapter;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) 
	{
		if(parentElement instanceof TourElements)
		{
			TourElements elements = (TourElements)parentElement;
			return elements.getTour().getElements();
		}
		
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) 
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) 
	{
		if(element instanceof TourElements)
			if( ((TourElements)(element)).getElementCount() > 0)
				return true;
		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) 
	{
		if(fTourTreeAdapter == null)
			return new Object[0];
		
		Vector<Object> elements = new Vector<Object>();
		//elements.add(fTourTreeAdapter.getTourTitle());
		//elements.add(fTourTreeAdapter.getTourAuthor());
		//elements.add(fTourTreeAdapter.getTourDescription());
		elements.add(fTourTreeAdapter.getTourElements());
		return elements.toArray(new Object[0]);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() 
	{
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) 
	{
		if(newInput != null && newInput instanceof TourTreeAdapter)
		{
			fTourTreeAdapter = (TourTreeAdapter)newInput;
		}
	}
}
