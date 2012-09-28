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

package com.ibm.research.cue.tourist.internal.win32.ppt.api;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;

/**
 * generated from Microsoft Powerpoint Object Library
 * 
    [
      odl,
      uuid(9149349D-5A91-11CF-8700-00AA0060263B),
      helpcontext(0x0008e558),
      hidden,
      dual,
      nonextensible,
      oleautomation
    ]
 * 
 * @author Li-Te Cheng
 * 
 */
public class _Presentation 
{

	private OleAutomation auto;
	
	public _Presentation (OleAutomation automation)
	{
	  auto = automation;
	}
	
	public OleAutomation getAutomation()
	{
	  return auto;
	}
	
	public void dispose()
	{
	  if ( auto!=null ) auto.dispose();
	}
	
	/*
	 * [id(0x000007d1), propget, helpcontext(0x0008e559)] HRESULT
	 * Application([out, retval] Application** Application)
	 */
	public Variant getApplication()
	{
	  return auto.getProperty(0x000007d1);
	}
	
	/*
	 * [id(0x000007d2), propget, helpcontext(0x0008e55a)] HRESULT Parent([out,
	 * retval] IDispatch** Parent)
	 */
	public Variant getParent()
	{
	  return auto.getProperty(0x000007d2);
	}
	
	/*
	 * [id(0x000007d3), propget, helpcontext(0x0008e55b)] HRESULT
	 * SlideMaster([out, retval] _Master** SlideMaster)
	 */
	public Variant getSlideMaster()
	{
	  return auto.getProperty(0x000007d3);
	}
	
	/*
	 * [id(0x000007d4), propget, helpcontext(0x0008e55c)] HRESULT
	 * TitleMaster([out, retval] _Master** TitleMaster)
	 */
	public Variant getTitleMaster()
	{
	  return auto.getProperty(0x000007d4);
	}
	
	/*
	 * [id(0x000007d5), propget, helpcontext(0x0008e55d)] HRESULT
	 * HasTitleMaster([out, retval] MsoTriState* HasTitleMaster)
	 */
	public Variant getHasTitleMaster()
	{
	  return auto.getProperty(0x000007d5);
	}
	
	/*
	 * [id(0x000007d6), helpcontext(0x0008e55e)] HRESULT AddTitleMaster([out,
	 * retval] _Master** TitleMaster)
	 */
	public Variant AddTitleMaster()
	{
	  int id = 0x000007d6;
	  return auto.invoke(id);
	}
	
	/*
	 * [id(0x000007d7), helpcontext(0x0008e55f)] HRESULT ApplyTemplate([in] BSTR
	 * FileName)
	 */
	public Variant ApplyTemplate(String filename)
	{
	  int id = 0x000007d7;
	  return auto.invoke(id,new Variant[]{new Variant(filename)});
	}
	
	/*
	 * [id(0x000007d8), propget, helpcontext(0x0008e560)] HRESULT
	 * TemplateName([out, retval] BSTR* TemplateName)
	 */
	public Variant getTemplateName()
	{
	  return auto.getProperty(0x000007d8);
	}
	
	/*
	 * [id(0x000007d9), propget, helpcontext(0x0008e561)] HRESULT
	 * NotesMaster([out, retval] _Master** NotesMaster)
	 */
	public Variant getNotesMaster()
	{
	  return auto.getProperty(0x000007d9);
	}
	
	/*
	 * [id(0x000007da), propget, helpcontext(0x0008e562)] HRESULT
	 * HandoutMaster([out, retval] _Master** HandoutMaster)
	 */
	public Variant getHandoutMaster()
	{
	  return auto.getProperty(0x000007da);
	}
	
	/*
	 * [id(0x000007db), propget, helpcontext(0x0008e563)] HRESULT Slides([out,
	 * retval] Slides** Slides)
	 */
	public Variant getSlides()
	{
	  return auto.getProperty(0x000007db);
	}
	
