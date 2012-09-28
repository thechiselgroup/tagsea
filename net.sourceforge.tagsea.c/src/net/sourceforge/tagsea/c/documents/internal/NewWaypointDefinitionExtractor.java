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
package net.sourceforge.tagsea.c.documents.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * 
 * @author Del Meyrs
 *
 */
public class NewWaypointDefinitionExtractor 
{
	protected static final char TAG_START_CHAR = '@';
	protected static final String TAG_STRING = "@tag"; //$NON-NLS-1$
	protected static final String COMMENT_STRING = "//"; //$NON-NLS-1$
	protected static final String MULTI_COMMENT_STRING = "/*"; //$NON-NLS-1$
	
	protected static final String END_COMMENT_STRING = "*/"; //$NON-NLS-1$
	
	/* 
	 * Token Keys
	 */
	protected static final Object TAG_KEY = new Object();
	protected static final Object END_COMMENT_KEY = new Object();
	protected static final Object SINGLE_COMMENT_KEY = new Object();
	protected static final Object MULTI_COMMENT_KEY = new Object();
	
	private static RuleBasedScanner fWaypointScanner;
	private static RuleBasedScanner fCommentScanner;
	
	static
	{
		fWaypointScanner = new RuleBasedScanner();
		fCommentScanner = new RuleBasedScanner();
		EndOfLineRule singleCommentRule = new EndOfLineRule(COMMENT_STRING, new Token(SINGLE_COMMENT_KEY));
		MultiLineRule multiCommentRule = new MultiLineRule(MULTI_COMMENT_STRING, END_COMMENT_STRING, new Token(SINGLE_COMMENT_KEY));
		fCommentScanner.setRules(new IRule[] {singleCommentRule, multiCommentRule});
		EndOfLineRule tagRule = new EndOfLineRule(TAG_STRING, new Token(TAG_KEY));
		fWaypointScanner.setRules(new IRule[]{tagRule});
	}
	
	/**
	 * Gets the tag regions associated with this entire document, if no tag regions 
	 * are found an empty array is returned
	 * @param source reference
	 * @return Array of tag regions
	 */
	public static IRegion[] getWaypointRegions(IDocument document)
	{
		return getWaypointRegions(document, 0, document.getLength());
	}
	
	/**
	 * Gets the tag regions associated with this document, restricted to the given source referece, if no tag regions 
	 * are found an empty array is returned
	 * @param document
	 * @param source reference
	 * @return Array of tag regions
	 */
	public static IRegion[] getWaypointRegions(IDocument document, IRegion region)
	{
				
		if(region.getOffset() < 0 || region.getOffset()  + region.getLength() > document.getLength())
			return new IRegion[0];
		
		return getWaypointRegions(document, region.getOffset(), region.getLength());
	}
	
	/**
	 * Gets the tag regions associated with this document from the given offset and length.
	 * If no tag regions are found an empty array is returned.
	 * @param source reference
	 * @return Array of tag regions
	 */
	public static IRegion[] getWaypointRegions(IDocument document, int offset, int length) 
	{
		IRegion[] commentRegions = getCommentRegions(document, offset, length);
		List<IRegion> tagRegions = new ArrayList<IRegion>();

		// for each comment block get all the tags
		for (IRegion commentRegion : commentRegions)
		{
			IRegion[] regions = null;
			
			regions = internalGetWaypointRegions(document, commentRegion.getOffset(), commentRegion.getLength());

			for (IRegion tagRegion : regions) 
			{
				//remove the newline.
				String delim = document.getLegalLineDelimiters()[0];
				try {
					int line = document.getLineOfOffset(tagRegion.getOffset());
					delim = document.getLineDelimiter(line);
				} catch (BadLocationException e) {
				}
				tagRegions.add(new Region(tagRegion.getOffset(), tagRegion.getLength()-delim.length()));
			}
		}

		IRegion[] result= new IRegion[tagRegions.size()];
		tagRegions.toArray(result);
		return result;

	}
	
	/**
	 * Gets the comment regions associated with this document, restricted to the given offset and length, if no comment regions 
	 * are found an empty array is returned
	 * @param document
	 * @param offset
	 * @param length
	 * @param returnSingleLine Wither to collect single line commments
	 * @return Array of tag regions
	 */
	public static IRegion[] getCommentRegions(IDocument document, int offset, int length)
	{

		List<IRegion> commentRegions = new ArrayList<IRegion>();
		fCommentScanner.setRange(document, offset, length);

		while (true) 
		{
			IToken token = fCommentScanner.nextToken();
			if (token.isEOF()) {
				break;
			} else if (token.getData() ==SINGLE_COMMENT_KEY || token.getData() == MULTI_COMMENT_KEY) {
				commentRegions.add(new Region(fCommentScanner.getTokenOffset(), fCommentScanner.getTokenLength()));
			}

		}

		IRegion[] result= new IRegion[commentRegions.size()];
		commentRegions.toArray(result);
		return result;
	

	}
	
