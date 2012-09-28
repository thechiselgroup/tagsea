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
package ca.uvic.cs.tagsea.tests;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import ca.uvic.cs.tagsea.extraction.RawTagStripper;
import ca.uvic.cs.tagsea.extraction.TagExtractor;

/**
 * @author Mike
 * 
 * @tag test : Extraction testing
 * [
 * 		Author = Mike;
 * 	    Date = now;
 *  ]
 */
public class ExtractionTest extends TestCase 
{
	String myInput = new String("//		@tag hello\n" +
			"//		@tag hello\n" +
			"//		@tag hello\n" +
			"//		@tag hello\n" +
			"//		@tag hello\n" +
			"		/**\n" +
			"		 * Header comment\n" +
			"		 *\n" +
			"		 * @tag hello : world\n" +
			"		 * [\n" +
			"		 * dfhfdhd\n" +
			"		 *\n" +
			"\n" +
			"		 * ]	qw	q\n" +
			"		 * @tag hello : world\n" +
			"		 * [\n" +
			"		 * dfhfdhd\n" +
			"		 *\n" +
			"		 * ]asfasfas\n" +
			"		 *\n" +
			"		 */\n" +
			"		public class Test\n" +
			"		{\n" +
			"		}\n");	

	public void testExtraction() throws Exception 
	{

		IDocument document = new Document();
		document.set(myInput);
		IRegion[] regions = TagExtractor.getTagRegions(document);
		assertEquals(7,regions.length);
		String[] tags = new String[ regions.length ];
		int i = 0;
		for (IRegion region : regions) {
			String rawTag = document.get(region.getOffset(), region.getLength());
			String strippedTag = RawTagStripper.stripRawTag(rawTag);
			tags[i++] = strippedTag;
		}
		assertEquals(tags[0], "@tag hello");
		assertEquals(tags[1], "@tag hello");
		assertEquals(tags[2], "@tag hello");
		assertEquals(tags[3], "@tag hello");
		assertEquals(tags[4], "@tag hello");
		assertEquals(tags[5], "@tag hello : world[dfhfdhd]");
		assertEquals(tags[6], "@tag hello : world[dfhfdhd]");
	}

	public void testCommentExtraction() throws Exception 
	{
		
		IDocument document = new Document();
		document.set(myInput);
		IRegion[] regions = TagExtractor.getCommentRegions(document,0,document.getLength());
		
		assertEquals(6,regions.length);

		String expected = new String("/**\n" +
				"		 * Header comment\n" +
				"		 *\n" +
				"		 * @tag hello : world\n" +
				"		 * [\n" +
				"		 * dfhfdhd\n" +
				"		 *\n" +
				"\n" +
				"		 * ]	qw	q\n" +
				"		 * @tag hello : world\n" +
				"		 * [\n" +
				"		 * dfhfdhd\n" +
				"		 *\n" +
				"		 * ]asfasfas\n" +
				"		 *\n" +
				"		 */\n").trim();
		
		assertEquals("//		@tag hello\n",document.get(regions[0].getOffset(),regions[0].getLength()));
		assertEquals("//		@tag hello\n",document.get(regions[1].getOffset(),regions[1].getLength()));
		assertEquals("//		@tag hello\n",document.get(regions[2].getOffset(),regions[2].getLength()));
		assertEquals("//		@tag hello\n",document.get(regions[3].getOffset(),regions[3].getLength()));
		assertEquals("//		@tag hello\n",document.get(regions[4].getOffset(),regions[4].getLength()));
		assertEquals(expected,document.get(regions[5].getOffset(),regions[5].getLength()).trim());		
	}
	
	public void testJavaDocExtraction() throws Exception 
	{
		IDocument document = new Document();
		String simpleJavaDoc = new String(
				"/**\n" +
				"		 * @tag args" +
				"		 */\n").trim();

		document.set(simpleJavaDoc);
		IRegion[] regions = TagExtractor.getTagRegions(document);
		String rawTag = document.get(regions[0].getOffset(), regions[0].getLength());
		String strippedTag = RawTagStripper.stripRawTag(rawTag);
		assertEquals("@tag args",strippedTag);
	}

	public void testEdgeCase1() throws Exception 
	{
		IDocument document = new Document();
		String simpleJavaDoc = new String(
				"/*@tag args*/").trim();

		document.set(simpleJavaDoc);
		IRegion[] regions = TagExtractor.getTagRegions(document);
		String rawTag = document.get(regions[0].getOffset(), regions[0].getLength());
		String strippedTag = RawTagStripper.stripRawTag(rawTag);
		assertEquals("@tag args",strippedTag);
	}
	
	public void testEdgeCase2() throws Exception 
	{
		IDocument document = new Document();
		String simpleJavaDoc = new String(
				"/**@tag args[SOMETHING DUMB]*/").trim();

		document.set(simpleJavaDoc);
		IRegion[] regions = TagExtractor.getTagRegions(document);
		String rawTag = document.get(regions[0].getOffset(), regions[0].getLength());
		String strippedTag = RawTagStripper.stripRawTag(rawTag);
		assertEquals("@tag args[SOMETHING DUMB]",strippedTag);
	}
	
	public void testEdgeCase3() throws Exception 
	{
		IDocument document = new Document();
		String simpleJavaDoc = new String(
				"/**@tag: \n\n\n\n\n\n\n @tag:*/").trim();

		document.set(simpleJavaDoc);
		IRegion[] regions = TagExtractor.getTagRegions(document);
		String rawTag = document.get(regions[0].getOffset(), regions[0].getLength());
		String strippedTag = RawTagStripper.stripRawTag(rawTag);
		assertEquals("@tag:",strippedTag);
		rawTag = document.get(regions[1].getOffset(), regions[1].getLength());
		strippedTag = RawTagStripper.stripRawTag(rawTag);
		assertEquals("@tag:",strippedTag);
	}
	
	public void testEdgeCase4() throws Exception 
	{
		IDocument document = new Document();
		String simpleJavaDoc = new String(
				"//An idiot would do this @tag:my comment").trim();

		document.set(simpleJavaDoc);
		IRegion[] regions = TagExtractor.getTagRegions(document);
		String rawTag = document.get(regions[0].getOffset(), regions[0].getLength());
		String strippedTag = RawTagStripper.stripRawTag(rawTag);
		assertEquals("@tag:my comment",strippedTag);
	}
	
	public void testEdgeCase5() throws Exception 
	{
		IDocument document = new Document();
		String simpleJavaDoc = new String(
				"/*\n" +
				 "* This also is a very taggable bit of code\n" +
				 "* @tag something : intresting\n"+ 
				 "*\n"+
				 "*/\n");

		document.set(simpleJavaDoc);
		IRegion[] regions = TagExtractor.getTagRegions(document);
		String rawTag = document.get(regions[0].getOffset(), regions[0].getLength());
		String strippedTag = RawTagStripper.stripRawTag(rawTag);
		assertEquals("@tag something : intresting",strippedTag);
	}
	
}
