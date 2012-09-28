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

import java.util.TreeMap;
import java.util.TreeSet;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ui.IWaypointUIExtension;


/**
 * Simple registry for ExpressionMatcher objects.  eventually should tie 
 * this into an extension point mechanism for FilteredTable.  used by ExpressionPatternFilter
 * 
 * Now creates matchers for all available attributes on all waypoint types.
 * 
 * 
 * @author Li-Te Cheng
 * CUE, IBM Reserach 2006
 */
public class ExpressionMatcherRegistry {
	
	private static final ExpressionMatcherRegistry instance;
	
	private TreeMap<String,ExpressionMatcher> registry = new TreeMap<String, ExpressionMatcher>();
	
	static {
		instance = new ExpressionMatcherRegistry();
		register(TypeMatcher.TYPE, new TypeMatcher());
		registerFromPlugins();
	}
	
	public static void register(String type, ExpressionMatcher matcher)
	{
		instance.registry.put(type, matcher);
	}
	
	/**
	 * 
	 */
	private static void registerFromPlugins() {
		String[] waypointTypes = TagSEAPlugin.getDefault().getWaypointTypes();
		TreeSet<String> attributeNames = new TreeSet<String>();
		for (String type : waypointTypes) {
			IWaypointUIExtension delegate = TagSEAPlugin.getDefault().getUI().getWaypointUI(type);
			if (delegate == null) continue;
			String[] attrs = delegate.getVisibleAttributes();
			for (String attr : attrs) {
				if (!attributeNames.contains(attr)) {
					attributeNames.add(attr);
					register(attr, new WaypointAttributeMatcher(attr));
				}
			}
		}
	}

	public static String[] getTypes()
	{
		return (String[]) instance.registry.keySet().toArray(new String[instance.registry.keySet().size()]);
	}

	public static ExpressionMatcher create(String type, char middle, String right)
	{
		ExpressionMatcher matcher = instance.registry.get(type);
		if ( matcher==null )
			return null;
		
		try {
			matcher.init(middle, right);
		} catch (IllegalArgumentException e) {
			// ignore - this means we have a parse error and the matcher can't handle the "right" string
			return null;
		}
		
		return matcher;
	}
}
