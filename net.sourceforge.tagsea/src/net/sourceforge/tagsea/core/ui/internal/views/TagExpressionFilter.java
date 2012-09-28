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

import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.runtime.IAdaptable;

/**
 * An expression filter for finding tags in a filtered tree or table.
 * @author Del Myers
 */

public class TagExpressionFilter extends RegexEspressionFilter {
	private static final Pattern TAG_PATTERN  = Pattern.compile("(\\@)(\\S)(\\S*)");

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.RegexEspressionFilter#matches(java.lang.Object, java.lang.String)
	 */
	public boolean matches(Object element, String string) {
		if (!(element instanceof IAdaptable)) return false;
		IWaypoint waypoint = (IWaypoint)((IAdaptable)element).getAdapter(IWaypoint.class);
		if (waypoint == null) return false;
		Matcher m = getPattern().matcher(string);
		if (!m.matches()) return false;
		String tagString = "";
		if (m.groupCount() >= 2) {
			tagString += m.group(2);
			if (m.groupCount() == 3) {
				tagString += m.group(3);
			}
		}
		tagString = tagString.toLowerCase();
		ITag[] tags = waypoint.getTags();
		for (ITag tag : tags) {
			if (tag.getName().toLowerCase().startsWith(tagString)) return true;
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.views.RegexEspressionFilter#getPattern()
	 */
	@Override
	protected Pattern getPattern() {
		return TAG_PATTERN;
	}
	

}
