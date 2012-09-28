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

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IRegion;

import ca.uvic.cs.tagsea.core.Tag;
import ca.uvic.cs.tagsea.editing.TagRefactoring;
import ca.uvic.cs.tagsea.extraction.TagExtractor;
import ca.uvic.cs.tagsea.parser.ParseNode;
import ca.uvic.cs.tagsea.parser.ParseTree;
import ca.uvic.cs.tagsea.parser.TagParser;

/**
 * Tests the TagRefactoring class.  
 * 
 * @author Chris Callendar
 */
public class TagRefactoringTest extends TestCase {
	
	public void testBuildParseTree() throws Exception {
		Tag root = new Tag(null, "root");
		Tag child = new Tag(root, "child");
		Tag tag1 = new Tag(child, "tag1");
		ParseTree tree = TagRefactoring.buildParseTreeUpHierarchy(tag1);

		Tag tag = tag1;
		ParseNode node = TagRefactoring.findParseNodeForTag(tag1, tree);
		assertNotNull(node);
		assertEquals(node.getName(), tag1.getName());
		while ((node != null) && (tag != null)) {
			assertEquals(node.getName(), tag.getName());
			node = node.getParent();
			tag = tag.getParent();			
		}
	}

	public void testAddParseNodeToTree() throws Exception {
		String line = "@tag root(child(tag1))";
		TagParser parser = new TagParser();
		ParseTree tree = parser.parse(line);
		
		Tag test = new Tag(null, "test");
		Tag test2 = new Tag(test, "test2");
		ParseTree treeHierarchy = TagRefactoring.buildParseTreeUpHierarchy(test2);
		
		TagRefactoring.addParseNodeHierarchyToTree(tree, treeHierarchy);
		ParseNode[] nodes = tree.getNodeCollection();
		assertEquals(2, nodes.length);
		assertTrue("test".equals(nodes[1].getName()));
		assertTrue("test2".equals(nodes[1].getChildren()[0].getName()));
	}
	
	public void testRemoveParseNodeFromParent() throws Exception {
		// try deleting a single hierarchy
		ParseNode node = new ParseNode("Root");
		ParseNode child = node.addChild("child");
		ParseNode subChild = child.addChild("subchild");
		
		boolean removedAll = TagRefactoring.removeParseNodeFromParent(subChild);
		assertTrue(removedAll);

		node = new ParseNode("Root");
		child = node.addChild("child");
		subChild = child.addChild("subchild1");
		ParseNode subChild2 = child.addChild("subchild2");
		removedAll = TagRefactoring.removeParseNodeFromParent(subChild2);
		assertFalse(removedAll);		

		node = new ParseNode("Root");
		ParseNode child2 = node.addChild("child2");
		child = node.addChild("child");
		subChild = child.addChild("subchild1");
		
		removedAll = TagRefactoring.removeParseNodeFromParent(child2);
		assertFalse(removedAll);	
	}

	public void testSimpleRename() throws Exception {
		String line = "// @tag tag1 : comment";
		Tag tag1 = new Tag(null, "tag1");
		doRenameInDocument(line, tag1, "tag2");

		Tag root = new Tag(null, "root");
		tag1 = new Tag(root, "tag1");
		line = "// @tag root(tag1) : comment";
		doRenameInDocument(line, tag1, "tag2");

		tag1.addChild("child");
		line = "// @tag root(tag1(child)) : comment";
		doRenameInDocument(line, tag1, "tag2");
	}

	public void testRenameTagWhenMultipleOnLine() throws Exception {
		String line = "// @tag sibling tag1";
		Tag tag1 = new Tag(null, "tag1");
		doRenameInDocument(line, tag1, "tag2");

		line = "// @tag root(sibling tag1)";
		Tag root = new Tag(null, "root");
		tag1 = new Tag(root, "tag1");
		root.addChild("sibling");
		doRenameInDocument(line, tag1, "tag2");

		line = "// @tag root(tag1(child) sibling)";
		tag1.addChild("child");
		doRenameInDocument(line, tag1, "tag2");

	}

	public void testDuplicateRename() throws Exception {
		Tag tag1 = new Tag(null, "tag1");
		String line = "// @tag tag2 tag1 : comment";
		doRenameInDocument(line, tag1, "tag2");

		line = "// @tag tag1 tag2 : comment";
		doRenameInDocument(line, tag1, "tag2");

		Tag root = new Tag(null, "root");
		tag1 = new Tag(root, "tag1");
		line = "// @tag root(tag2 tag1)";
		doRenameInDocument(line, tag1, "tag2");
		line = "// @tag root(tag1 tag2)";
		doRenameInDocument(line, tag1, "tag2");

		tag1.addChild("child");
		line = "// @tag root(tag1(child) tag2(child))";
		doRenameInDocument(line, tag1, "tag2");

		line = "// @tag root(tag1(child)) root(tag2)";
		doRenameInDocument(line, tag1, "tag2");
	}

