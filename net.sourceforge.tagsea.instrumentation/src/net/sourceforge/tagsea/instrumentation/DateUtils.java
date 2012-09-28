/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.instrumentation;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for dates in the instrumentation plugin.
 * @author Del Myers
 *
 */
public class DateUtils {
	
	private static Date today;
	
	public static final int ONE_DAY = 1000 * 60 * 60 * 24;
	
	/**
	 * Returns the date format used for saving files.
	 * @return
	 */
	public static DateFormat getDateFormat() {
		return  DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);
	}
	
	/**
	 * Returns the date in a standard formatted string.
	 * @param date
	 * @return
	 */
	public static String toString(Date date) {
		DateFormat formatter = getDateFormat();
		String dateString = formatter.format(date);
		return dateString;
	}
	
	/**
	 * Returns the date value for the given string using the date format of this utility
	 * class, or null if the string could not be parsed as a valid date.
	 * @param string
	 * @return
	 */
	public static Date fromString(String string) {
		if (string == null) return null;
		try {
			return getDateFormat().parse(string);
		} catch (ParseException e) {
			return null;
		}
	}
	
	
	/**
	 * Returns the given date to the nearest day.
	 * @param date
	 * @return
	 */
	public static Date toNearestDay(Date date) {
		try {
			return getDateFormat().parse(getDateFormat().format(date));
		} catch (ParseException e) {
			TagSEAInstrumentationPlugin.getDefault().log(e);
		}
		return date;
	}
	
	/**
	 * Returns the date for today, at the day's start.
	 * @return
	 */
	public static Date today() {
		if (today == null || System.currentTimeMillis() > (today.getTime() + ONE_DAY)) {
			today = toNearestDay(new Date());
		}
		return today;
	}
}
