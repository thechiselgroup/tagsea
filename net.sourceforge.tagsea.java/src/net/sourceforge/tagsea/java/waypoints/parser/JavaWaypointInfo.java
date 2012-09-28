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

package net.sourceforge.tagsea.java.waypoints.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import net.sourceforge.tagsea.core.IWaypoint;

public abstract class JavaWaypointInfo implements IJavaWaypointInfo
{
	
	
	
	@Override
	public String toString() 
	{
		StringBuffer result = new StringBuffer();
		result.append("Tags [");
		for(String tag : getTags())
			result.append("<" + tag + ">");
		result.append("]\n");
		result.append("Description [" + (getDescription()!=null?getDescription():"") + "]\n");
		result.append("Metadata [" + getAttributes().toString() + "]");
		return result.toString();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getAuthor()
	 */
	public String getAuthor() {
		return getAttributes().get(IWaypoint.ATTR_AUTHOR);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointInfo#getDate()
	 */
	public Date getDate() {
		String date = getAttributes().get(IWaypoint.ATTR_DATE);
		//standard way of getting the date: two characters for local, two characters for
		//country, colon, short date.
		if (date == null) return null;
		int colon = date.indexOf(':');
		Date result = null;
		if (colon == 4) {
			//interpret the locale.
			String lc = date.substring(0,2);
			String cn = date.substring(1,3);
			date = date.substring(colon+1);
			Locale locale = new Locale(lc, cn);
			try {
				result = DateFormat.getDateInstance(DateFormat.SHORT, locale).parse(date);
			} catch (ParseException e) {
			}
		} else if (colon == -1) {
			try {
				result = DateFormat.getDateInstance(DateFormat.SHORT).parse(date);
			} catch (ParseException e) {
			}
		}
		return result;
	}
}
