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

/**
 * An interface that checks to see if a series of non-whitespace characters
 * is matches an expression.
 * @author Del Myers
 */

public interface IExpressionFilter {
	
	/**
	 * Returns true if the given string represents a valid expression in
	 * this matcher.
	 * @param string the string to check.
	 * @return true if the given string represents a valid expression in this
	 * matcher.
	 */
	public boolean isExpression(String string);
	
	/**
	 * Returns true if the given element is a match for the expression.
	 * @param element the element to check.
	 * @param expression the expression to check for a match on.
	 * @return true if the given element is a match for the expression.
	 */
	public boolean matches(Object element, String expression);

}
