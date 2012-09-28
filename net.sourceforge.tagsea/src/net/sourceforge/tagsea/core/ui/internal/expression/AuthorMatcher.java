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
package net.sourceforge.tagsea.core.ui.internal.expression;

import net.sourceforge.tagsea.core.IWaypoint;

/**
 * handles author expressions in FilteredTable.  examples:
 * 
 *    author=li-te   (match all waypoints with author beginning with "li-te")
 *    author>li-te   (match all waypoints with author alphabetically greater than "li-te")
 *    author<li-te   (match all waypoints with author alphabetically less than "li-te")
 *    author!li-te   (match all waypoints with author not beginning with "li-te")
 *    
 * note: cannot handle queries with spaces in them
 * 
 * 
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */
public class AuthorMatcher extends ExpressionMatcher {
	public static final String TYPE = "author";
	
	public boolean matches(Object object) {
		if ( object instanceof IWaypoint )
		{
			IWaypoint waypoint = (IWaypoint) object;
			
			String wpAuthor = waypoint.getAuthor(),
					 right  = getRight();
			
			switch ( getMatcher() )
			{
				case '=':
					return wpAuthor!=null && wpAuthor.toLowerCase().startsWith(right);
					
				case '>':
					return wpAuthor!=null && wpAuthor.compareToIgnoreCase(right)>0;
					
				case '<':
					return wpAuthor!=null && wpAuthor.compareToIgnoreCase(right)<0;

				case '!':
					return wpAuthor==null || !(wpAuthor.toLowerCase().startsWith(right));
			}
		}
		return false;
	}
}
