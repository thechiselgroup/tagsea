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
package net.sourceforge.tagsea.core.ui.internal.views;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;


/**
 * Filters a tree based on words found in a pattern string. The words are
 * seperated by whitespace, and then checked against the tree labels to
 * see if any of the words match. If an item in the tree matches the pattern,
 * then the path from the root to that item passes teh filter, as do all of 
 * the children of the matching element.
 * 
 * Also implements IStringFinder so that the regions of the match can be found.
 * @author Del Myers
 */
public class CachingTreePatternFilter extends AbstractPatternFilter implements IStringFinder
{    
	/**
	 * The string pattern matcher used for this pattern filter.  
	 */
    private Vector<StringMatcher> matchers;
    
    
    /**
     * A map that caches visible elements for the specified viewer.
     */
    private HashMap<Viewer, HashSet<Object>> visibleElementsMap;
    
    /**
     * A map of viewer inputs to determine if the input of a viewer
     * has changed since the last invocation.
     */
    private HashMap<Viewer, Object> viewerInputs;
    
    /**
     * A map for watching dispose events.
     */
    private HashMap<Control, Viewer> disposeMap;


	private ViewerDisposeListener viewerDisposeListener;

	//private ViewerListener listener;
    
    private class ViewerDisposeListener implements DisposeListener {
    	/* (non-Javadoc)
		 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
		 */
		public void widgetDisposed(DisposeEvent e) {
			unhookViewer(disposeMap.get((Control)e.widget));
		}
    }
   
    
      
