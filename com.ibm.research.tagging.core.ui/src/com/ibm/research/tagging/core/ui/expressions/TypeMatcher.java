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

import com.ibm.research.tagging.core.IWaypoint;

/**
 * handle waypoint type expressions in FilteredTable.  types are based on
 * waypoint type ids, which require "." to separate the individual string elements.
 * 
 * examples:
 * 		type=dogear    (show all waypoints with the type containing ".dogear")
 *      type!java      (show all waypoints with the type not containing ".java")
 *      type=web       (show all waypoints with the type containing ".web")
 *      type!resource  (show all waypoints with the type not containing ".resource")
 * 
 * note: cannot handle queries with spaces in them
 * 
 * @tag tagsea expressions : instance of ExpressionMatcher
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research
 */
public class TypeMatcher extends ExpressionMatcher {

	public static final String TYPE = "type";
	
	public boolean matches(Object object) {
		
		if ( object instanceof IWaypoint )
		{
			IWaypoint wp = (IWaypoint) object;
			String  type = wp.getType().toLowerCase(),
				    right = "." + getRight();
			
			// System.out.println("type=" + type + " right=" + right);
			
			// only handle = and !
			switch (getMatcher())
			{
				case '=':
					return type.indexOf(right)>=0;
				case '!':
					return !(type.indexOf(right)>=0);
			}
		}
		
		return false;
	}

}
