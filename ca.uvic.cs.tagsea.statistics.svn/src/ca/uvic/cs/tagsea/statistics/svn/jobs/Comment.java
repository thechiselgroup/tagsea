/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.statistics.svn.jobs;

/**
 * A simple class that represents a comment.
 * @author Del Myers
 *
 */
public class Comment implements Comparable{
	public final String TEXT;
	public final int START_LINE;
	public final int END_LINE;
	public final String PACKAGE;
	public final String CLASS;
	Comment(String text, int start, int end, String pack, String clazz) {
		this.TEXT = text.trim();
		this.START_LINE = start;
		this.END_LINE = end;
		this.PACKAGE = pack;
		this.CLASS = clazz;
	}
	public String toString() {
		String t = TEXT.replaceAll("\t", "\\\\t ");
		return "Comment on "+PACKAGE+"."+CLASS+"\t("+START_LINE+"-"+END_LINE+")\t" + t;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		return this.toString().compareTo(o.toString());
	}
	
	public boolean equals(Object o) {
		if (o instanceof Comment)
			return this.toString().equals(o.toString());
		return super.equals(o);
	}
}