	/*
	 * [id(0x000007dc), propget, helpcontext(0x0008e564)] HRESULT
	 * PageSetup([out, retval] PageSetup** PageSetup)
	 */
	public Variant getPageSetup()
	{
	  return auto.getProperty(0x000007dc);
	}
	
	/*
	 * [id(0x000007dd), propget, helpcontext(0x0008e565)] HRESULT
	 * ColorSchemes([out, retval] ColorSchemes** ColorSchemes)
	 */
	public Variant getColorSchemes()
	{
	  return auto.getProperty(0x000007dd);
	}
	
	/*
	 * [id(0x000007de), propget, helpcontext(0x0008e566)] HRESULT
	 * ExtraColors([out, retval] ExtraColors** ExtraColors)
	 */
	public Variant getExtraColors()
	{
	  return auto.getProperty(0x000007de);
	}
	
	/*
	 * [id(0x000007df), propget, helpcontext(0x0008e567)] HRESULT
	 * SlideShowSettings([out, retval] SlideShowSettings** SlideShowSettings)
	 */
	public Variant getSlideShowSettings()
	{
	  return auto.getProperty(0x000007df);
	}
	
	/*
	 * [id(0x000007e0), propget, helpcontext(0x0008e568)] HRESULT Fonts([out,
	 * retval] Fonts** Fonts)
	 */
	public Variant getFonts()
	{
	  return auto.getProperty(0x000007e0);
	}
	
	/*
	 * [id(0x000007e1), propget, helpcontext(0x0008e569)] HRESULT Windows([out,
	 * retval] DocumentWindows** Windows)
	 */
	public Variant getWindows()
	{
	  return auto.getProperty(0x000007e1);
	}
	
	/*
	 * [id(0x000007e2), propget, helpcontext(0x0008e56a)] HRESULT Tags([out,
	 * retval] Tags** Tags)
	 */
	public Variant getTags()
	{
	  return auto.getProperty(0x000007e2);
	}
	
	/*
	 * [id(0x000007e3), propget, helpcontext(0x0008e56b)] HRESULT
	 * DefaultShape([out, retval] Shape** DefaultShape)
	 */
	public Variant getDefaultShape()
	{
	  return auto.getProperty(0x000007e3);
	}
	
	/*
	 * [id(0x000007e4), propget, helpcontext(0x0008e56c)] HRESULT
	 * BuiltInDocumentProperties([out, retval] IDispatch**
	 * BuiltInDocumentProperties)
	 */
	public Variant getBuiltInDocumentProperties()
	{
	  return auto.getProperty(0x000007e4);
	}
	
	/*
	 * [id(0x000007e5), propget, helpcontext(0x0008e56d)] HRESULT
	 * CustomDocumentProperties([out, retval] IDispatch**
	 * CustomDocumentProperties)
	 */
	public Variant getCustomDocumentProperties()
	{
	  return auto.getProperty(0x000007e5);
	}
	
	/*
	 * [id(0x000007e6), propget, helpcontext(0x0008e56e)] HRESULT
	 * VBProject([out, retval] VBProject** VBProject)
	 */
	public Variant getVBProject()
	{
	  return auto.getProperty(0x000007e6);
	}
	
	/*
	 * [id(0x000007e7), propget, helpcontext(0x0008e56f)] HRESULT ReadOnly([out,
	 * retval] MsoTriState* ReadOnly)
	 */
	public Variant getReadOnly()
	{
	  return auto.getProperty(0x000007e7);
	}
	
	/*
	 * [id(0x000007e8), propget, helpcontext(0x0008e570)] HRESULT FullName([out,
	 * retval] BSTR* FullName)
	 */
	public Variant getFullName()
	{
	  return auto.getProperty(0x000007e8);
	}
	
	/*
	 * [id(0x000007e9), propget, helpcontext(0x0008e571)] HRESULT Name([out,
	 * retval] BSTR* Name)
	 */
	public Variant getName()
	{
	  return auto.getProperty(0x000007e9);
	}
	
