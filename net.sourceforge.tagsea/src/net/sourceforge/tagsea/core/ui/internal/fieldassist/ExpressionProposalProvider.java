/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Research
 *******************************************************************************/
package net.sourceforge.tagsea.core.ui.internal.fieldassist;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tagsea.core.ui.internal.expression.ExpressionMatcherRegistry;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;


/**
 * simple proposal provider to give suggestions for expression queries
 * 
 * 
 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */
public class ExpressionProposalProvider implements IContentProposalProvider {

	class ExpressionProposal implements IContentProposal
	{
		private String type;
		private int offset;

		public ExpressionProposal(String type, int offset) {
			this.type = type;
			this.offset = offset;
		}
		
		public String getContent() {
			return type.substring(offset);
		}

		public int getCursorPosition() {
			return type.length();
		}

		public String getDescription() {
			return "operators: ! (except for), < (less than), > (greater than), = (equals)";
		}

		public String getLabel() {
			return type;
		}
	};
	
	public ExpressionProposalProvider() {
	}
	
	public IContentProposal[] getProposals(String contents, int position) {

		List<IContentProposal>   proposals = new ArrayList<IContentProposal>();
		
		// walk back to first part of the word in the contents
		int i = position-1;
		while ( i>0 && !Character.isSpaceChar(contents.charAt(i)) )
			i--;

		// retrieve available expression types
		String[] types = ExpressionMatcherRegistry.getTypes();

		int offset = position-i-1;
		if ( i==0 )
			offset++;
		
		if ( position==0 || offset<1 )
		{
			// if we have no text to work with, suggest everything
			for (String type: types)
			{
				proposals.add(new ExpressionProposal(type,0));
			}
		}
		else
		{
			String word        = contents.substring(i,position).trim().toLowerCase();
			for (String type: types)
			{
				if ( type.startsWith(word) )
					proposals.add(new ExpressionProposal(type,word.length()));
			}
		}
		
		if ( proposals.size()<1 )
			return new IContentProposal[0];
		
		return proposals.toArray(new IContentProposal[proposals.size()]);
	}

}
