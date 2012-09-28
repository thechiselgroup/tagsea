/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.core.ui.internal.views;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.tagsea.core.ui.internal.expression.ExpressionMatcher;
import net.sourceforge.tagsea.core.ui.internal.expression.ExpressionMatcherRegistry;

/**
 * Selects an element based on whether or not it matches an expression.
 * @author Del Myers
 */

public class ExpressionFilter extends RegexEspressionFilter {
	static final Pattern EXPRESSION_PATTERN = Pattern.compile("(\\w+)([<>=!])(\\S+)");

	private Vector<ExpressionMatcher> expressionMatchers;
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */

	public boolean matches(Object element, String string) {
		setString(string);
		if (expressionMatchers == null) return false;
		boolean result = true;
		for (ExpressionMatcher matcher : expressionMatchers) {
			if (!matcher.matches(element)) return false;
		}
		return result;
	}
	
	protected void setString(String word) {
		expressionMatchers = new Vector<ExpressionMatcher>();

		Matcher m = getPattern().matcher(word.toLowerCase());
		if ( m.matches() )
		{
			String left    = m.group(1),
			middle  = m.group(2),
			right   = m.group(3);

			if ( !right.equals("*") )
			{
				ExpressionMatcher matcher = ExpressionMatcherRegistry.create(left, middle.charAt(0), right);
				if ( matcher!=null )
				{
					expressionMatchers.add(matcher);
				}
			}
		}
		
	
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.RegexEspressionFilter#getPattern()
	 */
	@Override
	protected Pattern getPattern() {
		return EXPRESSION_PATTERN;
	}

 
}
