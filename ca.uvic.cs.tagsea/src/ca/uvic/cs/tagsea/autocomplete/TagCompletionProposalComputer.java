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
package ca.uvic.cs.tagsea.autocomplete;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import ca.uvic.cs.tagsea.ITagseaImages;
import ca.uvic.cs.tagsea.TagSEAPlugin;
import ca.uvic.cs.tagsea.core.Tag;
import ca.uvic.cs.tagsea.parser.ParseNode;
import ca.uvic.cs.tagsea.parser.ParseTree;
import ca.uvic.cs.tagsea.parser.TagParser;

/**
 * @author mdesmond
 * 
 */
public class TagCompletionProposalComputer implements IJavaCompletionProposalComputer 
{
	/* get the auto complete image */
	private static Image fAutoCompleteImage = null;
	
	public TagCompletionProposalComputer() {
	}
	
	private Image getAutoCompleteImage() {
		if (fAutoCompleteImage == null) {
			fAutoCompleteImage = TagSEAPlugin.getDefault().getTagseaImages().getImage(ITagseaImages.IMG_WAYPOINT);
		}
		return fAutoCompleteImage;
	}
	
	/**
	 * Gets the username from the system properties.  Defaults to "unknown".
	 * @return String the username
	 */
	@SuppressWarnings("restriction")
	private String getSystemUsername() {
		return System.getProperty("user.name", "unknown");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer#computeCompletionProposals(org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) 
	{	
		try 
		{
			int invocationOffset = context.getInvocationOffset();
			IDocument document = context.getDocument();
			
			// The line region
			final IRegion region= document.getLineInformationOfOffset(invocationOffset);
			// The line content
			final String content= document.get(region.getOffset(), region.getLength());
			int index = invocationOffset - region.getOffset() - 1;
			
			// read back through the line until we hit the @ or the start of the line
			// we need the context of the invocation
			char c = 0;
			
			while(true)
			{
				c = content.charAt(index);
	
				if(index == 0 || c=='@')
					break;
				else
					index--;
			}

			// Compute the tag, keep the @
			String workingTag = content.substring(index , invocationOffset - region.getOffset());
		
			// the offset where the tag starts
			int tagInsertionOffset = region.getOffset() + index;

			/* if its empty or doesnt start with @ then ignore */
			if (workingTag.length() > 0 && workingTag.startsWith("@")) 
			{
				// handle the case where the user wants to auto complete the keyword 
				// becase the keyword is short we can use simple combined if statment
				if(workingTag.equals("@") || 
				   workingTag.equals("@t") || 
				   workingTag.equals("@ta"))
				{
					Tag tags[] = TagSEAPlugin.getDefault().getTagCollection().getAllTags();
					ArrayList<TagCompletionProposal> proposals = new ArrayList<TagCompletionProposal>();

					proposals.add(createEmptyTagCompletionProposal(tagInsertionOffset, workingTag.length()));

					// add all available tags 
					for (Tag t : tags)
						proposals.add(createTagCompletionProposal(t.getName(), tagInsertionOffset, workingTag.length()));

					return proposals;
				}

				// we are now working with an existing tag definition somewhere after the @tag keyword 
				if(workingTag.trim().startsWith("@tag"))
				{
					// we will be scanning backward towards the (@)tag keyword to figure out what
					// the context of the invocation is
					// this will get complicated
					
					// working char
					char ch = 0;	
					//start offset
					int start = "@tag".length();
					//end offset
					int end = workingTag.length();
					
					// index ranges from 0 to length -1
					int i = end - 1;
					
					while(true)
					{
						if(i <= start)
							break;
						
						//populate the working char
						ch = workingTag.charAt(i);
						
						//we are intrested in certain cases
						
						/* the last special character is a colon */
						if(ch==':')
							break;
						/* the last special character is an open sq bracket */
						else if(ch == '[')
							break;
						/* the last special character is a semi colon */
						else if(ch == ';')
							break;
						/* the last special character is an equals */
						else if(ch == '=')
							break;
						
							i--;
					}

					int proposalLength = ((end - 1) - i);
					int proposalOffset = invocationOffset - proposalLength;
					// the proposal is the string following the special character to the invocation offset 
					String proposalString =  document.get(proposalOffset,proposalLength);
					
					// the last special char was an open sq bracket
					if(ch == '[')
					{	
						ArrayList<TagCompletionProposal> proposals = new ArrayList<TagCompletionProposal>();

						if(proposalString.trim().length() == 0)
						{
							proposals.add(createUnBracketedMetaDataCompletionProposal(invocationOffset, 0));
							proposals.add(createAuthorMetaDataCompletionProposal(invocationOffset, 0));
							proposals.add(createDateMetaDataCompletionProposal(invocationOffset, 0));
						}

						// @tag todo : more cases to handle here
						
						return proposals;
					}
					// the last special char was a regular colon, we are after the comment section
					else if(ch == ':')
					{	
						ArrayList<TagCompletionProposal> proposals = new ArrayList<TagCompletionProposal>();

						// no comment exists
						if(proposalString.trim().length() == 0)
							proposals.add(createCommentedBracketedMetaDataCompletionProposal(invocationOffset, 0));
							
						// create meta data after the comment 
						proposals.add(createBracketedMetaDataCompletionProposal(invocationOffset, 0));

						return proposals;
					}
					// the last special char was a regular colon, we are after the comment section
					else if(ch == ';')
					{	
						ArrayList<TagCompletionProposal> proposals = new ArrayList<TagCompletionProposal>();

						if(proposalString.trim().length() == 0)
						{
							proposals.add(createAuthorMetaDataCompletionProposal(invocationOffset, 0));
							proposals.add(createDateMetaDataCompletionProposal(invocationOffset, 0));
						}

						return proposals;
					}
					else // need a check here 
					{
						// Giggidy! here we go again, reset variables
						// working char
						ch = 0;	
						//start offset
						start = "@tag".length();
						//end offset
						end = workingTag.length();
						
						// index ranges from 0 to length -1
						i = end - 1;
						
						while(true)
						{
							if(i <= start)
								break;
							
							//populate the working char
							ch = workingTag.charAt(i);
							
							//this time we are only intrested in whitespace and open bracket, all other cases
							//have been handles
							
							/* the last special character is an open bracket */
							if(Character.isWhitespace(ch))
								break;
							else if(ch =='(')
								break;
							else if(ch == '.')
								break;
								
								i--;
						}
						
						proposalLength = ((end - 1) - i);
						proposalOffset = invocationOffset - proposalLength;
						proposalString =  document.get(proposalOffset,proposalLength);

						ArrayList<TagCompletionProposal> proposals = new ArrayList<TagCompletionProposal>();
						Tag tags[] = TagSEAPlugin.getDefault().getTagCollection().getAllTags();
						TagParser parser = new TagParser();
						ParseTree tree = parser.parse(workingTag.substring(0, i));
						Tag parent = null;
						//find the parent of the current tag being added for the waypoint.
						if (tree.getRoot().getChildren().length > 0) {
							ParseNode node = tree.getRoot().getChildren()[0];
							Tag[] roots = TagSEAPlugin.getDefault().getTagCollection().getRootTags();
							int quit = roots.length;
							int j = 0;
							while (true) {
								if (j >= quit) break;
								Tag ct = roots[j];
								if (ct.getName().equals(node.getName())) {
									parent = ct;
									roots = parent.getChildren();
									j=-1;
									quit = roots.length;
									if (node.getChildren().length <= 0) break;
									node = node.getChildren()[0];
								}
								j++;
							}
						}
						
						for (Tag t : tags) 
						{
							if (t.getName().startsWith(proposalString))
							{
								proposals.add(createTagNameCompletionProposal(t.getName(), proposalOffset, proposalLength));
							}
						}
						//if a parent is found, sort the proposals based on the parent.
						if (parent != null) {
							final Tag fparent = parent;
							Collections.sort(proposals, new Comparator<TagCompletionProposal>(){
								public int compare(TagCompletionProposal o1, TagCompletionProposal o2) {
									String name1 = o1.getReplacementString();
									String name2 = o2.getReplacementString();
									Tag[] children = fparent.getChildren();
									int result = 0;
									int foundNames = 2;
									for (Tag c : children) {
										if (c.getName().equals(name1)) {
											result -= 1;
											foundNames--;
										}
										if (c.getName().equals(name2)) {
											result += 1;
											foundNames--;
										}
										if (foundNames == 0) break;
									}
									if (result == 0) result = name1.compareTo(name2);
									return result;
								}
							});
							int relavence = proposals.size();
							for (TagCompletionProposal ct : proposals) {
								ct.setRelevance(relavence--);
							}
						}
						return proposals;
					}
				}
			}
		} 
		catch (BadLocationException exception) 
		{
			// log & ignore
			return Collections.EMPTY_LIST;
		}
		
		return Collections.EMPTY_LIST;
	}

	/**
	 * Empty Tag name completion proposal with meta data
	 * @param replacmentOffset
	 * @param replacmentLength
	 * @return
	 */
	private TagCompletionProposal createEmptyTagCompletionProposal(int replacmentOffset, int replacmentLength)
	{
		SimpleDateFormat format = new SimpleDateFormat();
		String date = format.format(new Date());
		String replacmentString = "@tag  :  [Author = "+ getSystemUsername()+ ";" + "Date = "+ date +";]";
		int caretOffset = "@tag ".length();

		TagCompletionProposal proposal = new TagCompletionProposal(replacmentString,
				replacmentOffset,
				replacmentLength,
				caretOffset, 
				getAutoCompleteImage(),
				"@tag",
				(IContextInformation)null,
				"Create a new tagSEA tag");
		
		return proposal;
	}
	
	/**
	 * Tag name completion proposal with meta data
	 * @param replacmentOffset
	 * @param replacmentLength
	 * @return
	 */
	private TagCompletionProposal createTagCompletionProposal(String tagName, int replacmentOffset, int replacmentLength)
	{
		SimpleDateFormat format = new SimpleDateFormat();
		String date = format.format(new Date());
		String tagStart = "@tag " + tagName + " : ";
		String tagEnd = " [Author = "+ getSystemUsername()+ ";" + "Date = "+ date +";]";
		String replacmentString = tagStart + tagEnd;
		int caretOffset = tagStart.length();
		
		TagCompletionProposal proposal = new TagCompletionProposal(replacmentString,
				replacmentOffset,
				replacmentLength,
				caretOffset, 
				getAutoCompleteImage(),
				"@tag " +  tagName,
				(IContextInformation)null,
				"Create a new tagSEA tag");
		
		return proposal;
	}
	
	/**
	 * Simple commented meta data proposal
	 * @param replacmentOffset
	 * @param replacmentLength
	 * @return
	 */
	private TagCompletionProposal createCommentedBracketedMetaDataCompletionProposal(int replacmentOffset, int replacmentLength)
	{
		SimpleDateFormat format = new SimpleDateFormat();
		String date = format.format(new Date());
		String replacmentString = "comment [Author = "+ getSystemUsername()+ ";" + "Date = "+ date +";]";
		int caretOffset = "comment".length();
		
		TagCompletionProposal proposal = new TagCompletionProposal(replacmentString,
				replacmentOffset,
				replacmentLength,
				caretOffset, 
				getAutoCompleteImage(),
				"@tag meta data (default comment, Author and date)",
				(IContextInformation)null,
				"Generate and insert commented tag meta data (default comment, Author and date)");
		
		return proposal;
	}
	
	/**
	 * Simple un commented meta data completion proposal
	 * @param replacmentOffset
	 * @param replacmentLength
	 * @return
	 */
	private TagCompletionProposal createBracketedMetaDataCompletionProposal(int replacmentOffset, int replacmentLength)
	{
		SimpleDateFormat format = new SimpleDateFormat();
		String date = format.format(new Date());
		String replacmentString = "[Author = "+ getSystemUsername()+ ";" + "Date = "+ date +";]";
		int caretOffset = 0;
		
		TagCompletionProposal proposal = new TagCompletionProposal(replacmentString,
				replacmentOffset,
				replacmentLength,
				caretOffset, 
				getAutoCompleteImage(),
				"@tag meta data (Author and date)",
				(IContextInformation)null,
				"Generate and insert tag meta data (Author and date)");
		
		return proposal;
	}
	
	/**
	 * Simple un commented meta data completion proposal
	 * @param replacmentOffset
	 * @param replacmentLength
	 * @return
	 */
	private TagCompletionProposal createUnBracketedMetaDataCompletionProposal(int replacmentOffset, int replacmentLength)
	{
		SimpleDateFormat format = new SimpleDateFormat();
		String date = format.format(new Date());
		String replacmentString = "Author = "+ getSystemUsername()+ ";" + "Date = "+ date +";]";
		int caretOffset = replacmentString.length();
		
		TagCompletionProposal proposal = new TagCompletionProposal(replacmentString,
				replacmentOffset,
				replacmentLength,
				caretOffset, 
				getAutoCompleteImage(),
				"@tag meta data (Author and date)",
				(IContextInformation)null,
				"Generate and insert tag meta data (Author and date)");
		
		return proposal;
	}
	
	/**
	 * Simple un commented meta data completion proposal
	 * @param replacmentOffset
	 * @param replacmentLength
	 * @return
	 */
	private TagCompletionProposal createAuthorMetaDataCompletionProposal(int replacmentOffset, int replacmentLength)
	{
		String replacmentString = "Author = "+ getSystemUsername() +";";
		int caretOffset = replacmentString.length();
		
		TagCompletionProposal proposal = new TagCompletionProposal(replacmentString,
				replacmentOffset,
				replacmentLength,
				caretOffset, 
				getAutoCompleteImage(),
				"@tag Author field",
				(IContextInformation)null,
				"Generate and insert Author field");
		
		return proposal;
	}
	
	/**
	 * Simple un commented meta data completion proposal
	 * @param replacmentOffset
	 * @param replacmentLength
	 * @return
	 */
	private TagCompletionProposal createDateMetaDataCompletionProposal(int replacmentOffset, int replacmentLength)
	{
		SimpleDateFormat format = new SimpleDateFormat();
		String date = format.format(new Date());
		String replacmentString = "Date = "+ date +";]";
		int caretOffset = replacmentString.length();
		
		TagCompletionProposal proposal = new TagCompletionProposal(replacmentString,
				replacmentOffset,
				replacmentLength,
				caretOffset, 
				getAutoCompleteImage(),
				"@tag Date field",
				(IContextInformation)null,
				"Generate and insert Date field");
		
		return proposal;
	}
	
	/**
	 * Simple tag name completion proposal
	 * @param replacmentOffset
	 * @param replacmentLength
	 * @return
	 */
	private TagCompletionProposal createTagNameCompletionProposal(String tagName,int replacmentOffset, int replacmentLength)
	{
		String replacmentString = tagName;;
		int caretOffset = tagName.length();
		
		TagCompletionProposal proposal = new TagCompletionProposal(replacmentString,
				replacmentOffset,
				replacmentLength,
				caretOffset, 
				getAutoCompleteImage(),
				tagName,
				(IContextInformation)null,
				"Insert a tagSEA tag");
		
		return proposal;
	}
	
	public List computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {return null;}
	public String getErrorMessage() {return null;}
	public void sessionEnded(){}
	public void sessionStarted(){}
}