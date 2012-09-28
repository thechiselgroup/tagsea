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
package ca.uvic.cs.tagsea.editing;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import ca.uvic.cs.tagsea.core.Tag;
import ca.uvic.cs.tagsea.extraction.TagExtractor;
import ca.uvic.cs.tagsea.parser.ParseNode;
import ca.uvic.cs.tagsea.parser.ParseTree;
import ca.uvic.cs.tagsea.parser.TagParser;


/**
 * This is a collection of useful methods for working with Tags and IDocuments.
 * 
 * @author Chris Callendar
 */
public class TagRefactoring {
		
	/**
	 * Finds the ParseNode in the ParseTree which matches the tag and its hierarchy.
	 * @param tag
	 * @param tree
	 * @return ParseNode or null if the tag wasn't found in the ParseTree
	 */
	public static ParseNode findParseNodeForTag(Tag tag, ParseTree tree) {
		// hierarchy of ParseNodes each having one child except the leaf
		ParseTree hierarchy = buildParseTreeUpHierarchy(tag);
		ParseNode node = tree.getRoot();
		ParseNode hierarchyNode = hierarchy.getRoot();
		if (node.hasChildren()) {
			while (node.hasChildren() && hierarchyNode.hasChildren()) {
				hierarchyNode = hierarchyNode.getChildren()[0];
				boolean found = false;
				for (ParseNode child : node.getChildren()) {
					if (hierarchyNode.getName().equals(child.getName())) {
						node = child;
						found = true;
						break;
					}
				}
				if (!found) {
					node = null;
					break;
				}
			}
		} else {
			node = null;
		}
		return node;
	}
	
	/**
	 * Creates a ParseTree for the given tag and its ancestors
	 * up the hierarchy.  The children of tag won't be included in the parse tree.
	 * @param tag the tag
	 * @return ParseTree containing ParseNodes that each has one child except the leaf node
	 */
	public static ParseTree buildParseTreeUpHierarchy(Tag tag) {
		ParseTree tree = new ParseTree();
		ParseNode current = null, child = null;
		while ((tag != null) && (tag.getName() != null) && (tag.getName().length() > 0)) {
			current = new ParseNode(tag.getName());
			if (child != null) {
				current.addChild(child);
			}
			child = current;
			tag = tag.getParent();
		}
		if (current != null) {
			tree.getRoot().addChild(current);
		}
		return tree;
	}
	
	/**
	 * Removes the node from it's parent.
	 * If the parent has no more children then it is removed from it's parent.
	 * @param node
	 * @return boolean if all the nodes were removed, false if some still exist
	 */
	public static boolean removeParseNodeFromParent(ParseNode node) {
		boolean removedAll = true;
		if (node != null) {
			ParseNode parent = node.getParent();
			if (parent != null) {
				parent.removeChild(node);
				if (parent.hasChildren()) {
					removedAll = false;
				} else {
					removedAll = removeParseNodeFromParent(parent);
				}
			}
		}		
		return removedAll;
	}
	