	public void testSpecialCases() throws Exception {
		String line = "// @tag tag11 tag1 : comment";
		Tag tag1 = new Tag(null, "tag1");
		doRenameInDocument(line, tag1, "tag2");

		Tag root = new Tag(null, "root");
		tag1 = new Tag(root, "tag1");
		line = "// @tag root(tag1 tag11) : comment";
		doRenameInDocument(line, tag1, "tag2");

		line = "// @tag root(tag tag11 tag1) : comment";
		doRenameInDocument(line, tag1, "tag2");
	}
	
	public void testRenameWhenCalledTag() throws Exception {
		// @tag Bug(166) Refactoring : this tests renaming a tag called 'tag'
		String line = "// @tag tag";
		Tag tag = new Tag(new Tag(), "tag");
		String updated = doRenameInDocument(line, tag, "mike");
		// this was failing in Bug 166
		assertTrue(updated.indexOf("@mike") == -1);
	}
	
	/**
	 * Only tests renaming inside a document - does not rename the tag!
	 * Assumes that the line parameter only contains one (@)tag inside a comment.
	 * @param line
	 * @param tag
	 * @param newTagName
	 * @return String the updated line
	 * @throws Exception
	 */
	private String doRenameInDocument(String line, Tag tag, String newTagName) throws Exception {
		Document doc = new Document(line);
		// System.out.print(doc.get() + "\t->\t");
		IRegion[] tagRegions = TagExtractor.getTagRegions(doc);
		assertEquals(1, tagRegions.length);
		boolean okay = TagRefactoring.renameTagInDocument(tag, newTagName, doc, tagRegions[0].getOffset(), tagRegions[0].getLength());
		assertTrue(okay);
		String contents = doc.get();
		assertTrue(contents.indexOf(newTagName) != -1);
		// System.out.println(doc.get());
		return contents;
	}

	public void testRemoveNode() throws Exception {
		TagParser tp = new TagParser();
		String line = "@tag tag1 : comment";

		ParseTree tree = tp.parse(line);
		ParseNode parent = tree.getRoot();
		assertTrue(parent.hasChildren());
		assertEquals(1, parent.getChildren().length);

		// add an already existing node
		ParseNode node = new ParseNode("tag1");
		node = parent.addChild(node);
		assertTrue(parent.hasChildren());
		assertEquals(1, parent.getChildren().length);
		assertEquals("tag1", parent.getChildren()[0].getName());
		assertNotNull(node.getParent());
		
		parent.removeChild(node);
		boolean hasChildren = parent.hasChildren();
		assertFalse(hasChildren);

		line = "@tag tag1 tag2 : comment";
		tree = tp.parse(line);
		parent = tree.getRoot();

		parent.removeChild("tag1");
		assertTrue(parent.hasChildren()); // tag2 still remains
		assertEquals(1, parent.getChildren().length);
		assertEquals("tag2", parent.getChildren()[0].getName());

		line = "@tag root(tag1 tag2) : comment";
		tree = tp.parse(line);
		parent = tree.getRoot();
		assertEquals(1, parent.getChildren().length);
		ParseNode root = parent.getChildren()[0];
		assertEquals(2, root.getChildren().length);
		
		root.removeChild("tag1");
		assertTrue(root.hasChildren()); // root(tag2) still remains
		assertEquals(1, parent.getChildren().length);

		line = "@tag root(tag1(child) tag2) : comment";
		tree = tp.parse(line);
		parent = tree.getRoot();
		root = parent.getChildren()[0];
		assertEquals(2, root.getChildren().length);
		
		root.removeChild("tag1");		
		assertTrue(root.hasChildren()); // root(tag2) still remains
	}
	
	public void testDeleteTag() throws Exception {
		String line = "// @tag tag1 : comment";
		Tag tag1 = new Tag(null, "tag1");
		String result = doDeleteInDocument(line, tag1);
		assertEquals(0, result.length());

		Tag root = new Tag(null, "root");
		tag1 = new Tag(root, "tag1");
		line = "// @tag root(tag1)";
		result = doDeleteInDocument(line, tag1);
		assertEquals(0, result.length());

		line = "// @tag root(tag1)";
		result = doDeleteInDocument(line, root);
		assertEquals(0, result.length());

		line = "// @tag root(tag1(child))";
		result = doDeleteInDocument(line, tag1);
		assertEquals(0, result.length());

		line = "int noDelete = 0;  // @tag root(tag1(child))";
		result = doDeleteInDocument(line, tag1);
		assertTrue(result.length() > 0);
	}
	

