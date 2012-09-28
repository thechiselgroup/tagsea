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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract class for finding expressions for a filtered table or tree
 * based on regular expressions.
 * @author Del Myers
 */

public abstract class RegexEspressionFilter implements IExpressionFilter {



	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.IExpressionFilter#isExpression(java.lang.String)
	 */
	public boolean isExpression(String string) {
		Matcher m = getPattern().matcher(string.toLowerCase());
		return m.matches();
	}

	
	/**
	 * Returns the pattern used to match the expression.
	 *
	 */
	protected abstract Pattern getPattern();
	
}
