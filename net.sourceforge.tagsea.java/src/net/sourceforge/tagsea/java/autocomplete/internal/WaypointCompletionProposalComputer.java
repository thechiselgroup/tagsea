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
package net.sourceforge.tagsea.java.autocomplete.internal;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import net.sourceforge.tagsea.TagSEAPlugin;
import net.sourceforge.tagsea.core.ITag;
import net.sourceforge.tagsea.core.ui.ITagSEAImageConstants;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;


/**
 * @author mdesmond
 * 
 */
//@tag tagsea.bug.11.fix -author="Del Myers" -date="enCA:17/01/07" : updated images for particular context.
public class WaypointCompletionProposalComputer implements IJavaCompletionProposalComputer 
{
	/* image for tags */
	private static Image fTagImage = TagSEAPlugin.getDefault().getImageRegistry().get(ITagSEAImageConstants.IMG_TAG);;
	
	private static Image fWaypointImage = TagSEAPlugin.getDefault().getImageRegistry().get(ITagSEAImageConstants.IMG_WAYPOINT);
	
	public WaypointCompletionProposalComputer() {
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
	public List<?> computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) 
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
					ITag tags[] = TagSEAPlugin.getTagsModel().getAllTags();
					ArrayList<WaypointCompletionProposal> proposals = new ArrayList<WaypointCompletionProposal>();

					proposals.add(createEmptyTagCompletionProposal(tagInsertionOffset, workingTag.length()));

					// add all available tags 
					for (ITag t : tags)
						proposals.add(createTagCompletionProposal(t.getName(), tagInsertionOffset, workingTag.length()));

