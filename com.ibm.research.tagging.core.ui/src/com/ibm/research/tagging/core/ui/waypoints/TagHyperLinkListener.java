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
package com.ibm.research.tagging.core.ui.waypoints;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.TagCorePlugin;
import com.ibm.research.tagging.core.ui.adapters.HyperLinkListenerAdapter;
import com.ibm.research.tagging.core.ui.tags.TagView;

/**
 * 
 * @author mdesmond
 *
 */
public class TagHyperLinkListener extends HyperLinkListenerAdapter
{
	/*
	 * (non-Javadoc)
	 * @see com.ibm.research.tagging.adapters.HyperLinkListenerAdapter#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
	 */
	public void linkActivated(HyperlinkEvent he) 
	{
		try 
		{
			TagView view = (TagView)TagCorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(TagView.ID);
			
			if(view != null)
			{
				String tagName = he.getLabel();
				ITag tag = TagCorePlugin.getDefault().getTagCore().getTagModel().getTag(tagName);
				
				if(tag != null)
				{
					view.getTagTableViewer().setSelection(new StructuredSelection(tag),true);
				}
			}
			
		} 
		catch (PartInitException e) 
		{
			e.printStackTrace();
		}
	}
}
