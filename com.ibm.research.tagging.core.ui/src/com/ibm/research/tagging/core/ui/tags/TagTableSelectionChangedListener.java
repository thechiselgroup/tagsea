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

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.ui.TagUIPlugin;

/**
 * 
 * @author mdesmond
 *
 */
public class TagTableSelectionChangedListener implements ISelectionChangedListener 
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) 
	{
		// Get the selection
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		
		if(TagUIPlugin.getDefault().getTagView().isLinkedWithWaypointView())
		{
			List<ITag> tagList = selection.toList();

			// Convert to array 
			ITag[] tags = new ITag[0];
			tags = tagList.toArray(tags);

			// Fire the selection event
			TagUIPlugin.getDefault().getTagUI().tagSelectionChanged(tags);
		}
		
		if(selection.size() == 1)
		{
			TagUIPlugin.getDefault().getTagView().getRenameTagAction().setEnabled(true);
		}
		else
		{
			TagUIPlugin.getDefault().getTagView().getRenameTagAction().setEnabled(false);
		}
	}
}
