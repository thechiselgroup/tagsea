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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.research.tagging.core.ui.TagUIPlugin;

/**
 * simple factory to generate ExpressionMatcher objects.  eventually should tie 
 * this into an extension point mechanism for FilteredTable.  used by ExpressionPatternFilter
 * 
 * @tag tagsea expressions : base for expression engine
 * 
 * @author Li-Te Cheng
 * CUE, IBM Reserach 2006
 */
public class ExpressionMatcherFactory {
	
	private static final ExpressionMatcherFactory instance = new ExpressionMatcherFactory();
	
	// @tag java generics wildcards factory-pattern : example of creating a registry/factory using generics 
	private Map<String,Class<? extends ExpressionMatcher>> registry = new HashMap<String, Class<? extends ExpressionMatcher>>();
	
	static {
		register(AuthorMatcher.TYPE,AuthorMatcher.class);
		register(DateMatcher.TYPE,DateMatcher.class);
		register(TypeMatcher.TYPE,TypeMatcher.class);
		register(TagMatcher.TYPE,TagMatcher.class);
	}
	
	public static void register(String type, Class<? extends ExpressionMatcher> matcher)
	{
		instance.registry.put(type, matcher);
	}
	
	public static String[] getTypes()
	{
		return (String[]) instance.registry.keySet().toArray(new String[instance.registry.keySet().size()]);
	}

	public static ExpressionMatcher create(String type, char middle, String right)
	{
		// @tag java generics wildcards factory-pattern : example of creating a registry/factory using generics 
		Class<? extends ExpressionMatcher> matcherClass = instance.registry.get(type);
		if ( matcherClass==null )
			return null;
		
		ExpressionMatcher matcher = null;
		
		try {
			matcher = matcherClass.getConstructor(new Class[]{}).newInstance(new Object[]{});
		} catch (IllegalArgumentException e) {
			TagUIPlugin.log("error while trying to create ExpressionMatcher class=" + matcherClass + " for type=" + type + " middle=" + middle + " right=" + right,e);
			return null;
		} catch (SecurityException e) {
			TagUIPlugin.log("error while trying to create ExpressionMatcher class=" + matcherClass + " for type=" + type + " middle=" + middle + " right=" + right,e);
			return null;
		} catch (InstantiationException e) {
			TagUIPlugin.log("error while trying to create ExpressionMatcher class=" + matcherClass + " for type=" + type + " middle=" + middle + " right=" + right,e);
			return null;
		} catch (IllegalAccessException e) {
			TagUIPlugin.log("error while trying to create ExpressionMatcher class=" + matcherClass + " for type=" + type + " middle=" + middle + " right=" + right,e);
			return null;
		} catch (InvocationTargetException e) {
			TagUIPlugin.log("error while trying to create ExpressionMatcher class=" + matcherClass + " for type=" + type + " middle=" + middle + " right=" + right,e);
			return null;
		} catch (NoSuchMethodException e) {
			TagUIPlugin.log("error while trying to create ExpressionMatcher class=" + matcherClass + " for type=" + type + " middle=" + middle + " right=" + right,e);
			return null;
		}
		
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