	/*
	 * [id(0x000007ea), propget, helpcontext(0x0008e572)] HRESULT Path([out,
	 * retval] BSTR* Path)
	 */
	public Variant getPath()
	{
	  return auto.getProperty(0x000007ea);
	}
	
	/*
	 * [id(0x000007eb), propget, helpcontext(0x0008e573)] HRESULT Saved([out,
	 * retval] MsoTriState* Saved)
	 */
	public Variant getSaved()
	{
	  return auto.getProperty(0x000007eb);
	}
	
	/*
	 * [id(0x000007eb), propput, helpcontext(0x0008e573)] HRESULT Saved([in]
	 * MsoTriState Saved)
	 */
	public boolean setSaved(Variant val)
	{
	  return auto.setProperty(0x000007eb,val);
	}
	
	/*
	 * [id(0x000007ec), propget, helpcontext(0x0008e574)] HRESULT
	 * LayoutDirection([out, retval] PpDirection* LayoutDirection)
	 */
	public Variant getLayoutDirection()
	{
	  return auto.getProperty(0x000007ec);
	}
	
	/*
	 * [id(0x000007ec), propput, helpcontext(0x0008e574)] HRESULT
	 * LayoutDirection([in] PpDirection LayoutDirection)
	 */
	public boolean setLayoutDirection(Variant val)
	{
	  return auto.setProperty(0x000007ec,val);
	}
	
	/*
	 * [id(0x000007ed), helpcontext(0x0008e575)] HRESULT NewWindow([out, retval]
	 * DocumentWindow** NewWindow)
	 */
	public Variant NewWindow()
	{
	  int id = 0x000007ed;
	  return auto.invoke(id);
	}
	
	/*
	 * [id(0x000007ee), helpcontext(0x0008e576)] HRESULT FollowHyperlink( [in]
	 * BSTR Address, [in, optional, defaultvalue("")] BSTR SubAddress, [in,
	 * optional, defaultvalue(0)] VARIANT_BOOL NewWindow, [in, optional,
	 * defaultvalue(-1)] VARIANT_BOOL AddHistory, [in, optional,
	 * defaultvalue("")] BSTR ExtraInfo, [in, optional, defaultvalue(0)]
	 * MsoExtraInfoMethod Method, [in, optional, defaultvalue("")] BSTR
	 * HeaderInfo)
	 */
	// unsupported
	//	public int FollowHyperlink()
	//	{
	//	  int id = 0x000007ee;
	//	  /*
	//		 * args=
	//		 * 
	//		 * [in] BSTR Address, [in, optional, defaultvalue(""
	//		 */
	//	}
	
	/*
	 * [id(0x000007ef), helpcontext(0x0008e577)] HRESULT AddToFavorites()
	 */
	public Variant AddToFavorites()
	{
	  int id = 0x000007ef;
	  return auto.invoke(id);
	}
	
	/*
	 * [id(0x000007f0), restricted, helpcontext(0x0008e578)] HRESULT Unused()
	 */
	// Unused is restricted access. no java code access will be provided
	
	/*
	 * [id(0x000007f1), propget, helpcontext(0x0008e579)] HRESULT
	 * PrintOptions([out, retval] PrintOptions** PrintOptions)
	 */
	public Variant getPrintOptions()
	{
	  return auto.getProperty(0x000007f1);
	}
	
	/*
	 * [id(0x000007f2), helpcontext(0x0008e57a)] HRESULT PrintOut( [in,
	 * optional, defaultvalue(-1)] int From, [in, optional, defaultvalue(-1)]
	 * int To, [in, optional, defaultvalue("")] BSTR PrintToFile, [in, optional,
	 * defaultvalue(0)] int Copies, [in, optional, defaultvalue(-99)]
	 * MsoTriState Collate)
	 */
	public Variant PrintOut()
	{
	  int id = 0x000007f2;
	  return auto.invoke(id);
	}
	
	/*
	 * [id(0x000007f3), helpcontext(0x0008e57b)] HRESULT Save()
	 */
	public Variant Save()
	{
	  int id = 0x000007f3;
	  return auto.invoke(id);
	}
	
