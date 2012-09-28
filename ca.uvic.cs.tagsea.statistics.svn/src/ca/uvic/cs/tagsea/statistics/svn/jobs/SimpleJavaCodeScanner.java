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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A simple scanner that searches throught the comments in source
 * code to find out how many comments have tags in them, and how
 * many do not.
 * @author Del Myers
 *
 */
public class SimpleJavaCodeScanner {
	public static class Stats {
		public final Comment[] NONTAGS;
		public final Comment[] TAGS;
		public final Comment[] TASKS;
		private Stats(Comment[] nontags, Comment[] tags, Comment[] tasks) {
			this.NONTAGS = nontags;
			this.TAGS = tags;
			this.TASKS = tasks;
		}
	}
	
	//ready to start
	private static final int START = 0;
	//inside a multiline comment
	private static final int MULTILINE = 1;
	//inside a singleline comment
	private static final int LINE = 2;
	//found a slash (possible start of a comment)
	private static final int SLASH = 3;
	//found a star (possible end of a comment)
	private static final int STAR = 4;
	//just after a */ comment is finished
	private static final int STARSLASH = 5;
	//no comment
	private static final int SCANNING = 6;
	//finished
	private static final int FINISHED = 7;



	private static final int NONE = 10;
	private static final int TAG = 11;
	private static final int TASK = 12;
	private static final int BOTH = 13;

	private static final String TAGS = "@tag";
	private static final String TODO = "TODO";
	private static final String XXX = "XXX";
	private static final String FIXME = "FIXME";
	/**
	 * Scans the given source code for comments with tags and without.
	 * @param code the code to scan
	 * @return the gathered statistics, or null if the process has been cancelled.
	 * @throws IOException 
	 */
	public static Stats scan(String code, IProgressMonitor monitor) throws IOException {
		StringReader reader = new StringReader(code);
		//pass a state variable around so that this call can be thread-safe
		//and not state specific.
		int state = SCANNING;
		int cstate = NONE;
		BufferedReader lineReader = new BufferedReader(reader);
		
		Pattern packPatt = Pattern.compile(".*package\\s+([a-zA-Z0-9\\.]+).*;.*");
		Pattern clazzPatt = Pattern.compile(".*class\\s+(\\w+).*");
		String pack = null;
		String clazz = null;
		String s;
		StringBuffer currentComment = new StringBuffer();
		List commentList = new LinkedList();
		List tagList = new LinkedList();
		List taskList = new LinkedList();
		monitor.beginTask("Parsing java source...", code.length());
		monitor.subTask("Parsing java source...");
		int line = 0;
		int commentStart = 1;
		Matcher matcher;
		while ((s= lineReader.readLine()) != null) {
			matcher = packPatt.matcher(s);
			if (matcher.matches()) {
				pack = matcher.group(1);
			}
			String s2 = new String(s);
			matcher = clazzPatt.matcher(s2);
			if (matcher.matches()) {
				clazz = matcher.group(1);
			}
			if (clazz != null && pack != null) break;
		}
		if (clazz == null) clazz = "";
		if (pack == null) pack = "default";
		reader.close();
		reader = new StringReader(code);
		lineReader = new BufferedReader(reader);
		while ((s = lineReader.readLine()) != null) {
			line++;
			//scan the line for the beginning of a comment.
			char[] characters = s.toCharArray();
			for (int i = 0; i < characters.length; i++) {
				if (characters[i] == '/') {
					switch (state) {
						case SLASH:
							state = LINE;
							commentStart = line;
							break;
						case STAR:
							state = STARSLASH;
							String comment = currentComment.toString();
							Comment c = new Comment(comment, commentStart, line, pack, clazz);
							currentComment.append(characters[i]);
							switch (cstate) {
								case TAG:
									tagList.add(c); break;
								case BOTH:
									tagList.add(c);
								case TASK:
									taskList.add(c); break;
								default:
									commentList.add(c); break;
							}
							cstate = NONE;
							currentComment = new StringBuffer();
							break;
						case MULTILINE:
						case LINE:
							currentComment.append(characters[i]);
							break;
						case SCANNING:
						case STARSLASH:
							state = SLASH;
					}
				} else if (characters[i] == '*') {
					switch (state) {
						case SLASH:
							state = MULTILINE;
							commentStart = line;
							break;
						case LINE:
							currentComment.append(characters[i]); break;
						case MULTILINE:
							state = STAR; break;
						case STARSLASH:
							state = SCANNING;
							break;
					}
				} else if (state == SLASH || state == STARSLASH) {
					state = SCANNING;
				} else {
					if (state == STAR) {
						currentComment.append('*');
						state = MULTILINE;
					}
					if (state == LINE || state == MULTILINE) {
						String comment = currentComment.toString();
						if (Character.isWhitespace(characters[i]) && cstate == NONE) {
							if (isTag(comment)) {
								cstate = TAG;
							}
							if (isTask(comment)) {
								if (cstate == TAG)
									cstate = BOTH;
								else
									cstate = TASK;
							}
						}
//						save the character.
						currentComment.append(characters[i]);
						if (characters[i] == '\\') {
							//append another \ so that there is no ambiguity with new lines
							currentComment.append('\\');
						}
					}
				}
				monitor.worked(1);
				if (monitor.isCanceled()) {
					return null;
				}
			}
			if (state == LINE) {
				String comment = currentComment.toString();
				Comment c = new Comment(comment, commentStart, line, pack, clazz);
				switch (cstate) {
					case TAG:
						tagList.add(c); break;
					case BOTH:
						tagList.add(c);
					case TASK:
						taskList.add(c); break;
					default:
						//check for when the info comes at the end
						if (isTag(comment))
							tagList.add(c);
						if (isTask(comment))
							taskList.add(c);
						else
							commentList.add(c); break;
				}
				currentComment = new StringBuffer();
				cstate = NONE;
				state = SCANNING;
			} else if (state == MULTILINE) {
				currentComment.append("\\ "); //append a newline indicator.
			}
		}
		Stats st = new Stats(
			(Comment[])commentList.toArray(new Comment[0]),
			(Comment[])tagList.toArray(new Comment[0]),
			(Comment[])taskList.toArray(new Comment[0])
		);
		monitor.done();
		return st;
	}
	private static boolean isTag(String comment) {
		if (comment.endsWith(TAGS)) {
			if (comment.length() == TAGS.length()) {
				return true;
			} else {
				char c = comment.charAt(comment.length()-TAGS.length()-1);
				if (Character.isWhitespace(c)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean isTask(String comment) {
		int length = -1;
		if (comment.endsWith(FIXME)) {
			length = FIXME.length();
		} else if (comment.endsWith(TODO)) {
			length = TODO.length();
		} else if (comment.endsWith(XXX)) {
			length = XXX.length();
		}
		if (length > 0) {
			if (comment.length() == length) {
				return true;
			} else {
				char c = comment.charAt(comment.length()-length-1);
				if (Character.isWhitespace(c)) {
					return true;
				}
			}
		}
		return false;
	}
}
