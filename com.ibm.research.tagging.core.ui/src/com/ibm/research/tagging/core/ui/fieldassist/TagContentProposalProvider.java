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
package com.ibm.research.tagging.core.ui.fieldassist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.ITagModelListener;
import com.ibm.research.tagging.core.IWaypoint;
import com.ibm.research.tagging.core.TagCorePlugin;

/**
 * simple tag provider for content-assist
 * 
 * @tag content-assist command-complete auto-complete field-assist : example of using ContentAssistField
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */
public class TagContentProposalProvider implements IContentProposalProvider {

	private static final int PREFIX_MAX = 5;  // maximum length of indices in prefixMap 
	
	private ITag[] tags;
	private Map<String, List<ITag>> prefixMap = new HashMap<String, List<ITag>>(); 
	private boolean ignoreAtSymbol = false;
	
	class TagProposal implements IContentProposal
	{
		private ITag tag;
		private int  offset;
		
		public TagProposal(ITag tag, int offset)
		{
			this.tag     = tag;
			this.offset  = offset;
		}
		
		public String getContent() {
			return tag.getName().substring(offset);
		}

		public int getCursorPosition() {
			return tag.getName().length();
		}

		public String getDescription() {
			return null;
		}

		public String getLabel() {
			return tag.getName() + " (" + tag.getWaypointCount() + ")";
		}
	}

	public TagContentProposalProvider()
	{
		updateTags();
		
		// if tags get changed, update our maps
		TagCorePlugin.getDefault().getTagCore().getTagModel().addTagModelListener(new ITagModelListener() {
			public void tagAdded(ITag tag) {
				updateTags();
			}

			public void tagRemoved(ITag tag, IWaypoint[] waypoints) {
				updateTags();
			}

			public void tagRenamed(ITag tag, String oldName) {
				updateTags();
			}
		});
		
	}
	
	public void setIgnoreAtSymbol(boolean flag)
	{
		ignoreAtSymbol = flag;
	}
	
	private void updateTags()
	{
		tags     = TagCorePlugin.getDefault().getTagCore().getTagModel().getTags();
		setupPrefixMap();
	}
	
	public IContentProposal[] getProposals(String contents, int position) {
		
		List<IContentProposal>   proposals = new ArrayList<IContentProposal>();

		if ( position>=contents.length())
			position = contents.length()-1;
		
		// walk back to first part of the word in the contents
		int i = position-1;
		
		while ( i>0 && !Character.isSpaceChar(contents.charAt(i)) )
			i--;

		int offset = position-i-1;
		if ( i==0 )
			offset++;
		
		if ( position==0 || offset<1 )
		{
			// if we have no text to work with, suggest everything

			// @tag todo tagging content-assist suggestions : would be nice to suggest tags related to selected content instead of showing everything
			for (ITag tag : tags)
				proposals.add(new TagProposal(tag,0));
		}
		else
		{
			String word        = contents.substring(i,position).trim().toLowerCase();
			
			if ( word.charAt(0)=='@' && ignoreAtSymbol )
			{
				word = word.substring(1);
			}
			
			List<ITag> matches = findTags(word);
			
			if ( matches!=null )
			{
				for (ITag tag : matches)
				{
					if ( tag.getName().toLowerCase().startsWith(word) )
						proposals.add(new TagProposal(tag,offset));
				}
			}
		}
		
		return proposals.toArray(new IContentProposal[proposals.size()]);
	}

	private void setupPrefixMap()
	{
		prefixMap.clear();
		for (ITag tag : tags)
		{
			String name = tag.getName();
			int    len  = (name.length()<PREFIX_MAX)? name.length() : PREFIX_MAX;
			for (int i=1; i<=len; i++)
			{
				String   prefix = name.substring(0,i).toLowerCase(); 
				List<ITag> list = prefixMap.get(prefix);
				if ( list==null )
					list = new ArrayList<ITag>();
				list.add(tag);
				prefixMap.put(prefix, list);
			}
		}
	}
	
	// assumes str is lowercase
	private List<ITag> findTags(String str)
	{
		List<ITag> matches = prefixMap.get(str);
		
		if ( matches==null )
			return null;
		
		if ( str.length()<PREFIX_MAX )
		{
			return matches;
		}
		
		// if we have words longer than PREFIX_MAX, use what we got from prefixMap and narrow down the matches
		List<ITag> submatches = new ArrayList<ITag>();
		
		for (ITag tag : matches)
		{
			if ( tag.getName().toLowerCase().startsWith(str) )
				submatches.add(tag);
		}
		
		return submatches;
	}
}
