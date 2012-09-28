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
package ca.uvic.cs.tagsea.extraction;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.IRegion;

/**
 * 
 * @author Mike
 *
 */
public class RawTagStripper 
{
	private static String COMMENT_STAR = "*";
	private static String COMMENT_STAR_REPLACMENT = "";
	
	/**
	 * This method strips a raw tag as it is represented in a document to a clean version
	 * without the leading stars caused by the tags inclusion in a comment block
	 * @param string The tag to be stripped
	 * @return the cleaned tag or return <code>null</code> if the tag is null of cannot be stripped
	 */
	public static String stripRawTag(String rawTag)
	{
		if(rawTag == null)
			return null;
		
		String[] lines = convertIntoLines(rawTag);
		
		if(lines == null)
			return null;
		
		StringBuffer result = new StringBuffer();
		
		/*
		 * Currently we strip all occurences of COMMENT_STAR from the string
		 * @tag todo : more intelligent scanning so we only remove the first COMMENT_STAR and leave others alone
		 * this is why we split the tag into lines, for easier processing
		 */
		for(String line : lines)
			result.append(line.replace(COMMENT_STAR,COMMENT_STAR_REPLACMENT).trim());

		return result.toString();
	}
	
	/**
	 * Converts the given string into an array of lines. The lines 
	 * don't contain any line delimiter characters.
	 *
	 * @return the string converted into an array of strings. Returns <code>
	 * 	null</code> if the input string can't be converted in an array of lines.
	 */
	private static String[] convertIntoLines(String input) 
	{
		try 
		{
			ILineTracker tracker= new DefaultLineTracker();
			tracker.set(input);
			int size= tracker.getNumberOfLines();
			String result[]= new String[size];
			
			for (int i= 0; i < size; i++) 
			{
				IRegion region= tracker.getLineInformation(i);
				int offset= region.getOffset();
				result[i]= input.substring(offset, offset + region.getLength());
			}
			return result;
			
		} 
		catch (BadLocationException e) 
		{
			return null;
		}
	}
}
