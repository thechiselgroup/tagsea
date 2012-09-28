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
package net.sourceforge.tagsea.c.waypoints.parser;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.jface.text.Region;

/**
 * Parses a string to find java waypoint info.
 * @author Del Myers
 */
@Deprecated
public class NewCWaypointParser implements ICWaypointParser {

	private int position;
	private int wordPosition;
	
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.tagsea.java.waypoints.parser.IJavaWaypointParser#parse(java.lang.String)
	 */
	public IParsedCWaypointInfo parse(String waypointDefinition, int offset) {
		//read ahead until we get to a "
		position = 0;
		waypointDefinition = chomp(waypointDefinition);
		ParsedCWaypointInfo info = new ParsedCWaypointInfo(waypointDefinition, offset);
		try {
			readToTag(waypointDefinition);
			Region[] words = getWords(waypointDefinition);
			interpretWords(words, waypointDefinition, info);
		} catch (MalformedWaypointException e) {
			info.caughtException(e);
		} catch (IOException e) {
			info.caughtException(new MalformedWaypointException(MalformedWaypointException.READ_ERROR, 0, waypointDefinition.length()));
		}
		String comment = "";
		if (position < waypointDefinition.length()) {
			comment = waypointDefinition.substring(position).trim();
		}
		info.setDescription(comment, new Region(position, comment.length()));
		return info;
	}

	/**
	 * @param waypointDefinition
	 * @return
	 */
	private String chomp(String waypointDefinition) {
		int index = waypointDefinition.length()-1;
		while (Character.isWhitespace(waypointDefinition.charAt(index)) && index >= 0) {
			index--;
		}
		if (index >= -1) {
			waypointDefinition = waypointDefinition.substring(0, index+1);
		}
		return waypointDefinition;
	}

	/**
	 * @param words
	 * @param waypointDefinition
	 * @param info 
	 * @throws MalformedWaypointException 
	 */
	private void interpretWords(Region[] words, String waypointDefinition, ParsedCWaypointInfo info) throws MalformedWaypointException {
		for (Region region : words) {
			String word = waypointDefinition.substring(region.getOffset(), region.getOffset()+region.getLength());
			if (word.startsWith("-")) {
				interpretMetaData(word, region, info);
			} else { 
				try {
					interpretTag(word, region, info);
				} catch (IOException e) {
					
				}
			}
		}
	}

	/**
	 * @param word
	 * @param region
	 * @param info
	 * @throws MalformedWaypointException 
	 * @throws IOException 
	 */
	private void interpretTag(String word, Region region, ParsedCWaypointInfo info) throws MalformedWaypointException, IOException {
		int open = word.indexOf('(');
		int close = word.lastIndexOf(')');
		int dot = word.indexOf('.');
		if (dot != -1 && (open != -1 || close != -1)) {
			throw new MalformedWaypointException(MalformedWaypointException.BAD_TAG_SYNTAX, region.getOffset(), region.getLength());
		}
		if (close != -1 && close != word.length()-1) {
			throw new MalformedWaypointException(MalformedWaypointException.MISPLACED_PARENS, region.getOffset(), region.getLength());
		}
		if (open != -1) {
			interpretParenTag(word, region, info);
		} else {
			info.putTag(word, region);
		}
	}

	/**
	 * @param word
	 * @param region
	 * @param info
	 * @throws IOException 
	 * @throws MalformedWaypointException 
	 */
	private void interpretParenTag(String word, Region region, ParsedCWaypointInfo info) throws IOException, MalformedWaypointException {
		LinkedList<String> tags = new LinkedList<String>();
		wordPosition = 0;
		tags.addAll(findTags(word,region.getOffset()));
		for (String s : tags) {
			info.putTag(s, region);
		}
	}

	/**
	 * @param word
	 * @param start where to start reading the word.
	 * @param offset the offset into the tag data at which the word occurs.
	 * @return
	 * @throws IOException 
	 * @throws MalformedWaypointException 
	 */
	private Collection<? extends String> findTags(String reader, int localOffset) throws IOException, MalformedWaypointException {
		char c;
		String currentWord = "";
		LinkedList<String> words = new LinkedList<String>();
		while (wordPosition < reader.length()) {
			 c = reader.charAt(wordPosition); 
			if (Character.isWhitespace(c)) {
				if (!"".equals(currentWord))
					words.add(currentWord);
				currentWord = "";
			} else {
				if (c == '(') {
					if ("".equals(currentWord)) 
						throw new MalformedWaypointException(MalformedWaypointException.EXPECTED_WORD, wordPosition+localOffset, 1);
					wordPosition++;
					Collection<? extends String> nextWords = findTags(reader, localOffset);
					for (String s : nextWords) {
						words.add(currentWord+'.'+s);
					}
					currentWord = "";
				} else if (c == ')') {
					if (!"".equals(currentWord))
						words.add(currentWord);
					else if (words.size() == 0) {
						throw new MalformedWaypointException(MalformedWaypointException.EXPECTED_WORD, wordPosition+localOffset, 1);
					}
					return words;
				} else
					currentWord += c;
			}
			wordPosition++;
		}
		return words;
	}

