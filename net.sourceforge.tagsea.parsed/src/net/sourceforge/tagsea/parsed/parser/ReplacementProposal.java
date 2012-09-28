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
package net.sourceforge.tagsea.parsed.parser;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * A replacement proposal replaces text in a document.
 * @author Del Myers
 *
 */
public class ReplacementProposal implements IReplacementProposal {
	
	private int offset;
	private int length;
	private String text;
	private String displayString;

	public ReplacementProposal(String displayString, int offset, int length, String text) {
		this.offset = offset;
		this.length = length;
		this.text = text;
		this.displayString = displayString;
	}
	
	public String getDisplayString() {
		return displayString;
	}
	
	/**
	 * Applies the replacement to the given document. Must be called within the display thread.
	 * @param document
	 * @throws BadLocationException if the replacement couldn't occur in the correct location
	 */
	public void apply(IDocument document) throws BadLocationException {
		document.replace(offset, length, text);
	}

}
