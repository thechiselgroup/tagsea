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
package ca.uvic.cs.tagsea.tests;

import junit.framework.TestCase;
import ca.uvic.cs.tagsea.parser.ParseNode;
import ca.uvic.cs.tagsea.parser.ParseTree;
import ca.uvic.cs.tagsea.parser.TagParser;

public class ParserTests extends TestCase {	
	
	
	/**
	 * @throws Exception
	 * @tag test(parser(single_root)): tests a single root tag
	 */
	public void testMultiRootParse() throws Exception {
		TagParser tp = new TagParser();
		ParseTree pt = tp.parse("@tag hello : this is a comment");
		
		ParseNode[] nodes = pt.getNodeCollection();
		assertEquals(1, nodes.length); // Should be 1 tag, hello
	}
	
	/**
	 * @throws Exception
	 * @tag test(parser(simple_hierarchy)): tests just one hierarchical tag and verifies that the hierarchy was created
	 */
	public void testParserSimpleHierarchy() throws Exception {
		TagParser tp = new TagParser();
		ParseTree pt = tp.parse("@tag peter(bugzilla(22))");
		
		ParseNode[] nodes = pt.getNodeCollection();
		assertEquals(1, nodes.length);
		assertEquals("peter(bugzilla(22))", pt.getKeywords());
		assertEquals("peter", nodes[0].getName());
		assertEquals(1, nodes[0].getChildren().length);	// buzgilla
		assertEquals(1, nodes[0].getChildren()[0].getChildren().length); // 22
	}
	
	/**
	 * @throws Exception
	 * @tag test(parser(whitespace)) : tests whitespace and a tab character
	 */
	public void testParserWithWhitespace() throws Exception {
		TagParser tp = new TagParser();

		ParseTree pt = tp.parse("" +
				"@tag peter bob ( 	67 )");
		
		ParseNode[] nodes = pt.getNodeCollection();
		assertEquals(2, nodes.length);		
		
		boolean found = false;
		for(int i = 0; i < nodes.length; i++) {
			if(nodes[i].getName().equals("bob")) {
				ParseNode[] children = nodes[i].getChildren();
				assertEquals("67", children[0].getName());
				found = true;
				break;
			}
		}
		
		assertEquals(true, found);
	}
	
	/**
	 * @tag test(parser(children)): Tests that the root tag parent1 is only created once and that children are properly created.
	 * @throws Exception
	 */
	public void testChildren() throws Exception {
		String name1 = "@tag parent1(child1 child2)";
		
		TagParser tp = new TagParser();
		ParseTree pt = tp.parse(name1);
		
		ParseNode[] nodes = pt.getNodeCollection();
		assertEquals(1, nodes.length);
	
		ParseNode[] subchildren = nodes[0].getChildren();
		assertEquals(2, subchildren.length);
	}

	/**
	 * @tag test(parser(single)) : Tests parser with a single tag.
	 * @throws Exception
	 */
	public void testParserSingleKeyword() throws Exception {
		TagParser tp = new TagParser();

		ParseTree pt = tp.parse("@tag a");
		
		ParseNode[] nodes = pt.getNodeCollection();
		assertEquals(1, nodes.length);
		assertEquals("a", nodes[0].getName());
	}
	
	/**
	 * @tag test(parser(metadata)): Tests parser with fully qualified metadata.
	 * @throws Exception
	 */
	public void testParserMetaData() throws Exception {
		TagParser tp = new TagParser();

		ParseTree pt = tp.parse("@tag peter(bugzilla(22 21)) : this is a comment [author = sean; date = 01/01/2006]");
		
		ParseNode[] nodes = pt.getNodeCollection();
		assertEquals(1, nodes.length);
		
		String[] keys = pt.getMetaDataKeys();
		String[] values = pt.getMetaDataValues();
		
		assertEquals(3, keys.length);
		assertEquals(3, values.length);
		
		int found = 0;
		for(int i = 0; i < keys.length; i++) {
			if(keys[i].equals("comment")) found++;
			else if(keys[i].equals("author")) found++;
			else if(keys[i].equals("date")) found++;
		}
		
		assertEquals(3, found);
		
		found = 0;
		for(int i = 0; i < values.length; i++) {
			if(values[i].equals("this is a comment")) found++;
			else if(values[i].equals("sean")) found++;
			else if(values[i].equals("01/01/2006")) found++;
		}
		
		assertEquals(3, found);
	}
	
	/**
	 * @tag test(parser(metadata)): Tests parser with tag and metadata, but no comment.
	 * @throws Exception
	 */
	public void testParserMetaDataWithoutComment() throws Exception {
		TagParser tp = new TagParser();

		ParseTree pt = tp.parse("@tag peter(bugzilla(22 21)) : [author = sean; date = 01/01/2006]");
		
		ParseNode[] nodes = pt.getNodeCollection();
		assertEquals(1, nodes.length);
		
		String[] keys = pt.getMetaDataKeys();
		String[] values = pt.getMetaDataValues();
		
		assertEquals(2, keys.length);
		assertEquals(2, values.length);
		
		int found = 0;
		for(int i = 0; i < keys.length; i++) {
			if(keys[i].equals("author")) found++;
			else if(keys[i].equals("date")) found++;
		}
		
		assertEquals(2, found);
		
		found = 0;
		for(int i = 0; i < values.length; i++) {
			if(values[i].equals("sean")) found++;
			else if(values[i].equals("01/01/2006")) found++;
		}
		
		assertEquals(2, found);
	}
	