	public void testDeleteTagWithSiblings() throws Exception {
		String line = "// @tag tag1 tag2 : comment";
		Tag tag1 = new Tag(null, "tag1");
		String result = doDeleteInDocument(line, tag1);
		assertTrue(result.length() > 0);

		Tag root = new Tag(null, "root");
		tag1 = new Tag(root, "tag1");
		line = "// @tag root(tag1 tag2)";
		result = doDeleteInDocument(line, tag1);
		assertTrue(result.length() > 0);

		line = "// @tag root(tag1) tag2";
		result = doDeleteInDocument(line, root);
		assertTrue(result.length() > 0);

		line = "// @tag root(tag1(child test) tag2)";
		result = doDeleteInDocument(line, tag1);
		assertTrue(result.length() > 0);

	}

	public void testDeleteMultiLineTag() throws Exception {
		String line = "/* @tag tag1 : comment [\n" + "     author = Chris;\n" + "     date = now;\n" + "   ] */";

		Tag tag1 = new Tag(null, "tag1");
		String result = doDeleteInDocument(line, tag1);
		assertEquals(0, result.length());

		line = "/* @tag tag1 : comment [\n" + "     author = Chris;\n" + "     date = now;\n"
				+ "   ] should leave this */";
		result = doDeleteInDocument(line, tag1);
		assertTrue(result.length() > 0);
	}

	public void testDeleteTagFromJavaDoc() throws Exception {
		String line = "/**\n" +
					  " * @tag tag1 : comment [\n" + 
					  " * author = Chris;\n" + 
					  " * date = now;\n" + 
					  " * ]" +
					  " */";

		Tag tag1 = new Tag(null, "tag1");
		String result = doDeleteInDocument(line, tag1);
		assertEquals(0, result.length());

		line = "/**\n" +
			   " * @param doNotDelete this \n" + 
			   " * @tag tag1 : comment [\n" + 
			   " * author = Chris;\n" + 
			   " * date = now;\n" + 
			   " * ]" +
			   " */";
		result = doDeleteInDocument(line, tag1);
		assertTrue(result.length() > 0);
	}

	public void testDeleteWhenCalledTag() throws Exception {
		// @tag Bug(166) Refactoring : this tests renaming a tag called 'tag'
		String line = "// @tag tag";
		Tag tag = new Tag(new Tag(), "tag");
		doDeleteInDocument(line, tag);
	}
		
	private String doDeleteInDocument(String line, Tag tag) throws Exception {
		Document doc = new Document(line);
		//System.out.print(doc.get() + "\t->\t");
		IRegion[] tagRegions = TagExtractor.getTagRegions(doc);
		assertEquals(1, tagRegions.length);		
		boolean okay = TagRefactoring.deleteTagInDocument(tag, doc, tagRegions[0].getOffset(), tagRegions[0].getLength());
		assertTrue(okay);
		String contents = doc.get();
		assertTrue(contents.indexOf(tag.getName()) == -1);
		//System.out.println(contents);
		return contents;
	}
	
	public void testMoveToRoot() throws Exception {
		String line = "// @tag root(tag1)";
		Tag root = new Tag(null, "root");
		Tag tag1 = new Tag(root, "tag1");
		
		String result = doMoveInDocument(line, tag1, null);
		assertEquals("// @tag tag1", result);
		
		line = "// @tag root(child(tag1))";
		root = new Tag(null, "root");
		Tag child = new Tag(root, "child");
		tag1 = new Tag(child, "tag1");
		result = doMoveInDocument(line, tag1, null);
		assertEquals("// @tag tag1", result);
		
		line = "// @tag root(tag1 tag2)";
		root = new Tag(null, "root");
		tag1 = new Tag(root, "tag1");
		root.addChild("tag2");
		result = doMoveInDocument(line, tag1, null);
		assertEquals("// @tag root(tag2) tag1", result);
		
		line = "// @tag root(tag1) tag2";
		root = new Tag(null, "root");
		tag1 = new Tag(root, "tag1");
		result = doMoveInDocument(line, tag1, null);
		assertEquals("// @tag tag2 tag1", result);
				
		line = "// @tag root(tag1) tag1";
		root = new Tag(null, "root");
		tag1 = new Tag(root, "tag1");
		result = doMoveInDocument(line, tag1, null);
		assertEquals("// @tag tag1", result);
	}
	