					return proposals;
				}

				// we are now working with an existing tag definition somewhere after the tag keyword 
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
						
						if (ch=='"') {
							//ignore anything that is in quotes, and
							//get the context before for special characters
							int j = i-1;
							while (j > start) {
								char c2 = workingTag.charAt(j);
								if (c2 == '"') {
									//stop here.
									i = j;
									break;
								}
								j--;
							}
						} else {
							/* the last special character is a colon */
							if(ch==':')
								break;
						}
							i--;
					}

					int proposalLength = ((end - 1) - i);
					int proposalOffset = invocationOffset - proposalLength;
					// the proposal is the string following the special character to the invocation offset 
					String proposalString =  document.get(proposalOffset,proposalLength);
					
					// the last special char was a regular colon, we are after the comment section
					if(ch == ':')
					{	
						//the proposals are empty because we can't know what the comment should be.
						ArrayList<WaypointCompletionProposal> proposals = new ArrayList<WaypointCompletionProposal>();
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
							
							if (ch=='"') {
								//ignore anything that is in quotes, and
								//get the context before for special characters
								int j = i-1;
								while (j > start) {
									char c2 = workingTag.charAt(j);
									if (c2 == '"') {
										//stop here.
										i = j;
										break;
									}
								}
							} else {
								if(Character.isWhitespace(ch))
									break;
							}
								i--;
						}
						
						proposalLength = ((end - 1) - i);
						proposalOffset = invocationOffset - proposalLength;
						proposalString =  document.get(proposalOffset,proposalLength);
						
						if (end == start) return Collections.EMPTY_LIST;
						
						ArrayList<IJavaCompletionProposal> proposals = new ArrayList<IJavaCompletionProposal>();
						if (proposalString.startsWith("-")) {
							//this is metadata, propose metadata
							proposals.addAll(createMetaDataCompletionProposals(proposalString, proposalOffset, proposalLength));
						} else {
							//@tag tagsea.bug.7.fix -author="Del Myers" -date="enCA:17/01/07" : check for empty string for the proposal.
							if ("".equals(proposalString)) {
								//add the author and date metadata proposals.
								proposals.addAll(createMetaDataCompletionProposals(proposalString, proposalOffset, proposalLength));
							}
							String tagName = proposalString;
							int paren = tagName.indexOf('(');
							if (paren != -1) {
								//change the syntax to standard
								int tagIndex = tagName.length()-1;
								while (tagIndex >= 0) {
									char curr = tagName.charAt(tagIndex);
									if (curr != ')') break;
									else tagIndex--;
								}
								tagName = tagName.substring(0, tagIndex+1);
								tagName = tagName.replace('(', '.');
							}
							tagName = tagName.toLowerCase();
							ITag tags[] = TagSEAPlugin.getTagsModel().getAllTags();
							//@tag tagsea.bug.3.fix -author="Del Myers" -date="enCA:18/01/07" : use the new completion proposals for tags.
							for (ITag t : tags) 
							{
								if (t.getName().toLowerCase().startsWith(tagName))
								{
									if (t.getName().toLowerCase().equals(tagName)) {
										//create a volitile proposal that will update itself
										//with the tag name.
										proposals.add(createVolatileCompletionProposal(t, proposalOffset, proposalLength, paren != -1));
									} else {
										proposals.add(createTagNameCompletionProposal(t, proposalOffset, proposalLength, paren != -1));
									}
								}
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
	 * Creates 
	 * @param proposalString
	 * @return
	 */
	private List<WaypointCompletionProposal> createMetaDataCompletionProposals(String proposalString, int replacementOffset, int replacementLength) {
		//get the key for the metadata
		int i = 1;
		String key = "";
		ArrayList<WaypointCompletionProposal> proposals =
			new ArrayList<WaypointCompletionProposal>();
		while (i < proposalString.length()) {
			char c = proposalString.charAt(i);
			if (c == '=') break;
			key += c;
			i++;
		}
		if ("".equals(key)) {
			//add all proposals.
			proposals.add(createAuthorMetaDataCompletionProposal(replacementOffset, replacementLength));
			proposals.add(createDateMetaDataCompletionProposal(replacementOffset, replacementLength));
		} else if ("date".startsWith(key)) {
			//add the date proposal
			proposals.add(createDateMetaDataCompletionProposal(replacementOffset, replacementLength));
		} else if ("author".startsWith(key)) {
			//add the author proposal
			proposals.add(createAuthorMetaDataCompletionProposal(replacementOffset, replacementLength));
		} 
		return proposals;
	}

	/**
	 * Empty Tag name completion proposal with meta data
	 * @param replacmentOffset
	 * @param replacmentLength
	 * @return
	 */
	private WaypointCompletionProposal createEmptyTagCompletionProposal(int replacmentOffset, int replacmentLength)
	{
		String tagStart = "@tag ";
		String tagEnd = "-author="+ getUserString() + " -date=\""+getDateString()+"\"";
		String replacmentString = tagStart + tagEnd;
		int caretOffset = tagStart.length();

		WaypointCompletionProposal proposal = new WaypointCompletionProposal(replacmentString,
				replacmentOffset,
				replacmentLength,
				caretOffset, 
				fTagImage,
				"@tag",
				(IContextInformation)null,
				"Create a new java tag");
		
		return proposal;
	}
	
	/**
	 * Tag name completion proposal with meta data
	 * @param replacmentOffset
	 * @param replacmentLength
	 * @return
	 */
	private WaypointCompletionProposal createTagCompletionProposal(String tagName, int replacmentOffset, int replacmentLength)
	{
		String tagStart = "@tag " + tagName + " ";
		String tagEnd = "-author="+ getUserString() + " -date=\""+getDateString()+"\"";
		String replacmentString = tagStart + tagEnd;
		int caretOffset = tagStart.length();
		
		WaypointCompletionProposal proposal = new WaypointCompletionProposal(replacmentString,
				replacmentOffset,
				replacmentLength,
				caretOffset, 
				fTagImage,
				"@tag " +  tagName,
				(IContextInformation)null,
				"Create a new java tag");
		
		return proposal;
	}
	


	/**
	 * Simple un commented meta data completion proposal
	 * @param replacementOffset
	 * @param replacementLength
	 * @return
	 */
	private WaypointCompletionProposal createAuthorMetaDataCompletionProposal(int replacementOffset, int replacementLength)
	{
		String username = getUserString();
		
		String replacmentString = "-author="+username;
		int caretOffset = replacmentString.length();
		
		WaypointCompletionProposal proposal = new WaypointCompletionProposal(replacmentString,
				replacementOffset,
				replacementLength,
				caretOffset, 
				fWaypointImage,
				"@tag Author field",
				(IContextInformation)null,
				"Generate and insert Author field");
		
		return proposal;
	}
	
	/**
	 * Returns a formatted string for the default user name.
	 * @return
	 */
	private String getUserString() {
		String username = getSystemUsername();
		int colonOrWhitespace = -1;
		int quote = username.indexOf('"');
		if (quote != -1) {
			//the username can't have quotes.
			return null;
		}
		//search for a colon or a whitespace to see if we should add quotes.
		for (int i = 0; i < username.length(); i++) {
			char c = username.charAt(i);
			if (Character.isWhitespace(c)) {
				colonOrWhitespace = i;
				break;
			} else if (c == ':') {
				colonOrWhitespace = i;
				break;
			}
		}
		if (colonOrWhitespace != -1) {
			username = '"' + username + '"';
		}
		return username;
	}

	/**
	 * Simple un commented meta data completion proposal
	 * @param replacementOffset
	 * @param replacementLength
	 * @return
	 */
	private WaypointCompletionProposal createDateMetaDataCompletionProposal(int replacementOffset, int replacementLength)
	{
		String dateString = getDateString();
		String replacmentString = "-date=\""+dateString+"\"";
		int caretOffset = replacmentString.length();
		
		WaypointCompletionProposal proposal = new WaypointCompletionProposal(
				replacmentString,
				replacementOffset,
				replacementLength,
				caretOffset, 
				fWaypointImage,
				"@tag Date field",
				(IContextInformation)null,
				"Generate and insert Date field");
		
		return proposal;
	}
	
	/**
	 * @return a formatted string for the current date.
	 */
	private String getDateString() {
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
		String date = format.format(new Date());
		Locale locale = Locale.getDefault();
		String localeString = locale.getLanguage().toLowerCase() + locale.getCountry().toUpperCase();
		return localeString+":"+date;
	}

	/**
	 * Simple tag name completion proposal
	 * @param replacmentOffset
	 * @param replacmentLength
	 * @return
	 */
	private TagCompletionProposal createTagNameCompletionProposal(ITag tag,int replacmentOffset, int replacmentLength, boolean useParens)
	{
		
		TagCompletionProposal proposal = new TagCompletionProposal(
				tag,
				replacmentOffset,
				replacmentLength,
				fTagImage,
				(IContextInformation)null,
				"Insert a java tag",
				useParens);
		
		return proposal;
	}
	
	private VolatileTagCompletionProposal createVolatileCompletionProposal(ITag tag,int replacmentOffset, int replacmentLength, boolean useParens)
	{
		
				
		VolatileTagCompletionProposal proposal = new VolatileTagCompletionProposal(
				tag,
				replacmentOffset,
				replacmentLength,
				fTagImage,
				(IContextInformation)null,
				"Insert a java tag",
				useParens);
		
		return proposal;
	}
	
	public List<?> computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		List<?> proposals = computeCompletionProposals(context, monitor);
		if (proposals == null)
			return null;
		ArrayList<IContextInformation> info = new ArrayList<IContextInformation>(proposals.size());
		for (Iterator<?> i = proposals.iterator(); i.hasNext();) {
			IJavaCompletionProposal proposal = (IJavaCompletionProposal) i.next();
			if (proposal.getContextInformation() != null) {
				info.add(proposal.getContextInformation());
			}
		}
		return info;
			
	}
	public String getErrorMessage() {return null;}
	public void sessionEnded(){}
	public void sessionStarted(){}
}