	/*
	 * [id(0x000007f4), helpcontext(0x0008e57c)] HRESULT SaveAs( [in] BSTR
	 * FileName, [in, optional, defaultvalue(1)] PpSaveAsFileType FileFormat,
	 * [in, optional, defaultvalue(-2)] MsoTriState EmbedTrueTypeFonts)
	 */
	public Variant SaveAs(String filename)
	{
	  int id = 0x000007f4;
	  return auto.invoke(id, new Variant[]{new Variant(filename)});
	}
	
	/*
	 * [id(0x000007f5), helpcontext(0x0008e57d)] HRESULT SaveCopyAs( [in] BSTR
	 * FileName, [in, optional, defaultvalue(11)] PpSaveAsFileType FileFormat,
	 * [in, optional, defaultvalue(-2)] MsoTriState EmbedTrueTypeFonts)
	 */
	public Variant SaveCopyAs(String filename)
	{
	  int id = 0x000007f5;
	  return auto.invoke(id, new Variant[]{new Variant(filename)});
	}
	
	/*
	 * [id(0x000007f6), helpcontext(0x0008e57e)] HRESULT Export( [in] BSTR Path,
	 * [in] BSTR FilterName, [in, optional, defaultvalue(0)] int ScaleWidth,
	 * [in, optional, defaultvalue(0)] int ScaleHeight)
	 */
	// unsupported
	//	public int Export()
	//	{
	//	  int id = 0x000007f6;
	//	  /*
	//		 * args=
	//		 * 
	//		 * [in] BSTR Path, [in] BSTR FilterName, [in, optional, defaultvalue(0
	//		 */
	//	}
	
	/*
	 * [id(0x000007f7), helpcontext(0x0008e57f)] HRESULT Close()
	 */
	// unsupported
	//	---- *** OLE 2 JAVA NEEDS YOUR HELP TO PARSE THIS METHOD *** ---
	//	public int Close()
	//	{
	//	  int id = 0x000007f7;
	//	  /*
	//		 * args=
	//		 * 
	//		 */
	//	}
	
	/*
	 * [id(0x000007f8), hidden, helpcontext(0x0008e580)] HRESULT
	 * SetUndoText([in] BSTR Text)
	 */
	// unsupported
	//	---- *** OLE 2 JAVA NEEDS YOUR HELP TO PARSE THIS METHOD *** ---
	//	public int SetUndoText()
	//	{
	//	  int id = 0x000007f8;
	//	  /*
	//		 * args= [in] BSTR Text
	//		 */
	//	}
	
	/*
	 * [id(0x000007f9), propget, helpcontext(0x0008e581)] HRESULT
	 * Container([out, retval] IDispatch** Container)
	 */
	public Variant getContainer()
	{
	  return auto.getProperty(0x000007f9);
	}
	
	/*
	 * [id(0x000007fa), propget, helpcontext(0x0008e582)] HRESULT
	 * DisplayComments([out, retval] MsoTriState* DisplayComments)
	 */
	public Variant getDisplayComments()
	{
	  return auto.getProperty(0x000007fa);
	}
	
	/*
	 * [id(0x000007fa), propput, helpcontext(0x0008e582)] HRESULT
	 * DisplayComments([in] MsoTriState DisplayComments)
	 */
	public boolean setDisplayComments(Variant val)
	{
	  return auto.setProperty(0x000007fa,val);
	}
	
	/*
	 * [id(0x000007fb), propget, helpcontext(0x0008e583)] HRESULT
	 * FarEastLineBreakLevel([out, retval] PpFarEastLineBreakLevel*
	 * FarEastLineBreakLevel)
	 */
	public Variant getFarEastLineBreakLevel()
	{
	  return auto.getProperty(0x000007fb);
	}
	
	/*
	 * [id(0x000007fb), propput, helpcontext(0x0008e583)] HRESULT
	 * FarEastLineBreakLevel([in] PpFarEastLineBreakLevel FarEastLineBreakLevel)
	 */
	public boolean setFarEastLineBreakLevel(Variant val)
	{
	  return auto.setProperty(0x000007fb,val);
	}
	
