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
package net.sourceforge.tagsea.core.ui.internal.expression;

import java.util.Date;

import net.sourceforge.tagsea.AbstractWaypointDelegate;
import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.IWaypoint;

import org.eclipse.core.runtime.IAdaptable;

/**
 * An expression matcher for matching attributes on waypoints.
 * @author Del Myers
 */

public class WaypointAttributeMatcher extends ExpressionMatcher {
	
	private String attribute;
	

	/**
	 * Matches a waypoint to the given attribute.
	 * @param attribute the attribute to match against.
	 */
	public WaypointAttributeMatcher(String attribute) {
		this.attribute = attribute;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.core.ui.internal.expression.ExpressionMatcher#matches(java.lang.Object)
	 */
	@Override
	public boolean matches(Object object) {
		if (!(object instanceof IAdaptable)) return false;
		
		IWaypoint waypoint = (IWaypoint)((IAdaptable)object).getAdapter(IWaypoint.class);
		if (waypoint == null) return false;
		String type = waypoint.getType();
		AbstractWaypointDelegate delegate = TagSEAPlugin.getDefault().getWaypointDelegate(type);
		
		Class<?> clazz = delegate.getAttributeType(getAttribute());
		if (clazz == null)
			return false;
		else if (clazz.equals(String.class)) {
			return matchesString(waypoint);
		} else if (clazz.equals(Integer.class)) {
			return matchesNumber(waypoint);
		} else if (clazz.equals(Boolean.class)) {
			return matchesBoolean(waypoint);
		} else if (clazz.equals(Date.class)) {
			return matchesDate(waypoint);
		}
		
		return false;
	}
	
	/**
	 * @param waypoint
	 * @return
	 */
	private boolean matchesDate(IWaypoint waypoint) {
		return DateAttributeMatch.matches(waypoint, getRight(), getMatcher());
	}

	/**
	 * @param waypoint
	 * @return
	 */
	private boolean matchesBoolean(IWaypoint waypoint) {
		boolean value = waypoint.getBooleanValue(getAttribute(), false);
		boolean rValue = false;
		try {
			rValue = Boolean.parseBoolean(getRight());
		} catch (RuntimeException e) {
			return false;
		}
		switch (getMatcher()) {
		case '=':
			return value == rValue;
		case '!':
			return value != rValue;
		}
		return false;
	}

	/**
	 * @param waypoint
	 * @return
	 */
	private boolean matchesNumber(IWaypoint waypoint) {
		int value = waypoint.getIntValue(getAttribute(), Integer.MIN_VALUE);
		int rValue = -1;
		try {
			rValue = Integer.parseInt(getRight());
		} catch (RuntimeException e) {
			return false;
		}
		switch (getMatcher()) {
		case '<':
			return value < rValue;
		case '>':
			return value > rValue;
		case '=':
			return value == rValue;
		case '!':
			return value != rValue;
		}
		return false;
	}

	/**
	 * @param waypoint
	 * @return
	 */
	private boolean matchesString(IWaypoint waypoint) {
		String value = waypoint.getStringValue(getAttribute(), null);
				
		String right  = getRight();
		if ("".equals(right)) return true;
		switch ( getMatcher() )
		{
		case '=':
			return value!=null && !"".equals(value) && value.toLowerCase().startsWith(right);

		case '>':
			return value!=null && !"".equals(value) && value.compareToIgnoreCase(right)>0;

		case '<':
			return value!=null && !"".equals(value) && value.compareToIgnoreCase(right)<0;

		case '!':
			return value==null || "".equals(value) || !(value.toLowerCase().startsWith(right));
		}
		return false;
	}

	/**
	 * Returns the l-value of the expression that this matcher will match
	 * (this is equivalent to the attribute).
	 * @return the l-value;
	 */
	public String getType() {
		return getAttribute();
	}

	/**
	 * Returns the attribute on the waypoint that this matcher will match.
	 * @return the attribute on the waypoint that this matcher will match.
	 */
	private String getAttribute() {
		return attribute;
	}
	
	

}
