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

import java.util.LinkedList;

/**
 * Generic node used in the ParseTree data structure.
 * Contains the name of the node and a linked list of children.
 * 
 * @author Sean "The Hitman" Falconer
 */
public class ParseNode {
	// this node's name
	private String name = null;
	
	private ParseNode parent = null;

	// a list of child nodes
	private LinkedList<ParseNode> children = new LinkedList<ParseNode>();

	public ParseNode() {
	}

	public ParseNode(String name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ParseNode getParent() {
		return parent;
	}
	
	public ParseNode[] getChildren() {
		return (ParseNode[]) children.toArray(new ParseNode[children.size()]);
	}
	
	public ParseNode addChild(String childName) {
		ParseNode node = null;
		if (childName != null) {
			node = addChild(new ParseNode(childName));
		}
		return node;
	}

	/**
	 * Adds the child if it doesn't already exist already (based on the name).
	 * @param child
	 * @return ParseNode the added node
	 */
	public ParseNode addChild(ParseNode newChild) {
		ParseNode child = newChild;
		if (newChild != null) {
			boolean existing = false;
			// only add the child if one with that name doesn't already exist
			for (ParseNode node : children) {
				if ((node.getName() != null) && (node.getName().equals(newChild.getName()))) {
					child = node;
					existing = true;
					break;
				}
			}
			if (child != null) {
				if (!existing) {
					children.add(child);
				}
				child.parent = this;
			}
		}
		return child;
	}

	/**
	 * Removes the child. If no child with that name exists null is returned.
	 * Also sets the child to have a null parent.
	 * @param child the child node to remove
	 * @return the ParseNode removed or null if not found
	 */
	public ParseNode removeChild(ParseNode child) {
		ParseNode removedChild = child;
		if (child != null) {
			removedChild = removeChild(child.getName());
			child.parent = null;
		}
		return removedChild;
	}
	
	/**
	 * Removes a child with the given name.
	 * Also sets the removed node to have a null parent.
	 * If no child with that name exists null is returned.
	 * @param name the child's name
	 * @return the ParseNode removed or null
	 */
	public ParseNode removeChild(String name) {
		ParseNode node = null;
		if (name != null) {
			for (ParseNode pn : children) {
				if (name.equals(pn.getName())) {
					node = pn;
					break;
				}
			}
		} 
		if (node != null) {
			children.remove(node);
			node.parent = null;
		}
		return node;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @return true if this node has children
	 */
	public boolean hasChildren() {
		return (children.size() > 0);
	}
	
}
