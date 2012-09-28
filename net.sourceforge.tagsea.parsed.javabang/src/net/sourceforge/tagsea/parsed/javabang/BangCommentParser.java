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
package net.sourceforge.tagsea.parsed.javabang;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.tagsea.parsed.parser.BasicCommentParser;
import net.sourceforge.tagsea.parsed.parser.IParsedWaypointDescriptor;
import net.sourceforge.tagsea.parsed.parser.IWaypointParseProblemCollector;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

/**
 * A parser that parses comments to pull out `words to create tags
 * @author Del Myers
 *
 */
public class BangCommentParser extends BasicCommentParser {

	private String kind;

	/**
	 * @param singleLine
	 * @param multiLineStart
	 * @param multiLineEnd
	 */
	public BangCommentParser(String kind) {
		super(new String[]{"//"},new String[]{"/*"}, new String[]{"*/"}, new String[]{"\"", "\'"}, new String[]{"\"", "\'"});
		this.kind = kind;
	}

	@Override
	public List<IParsedWaypointDescriptor> doParseInComment(
			IDocument document, IRegion region,
			IWaypointParseProblemCollector collector) {
		try {
			//there will be only one descriptor
			StringBuilder messageBuilder = new StringBuilder();
			StringBuilder wordCollector = new StringBuilder();
			LinkedList<String> tags = new LinkedList<String>();
			LinkedList<IRegion> tagRegions = new LinkedList<IRegion>();
			String readArea = document.get(region.getOffset(), region.getLength());
			int offset = region.getOffset();
			if (readArea.startsWith("/*")) {
				//cut off all of the comment characters
				int i = 2;
				while (i < readArea.length()) {
					char c = readArea.charAt(i);
					if (c == '*') {
						i++;
					} else {
						break;
					}
				}
				offset = region.getOffset()+i;
				readArea = readArea.substring(i);
				if (readArea.length() >= 2) {
					readArea = readArea.substring(0, readArea.length()-2);
				} else {
					readArea = "";
				}
			} else if (readArea.startsWith("//")) {
				//cut off all of the comment characters
				int i = 2;
				while (i < readArea.length()) {
					char c = readArea.charAt(i);
					if (c == '*' || c == '/') {
						i++;
					} else {
						break;
					}
				}
				offset = region.getOffset()+i;
				readArea = readArea.substring(i);
			}
			StringReader stringReader = new StringReader(readArea);
			int ch = -1;

			while ((ch = stringReader.read())!=-1) {
				char c = (char)ch;
				if (!Character.isWhitespace(c)) {
					wordCollector.append(c);
				} else {
					String word = wordCollector.toString();
					if (isTag(word)) {
						String untagified = untagify(word);
						tags.add(untagified);
						if (messageBuilder.length() > 0) {
							messageBuilder.append(' ');
						}
						tagRegions.add(new Region(offset-region.getOffset()-word.length(), word.length()));
						messageBuilder.append(untagified);
						wordCollector = new StringBuilder();
					} else if (isWord(word, document, offset, region.getOffset(), region.getOffset()+region.getLength())) {
						if (messageBuilder.length() > 0) {
							messageBuilder.append(' ');
						}
						messageBuilder.append(word);
						wordCollector = new StringBuilder();
					}
				}
				offset++;
			}
			String word = wordCollector.toString();
			if (isTag(word)) {
				String untagified = untagify(word);
				tags.add(untagified);
				if (messageBuilder.length() > 0) {
					messageBuilder.append(' ');
				}
				tagRegions.add(new Region(offset-region.getOffset()-word.length(), word.length()));
				messageBuilder.append(untagified);
				wordCollector = new StringBuilder();
			} else if (isWord(word, document, offset, region.getOffset(), region.getOffset()+region.getLength())) {
				if (messageBuilder.length() > 0) {
					messageBuilder.append(' ');
				}
				messageBuilder.append(word);
				wordCollector = new StringBuilder();
			}
			ArrayList<IParsedWaypointDescriptor> result = new ArrayList<IParsedWaypointDescriptor>(1);
			if (tags.size() != 0) {
				int line = document.getLineOfOffset(region.getOffset());
				IParsedWaypointDescriptor descriptor = new MutableBangWaypointDescriptor(region.getOffset(), document.get(region.getOffset(), region.getLength()), tagRegions, line);
				result.add(descriptor);
			}
			return result;
		} catch (BadLocationException e) {
		} catch (IOException e) {
		} 
		return new LinkedList<IParsedWaypointDescriptor>();
	}



	/**
	 * @param word
	 * @param document
	 * @param offset
	 * @return
	 */
	private boolean isWord(String word, IDocument document, int offset, int start, int end) {
		return !"".equals(word);
	}

	/**
	 * @param word
	 * @return
	 */
	private String untagify(String word) {
		String tag = word.substring(1);
		//remove trailing periods
		int index = word.length()-1;
		while (index > 0) {
			char c = word.charAt(index);
			if (c != '.') break;
			index--;
		}
		tag = tag.substring(0, index);
		return tag;
	}

	/**
	 * @param word
	 * @return
	 */
	public static boolean isTag(String word) {
		if (word.length() >= 2) {
			if (word.startsWith("`")) {
				char ch = word.charAt(1);
				if (Character.isDigit(ch) || Character.isLetter(ch)) {
					int dot = word.indexOf('.');
					int paren = word.indexOf('(');
					if (dot != -1 && paren != -1) {
						//don't allow both a dot and a parenthesis.
						return false;
					}
					return !(word.matches(".*\\.\\..*"));
				}
			}
		}
		return false;
	}
	

	@Override
	public String getParsedWaypointKind() {
		return kind;
	}

}
