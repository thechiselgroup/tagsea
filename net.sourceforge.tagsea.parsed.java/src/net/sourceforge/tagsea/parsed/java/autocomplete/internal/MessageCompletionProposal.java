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
package net.sourceforge.tagsea.parsed.java.autocomplete.internal;

import net.sourceforge.tagsea.parsed.java.ParsedJavaWaypointsPlugin;

import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * A generic replacement proposal
 * @author Del Myers
 *
 */
public class MessageCompletionProposal implements IJavaCompletionProposal {

	private int offset;
	private int length;
	private String replacement;
	
	private class MessageContextInformation  implements IContextInformation {

		public String getContextDisplayString() {
			return "Replace the message for this waypoint";
		}

		public Image getImage() {
			return null;
		}

		public String getInformationDisplayString() {
			return replacement;
		}
		
		@Override
		public boolean equals(Object obj) {
			return (obj.getClass().equals(this.getClass())) && 
			((MessageContextInformation)obj).getInformationDisplayString().equals(getInformationDisplayString());
		}
		
	}

	public MessageCompletionProposal(int offset, int length, String replacement) {
		this.offset = offset;
		this.length = length;
		this.replacement = replacement;
	}
	
	public int getRelevance() {
		return 0;
	}

	public void apply(IDocument document) {
		try {
			document.replace(offset, length, replacement);
		} catch (BadLocationException e) {
			ParsedJavaWaypointsPlugin.getDefault().log(e);
		}
	}

	public String getAdditionalProposalInfo() {
		return "Replace the message for this waypoint";
	}

	public IContextInformation getContextInformation() {
		return new MessageContextInformation();
	}

	public String getDisplayString() {
		return replacement;
	}

	public Image getImage() {
		return null;
	}

	public Point getSelection(IDocument document) {
		return new Point(offset+replacement.length(), 0);
	}

}
