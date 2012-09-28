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
package net.sourceforge.tagsea.core.ui.internal.waypoints;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ui.adapters.HyperLinkListenerAdapter;
import net.sourceforge.tagsea.core.ui.tags.TagsView;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.HyperlinkEvent;


/**
 * 
 * @author mdesmond
 * @author Del Myers
 *
 */
@Deprecated
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
			TagsView view = (TagsView)TagSEAPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(TagsView.ID);
			
			if(view != null)
			{
				String tagName = he.getLabel();
				ITag tag = TagSEAPlugin.getTagsModel().getTag(tagName);
				
				if(tag != null)
				{
					view.setSelection(new StructuredSelection(tag));
				}
			}
			
		} 
		catch (PartInitException e) 
		{
			e.printStackTrace();
		}
	}
}