    public CachingTreePatternFilter() {
    	this.viewerDisposeListener = new ViewerDisposeListener();
    	this.visibleElementsMap = new HashMap<Viewer, HashSet<Object>>();
    	this.viewerInputs = new HashMap<Viewer, Object>();
    	this.disposeMap = new HashMap<Control, Viewer>();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#filter(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object[])
     */
    public final Object[] filter(Viewer viewer, Object parent, Object[] elements) 
    {     
    	if (matchers == null) 
			return elements;
 
        return super.filter(viewer, parent, elements);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public final boolean select(Viewer viewer, Object parentElement, Object element) 
    {
    	return isElementVisible(viewer, element);
    }
   	
 	/**
     * The pattern string for which this filter should select 
     * elements in the viewer.
     * 
     * @param patternString
     */
    public void setPattern(String patternString) 
    {
    	uhookAllViewers();
        if (patternString == null || patternString.equals("")) { //$NON-NLS-1$
        	matchers = null;
		}
        else 
		{			
			// Split on whitespace
			String[] words = patternString.split("[ \t]");
		
			for(int i = 0 ; i < words.length; i++)
			{
				if(!words[i].endsWith("*"))
					words[i] = words[i] + "*";
			}
			
			if(words.length > 0)
			{
				matchers = new Vector<StringMatcher>();
				for(String word : words)
				{
					matchers.add(new StringMatcher(word, true, false));
				}
			}
		}
    }

    /**
	 * Unhooks all the viewers to clear the cache.
	 */
	private void uhookAllViewers() {
		ArrayList<Viewer> viewers = new ArrayList<Viewer>(visibleElementsMap.keySet());
		for (Viewer viewer : viewers) {
			unhookViewer(viewer);
		}
	} 

	/**
     * Answers whether the given String matches the pattern.
     * 
     * @param string the String to test
     * 
     * @return whether the string matches the pattern
     */
    private boolean match(String string) 
    {
    	if (matchers == null) 
    	{
			return true;
    	}
    	
    	for(StringMatcher matcher : matchers)
    	{
    		if(matcher.match(string))
    			return true;
    	}
    	
        return false;
    }
    
    /**
     * Answers whether the given element is a valid selection in 
     * the filtered tree.  For example, if a tree has items that 
     * are categorized, the category itself may  not be a valid 
     * selection since it is used merely to organize the elements.
     * 
     * @param element
     * @return true if this element is eligible for automatic selection
     */
    public boolean isElementSelectable(Object element){
    	return element != null;
    }
    
    /**
     * Answers whether the given element in the given viewer matches
     * the filter pattern.  This is a default implementation that will 
     * show a leaf element in the tree based on whether the provided  
     * filter text matches the text of the given element's text, or that 
     * of it's children (if the element has any).  
     * 
     * Subclasses may override this method.
     * 
     * @param viewer the tree viewer in which the element resides
     * @param element the element in the tree to check for a match
     * 
     * @return true if the element matches the filter pattern
     */
    public boolean isElementVisible(Viewer viewer, Object element){
    	if (!visibleElementsMap.containsKey(viewer)) {
    		hookViewer(viewer);
    	} else if (viewer.getInput() != viewerInputs.get(viewer)) {
    		hookViewer(viewer);
    	}
    	HashSet<Object> visibleElements = visibleElementsMap.get(viewer);
    	return visibleElements.contains(element);
    }
   
    /**
     * Hooks the given viewer and computes all of the visible elements
     * in it, and caches them in order that the tree need only be
     * walked once.
	 * @param viewer
	 */
	private void hookViewer(Viewer viewer) {
		unhookViewer(viewer);
		viewer.getControl().addDisposeListener(viewerDisposeListener);
		viewerInputs.put(viewer, viewer.getInput());
		visibleElementsMap.put(viewer, new HashSet<Object>());
		calculateVisibility(viewer);
	}
	
	/**
	 * @param viewer
	 */
	private void calculateVisibility(Viewer viewer) {
		if (!(viewer instanceof TreeViewer)) return;
		ITreeContentProvider provider = 
			(ITreeContentProvider) ((TreeViewer)viewer).getContentProvider();
		Object[] elements = provider.getElements(viewer.getInput());
		calculateChildVisibility(viewer, viewer.getInput(), elements, provider);
	}

	/**
	 * @param elements
	 * @param provider
	 * @return true if the caller should be made visible due to one or more if its children being visible.
	 */
	private boolean calculateChildVisibility(Viewer viewer, Object parent, Object[] children, ITreeContentProvider provider) {
		boolean visible = false;
		for (int i = 0; i < children.length; i++) {
			Object child = children[i];
			if (isLeafMatch(viewer, child)) {
				visible = true;
				addAllChildren(viewer, child, provider);
			} else {
				if (provider.hasChildren(child)) {
					visible |= calculateChildVisibility(viewer, child, provider.getChildren(child), provider);
				}
			}
			
		}
		if (visible) {
			visibleElementsMap.get(viewer).add(parent);
		}
		return visible;
	}

	/**
	 * @param viewer
	 * @param child
	 * @param children
	 * @param provider
	 */
	private void addAllChildren(Viewer viewer, Object parent, ITreeContentProvider provider) {
		visibleElementsMap.get(viewer).add(parent);
		Object[] children = new Object[0];
		if (provider.hasChildren(parent)) {
			children = provider.getChildren(parent);
		}
		for (int i = 0; i < children.length; i++) {
			Object child = children[i];
			visibleElementsMap.get(viewer).add(child);
			if (provider.hasChildren(child)) {
				addAllChildren(viewer, child, provider);
			}
		}
	}

	private void unhookViewer(Viewer viewer) {
		viewerInputs.remove(viewer);
		visibleElementsMap.remove(viewer);
		if (!viewer.getControl().isDisposed()) {
			viewer.getControl().removeDisposeListener(viewerDisposeListener);
		}
		disposeMap.remove(viewer.getControl());
	}

	/**
     * Check if the current (leaf) element is a match with the filter text.  
     * The default behavior checks that the label of the element is a match. 
     * 
     * Subclasses should override this method.
     * 
     * @param viewer the viewer that contains the element
     * @param element the tree element to check
     * @return true if the given element's label matches the filter text
     */
    public boolean isLeafMatch(Viewer viewer, Object element)
    {
    	if (element == viewer.getInput()) return false;
    	if (!(viewer instanceof StructuredViewer)) return false;
    	StructuredViewer sv = (StructuredViewer) viewer;
    	String labelText = null;
        Widget widget = sv.testFindItem(element);
        if (widget instanceof Item) {
        	labelText = ((Item)widget).getText();
        }
        if (labelText == null) {
        	if (!(sv.getLabelProvider() instanceof ILabelProvider)) return false;
        	ILabelProvider labelProvider = (ILabelProvider) sv.getLabelProvider();
        	labelText = labelProvider.getText(element);
        }
        if(labelText == null) {
			return false;
		}
        //check all words.
        String[] fullText = labelText.split("\\.");
        boolean match = false;
        for (int i = 0; i < fullText.length; i++) {
        	match = wordMatches(fullText[i]);
        	if (match) return match;
        }
        return match;  
    }

  	/**
     * Take the given filter text and break it down into words using a 
     * BreakIterator.  
     * 
     * @param text
     * @return an array of words
     */
    private String[] getWords(String text)
    {
    	List<String> words = new ArrayList<String>();
		// Break the text up into words, separating based on whitespace and
		// common punctuation.
		// Previously used String.split(..., "\\W"), where "\W" is a regular
		// expression (see the Javadoc for class Pattern).
		// Need to avoid both String.split and regular expressions, in order to
		// compile against JCL Foundation (bug 80053).
		// Also need to do this in an NL-sensitive way. The use of BreakIterator
		// was suggested in bug 90579.
		BreakIterator iter = BreakIterator.getWordInstance();
		iter.setText(text);
		int i = iter.first();
		while (i != java.text.BreakIterator.DONE && i < text.length()) {
			int j = iter.following(i);
			if (j == java.text.BreakIterator.DONE) {
				j = text.length();
			}
			// match the word
			if (Character.isLetterOrDigit(text.charAt(i))) 
			{
				String word = text.substring(i, j);
				words.add(word);
			}
			
			i = j;
		}
		return (String[]) words.toArray(new String[words.size()]);
    }
    
	/**
	 * Return whether or not if any of the words in text satisfy the
	 * match critera.
	 * 
	 * @param text the text to match
	 * @return boolean <code>true</code> if one of the words in text 
	 * 					satisifes the match criteria.
	 */
	protected boolean wordMatches(String text) {
		
		if (text == null) {
			return false;
		}

		//If the whole text matches we are all set
		if(match(text)) 
		{
			return true;
		}
		
		// Otherwise check if any of the words of the text matches
		String[] words = getWords(text);
		
		for (int i = 0; i < words.length; i++) 
		{	
			String word = words[i];
			
			if (match(word)) 
			{
				return true;
			}
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.IStringFinder#findRegions(java.lang.String)
	 */
	public IRegion[] findRegions(String text) {
		 //check all words.
        String[] fullText = text.split("\\.");
        boolean match = false;
        for (int i = 0; i < fullText.length; i++) {
        	match = wordMatches(fullText[i]);
        	if (match) break;
        }
		if (!match) return new IRegion[0];
		LinkedList<IRegion> regions = new LinkedList<IRegion>();
		if (matchers == null) return new IRegion[0];
		for (StringMatcher matcher : matchers) {
			StringMatcher.Position p = matcher.find(text, 0, text.length());
			if (p != null && !(p.start==0 && p.end==0)) {
				int offset = p.getStart();
				int length = p.getEnd()-p.getStart();
				regions.add(new Region(offset, length));
			}
		}
		return sortAndOrganize(regions);
		
	}

	/**
	 * @param regions
	 * @return
	 */
	private IRegion[] sortAndOrganize(LinkedList<IRegion> regions) {
		Collections.sort(regions, new Comparator<IRegion>() {
			public int compare(IRegion o1, IRegion o2) {
				return o1.getOffset()-o2.getOffset();
			}
		});
		for (int i = 0; i < regions.size(); i++) {
			//check for overlap
			if (i > 0) {
				IRegion last = regions.get(i-1);
				IRegion next = regions.get(i);
				int end = last.getOffset() + last.getLength();
				if ((next.getOffset() <= end)) {
					int offset = last.getOffset();
					int newEnd = Math.max(end, next.getOffset()+next.getLength());
					IRegion newRegion = new Region(offset, newEnd-offset);
					regions.add(i-1, newRegion);
					regions.remove(i);
					regions.remove(i);
				}
			}
		}
		return regions.toArray(new IRegion[regions.size()]);
	}    
}