	/*
	 * [id(0x000007fc), propget, helpcontext(0x0008e584)] HRESULT
	 * NoLineBreakBefore([out, retval] BSTR* NoLineBreakBefore)
	 */
	public Variant getNoLineBreakBefore()
	{
	  return auto.getProperty(0x000007fc);
	}
	
	/*
	 * [id(0x000007fc), propput, helpcontext(0x0008e584)] HRESULT
	 * NoLineBreakBefore([in] BSTR NoLineBreakBefore)
	 */
	public boolean setNoLineBreakBefore(Variant val)
	{
	  return auto.setProperty(0x000007fc,val);
	}
	
	/*
	 * [id(0x000007fd), propget, helpcontext(0x0008e585)] HRESULT
	 * NoLineBreakAfter([out, retval] BSTR* NoLineBreakAfter)
	 */
	public Variant getNoLineBreakAfter()
	{
	  return auto.getProperty(0x000007fd);
	}
	
	/*
	 * [id(0x000007fd), propput, helpcontext(0x0008e585)] HRESULT
	 * NoLineBreakAfter([in] BSTR NoLineBreakAfter)
	 */
	public boolean setNoLineBreakAfter(Variant val)
	{
	  return auto.setProperty(0x000007fd,val);
	}
	
	/*
	 * [id(0x000007fe), helpcontext(0x0008e586)] HRESULT UpdateLinks()
	 */
	public Variant UpdateLinks()
	{
	  int id = 0x000007fe;
	  return auto.invoke(id);
	}
	
	/*
	 * [id(0x000007ff), propget, helpcontext(0x0008e587)] HRESULT
	 * SlideShowWindow([out, retval] SlideShowWindow** SlideShowWindow)
	 */
	public Variant getSlideShowWindow()
	{
	  return auto.getProperty(0x000007ff);
	}
	
	/*
	 * [id(0x00000800), propget, helpcontext(0x0008e588)] HRESULT
	 * FarEastLineBreakLanguage([out, retval] MsoFarEastLineBreakLanguageID*
	 * FarEastLineBreakLanguage)
	 */
	public Variant getFarEastLineBreakLanguage()
	{
	  return auto.getProperty(0x00000800);
	}
	
	/*
	 * [id(0x00000800), propput, helpcontext(0x0008e588)] HRESULT
	 * FarEastLineBreakLanguage([in] MsoFarEastLineBreakLanguageID
	 * FarEastLineBreakLanguage)
	 */
	public boolean setFarEastLineBreakLanguage(Variant val)
	{
	  return auto.setProperty(0x00000800,val);
	}
	
	/*
	 * [id(0x00000801), helpcontext(0x0008e589)] HRESULT WebPagePreview()
	 */
	public Variant WebPagePreview()
	{
	  int id = 0x00000801;
	  return auto.invoke(id);
	}
	
	/*
	 * [id(0x00000802), propget, helpcontext(0x0008e58a)] HRESULT
	 * DefaultLanguageID([out, retval] MsoLanguageID* DefaultLanguageID)
	 */
	public Variant getDefaultLanguageID()
	{
	  return auto.getProperty(0x00000802);
	}
	
	/*
	 * [id(0x00000802), propput, helpcontext(0x0008e58a)] HRESULT
	 * DefaultLanguageID([in] MsoLanguageID DefaultLanguageID)
	 */
	public boolean setDefaultLanguageID(Variant val)
	{
	  return auto.setProperty(0x00000802,val);
	}
	
	/*
	 * [id(0x00000803), propget, helpcontext(0x0008e58b)] HRESULT
	 * CommandBars([out, retval] CommandBars** CommandBars)
	 */
	public Variant getCommandBars()
	{
	  return auto.getProperty(0x00000803);
	}
	
