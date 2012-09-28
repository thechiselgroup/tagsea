/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.java.autocomplete.internal;

import net.sourceforge.tagsea.core.ITag;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

/**
 * Whenever a tag is being added to a waypoint, there will always be one tag
 * that exactly matches what is being typed. This proposal constantly updates
 * itself to match that tag.
 * @author Del Myers
 *
 */
//@tag tagsea.bug.3.fix -author="Del Myers" -date="enCA:18/01/07" : created so that the assist gets updated as you type.
public class VolatileTagCompletionProposal extends
		TagCompletionProposal {

	public VolatileTagCompletionProposal(ITag tag, int replacementOffset, int replacementLength, Image image, IContextInformation contextInformation, String additionalProposalInfo, boolean useParas) {
		super(tag, replacementOffset, replacementLength, image,
				contextInformation, additionalProposalInfo, useParas);
	}

	@Override
	public boolean validate(IDocument document, int offset, DocumentEvent event) {
		String word = "";
		try {
			offset--;
			char c = document.getChar(offset);
			while (!Character.isWhitespace(c)) {
				word = c + word;
				c = document.getChar(--offset);
			}
			String tagString = updateString(word);
			return tagString != null;
		} catch (BadLocationException e) {
		}
		return false;
	}
	
	//returns a tag name for the given new string
	private String updateString(String newString) {
		int dot = newString.indexOf('.');
		int para = newString.indexOf('(');
		if (para != -1 && dot !=-1) {
			//not a valid tag name.
			return null;
		}
		if (para >= 0) {
			//count the opening parentheses and match them at the end.
			int paraCount = 0;
			boolean closeFound = false;
			for (int i = 0; i < newString.length(); i++) {
				char c = newString.charAt(i);
				if (c == '(') {
					paraCount++;
				} else if (c == ')') {
					paraCount--;
					closeFound = true;
				} else if (closeFound) {
					return null;
				}
			}
			fReplacementString = newString;
			for (int i = 0; i < paraCount; i++) {
				fReplacementString += ')';
			}
			fReplacementLength = newString.length();
			fCursorPosition=fReplacementString.length();
			//get a proper tag name
			newString = newString.replace('(', '.');
			int closeCount = 0;
			for (int i = newString.length()-1; i >=0; i--) {
				char c = newString.charAt(i);
				if (c == ')') {
					closeCount++;
				} else {
					break;
				}
			}
			return newString.substring(0, newString.length()-closeCount);
		} else {
			fReplacementString = newString;
			fReplacementLength = newString.length();
			fCursorPosition=fReplacementLength;
			return newString;
		}
	}

}