	/**
	 * Renames a tag in the given document.  Duplicate tag names are condensed into one tag.
	 * @param tag the tag to rename in the document - this tag's name does not get changed in this method
	 * @param newTagName the new name for the tag
	 * @param doc the document
	 * @param offset the offset in the document where the (@)tag resides
	 * @param length the length of the tag
	 * @return boolean if the rename happened
	 */
	public static boolean renameTagInDocument(Tag tag, String newTagName, IDocument doc, int offset, int length) throws Exception {
		if ((tag == null) || (newTagName == null) || (newTagName.length() == 0))
			throw new NullPointerException("The tag cannot be null, and the new tag name must be valid.");

		if (newTagName.equals(tag.getName()))
			return false;
		
		String tagText = doc.get(offset, length);	// something like '(@)tag tag1 : comment'
		// find the position of the (@)tag
		int indexOfAtTag = tagText.indexOf("@tag");
		if (indexOfAtTag == -1)
			throw new Exception("Failed to find '@tag' in the document at the given offset and length.");
		
		TagParser parser = new TagParser();
		ParseTree tree = parser.parse(tagText);
		ParseNode foundNode = findParseNodeForTag(tag, tree);
		
		if (foundNode != null) {
			String oldKeywords = tree.getKeywords();
			if (foundNode.getParent() != null) {
			// if parent already contains a child with newTagName then remove it to not have duplicates
				foundNode.getParent().removeChild(newTagName);
			}
			// now update the found ParseNode to have the new name
			foundNode.setName(newTagName);
			
			// now regenerate the keywords with the updated tag names
			String newKeywords = tree.generateKeywords();
			// find the position of the old keywords, starting after the (@)tag
			// @tag Bug(166) Refactoring : must start looking after the (@)tag!
			int index = tagText.indexOf(oldKeywords, indexOfAtTag + 4);
			if (index != -1) {
				doc.replace(offset + index, oldKeywords.length(), newKeywords);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Renames a tag in the given document.  Duplicate tag names are condensed into one tag.
	 * @param tag the tag to delete in the document
	 * @param doc the document
	 * @param offset the offset in the document where the (@)tag resides
	 * @param length the length of the tag
	 * @return boolean if the rename happened
	 */
	public static boolean deleteTagInDocument(Tag tag, IDocument doc, int offset, int length) throws Exception {
		if (tag == null)
			throw new NullPointerException("The tag cannot be null.");

		String tagText = doc.get(offset, length);	// something like '(@)tag tag1 : comment'
		// find the position of the (@)tag
		int indexOfAtTag = tagText.indexOf("@tag");
		if (indexOfAtTag == -1)
			throw new Exception("Couldn't find '@tag' in the document at the given offset and length.");

		TagParser parser = new TagParser();
		ParseTree tree = parser.parse(tagText);
		ParseNode nodeToRemove = findParseNodeForTag(tag, tree);
		
		// remove the node, and 
		if (nodeToRemove != null) {
			boolean removedAll = removeParseNodeFromParent(nodeToRemove);
			
			String oldKeywords = tree.getKeywords();
			// now regenerate the keywords with the updated tag names
			String newKeywords = tree.generateKeywords();
			// must start looking after the (@)tag
			int index = tagText.indexOf(oldKeywords, indexOfAtTag + 4);
			if (index != -1) {
				if (!removedAll) {
					// there is still at least one tag on this line
					doc.replace(offset + index, oldKeywords.length(), newKeywords);
				} else {
					// there are no tags on this line anymore, remove them all
					doc.replace(offset, tagText.length(), "");
					removeEmptyComment(doc, offset, length - tagText.length());
				}
				return true;
			}
		} else {
			throw new Exception("Couldn't find the node to remove at the given offset and length");
		}

		return false;
	}	
	
	/**
	 * Moves a tag in the given document.
	 * @param tag the tag to rename in the document - this tag's name does not get changed in this method
	 * @param newParent the new parent for tag - can be to make the tag become a root node
	 * @param doc the document
	 * @param offset the offset in the document where the (@)tag resides
	 * @param length the length of the tag
	 * @return boolean if the move happened
	 * @throws Exception if tag is null, if the document doesn't contain (@)tag
	 */
	public static boolean moveTagInDocument(Tag tag, Tag newParent, IDocument doc, int offset, int length) throws Exception {
		if (tag == null)
			throw new NullPointerException("The tag cannot be null.");

		// don't do anything if already have the same parents
		if (tag.getParent() == newParent)
			return false;
		
		// don't do anything if tag equals newParent or is a parent of newParent 
		Tag compare = newParent;
		while (compare != null) {
			if (compare == tag) {
				return false;
			}
			compare = compare.getParent();
		}
		
		String tagText = doc.get(offset, length);	// something like '(@)tag tag1 : comment'
		// find the position of the (@)tag
		int indexOfAtTag = tagText.indexOf("@tag");
		if (indexOfAtTag == -1)
			throw new Exception("Couldn't find '@tag' in the document at the given offset and length.");

		TagParser parser = new TagParser();
		ParseTree tree = parser.parse(tagText);
		String oldKeywords = tree.getKeywords();

		// find the parse node for the given tag
		ParseNode node = findParseNodeForTag(tag, tree);
		if (node != null) {
			removeParseNodeFromParent(node);
			
			// now build a new parse node tree for the parent
			if (newParent == null) {
				tree.getRoot().addChild(node);
			} else {
				ParseTree pt = buildParseTreeUpHierarchy(newParent);
				ParseNode parentNode = addParseNodeHierarchyToTree(tree, pt);
				parentNode.addChild(node);
			}
			
			// now regenerate the keywords with the updated tag names
			String newKeywords = tree.generateKeywords();
			// must start looking after the (@)tag
			int index = tagText.indexOf(oldKeywords, indexOfAtTag + 4);
			if (index != -1) {
				doc.replace(offset + index, oldKeywords.length(), newKeywords);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * For debugging.  Prints out the parse tree.
	 * @param tree
	 */
	@SuppressWarnings("restriction")
	public static void printParseTree(ParseTree tree) {
		ParseNode root = tree.getRoot();
		System.out.println((root.getName() == null ? "ROOT" : root.getName()));
		printParseTree(root.getChildren(), " ");
	}

	/**
	 * For debugging.  Prints out the parse node and it's descendents.
	 * @param node
	 */
	@SuppressWarnings("restriction")
	public static void printParseTree(ParseNode node) {
		System.out.println((node.getName() == null ? "ROOT" : node.getName()));
		printParseTree(node.getChildren(), " ");
	}
	
	@SuppressWarnings("restriction")
	private static void printParseTree(ParseNode[] children, String spc) {
		for (ParseNode child : children) {
			if (child.hasChildren()) {
				System.out.println(spc + "+ " + child.getName());
				printParseTree(child.getChildren(), spc + "   ");
			} else {
				System.out.println(spc + "- " + child.getName());
			}
		}
	}

	/**
	 * Adds the given node and its children to the parse tree.
	 * @param realTree the ParseTree which will have ParseNodes added to it from the treeHierarchy ParseTree.
	 * @param treeHierarchy the single hierarchy of ParseNodes which all only <b>one</b> child except for the leaf node. 
	 * These will be added to the realTree ParseTree.
	 * @return ParseNode the added (or already existing) <b>leaf</b> ParseNode from the ParseTree
	 */
	public static ParseNode addParseNodeHierarchyToTree(ParseTree realTree, ParseTree treeHierarchy) {
		ParseNode leaf = null;
		ParseNode[] root = treeHierarchy.getNodeCollection();
		if (root.length == 1) {
			leaf = addParseNodeToParent(realTree.getRoot(), root[0]);
		}
		return leaf;
	}	
	
	/**
	 * Adds the given node and its children to the parent ParseNode.
	 * @param parent the parent ParseNode
	 * @param node the node to add (single hierarchy)
	 * @return ParseNode the added (or already existing) <b>leaf</b> ParseNode
	 */
	private static ParseNode addParseNodeToParent(ParseNode parent, ParseNode node) {
		String name = node.getName();
		if (name == null)
			return null;
		
		ParseNode addedNode = null;
		// try to find a ParseNode with the same name
		for (ParseNode pn : parent.getChildren()) {
			if (name.equals(pn.getName())) {
				if (node.hasChildren()) {
					addedNode = addParseNodeToParent(pn, node.getChildren()[0]);
				} else {
					// already exists
					addedNode = pn;
				}
			}
		}
		if (addedNode == null) {
			addedNode = parent.addChild(name);
			if (node.hasChildren()) {
				addedNode = addParseNodeToParent(addedNode, node.getChildren()[0]);
			}
		}
		return addedNode;
	}

	/**
	 * Uses the TagExtractor to get the comment from the document at the given offset and length.
	 * If there is nothing but whitespace inside the comment, it is removed.
	 * @param doc the document containing the comment
	 * @param offset the offset where the comment is
	 * @param length the length of the comment
	 * @return boolean if the comment was empty and was removed
	 * @throws Exception
	 */
	public static boolean removeEmptyComment(IDocument doc, final int offset, int length) throws Exception {
		// now get the comment and check if anything is left behind, if not remove it too
		IRegion[] comments = TagExtractor.getCommentRegions(doc, 0, doc.getLength());
		if (comments.length == 0) 
			return false;

		int commentOffset = offset;
		boolean foundComment = false;
		for (IRegion region : comments) {
			int regionOffset = region.getOffset();
			int regionLength = region.getLength();
			if ((regionOffset < offset) && ((regionOffset + regionLength) >= (offset + length))) {
				commentOffset = regionOffset;
				length = regionLength;
				foundComment = true;
				break;
			}
		}
		if (!foundComment)
			return false;
		
		String comment = doc.get(commentOffset, length);
		String trimmed = comment.trim();
		boolean singleLine = trimmed.startsWith("//");
		boolean multiLine = trimmed.startsWith("/*") && trimmed.endsWith("*/");
		//boolean javaDoc = multiLine && trimmed.startsWith("/**");

		// remove the comment characters
		if (singleLine) {
			trimmed = trimmed.substring(2);
		} else if (multiLine) {
			trimmed = trimmed.substring(2, trimmed.length() - 4);
			//if (javaDoc) {
				// note this also removes any '*' that appear in the java doc comment...
				trimmed = trimmed.replace('*', ' ');
			//}
		}
		// now remove any extra whitespace
		trimmed = trimmed.trim();
		if (trimmed.length() == 0) {
			// since the comment is only whitespace, remove it entirely
			// check if the text before the comment on the same line if not whitespace
			int startOfLine = doc.getLineOffset(doc.getLineOfOffset(commentOffset));
			if (commentOffset > startOfLine) {
				String beforeComment = doc.get(startOfLine, commentOffset - startOfLine).trim();
				// adjust the offset to the start of the line to remove the line altogether
				if (beforeComment.length() == 0) {
					length = length + (commentOffset - startOfLine);
					commentOffset = startOfLine;
				}
			}
			// now replace the comment with the empty string
			doc.replace(commentOffset, length, "");
			return true;
		}
		
		// if the whole comment wasn't remove - check if a multiline comment left a blank line
		if (multiLine) {
			int line = doc.getLineOfOffset(offset);
			int startOfLine = doc.getLineOffset(line);
			int lineLength = doc.getLineLength(line); // includes newline delimeters
			String blankLine = doc.get(startOfLine, lineLength);
			blankLine = blankLine.replace('*', ' ').trim();
			if (blankLine.length() == 0) {
				// empty line, so remove
				doc.replace(startOfLine, lineLength, "");
			}			
		}
		return false;
	}	
}