	/*
	 * [id(0x00000804), propget, helpcontext(0x0008e58c)] HRESULT
	 * PublishObjects([out, retval] PublishObjects** PublishObjects)
	 */
	public Variant getPublishObjects()
	{
	  return auto.getProperty(0x00000804);
	}
	
	/*
	 * [id(0x00000805), propget, helpcontext(0x0008e58d)] HRESULT
	 * WebOptions([out, retval] WebOptions** WebOptions)
	 */
	public Variant getWebOptions()
	{
	  return auto.getProperty(0x00000805);
	}
	
	/*
	 * [id(0x00000806), propget, helpcontext(0x0008e58e)] HRESULT
	 * HTMLProject([out, retval] HTMLProject** HTMLProject)
	 */
	public Variant getHTMLProject()
	{
	  return auto.getProperty(0x00000806);
	}
	
	/*
	 * [id(0x00000807), helpcontext(0x0008e58f)] HRESULT ReloadAs([in]
	 * MsoEncoding cp)
	 */
	// unsupported
	//	public int ReloadAs()
	//	{
	//	  int id = 0x00000807;
	//	  /*
	//		 * args= [in] MsoEncoding cp
	//		 */
	//	}
	
	/*
	 * [id(0x00000808), hidden, helpcontext(0x0008e590)] HRESULT
	 * MakeIntoTemplate([in] MsoTriState IsDesignTemplate)
	 */
	public Variant MakeIntoTemplate(int msoTriState)
	{
	  int id = 0x00000808;
	  return auto.invoke(id,new Variant[]{new Variant(msoTriState)});
	}
	
	/*
	 * [id(0x00000809), propget, helpcontext(0x0008e591)] HRESULT
	 * EnvelopeVisible([out, retval] MsoTriState* EnvelopeVisible)
	 */
	public Variant getEnvelopeVisible()
	{
	  return auto.getProperty(0x00000809);
	}
	
	/*
	 * [id(0x00000809), propput, helpcontext(0x0008e591)] HRESULT
	 * EnvelopeVisible([in] MsoTriState EnvelopeVisible)
	 */
	public boolean setEnvelopeVisible(Variant val)
	{
	  return auto.setProperty(0x00000809,val);
	}
	
	/*
	 * [id(0x0000080a), hidden, helpcontext(0x0008e592)] HRESULT sblt([in] BSTR
	 * s)
	 */
	public Variant sblt(String s)
	{
	  int id = 0x0000080a;
	  return auto.invoke(id,new Variant[]{new Variant(s)});
	}
	
	/*
	 * [id(0x0000080b), propget, helpcontext(0x0008e593)] HRESULT
	 * VBASigned([out, retval] MsoTriState* VBASigned)
	 */
	public Variant getVBASigned()
	{
	  return auto.getProperty(0x0000080b);
	}
	
	/*
	 * [id(0x0000080d), propget, helpcontext(0x0008e595)] HRESULT
	 * SnapToGrid([out, retval] MsoTriState* SnapToGrid)
	 */
	public Variant getSnapToGrid()
	{
	  return auto.getProperty(0x0000080d);
	}
	
	/*
	 * [id(0x0000080d), propput, helpcontext(0x0008e595)] HRESULT
	 * SnapToGrid([in] MsoTriState SnapToGrid)
	 */
	public boolean setSnapToGrid(Variant val)
	{
	  return auto.setProperty(0x0000080d,val);
	}
	
	/*
	 * [id(0x0000080e), propget, helpcontext(0x0008e596)] HRESULT
	 * GridDistance([out, retval] single* GridDistance)
	 */
	public Variant getGridDistance()
	{
	  return auto.getProperty(0x0000080e);
	}
	
	/*
	 * [id(0x0000080e), propput, helpcontext(0x0008e596)] HRESULT
	 * GridDistance([in] single GridDistance)
	 */
	public boolean setGridDistance(Variant val)
	{
	  return auto.setProperty(0x0000080e,val);
	}
	
