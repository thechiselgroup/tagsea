/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core.ui.tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ITagChangeEvent;
import net.sourceforge.tagsea.core.ITagChangeListener;
import net.sourceforge.tagsea.core.TagDelta;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;

/**
 * A content proposal provider that is hooked to a parent control, and will become invalid
 * after that control is disposed.
 * @author Del Myers
 */

public class TagProposalProvider implements IContentProposalProvider {

	private ITag[] tags;
	private Control parentControl;
	private TagChangeListener tagListener;
	
	
	private class TagChangeListener implements ITagChangeListener {

		/* (non-Javadoc)
		 * @see net.sourceforge.tagsea.core.ITagChangeListener#tagsChanged(net.sourceforge.tagsea.core.TagDelta)
		 */
		public void tagsChanged(TagDelta delta) {
			//no need to do anything if the tags are already null.
			if (TagProposalProvider.this.tags == null) return;
			for (ITagChangeEvent event : delta.events) {
				if (isRelatedType(event)) {
					TagProposalProvider.this.tags = null;
					break;
				}
			}
		}

		private boolean isRelatedType(ITagChangeEvent event) {
			int type = event.getType();
			return (type != ITagChangeEvent.WAYPOINTS);
		}
	}
	
	private class TagComparator implements Comparator<ITag> {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(ITag o1, ITag o2) {
			String name1 = o1.getName().toLowerCase();
			String name2 = o2.getName().toLowerCase();
			return name1.compareTo(name2);
		}
		
	}
	
	public TagProposalProvider(Control parentControl) {
		this.tags = null;
		this.parentControl = parentControl;
		this.tagListener = new TagChangeListener();
		TagSEAPlugin.addTagChangeListener(tagListener);
		hookControl();
	}
	
	/**
	 * hooks this provider to the control so that we can stop listening for tag changes. 
	 */
	private void hookControl() {
		parentControl.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				TagSEAPlugin.removeTagChangeListener(tagListener);
			}
			
		});		
	}
	
	/**
	 * Gets all tags and sorts them according to case-insensitive names.
	 */
	public synchronized void refreshTags() {
		ArrayList<ITag> tagList = new ArrayList<ITag>(
			Arrays.asList(TagSEAPlugin.getTagsModel().getAllTags())
		);
		Collections.sort(tagList, new TagComparator());
		this.tags = tagList.toArray(new ITag[tagList.size()]);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.fieldassist.IContentProposalProvider#getProposals(java.lang.String, int)
	 */
	public synchronized IContentProposal[] getProposals(String contents, int position) {
		String lastTagName = getLastWord(contents, position);
		ITag[] matches = getMatchingTags(lastTagName.toLowerCase());
		IContentProposal[] proposals = new IContentProposal[matches.length];
		for (int i = 0; i < matches.length; i++) {
			proposals[i] = new TagContentProposal(matches[i], lastTagName.length());
		}
		return proposals;
	}

	/**
	 * @return
	 */
	private ITag[] getMatchingTags(String name) {
		if (tags == null) {
			refreshTags();
		}
		if (name == null || "".equals(name)) return tags;
		int closestIndex = findClosestMatch(name, 0, tags.length-1);
		return tagsStaringWith(name, closestIndex);
	}

	/**
	 * @param name
	 * @param closestIndex
	 * @return
	 */
	private ITag[] tagsStaringWith(String name, int closestIndex) {
		ArrayList<ITag> tagList = new ArrayList<ITag>();
		for (int i = closestIndex ; i < tags.length; i++) {
			String tagName = tags[i].getName().toLowerCase();
			if (tagName.startsWith(name)) {
				tagList.add(tags[i]);
			} else {
				break;
			}
		}
		return tagList.toArray(new ITag[tagList.size()]);
	}

	/**
	 * Finds the lowest index of a tag matching the given lower-case name. Or, if
	 * a match cannot be found, it returns the next-lowest index.
	 * @param name
	 * @param i
	 * @param j
	 * @return
	 */
	private int findClosestMatch(String name, int i, int j) {
		if (i >= j) {
			if (!(tags[i].getName().toLowerCase().startsWith(name)))
				i++;
			return(iterateToLowest(i, name));
		}
		int index = i + ((j-i)/2);
		String tagName = tags[index].getName().toLowerCase();
		int diff = name.compareTo(tagName);
		if (diff == 0) {
			return iterateToLowest(index, name);
		} else if (diff < 0) {
			return findClosestMatch(name, i, index-1);
		} else {
			return findClosestMatch(name, index+1, j);
		}
	}

	/**
	 * @param i
	 * @param name
	 * @return
	 */
	private int iterateToLowest(int i, String name) {
		String tagName = tags[i].getName().toLowerCase();
		if (!name.equals(tagName)) return i;
		i--;
		while (i >= 0) {
			tagName = tags[i].getName().toLowerCase();
			if (!name.equals(tagName)) return i+1;
			i--;
		}
		return i+1;
	}

	/**
	 * @param contents
	 * @return
	 */
	private String getLastWord(String contents, int position) {
		//scan backwards until we match a whitespace.
		int i = position-1;
		while ( i>0 && !Character.isWhitespace(contents.charAt(i)) )
			i--;
		if (i < 0) return "";
		String s = contents.substring(i,position).trim().toLowerCase();
		return s;
	}

}
