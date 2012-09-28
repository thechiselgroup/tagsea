/*******************************************************************************
 * Copyright 2006, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *     IBM Corporation
 *******************************************************************************/
package net.sourceforge.tagsea.parsed.java.autocomplete.internal;

import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.parsed.java.ParsedJavaWaypointsPlugin;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;


/**
 * The standard implementation of the <code>ICompletionProposal</code> interface.
 */
public class TagCompletionProposal implements ICompletionProposal, ICompletionProposalExtension2, IJavaCompletionProposal{

	/** The replacement string. */
	protected String fReplacementString;
	/** The replacement offset. */
	protected int fReplacementOffset;
	/** The string using parenthesis **/
	protected String fParaString;
	/** The replacement length. */
	protected int fReplacementLength;
	/** The cursor position after this proposal has been applied. */
	protected int fCursorPosition;
	/** The image to be displayed in the completion proposal popup. */
	private Image fImage;
	/** The context information of this proposal. */
	private IContextInformation fContextInformation;
	/** The additional info of this proposal. */
	private String fAdditionalProposalInfo;
	/** The tag that is being used for the replace */
	private ITag fTag;
	
	private int relevance = 5000;

	/**
	 * Creates a new completion proposal based on the provided information. The replacement string is
	 * considered being the display string too. All remaining fields are set to <code>null</code>.
	 *
	 * @param tag the tag that is used for the replacement
	 * @param replacementOffset the offset of the text to be replaced
	 * @param replacementLength the length of the text to be replaced
	 * @param cursorPosition the position of the cursor following the insert relative to replacementOffset
	 * @param useParas whether or not to use parentheses in the proposal
	 */
	public TagCompletionProposal(ITag tag, int replacementOffset, int replacementLength, int cursorPosition, boolean useParas) {
		this(tag, replacementOffset, replacementLength, null, null, null, useParas);
	}

	/**
	 * Creates a new completion proposal. All fields are initialized based on the provided information.
	 *
	 * @param tag the tag that is used for the replacement.
	 * @param replacementOffset the offset of the text to be replaced
	 * @param replacementLength the length of the text to be replaced
	 * @param cursorPosition the position of the cursor following the insert relative to replacementOffset
	 * @param image the image to display for this proposal
	 * @param contextInformation the context information associated with this proposal
	 * @param additionalProposalInfo the additional information associated with this proposal
	 */
	public TagCompletionProposal(ITag tag, int replacementOffset, int replacementLength, Image image, IContextInformation contextInformation, String additionalProposalInfo, boolean useParas) {
		
		Assert.isTrue(replacementOffset >= 0);
		Assert.isTrue(replacementLength >= 0);
		
		this.fTag = tag;
		fReplacementOffset= replacementOffset;
		fReplacementLength= replacementLength;
		
		fImage= image;
		fContextInformation= contextInformation;
		fAdditionalProposalInfo= additionalProposalInfo;
		fParaString = createParaString();
		if (useParas) {
			fReplacementString = fParaString;
		} else {
			fReplacementString = tag.getName();
		}
		fCursorPosition= fReplacementString.length();
	}

	/*
	 * @see ICompletionProposal#apply(IDocument)
	 */
	public void apply(IDocument document) {
		try {
			document.replace(fReplacementOffset, fReplacementLength, fReplacementString);
		} catch (BadLocationException x) {
			// ignore
		}
	}

	/*
	 * @see ICompletionProposal#getSelection(IDocument)
	 */
	public Point getSelection(IDocument document) {
		return new Point(fReplacementOffset + fCursorPosition, 0);
	}

	/*
	 * @see ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		return fContextInformation;
	}

	/*
	 * @see ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return fImage;
	}

	/*
	 * @see ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		return fReplacementString;
	}

	/*
	 * @see ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return fAdditionalProposalInfo;
	}
	
	public int getRelevance() {
		return relevance;
	}

	public void setRelevance(int relevance) {
		this.relevance = relevance;
	}

	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		try {
			viewer.getDocument().replace(fReplacementOffset, fReplacementLength, fReplacementString);
		} catch (BadLocationException e) {
			ParsedJavaWaypointsPlugin.getDefault().log(e);
		}		
	}

	public void selected(ITextViewer viewer, boolean smartToggle) {
	}

	public void unselected(ITextViewer viewer) {
	}

	public boolean validate(IDocument document, int offset, DocumentEvent event) {
		//grab the word to the last whitespace and return true if there is a match.
		String word = "";
		try {
			
			offset--;
			char c = document.getChar(offset);
			while (!Character.isWhitespace(c)) {
				word = c + word;
				c = document.getChar(--offset);
			}
		} catch (BadLocationException e) {
			return false;
		}
		updateString(word);
		return fTag.getName().toLowerCase().startsWith(word.replace('(', '.').toLowerCase());
	}
	
	/**
	 * Creates the string using parenthesis for the tag name.
	 * @return the tag name in parenthesis form
	 */
	protected String createParaString() {
		StringBuilder tagName = new StringBuilder(fTag.getName());
		for (int i = 0; i < tagName.length(); i++) {
			char c = tagName.charAt(i);
			if (c == '.') {
				tagName.setCharAt(i, '(');
				tagName.append(')');
			}
		}
		return tagName.toString();
	}
	
	/**
	 * Update the replacement string based on the kind of syntax the user is using.
	 * @param newString
	 */
	private void updateString(String newString) {
		int dot = newString.indexOf('.');
		int para = newString.indexOf('(');
		if (para >= 0 && dot < 0) {
			fReplacementString = fParaString;
		} else {
			fReplacementString = fTag.getName();
		}
		fCursorPosition = fReplacementString.length();
		fReplacementLength = newString.length();
	}

	
	
}