	/*
	 * [id(0x0000080f), propget, helpcontext(0x0008e597)] HRESULT Designs([out,
	 * retval] Designs** Designs)
	 */
	public Variant getDesigns()
	{
	  return auto.getProperty(0x0000080f);
	}
	
	/*
	 * [id(0x00000810), helpcontext(0x0008e598)] HRESULT Merge([in] BSTR Path)
	 */
	public Variant Merge(String path)
	{
	  int id = 0x00000810;
	  return auto.invoke(id,new Variant[]{new Variant(path)});
	}
	
	/*
	 * [id(0x00000811), helpcontext(0x0008e599)] HRESULT CheckIn( [in, optional,
	 * defaultvalue(-1)] VARIANT_BOOL SaveChanges, [in, optional] VARIANT
	 * Comments, [in, optional] VARIANT MakePublic)
	 */
	// unsupported
	//	public int CheckIn()
	//	{
	//	  int id = 0x00000811;
	//	  /*
	//		 * args=
	//		 * 
	//		 * [in, optional, defaultvalue(-1
	//		 */
	//	}
	
	/*
	 * [id(0x00000812), helpcontext(0x0008e59a)] HRESULT CanCheckIn([out,
	 * retval] VARIANT_BOOL* CanCheckIn)
	 */
	// unsupported
	//	public int CanCheckIn()
	//	{
	//	  int id = 0x00000812;
	//	  /*
	//		 * args= [out, retval] VARIANT_BOOL* CanCheckIn
	//		 */
	//	}
	
	/*
	 * [id(0x00000813), propget, helpcontext(0x0008e59b)] HRESULT
	 * Signatures([out, retval] SignatureSet** Signatures)
	 */
	public Variant getSignatures()
	{
	  return auto.getProperty(0x00000813);
	}
	
	/*
	 * [id(0x00000814), propget, helpcontext(0x0008e59c)] HRESULT
	 * RemovePersonalInformation([out, retval] MsoTriState*
	 * RemovePersonalInformation)
	 */
	public Variant getRemovePersonalInformation()
	{
	  return auto.getProperty(0x00000814);
	}
	
	/*
	 * [id(0x00000814), propput, helpcontext(0x0008e59c)] HRESULT
	 * RemovePersonalInformation([in] MsoTriState RemovePersonalInformation)
	 */
	public boolean setRemovePersonalInformation(Variant val)
	{
	  return auto.setProperty(0x00000814,val);
	}
	
	/*
	 * [id(0x00000815), helpcontext(0x0008e59d)] HRESULT SendForReview( [in,
	 * optional, defaultvalue("")] BSTR Recipients, [in, optional,
	 * defaultvalue("")] BSTR Subject, [in, optional, defaultvalue(-1)]
	 * VARIANT_BOOL ShowMessage, [in, optional] VARIANT IncludeAttachment)
	 */
	// unsupported
	//	public int SendForReview()
	//	{
	//	  int id = 0x00000815;
	//	  /*
	//		 * args=
	//		 * 
	//		 * [in, optional, defaultvalue(""
	//		 */
	//	}
	
	/*
	 * [id(0x00000816), helpcontext(0x0008e59e)] HRESULT ReplyWithChanges([in,
	 * optional, defaultvalue(-1)] VARIANT_BOOL ShowMessage)
	 */
	// unsupported
	//	public int ReplyWithChanges()
	//	{
	//	  int id = 0x00000816;
	//	  /*
	//		 * args= [in, optional, defaultvalue(-1
	//		 */
	//	}
	
	/*
	 * [id(0x00000817), helpcontext(0x0008e59f)] HRESULT EndReview()
	 */
	public Variant EndReview()
	{
	  int id = 0x00000817;
	  return auto.invoke(id);
	}
	
	/*
	 * [id(0x00000818), propget, helpcontext(0x0008e5a0)] HRESULT
	 * HasRevisionInfo([out, retval] PpRevisionInfo* HasRevisionInfo)
	 */
	public Variant getHasRevisionInfo()
	{
	  return auto.getProperty(0x00000818);
	}
	