	/**
	 * @param word
	 * @param region
	 * @param info
	 * @throws MalformedWaypointException 
	 */
	private void interpretMetaData(String word, Region region, ParsedCWaypointInfo info) throws MalformedWaypointException {
		int equals = word.indexOf('=');
		if (equals < 0) {
			throw new MalformedWaypointException(MalformedWaypointException.EXPECTED_EQUALS, region.getOffset(), region.getLength());
		} else if (equals == 0) {
			throw new MalformedWaypointException(MalformedWaypointException.EXPECTED_LVALUE, region.getOffset(), 1);
		} else if (equals == word.length()-1) {
			throw new MalformedWaypointException(MalformedWaypointException.EXPECTED_RVALUE, region.getOffset()+word.length()-1, 1);
		}
		String key = word.substring(1, equals);
		String value = word.substring(equals+1, word.length());
		if (value.startsWith("\"")) {
			value = value.substring(1, value.length()-1);
		}
		info.putAttribute(key, value, region);
	}

	/**
	 * @param reader
	 * @return
	 * @throws IOException 
	 * @throws MalformedWaypointException 
	 */
	private Region[] getWords(String reader) throws IOException, MalformedWaypointException {
		LinkedList<Region> words = new LinkedList<Region>();
		char c = 0;
		String currentWord = "";
		int wordStart = position;
		boolean inQuotes =false;
		int parensCount = 0;
		boolean isMetaData = false;
		char lastCharacter = ' ';
		boolean hasPeriod = false;
		while (position < reader.length()) {
			lastCharacter = c;
			c = (char)reader.charAt(position);
			position++;
			if (Character.isWhitespace(c)) {
				if (!"".equals(currentWord)) {
					if (isMetaData) {
						if (!inQuotes) {
							words.add(new Region(wordStart, currentWord.length()));
							isMetaData = false;
							currentWord = "";
							wordStart = position;
							hasPeriod = false;
							parensCount = 0;
						}  else {
							currentWord += c;
						}
					} else {
						if (parensCount == 0) {
							if (currentWord.endsWith(".")) {
								//trailing period is bad.
								throw new MalformedWaypointException(MalformedWaypointException.ILLEGAL_CHARACTER + ": '.'", wordStart+currentWord.length()-1, 1);
							}
							words.add(new Region(wordStart, currentWord.length()));
							isMetaData = false;
							currentWord = "";
							hasPeriod = false;
							wordStart = position;
						} else {
							currentWord += c;
						}
					}
				} else {
					wordStart = position;
				}
			} else {
				if (c == '-' && Character.isWhitespace(lastCharacter)) {
					isMetaData = true;
				} else if (c == '(' && !isMetaData) {
					parensCount++;
					if (hasPeriod) {
						throw new MalformedWaypointException(MalformedWaypointException.ILLEGAL_CHARACTER + ": \'"+c+"\'", position-1, 1);
					}
				} else if (c == ')' && !isMetaData) {
					parensCount--;
					if (hasPeriod) {
						throw new MalformedWaypointException(MalformedWaypointException.ILLEGAL_CHARACTER + ": \'"+c+"\'", position-1, 1);
					}
				}
				if (c == '"' && !isMetaData) {
					throw new MalformedWaypointException(MalformedWaypointException.ILLEGAL_CHARACTER + ": \'"+c+"\'", position-1, 1);
				}
				if (isMetaData && c == '\"') {
					inQuotes = !inQuotes;
					if (position < reader.length() && !inQuotes) {
						if (!Character.isWhitespace(reader.charAt(position))) {
							throw new MalformedWaypointException(MalformedWaypointException.EXPECTED_SPACE, position-1, 1);
						}
					}
				} else if (c == ':' && parensCount != 0) {
					throw new MalformedWaypointException(MalformedWaypointException.ILLEGAL_CHARACTER + ": \'"+c+"\'", position-1, 1);					
				} else if (c=='.' && !isMetaData) {
					 if (parensCount != 0) {
						 throw new MalformedWaypointException(MalformedWaypointException.ILLEGAL_CHARACTER + ": \'"+c+"\'", position-1, 1);
					 }
					 hasPeriod = true;
				} else if (c == ':' && !inQuotes) {
					//quit, we have reached the end.
					break;
				}
				currentWord += c;
			}
		}
		if (inQuotes) {
			throw new MalformedWaypointException(MalformedWaypointException.EXPECTED_EQUALS, wordStart, currentWord.length());
		} else if (parensCount != 0) {
			throw new MalformedWaypointException(MalformedWaypointException.UNMATCHED_PAREN, wordStart, currentWord.length());
		} else if (!"".equals(currentWord)) {
			words.add(new Region(wordStart, currentWord.length()));
		}
 		return (Region[])words.toArray(new Region[words.size()]);
	}

	/**
	 * @param reader
	 * @throws IOException 
	 */
	private void readToTag(String reader) throws IOException {
		String target = TAG_SEQUENCE;
		String currentWord = "";
		position = 0;
		while (position < reader.length()) {
			char c = reader.charAt(position);
			position++;
			if (Character.isWhitespace(c)) {
				currentWord = "";
				continue;
			} else {
				currentWord = currentWord + c;
				if (currentWord.equals(target)) {
					if (position < reader.length() && Character.isWhitespace(reader.charAt(position)))
						break;
					else 
						currentWord = "";
				}
			}
		}
		
	}

}
