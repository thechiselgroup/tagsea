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
package net.sourceforge.tagsea.core.ui.tags;

import net.sourceforge.tagsea.core.ITag;

import org.eclipse.jface.fieldassist.IContentProposal;

/**
 * A content proposal for tags.
 * @author Del Myers
 */

public class TagContentProposal implements IContentProposal {
	
	private ITag tag;
	private int offset;

	/**
	 * Creates a proposal for the given tag.
	 * @param tag the tag
	 * @param offset the offset into the tag name that the proposal begins at.
	 */
	public TagContentProposal(ITag tag, int offset) {
		this.tag = tag;
		this.offset = offset;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.fieldassist.IContentProposal#getContent()
	 */
	public String getContent() {
		return tag.getName().substring(offset);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.fieldassist.IContentProposal#getCursorPosition()
	 */
	public int getCursorPosition() {
		return tag.getName().length();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.fieldassist.IContentProposal#getDescription()
	 */
	public String getDescription() {
		return "Insert this tag";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.fieldassist.IContentProposal#getLabel()
	 */
	public String getLabel() {
		return tag.getName();
	}

}