	/*
	 * [id(0x00000819), helpcontext(0x0008e5a1)] HRESULT AddBaseline([in,
	 * optional, defaultvalue("")] BSTR FileName)
	 */
	public Variant AddBaseline(String filename)
	{
	  int id = 0x00000819;
	  return auto.invoke(id,new Variant[]{new Variant(filename)});
	}
	
	/*
	 * [id(0x0000081a), helpcontext(0x0008e5a2)] HRESULT RemoveBaseline()
	 */
	public Variant RemoveBaseline()
	{
	  int id = 0x0000081a;
	  return auto.invoke(id);
	}
	
	/*
	 * [id(0x0000081b), propget, helpcontext(0x0008e5a3)] HRESULT
	 * PasswordEncryptionProvider([out, retval] BSTR*
	 * PasswordEncryptionProvider)
	 */
	public Variant getPasswordEncryptionProvider()
	{
	  return auto.getProperty(0x0000081b);
	}
	
	/*
	 * [id(0x0000081c), propget, helpcontext(0x0008e5a4)] HRESULT
	 * PasswordEncryptionAlgorithm([out, retval] BSTR*
	 * PasswordEncryptionAlgorithm)
	 */
	public Variant getPasswordEncryptionAlgorithm()
	{
	  return auto.getProperty(0x0000081c);
	}
	
	/*
	 * [id(0x0000081d), propget, helpcontext(0x0008e5a5)] HRESULT
	 * PasswordEncryptionKeyLength([out, retval] int*
	 * PasswordEncryptionKeyLength)
	 */
	public Variant getPasswordEncryptionKeyLength()
	{
	  return auto.getProperty(0x0000081d);
	}
	
	/*
	 * [id(0x0000081e), propget, helpcontext(0x0008e5a6)] HRESULT
	 * PasswordEncryptionFileProperties([out, retval] VARIANT_BOOL*
	 * PasswordEncryptionFileProperties)
	 */
	public Variant getPasswordEncryptionFileProperties()
	{
	  return auto.getProperty(0x0000081e);
	}
	
	/*
	 * [id(0x0000081f), helpcontext(0x0008e5a7)] HRESULT
	 * SetPasswordEncryptionOptions( [in] BSTR PasswordEncryptionProvider, [in]
	 * BSTR PasswordEncryptionAlgorithm, [in] int PasswordEncryptionKeyLength,
	 * [in] VARIANT_BOOL PasswordEncryptionFileProperties)
	 */
	// unsupported
	//	public int SetPasswordEncryptionOptions()
	//	{
	//	  int id = 0x0000081f;
	//	  /*
	//		 * args=
	//		 * 
	//		 * [in] BSTR PasswordEncryptionProvider, [in] BSTR
	//		 * PasswordEncryptionAlgorithm, [in] int PasswordEncryptionKeyLength,
	//		 * [in] VARIANT_BOOL PasswordEncryptionFileProperties
	//		 */
	//	}
	
	/*
	 * [id(0x00000820), propget, helpcontext(0x0008e5a8)] HRESULT Password([out,
	 * retval] BSTR* Password)
	 */
	public Variant getPassword()
	{
	  return auto.getProperty(0x00000820);
	}
	
	/*
	 * [id(0x00000820), propput, helpcontext(0x0008e5a8)] HRESULT Password([in]
	 * BSTR Password)
	 */
	public boolean setPassword(Variant val)
	{
	  return auto.setProperty(0x00000820,val);
	}
	
	/*
	 * [id(0x00000821), propget, helpcontext(0x0008e5a9)] HRESULT
	 * WritePassword([out, retval] BSTR* WritePassword)
	 */
	public Variant getWritePassword()
	{
	  return auto.getProperty(0x00000821);
	}
	
	/*
	 * [id(0x00000821), propput, helpcontext(0x0008e5a9)] HRESULT
	 * WritePassword([in] BSTR WritePassword)
	 */
	public boolean setWritePassword(Variant val)
	{
	  return auto.setProperty(0x00000821,val);
	}


}
