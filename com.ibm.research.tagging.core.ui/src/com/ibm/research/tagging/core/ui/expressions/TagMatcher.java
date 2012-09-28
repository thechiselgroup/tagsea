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

import com.ibm.research.tagging.core.ITag;
import com.ibm.research.tagging.core.IWaypoint;

/**
 * handles tag expressions in FilteredTable.  examples:
 * 
 *    @<tag>           (match all waypoints with the given tag)
 *    @!<tag>           (match all waypoints without the given tag)
 *    
 *    less than and greater than operators are currently not supported
 *    
 * note: cannot handle queries with spaces in them
 * 
 * @tag tagsea expressions : instance of an ExpressionMatcher
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */

public class TagMatcher extends ExpressionMatcher {
	public static final String TYPE = "@";
	
	private static final String VALID_MATCHERS = "=!";
	
	public boolean matches(Object object) {
		if ( object instanceof IWaypoint )
		{
			IWaypoint waypoint = (IWaypoint) object;
			
			ITag[] tags   = waypoint.getTags();
			String right  = getRight();
			char lastChar = 0;
			
			if ( right.length()>1 )
			{
				lastChar = right.charAt(right.length()-1);
				if ( lastChar == '*' )
					right = right.substring(0,right.length()-1);
			}
			
			if ( VALID_MATCHERS.indexOf(getMatcher())<0 )
			{
				right = getMatcher() + right;
			}
			
			boolean ok = getMatcher()=='!'?true:false;
			
			for (ITag tag : tags)
			{
				boolean tagMatch = false;
				
				if ( lastChar=='*' )
					tagMatch = tag.getName().toLowerCase().startsWith(right);
				else
					tagMatch = tag.getName().toLowerCase().equals(right);
				
				if ( tagMatch && getMatcher()!='!' )
				{
					ok = true;
					break;
				}
				else if ( tagMatch && getMatcher()=='!' )
				{
					ok = false;
					break;
				}
			}
			
			return ok;
		}
		return false;
	}

}