	public void testMove() throws Exception {
		String line = "// @tag tag1";
		Tag tag1 = new Tag(null, "tag1");
		Tag newParent = new Tag(null, "newParent");
		
		String result = doMoveInDocument(line, tag1, newParent);
		assertEquals("// @tag newParent(tag1)", result);
		
		tag1 = new Tag(null, "tag1");
		Tag root = new Tag(null, "root");
		newParent = new Tag(root, "newParent");
		result = doMoveInDocument(line, tag1, newParent);
		assertEquals("// @tag root(newParent(tag1))", result);

		line = "// @tag root(tag1)";
		root = new Tag(null, "root");
		tag1 = new Tag(root, "tag1");
		newParent = new Tag(null, "newParent");
		result = doMoveInDocument(line, tag1, newParent);
		assertEquals("// @tag newParent(tag1)", result);

		line = "// @tag root(tag1)";
		root = new Tag(null, "root");
		tag1 = new Tag(root, "tag1");
		Tag newRoot = new Tag(null, "newRoot");
		newParent = new Tag(newRoot, "newParent");
		result = doMoveInDocument(line, tag1, newParent);
		assertEquals("// @tag newRoot(newParent(tag1))", result);
		
		line = "// @tag root(tag1 tag2(child))";
		root = new Tag(null, "root");
		tag1 = new Tag(root, "tag1");
		Tag tag2 = new Tag(root, "tag2");
		tag2.addChild("child");
		result = doMoveInDocument(line, tag1, tag2);
		
	}
	
	public void testMoveWithChildren() throws Exception {
		// @tag Bug(169) Refactoring : moving children causes children to disappear
		String line = "// @tag tag1(child1 child2)";
		Tag tag1 = new Tag(null, "tag1");
		Tag child1 = new Tag(tag1, "child1");
		Tag child2 = new Tag(tag1, "child2");
		
		Tag newParent = new Tag(null, "newParent");
		String result = doMoveInDocument(line, tag1, newParent);
		assertEquals("// @tag newParent(tag1(child1 child2))", result);

		line = "// @tag tag1(child1(sub1 sub2) child2)";
		tag1 = new Tag(null, "tag1");
		child1 = new Tag(tag1, "child1");
		child1.addChild("sub1");
		child2.addChild("sub2");
		child2 = new Tag(tag1, "child2");
		
		result = doMoveInDocument(line, child1, null);
		assertEquals("// @tag tag1(child2) child1(sub1 sub2)", result);
	}

	public void testMoveWhenCalledTag() throws Exception {
		// @tag Bug(166) Refactoring : this tests renaming a tag called 'tag'
		String line = "// @tag tag";
		Tag tag = new Tag(null, "tag");
		Tag newParent = new Tag(null, "mike");
		doMoveInDocument(line, tag, newParent);
	}
	
	public void testBadMoves() throws Exception {
		// test moving the tag onto itself - not allowed
		String line = "// @tag tag1";
		Tag tag1 = new Tag(null, "tag1");
		doMoveInDocument(line, tag1, tag1, false);
		
		// test moving the tag to be a root tag when it is already a root tag
		doMoveInDocument(line, tag1, null, false);

		// test moving the tag onto its parent - nothing needs to happen
		line = "// @tag root(tag1)";
		Tag root = new Tag(null, "root");
		tag1 = new Tag(root, "tag1");
		doMoveInDocument(line, tag1, root, false);
		
		// test moving a tag onto its child - not allowed
		line = "// @tag tag1(child)";
		tag1 = new Tag(null, "tag1");
		Tag child = new Tag(tag1, "child");
		doMoveInDocument(line, tag1, child, false);
	}
	
	/**
	 * Tests valid moves - assumes the TagRefactoring.moveTagInDocument() will return true.
	 */
	private String doMoveInDocument(String docText, Tag tag, Tag newParent) throws Exception {
		return doMoveInDocument(docText, tag, newParent, true);
	}

	/**
	 * Simulates a moving a tag to a new parent in a document.  The actual tag hierarchy is not changed.
	 * @param docText the text for the document - must contain a comment with a tag.
	 * @param tag the tag to move
	 * @param newParent the new parent for tag (can be null to make tag into a root tag)
	 * @param expectedBoolean what the expected result is from the TagRefactoring.moveTagInDocument() method.
	 * That method returns false if the move is not valid.
	 * @throws Exception if docText doesn't contain a tag or if tag is null
	 */
	private String doMoveInDocument(String docText, Tag tag, Tag newParent, boolean expectedBoolean) throws Exception {
		Document doc = new Document(docText);
		//System.out.print(doc.get() + "\t->\t");
		IRegion[] tagRegions = TagExtractor.getTagRegions(doc);
		assertEquals(1, tagRegions.length);
		
		boolean okay = TagRefactoring.moveTagInDocument(tag, newParent, doc, tagRegions[0].getOffset(), tagRegions[0].getLength());
		assertEquals(expectedBoolean, okay);
		String contents = doc.get();
		//System.out.println(contents);
		return contents;
	}	

}
