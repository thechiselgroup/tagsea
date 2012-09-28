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

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.ITagModel;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.ui.validation.DuplicateTagNameValidator;
import com.ibm.research.tagging.core.ui.validation.NoWhitespaceNameValidator;

/**
 * 
 * @author mdesmond
 *
 */
public class TagViewListener implements ITagViewListener {

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.ui.tags.ITagViewListener#createTag(com.ibm.research.tagging.ui.tags.TagView)
	 */
	public void createTag(TagView view) 
	{
		DuplicateTagNameValidator validator = new DuplicateTagNameValidator();
		InputDialog dialog = new InputDialog(view.getTagTableViewer().getTable().getShell(), 
				"Create a new tag", "Enter a name for the new tag:","", validator);
	
		if (dialog.open() == InputDialog.OK) 
		{
			ITag tag = TagCorePlugin.getDefault().getTagCore().getTagModel().addTag(dialog.getValue());			
			view.getTagTableViewer().refresh();
			view.getTagTableViewer().setSelection(new StructuredSelection(tag), true);
			view.getTagTableViewer().getControl().setFocus();		
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.ui.tags.ITagViewListener#deleteTag(com.ibm.research.tagging.ui.tags.TagView)
	 */
	public void deleteTag(TagView view) 
	{
		IStructuredSelection tagSelection = (IStructuredSelection)view.getTagTableViewer().getSelection();
		
		boolean doIt = true;
		
	    if (!MessageDialog.openQuestion(view.getSite().getShell(), "Tag View", "Are you sure you want to delete the selected tag(s)?")) 
	    	doIt = false;			

		if(doIt)
		{
			for(Object o : tagSelection.toArray())
			{
				ITag tag = (ITag)o;
				TagCorePlugin.getDefault().getTagCore().getTagModel().removeTag(tag);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.ui.tags.ITagViewListener#renameTag(com.ibm.research.tagging.ui.tags.TagView)
	 */
	public void renameTag(TagView view) 
	{
		IStructuredSelection selection = (IStructuredSelection)view.getTagTableViewer().getSelection();
		
		if(selection.size() == 1)
		{
			ITag tag = (ITag)selection.toArray()[0];
			
			NoWhitespaceNameValidator validator = new NoWhitespaceNameValidator();

			InputDialog dialog = new InputDialog(view.getTagTableViewer().getTable().getShell(), 
					"Rename tag", "Enter the new name for the tag:", tag.getName() , validator);

			if (dialog.open() == InputDialog.OK) 
			{
				String newName = dialog.getValue().trim();	
				TagCorePlugin.getDefault().getTagCore().getTagModel().renameTag(tag, newName);
			}
		}
	}

	public void deleteUnusedTags(TagView view) 
	{
	    if (MessageDialog.openQuestion(view.getSite().getShell(), "Delete unused tags", "Are you sure you want to delete all un-used tags?")) 
	    {
	    	ITagModel collection = TagCorePlugin.getDefault().getTagCore().getTagModel();
		
	    	for(ITag tag : collection.getTags())
	    		if(tag.getWaypointCount() == 0)
	    			collection.removeTag(tag);
	    }
	}
}
