/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package ca.uvic.cs.tagsea.parser;

import ca.uvic.cs.tagsea.TagSEAPlugin;

/**
 * Parses tag text into a parse tree object.
 * The parse tree contains the tree structure of the tag hierachy
 * and also the meta data and keyword associated with the tag.
 * 
 * @author Sean Falconer
 * @author Peggy Storey
 */
public class TagParser {
	// parsing constants
	private static final char OPEN_BRACKET = '(';
	private static final char CLOSE_BRACKET = ')';
	private static final char PERIOD = '.';
	private static final char OPEN_SQUARE_BRACKET = '[';
	private static final char CLOSE_SQUARE_BRACKET = ']';
	private static final String SEPARATOR = ":";
	private static final String METADATA_SEPARATOR = ";";
	private static final String KEYVALUE_SEPARATOR = "=";
	
	public TagParser() {}
	
	/**
	 * Parses tag text based on the BFN grammar for tags and waypoints.
	 * @tag parser(concerns) : what do we do with invalid syntax?
	 * @tag parser(assumptions) : we split on the first colon irrespective of what follows
	 * @tag parser(assumptions) : if brackets do not match, we ignore the tag
	 */
	public ParseTree parse(String tagText) {
		ParseTree parseTree = new ParseTree();
		
		try {
			tagText = tagText.trim().replace("@tag", "");
//			@tag bug(1522072) : look for the metadata first, because times can have SEPARATOR in them.
			int metaDataIndex = tagText.indexOf(OPEN_SQUARE_BRACKET);
			int endIndex = tagText.indexOf(SEPARATOR);
			if (endIndex >= metaDataIndex && metaDataIndex != -1) {
				endIndex = metaDataIndex;
			} 
			//if(endIndex < 0) endIndex = tagText.indexOf(OPEN_SQUARE_BRACKET);
			if(endIndex < 0) endIndex = tagText.length();
			
			String tags = stripEmptyBrackets(tagText.substring(0, endIndex));
			
			
			// ignore tags that have invalid syntax
			if(invalidSyntax(tags)) return parseTree;
			
			if(endIndex < tagText.length()) {
				String metadata = tagText.substring(endIndex, tagText.length());
				
				// metadata must be parsed first to be used in tag waypoints
				if(metadata.length() > 0) parseMetadata(metadata, parseTree);	
			}

			// parse tag information
			if(tags.length() > 0) {
				parseTree.setKeywords(tags.trim());
				parseTags(tags, parseTree.getRoot(), 0);
			}
		}
		catch(Exception e) {
			TagSEAPlugin.log("Tag Parser error", e);
		}
		
		return parseTree;
	}
	
	/**
	 * Parses the tag symbols into a tag hierarchy
	 * @return Current position in the symbols string
	 */
	private int parseTags(String symbols, ParseNode currentNode, int start) {
		StringBuilder keyword = new StringBuilder();
		ParseNode newNode = currentNode;
		
		for(int i = start; i < symbols.length(); i++) {
			if(isDelimiter(symbols.charAt(i))) {
				//	add new keyword tag if we have one
				if(keyword.length() > 0) {
					newNode = currentNode.addChild(new ParseNode(keyword.toString()));
					keyword.delete(0, keyword.length());
				}
				
				// recurse on open bracket or a period
				if(symbols.charAt(i) == OPEN_BRACKET) {
					i = parseTags(symbols, newNode, i+1);
				} 
				else if (symbols.charAt(i) == PERIOD) {
					i = parseTags(symbols, newNode, i+1);
				}
				else if(symbols.charAt(i) == CLOSE_BRACKET) { 
					return i;
				}
			}
			else if(!Character.isWhitespace(symbols.charAt(i))) {
				// append non-whitespace characters to our current keyword
				keyword.append(symbols.charAt(i));
			}
		}
		
		// needed for a single tag that does not have a delimiter following that tag name
		if(keyword.length() > 0) {
			newNode = currentNode.addChild(new ParseNode(keyword.toString()));
		}
		
		return symbols.length();
	}
	
	/**
	 * Parses the metadata
	 */
	private void parseMetadata(String metadata, ParseTree parseTree) {		
		// grab the start and end index to parse the comment
		int startIndex = metadata.indexOf(SEPARATOR) + 1;
		int endIndex = metadata.indexOf(OPEN_SQUARE_BRACKET);
		if(endIndex < 0) endIndex = metadata.length();
	
		// parse comment and create key/value if exists
		if (startIndex < endIndex && startIndex >= 0 && endIndex >= 0) { 
			String comment = metadata.substring(startIndex, endIndex).trim();
			if(comment.length() > 0) {
				parseTree.addMetaData("comment", comment.trim());
			}
		}
		
		// parsing folded metadata
		startIndex = endIndex + 1;
		endIndex = metadata.indexOf(CLOSE_SQUARE_BRACKET);
		
		if(endIndex > 0) {
			String foldedMetadata = metadata.substring(startIndex, endIndex);
			String[] data = foldedMetadata.split(METADATA_SEPARATOR);
			
			for(int i = 0; i < data.length; i++) {
				// parse the key
				endIndex = data[i].indexOf(KEYVALUE_SEPARATOR);
				if(endIndex > 0) {
					String key = data[i].substring(0, endIndex).trim();
					
					// parse the value 
					startIndex = endIndex+1;
					endIndex = data[i].length();					
					
					if(startIndex < endIndex) {
						String value = data[i].substring(startIndex, endIndex).trim();
						parseTree.addMetaData(key, value);
					}
				}
			}
		}
	}
	
	/**
	 * Removes empty bracket sequences from the tag.
	 * @return New representation of tag string.
	 */
	private String stripEmptyBrackets(String symbols) {
		String regex = "\\(\\s*\\)";
		return symbols.replaceAll(regex, "");
	}
	
	/**
	 * Check that the tag contains matching brackets.
	 * @return true if the syntax is invalid
	 */
	private boolean invalidSyntax(String symbols) {
		int rightBrackets = 0;
		int leftBrackets = 0;
		int firstBracket = symbols.indexOf(OPEN_BRACKET);
		int firstPeriod = symbols.indexOf(PERIOD);
		if (firstBracket > 0 && firstPeriod > 0) return false;
		
		for(int i = 0; i < symbols.length(); i++) {
			if(symbols.charAt(i) == OPEN_BRACKET) rightBrackets++;
			else if(symbols.charAt(i) == CLOSE_BRACKET) leftBrackets++;
			
			if(leftBrackets > rightBrackets) return true;
		}
		
		return leftBrackets != rightBrackets;
	}
	
	private boolean isDelimiter(char c) {
		return c == OPEN_BRACKET || c == CLOSE_BRACKET || c == PERIOD || Character.isWhitespace(c);
	}
}