	/**
	 * Gets the tag regions associated with this document, restricted to the given offset and length, if no tag regions 
	 * are found an empty array is returned, if a tag region overlaps the end of the region it will be ignored
	 * @param document
	 * @param offset
	 * @param length
	 * @return Array of tag regions
	 */
//	private static IRegion[] internalGetWaypointRegions(IDocument document, int offset, int length)
//	{
//		List<IRegion> tagRegions = new ArrayList<IRegion>();
//		fWaypointScanner.setRange(document,offset,length);
//		
//		boolean tagDetected = false;
//		boolean eolDetected = false;
//		boolean metaOpenDetected = false;
//		
//		int tagOffset = 0;
//		int eolOffset = 0;
//		
//		while (true) 
//		{
//			IToken token = fWaypointScanner.nextToken();
//			
//			if(token.getData() == TAG_KEY) 
//			{
//				/* We have already detected a tag start and eol so we have a single line tag */
//				if(tagDetected && eolDetected)
//				{	
//					tagRegions.add(new Region(tagOffset, eolOffset - tagOffset));
//					/* reset eol detection */
//					eolDetected = false;
//				}
//			
//				tagDetected = true;
//				tagOffset = fWaypointScanner.getTokenOffset();
//			}
//			else if(token.getData() == EOL_KEY) 
//			{
//				/* we only store the first eol after the tag start */
//				if(tagDetected && !eolDetected)
//				{
//					eolDetected = true;
//					eolOffset = fWaypointScanner.getTokenOffset();
//				}
//			}
//			else if(token.getData() == TAG_META_OPEN_KEY) 
//			{
//				metaOpenDetected = true;
//			}
//			else if(token.getData() == TAG_META_CLOSE_KEY) 
//			{
//				/* 
//				 * We reached a meta end and we have already found a tag and meta open
//				 * so we have a multi line tag
//				 */
//				if(tagDetected && metaOpenDetected)
//				{
//					tagRegions.add(new Region(tagOffset, (fWaypointScanner.getTokenOffset() + 1) - tagOffset));
//				}
//				
//				tagDetected = false;
//				eolDetected = false;
//				metaOpenDetected = false;
//			}
//			else if(token.getData() == END_COMMENT_KEY)
//			{
//				/* We have already detected a tag start and eol so we have a tag in there somewhere */
//				if(tagDetected && eolDetected)
//					tagRegions.add(new Region(tagOffset, (eolOffset) - tagOffset));
//				/* We have already detected a tag start but no eol so we have a single line tag */
//				else if(tagDetected)
//						tagRegions.add(new Region(tagOffset, (fWaypointScanner.getTokenOffset()) - tagOffset));
//
//				break;
//			}
//			else if(token.equals(Token.EOF))
//			{
//				/* We have already detected a tag start and eol so we have a single line tag */
//				if(tagDetected && eolDetected)
//						tagRegions.add(new Region(tagOffset, (eolOffset) - tagOffset));
//				else
//					/* Real edge case here, a rogue tag, assume its a single line without line delimeter*/
//					if(tagDetected)
//						tagRegions.add(new Region(tagOffset, (fWaypointScanner.getTokenOffset()) - tagOffset));
//
//				break;
//			}
//		}
//	
//		IRegion[] result= new IRegion[tagRegions.size()];
//		tagRegions.toArray(result);
//		return result;
//	}
	// Wwe only support single line tags, the method above will pull multi line tags
	public static IRegion[] internalGetWaypointRegions(IDocument document, int offset, int length)
	{
		List<IRegion> tagRegions = new ArrayList<IRegion>();
		fWaypointScanner.setRange(document,offset,length);
		
	
		while (true) 
		{
			IToken token = fWaypointScanner.nextToken();
			
			if (token.getData() == TAG_KEY) {
				Region r = new Region(fWaypointScanner.getTokenOffset(), fWaypointScanner.getTokenLength());
				tagRegions.add(r);
			} else if (token.equals(Token.EOF)) {
				break;
			}
		}		
	
		IRegion[] result= new IRegion[tagRegions.size()];
		tagRegions.toArray(result);
		return result;
		
	}
}