	/**
	 * @tag test(parser(metadata)): Tests parser with tag and metadata, but no comment and no colon seperator.
	 * @throws Exception
	 */
	public void testParserMetaDataWithoutComment2() throws Exception {
		TagParser tp = new TagParser();

		ParseTree pt = tp.parse("@tag peter(bugzilla(22 21)) [author = sean; date = 01/01/2006]");
		
		ParseNode[] nodes = pt.getNodeCollection();
		assertEquals(1, nodes.length);
		
		String[] keys = pt.getMetaDataKeys();
		String[] values = pt.getMetaDataValues();
		
		assertEquals(2, keys.length);
		assertEquals(2, values.length);
		
		int found = 0;
		for(int i = 0; i < keys.length; i++) {
			if(keys[i].equals("author")) found++;
			else if(keys[i].equals("date")) found++;
		}
		
		assertEquals(2, found);
		
		found = 0;
		for(int i = 0; i < values.length; i++) {
			if(values[i].equals("sean")) found++;
			else if(values[i].equals("01/01/2006")) found++;
		}
		
		assertEquals(2, found);
	}
	
	/**
	 * @tag test(parser(multiline)) : Tests a multiline tag.
	 * @throws Exception
	 */
	public void testParserMetaDataMultiline() throws Exception {
		TagParser tp = new TagParser();
	
		ParseTree pt = tp.parse("@tag peter(bugzilla(22 21)) : this is a comment [\n" +
				"author = sean; " +
				"date = 01/01/2006" +
				"]");
		
		ParseNode[] nodes = pt.getNodeCollection();
		assertEquals(1, nodes.length);
		
		String[] keys = pt.getMetaDataKeys();
		String[] values = pt.getMetaDataValues();
		
		assertEquals(3, keys.length);
		assertEquals(3, values.length);
		
		int found = 0;
		for(int i = 0; i < keys.length; i++) {
			if(keys[i].equals("comment")) found++;
			else if(keys[i].equals("author")) found++;
			else if(keys[i].equals("date")) found++;
		}
		
		assertEquals(3, found);
		
		found = 0;
		for(int i = 0; i < values.length; i++) {
			if(values[i].equals("this is a comment")) found++;
			else if(values[i].equals("sean")) found++;
			else if(values[i].equals("01/01/2006")) found++;
		}
		
		assertEquals(3, found);
	}
	
	/**
	 * @tag test(parser(special_cases))
	 * @throws Exception
	 */
	public void testParserSpecialCases() throws Exception {
		TagParser tp = new TagParser();

		ParseTree pt = tp.parse("(parser)");		
		assertEquals(1, pt.getNodeCollection().length);
		
		pt = tp.parse("parser( 	)");			
		assertEquals(1, pt.getNodeCollection().length);
	}
	
	/**
	 * @tag test(parser(invalid_syntax))
	 * @throws Exception
	 */
	public void testParserInvalidSyntax() throws Exception {
		TagParser tp = new TagParser();

		ParseTree pt = tp.parse("parser(sean : bob)");		
		assertEquals(0, pt.getNodeCollection().length);
		
		pt = tp.parse("wrong(1)(:");
		assertEquals(0, pt.getNodeCollection().length);
		
		pt = tp.parse(")sillybracket(");
		assertEquals(0, pt.getNodeCollection().length);
		
		pt = tp.parse("parses screwy(:) : hello world");
		assertEquals(0, pt.getNodeCollection().length);
		
		pt = tp.parse("parses screwy) : hello world");
		assertEquals(0, pt.getNodeCollection().length);
		
		pt = tp.parse("[author=sean]");
		assertEquals(0, pt.getNodeCollection().length);
		
		pt = tp.parse("mytag : hi [author=sean] lalalala");
		assertEquals(1, pt.getNodeCollection().length);
		
		String[] keys = pt.getMetaDataKeys();
		String[] values = pt.getMetaDataValues();
		
		assertEquals(2, keys.length);
		assertEquals(2, values.length);
		
		int found = 0;
		for(int i = 0; i < keys.length; i++) {
			if(keys[i].equals("author")) found++;
			else if(keys[i].equals("comment")) found++;
		}
		
		assertEquals(2, found);
		
		found = 0;
		for(int i = 0; i < values.length; i++) {
			if(values[i].equals("sean")) found++;
			else if(values[i].equals("hi")) found++;
		}
		
		assertEquals(2, found);
	}
	
	public void testParseTreeGenerateKeywords() throws Exception {
		TagParser tp = new TagParser();
		ParseTree pt = tp.parse("@tag peter(bugzilla(22 21)) : this is a comment");
		
		ParseNode root = pt.getNodeCollection()[0];
		root.setName("root");
		String keywords = pt.generateKeywords();
		assertEquals("root(bugzilla(22 21))", keywords);
		
		ParseNode bugz = root.getChildren()[0];
		bugz.setName("Bugz");
		keywords = pt.generateKeywords();
		assertEquals("root(Bugz(22 21))", keywords);
		
		bugz.getChildren()[0].setName("22B");
		bugz.getChildren()[1].setName("21B");
		keywords = pt.generateKeywords();
		assertEquals("root(Bugz(22B 21B))", keywords);
		
	}
	
}