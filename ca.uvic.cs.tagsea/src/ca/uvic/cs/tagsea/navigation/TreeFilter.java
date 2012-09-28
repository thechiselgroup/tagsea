/*******************************************************************************
 * Copyright 2005, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.navigation;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filters tree items based on a regular expression pattern.
 * If an item is accepted then all its children are accepted too.
 * 
 * The pattern is converted into a regular expression which will be used to match
 * each element.  It checks for that pattern at the start of the element's string,
 * after a space, bracket, or underscore.
 * 
 * Override the getElementString() method to return something different than to toString() representation 
 * of each element.
 * 
 * See org.eclipse.ui.internal.dialogs.PatternFilter and FilteredTree for another implementation
 * of a filtered tree.
 * 
 * @author Chris Callendar
 */
public class TreeFilter extends ViewerFilter {
	
	private String patternText;
    private Set<Object> matchedElements;
	protected Pattern pattern;
	
	public TreeFilter() {
		this.patternText = "";
		this.pattern = Pattern.compile("");
		this.matchedElements = new HashSet<Object>();
	}

	public String getPattern() {
		return patternText;
	}
	
	/**
	 * If the pattern has changed the cache is cleared
	 * and the new Pattern is set.
	 * @param newPattern the new string pattern (regex)
	 * @throws PatternSyntaxException
	 */
	public void setPattern(String newPattern) throws PatternSyntaxException {
		if (!patternText.equals(newPattern)) {
			matchedElements.clear();
			String regex = "";
			if (newPattern.length() > 0) {
				// matches text at the start of the pattern or after a space/underscore/bracket
				// e.g. the pattern T will match Tag, A Tag, A_Tag, A(Tag), "Tag", and 'Tag'
//				@tag bug(sourceforge(1528033)) : added '/' character to the pattern, so that all children would be found.
				regex = "^" + newPattern + "|[ _\\(\\[\\{\"\'\\/]" + newPattern;
			}
			// throws PatternSyntaxException if the pattern is invalid
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			patternText = newPattern;
		}
	}
	
    public Object[] filter(Viewer viewer, Object parent, Object[] elements) {
        return super.filter(viewer, parent, elements);
	}
		
	/**
	 * Determines if the given element should accepted (not filtered).
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ((element == null) || (parentElement == null))
			return false;

		// if no pattern then accept everything
		if (patternText.length() == 0) 
			return true;
		
		boolean matched = match(viewer, parentElement, element);
		if (matched) {
			// add to the list of accepted elements
			//matchedElements.add(element);
		}
		return matched;
	}
	
	/**
	 * Attempts to match the element with the current pattern (regex).
	 * If the parent has already been accepted, then the element is also accepted.
	 * @return boolean if the element matched
	 */
	private boolean match(Viewer viewer, Object parentElement, Object element) {
		// check if the parent has already been accepted
		if (matchedElements.contains(parentElement)) {
//			@tag bug(sourceforge(1528033)) : make sure all parents are traced.
			matchedElements.add(element);
			return true;
		}

		// get the string representation of the element
		if (match(element)) {
			matchedElements.add(element);
			return true;
		}

		// have to check the children now to see if any of them are accepted
		// inefficient - see org.eclipse.ui.internal.dialogs.PatternFilter
		Object[] children = ((ITreeContentProvider) ((AbstractTreeViewer) viewer)
            .getContentProvider()).getChildren(element);
		
		if ((children != null) && (children.length > 0)) {
			return (filter(viewer, element, children).length > 0);
		}
		
		return false;
	}
	
	/**
	 * Public method for testing purposes.  This method is where the regular
	 * expression is matched.
	 * @param element the object to match.  It calls getElementString(element) to 
	 * 	get the string representation of the object.
	 * @return boolean true if the string matches the current pattern
	 */
	public boolean match(Object element) {
		String str = getElementString(element);
		if ((str != null) && (str.length() > 0)) {
			// compare the string to the regular expression and try to find one instance
			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the String value of the given element.
	 * This is what is matched with the regular expression.
	 * Override this method to do something more than just element.toString()
	 * @param element the element to convert into a string.
	 * @return String just element.toString() in this case
	 */
	public String getElementString(Object element) {
		return (element != null ? element.toString() : null);
	}

	
}
