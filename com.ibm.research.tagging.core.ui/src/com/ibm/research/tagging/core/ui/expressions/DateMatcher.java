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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.ibm.research.tagging.core.IWaypoint;

/**
 * handles date expressions in FilteredTable.  examples:
 * 
 *    date=June    (show all waypoints with dates set in June, regardless of day and year)
 *    date>7/05    (show all waypoints with dates after July 2005)
 *    date<3/1/06  (show all waypoints with dates before March 1st, 2006)
 *    date!2005    (show all waypoints with dates not during 2005)
 *    
 * note: cannot handle queries with spaces in them
 * 
 * @tag tagsea expressions : instance of an ExpressionMatcher
 * @tag date tagsea : example of doing simple date logic and formatting
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */
public class DateMatcher extends ExpressionMatcher {
	
	public static final String TYPE = "date";
	
	private static final int IGNORE_DATE_FIELD = -2,
	                         LT_DATE_FIELD = -1,
	                         EQ_DATE_FIELD = 0,
	                         GT_DATE_FIELD = 1; 

	// simple internal class to parse dates and do date field comparisons
	static class DateFieldComparator
	{
		private SimpleDateFormat parser;
		private boolean compareMonth;
		private boolean compareDay;
		private boolean compareYear;

		public DateFieldComparator(SimpleDateFormat parser, boolean compareMonth, boolean compareDay, boolean compareYear)
		{
			this.parser 	  = parser;
			this.compareMonth = compareMonth;
			this.compareDay   = compareDay;
			this.compareYear  = compareYear;
		}
		
		public Date parse(String date)
		{
			Date parsed = null;
			
			try {
				parsed = parser.parse(date);
			} catch (ParseException e) {
				return null;
			}
			
			return parsed;
		}
		
		private int compareField(int f1, int f2)
		{
			if ( f1<f2 )
				return LT_DATE_FIELD;
			else if ( f1>f2 )
				return GT_DATE_FIELD;
			
			return EQ_DATE_FIELD;
		}
		
		public int compare(Date date1, Date date2)
		{
			if (date1==null || date2==null)
				return IGNORE_DATE_FIELD;
			
			Calendar cal1 = new GregorianCalendar(),
			         cal2 = new GregorianCalendar();
			
			cal1.setTime(date1);
			cal2.setTime(date2);

			int month=IGNORE_DATE_FIELD,
			    day=IGNORE_DATE_FIELD,
			    year=IGNORE_DATE_FIELD;
			
			if ( compareMonth )
				month = compareField(cal1.get(Calendar.MONTH),cal2.get(Calendar.MONTH));
			
			if ( compareDay )
				day = compareField(cal1.get(Calendar.DAY_OF_MONTH),cal2.get(Calendar.DAY_OF_MONTH));
			
			if ( compareYear )
				year = compareField(cal1.get(Calendar.YEAR),cal2.get(Calendar.YEAR));
			
			if ( compareYear )
			{
				if (year!=EQ_DATE_FIELD)
					return year;
				
				if ( compareMonth )
				{
					if (month!=EQ_DATE_FIELD)
						return month;
					
					if ( compareDay )
						return day;
					
					return EQ_DATE_FIELD;  // months are equal, no days to compare against
				}
				
				return EQ_DATE_FIELD; // years are equal, no months to compare against
			}
			else if ( compareMonth )
			{
				if ( month!=EQ_DATE_FIELD )
					return month;
				
				if ( compareDay )
					return day;
				
				return EQ_DATE_FIELD;  // months are equal, no days to compare against
			}
			else if ( compareDay )
				return day;
			
			return IGNORE_DATE_FIELD;
		}
	};
	
	// set of date patterns recognized by this matcher
	private static final DateFieldComparator[] COMPARATORS = {
		new DateFieldComparator(new SimpleDateFormat("MM/dd/yy"),true,true,true),
		new DateFieldComparator(new SimpleDateFormat("MM/dd/yyyy"),true,true,true),
		new DateFieldComparator(new SimpleDateFormat("MM/yy"),true,false,true),
		new DateFieldComparator(new SimpleDateFormat("MM/yyyy"),true,false,true),
		new DateFieldComparator(new SimpleDateFormat("MMM/yy"),true,false,false),
		new DateFieldComparator(new SimpleDateFormat("MMM/yyyy"),true,false,false),
		new DateFieldComparator(new SimpleDateFormat("MMM"),true,false,false),
		new DateFieldComparator(new SimpleDateFormat("yy"),false,false,true),
		new DateFieldComparator(new SimpleDateFormat("yyyy"),false,false,true)
	};
	
	// recognized matcher and date to use in matching
	private DateFieldComparator comparator;
	private Date rightDate;
	
	/**
	 * throws IllegalArgumentException if cannot parse supplied date
	 * @param matcher
	 * @param right - date string to match against
	 */
	public void init(char matcher, String right)
	{
		super.init(matcher,right);
		
		for (DateFieldComparator c : COMPARATORS)
		{
			rightDate = c.parse(right);
			if ( rightDate!=null )
			{
				comparator = c;
				break;
			}
		}
		
		if ( rightDate==null || comparator==null)
		{
			throw new IllegalArgumentException("unable to parse date="  + right);
		}
		
		// @tag datematcher debug tagsea expressions : debug statement
		// System.out.println("using rightDate=" + rightDate + " comparator=" + comparator);
	}
	
	public boolean matches(Object object) {
		if ( object instanceof IWaypoint )
		{
			IWaypoint wp = (IWaypoint) object;
			if ( rightDate!=null && comparator!=null )
			{
				int result = comparator.compare(wp.getDate(), rightDate);
				switch ( getMatcher() )
				{
					case '<':
						return result==LT_DATE_FIELD;
					case '>':
						return result==GT_DATE_FIELD;
					case '=':
						return result==EQ_DATE_FIELD;
					case '!':
						return !(result==EQ_DATE_FIELD);
				}
			}
		}
		return false;
	}
}
