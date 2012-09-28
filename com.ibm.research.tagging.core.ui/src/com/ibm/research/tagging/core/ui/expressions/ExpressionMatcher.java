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
package com.ibm.research.tagging.core.ui.expressions;

/**
 * abstract base class for entering expression patterns into FilteredTable - used by PatternFilter
 * 
 * @tag tagsea expressions : base for expression engine
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research
 */
public abstract class ExpressionMatcher {
	
	private char 	matcher;
	private String 	right;

	/**
	 * initializer called by ExpressionMatcherFactory
	 * 
	 * @param left  - left side of expression - which is the type field 
	 * @param matcher - <, >, !, =
	 * @param right - right side of expression
	 */
	public void init(char matcher, String right)
	{
		this.matcher = matcher;
		this.right = right.toLowerCase();
	}
	
	public char getMatcher()
	{
		return matcher;
	}
	
	public String getRight()
	{
		return right;
	}
	
	public abstract boolean matches(Object object);
}
