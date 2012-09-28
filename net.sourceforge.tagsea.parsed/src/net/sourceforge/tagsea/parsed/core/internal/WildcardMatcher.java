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
package net.sourceforge.tagsea.parsed.core.internal;

import org.eclipse.core.resources.IFile;

/**
 * Simple class to match wildcard file names.
 * @author Del Myers
 *
 */
public class WildcardMatcher {
	public static final int PERFECT = 0;
	public static final int WILDCARD = 1;
	public static final int NO_MATCH = 2;
	
	
	public int matches(String name, String pattern) {
		if (pattern.startsWith("*.")) {
			int lastDot = pattern.lastIndexOf('.');
			if (lastDot == 1) {
				String matchString = pattern.substring(lastDot);
				if (name.endsWith(matchString)) {
					return WILDCARD;
				}
			}
			return NO_MATCH;
		}
		if (name.equals(pattern)) {
			return PERFECT;
		}
		return NO_MATCH;
	}
	
	public int matchesName(IFile file, String pattern) {
		return matches(file.getName(), pattern);
	}
}
