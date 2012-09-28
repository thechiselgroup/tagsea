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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

/**
 * Generic parse tree data structure used by the TagParser.
 * Contains the root of the parse tree stucture, metadata as a key/value hashtable
 * and the keywords associated with the parse tree.
 * 
 * @author Sean Falconer
 */
public class ParseTree {
	// this tree's root node, there's always a unique root
	private ParseNode root = new ParseNode();
	private final int PARA = 1 << 0;
	private final int DOT = 1 << 1;
	private int style = PARA;
	
	// key/value pairs for storing metadata
	private Hashtable<String, String> metadata = new Hashtable<String, String>();
	
	// keywords associated with the parse
	private String keywords;
	
	public ParseTree() {}
	
	public void setKeywords(String keywords) throws IllegalArgumentException {
		int bits = 0;
		bits |= (keywords.indexOf('(') > 0) ? PARA : 0;
		bits |= (keywords.indexOf('.') > 0) ? DOT : 0;
		int illegal = PARA | DOT;
		bits &= illegal;
		int rightBit = bits & (-bits);
		if (bits != rightBit) {
			throw new IllegalArgumentException("Can only have either '(' or '.' in a tag name.");
		}
		style = bits;
		this.keywords = keywords;
	}
	
	public String getKeywords() {
		return keywords;
	}
	
	@Override
	public String toString() {
		return "ParseTree: " + getKeywords();
	}
	
	public ParseNode getRoot() {
		return root;
	}
	
	public ParseNode[] getNodeCollection() {
		return root.getChildren();
	}
	
	public void addMetaData(String key, String value) {
		metadata.put(key, value);
	}
	
	public String[] getMetaDataKeys() {
		Set<String> keys = metadata.keySet();
		return (String[])keys.toArray(new String[keys.size()]);
	}
	
	public String[] getMetaDataValues() {
		Collection<String> values = metadata.values();
		return (String[])values.toArray(new String[values.size()]);
	}

	/**
	 * Generates the keywords based on the root nodes.
	 * Does <b>not</b> update the keywords.
	 * This is useful if the name of a ParseNode changes.
	 * @return String the keywords string.
	 */
	public String generateKeywords() {
		StringBuilder buffer = new StringBuilder();
		ParseNode[] nodes = getNodeCollection();
		for (int i = 0; i < nodes.length; i++) {
			if (i > 0) {
				buffer.append(" ");
			}
			generateKeyword(nodes[i], buffer);
		}
		return buffer.toString();
	}
	
	private void generateKeyword(ParseNode node, StringBuilder buffer) {
		if (buffer.length() > 0) {
		}
		buffer.append(node.getName());
		char delim = getDelimiter();
		if (node.hasChildren()) {
			buffer.append(delim);
			ParseNode[] children = node.getChildren();
			for (int i = 0; i < children.length; i++) {
				generateKeyword(children[i], buffer);
				if (i < (children.length - 1)) {
					buffer.append(" ");
				}
			}
			if (delim == '(') buffer.append(")");
		}
	}
	
	public char getDelimiter() {
		if ((style & PARA) == PARA) return '(';
		else if ((style & DOT) == DOT) return '.';
		return '(';
	}

}
