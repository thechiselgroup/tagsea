/*******************************************************************************
 * Copyright (c) 2006-2007 IBM Corporation and others.
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
      uuid(9149345A-5A91-11CF-8700-00AA0060263B),
      helpcontext(0x0007d7d0),
      dual,
      nonextensible,
      oleautomation
    ]
 * 
 * @author Li-Te Cheng
 * IBM Research, 2006
 */
public class SlideShowSettings 
{

	private OleAutomation auto;
	
	public SlideShowSettings (OleAutomation automation)
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
		[id(0x000007d1), propget, helpcontext(0x0007d7d1)]
	        HRESULT Application([out, retval] Application** Application)
	*/
	public Variant getApplication()
	{
	  return auto.getProperty(0x000007d1);
	}
	
	/*
		[id(0x000007d2), propget, helpcontext(0x0007d7d2)]
	        HRESULT Parent([out, retval] IDispatch** Parent)
	*/
	public Variant getParent()
	{
	  return auto.getProperty(0x000007d2);
	}
	
	/*
		[id(0x000007d3), propget, helpcontext(0x0007d7d3)]
	        HRESULT PointerColor([out, retval] ColorFormat** PointerColor)
	*/
	public Variant getPointerColor()
	{
	  return auto.getProperty(0x000007d3);
	}
	
	/*
		[id(0x000007d4), propget, helpcontext(0x0007d7d4)]
	        HRESULT NamedSlideShows([out, retval] NamedSlideShows** NamedSlideShows)
	*/
	public Variant getNamedSlideShows()
	{
	  return auto.getProperty(0x000007d4);
	}
	
	/*
		[id(0x000007d5), propget, helpcontext(0x0007d7d5)]
	        HRESULT StartingSlide([out, retval] int* StartingSlide)
	*/
	public Variant getStartingSlide()
	{
	  return auto.getProperty(0x000007d5);
	}
	
	/*
		[id(0x000007d5), propput, helpcontext(0x0007d7d5)]
	        HRESULT StartingSlide([in] int StartingSlide)
	*/
	public boolean setStartingSlide(Variant val)
	{
	  return auto.setProperty(0x000007d5,val);
	}
	
	/*
		[id(0x000007d6), propget, helpcontext(0x0007d7d6)]
	        HRESULT EndingSlide([out, retval] int* EndingSlide)
	*/
	public Variant getEndingSlide()
	{
	  return auto.getProperty(0x000007d6);
	}
	
	/*
		[id(0x000007d6), propput, helpcontext(0x0007d7d6)]
	        HRESULT EndingSlide([in] int EndingSlide)
	*/
	public boolean setEndingSlide(Variant val)
	{
	  return auto.setProperty(0x000007d6,val);
	}
	
	/*
		[id(0x000007d7), propget, helpcontext(0x0007d7d7)]
	        HRESULT AdvanceMode([out, retval] PpSlideShowAdvanceMode* AdvanceMode)
	*/
	public Variant getAdvanceMode()
	{
	  return auto.getProperty(0x000007d7);
	}
	
	/*
		[id(0x000007d7), propput, helpcontext(0x0007d7d7)]
	        HRESULT AdvanceMode([in] PpSlideShowAdvanceMode AdvanceMode)
	*/
	public boolean setAdvanceMode(Variant val)
	{
	  return auto.setProperty(0x000007d7,val);
	}
	
	/*
		[id(0x000007d8), helpcontext(0x0007d7d8)]
	        HRESULT Run([out, retval] SlideShowWindow** Run)
	*/
	public Variant Run()
	{
	  int id = 0x000007d8;
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007d9), propget, helpcontext(0x0007d7d9)]
	        HRESULT LoopUntilStopped([out, retval] MsoTriState* LoopUntilStopped)
	*/
	public Variant getLoopUntilStopped()
	{
	  return auto.getProperty(0x000007d9);
	}
	
	/*
		[id(0x000007d9), propput, helpcontext(0x0007d7d9)]
	        HRESULT LoopUntilStopped([in] MsoTriState LoopUntilStopped)
	*/
	public boolean setLoopUntilStopped(Variant val)
	{
	  return auto.setProperty(0x000007d9,val);
	}
	
	/*
		[id(0x000007da), propget, helpcontext(0x0007d7da)]
	        HRESULT ShowType([out, retval] PpSlideShowType* ShowType)
	*/
	public Variant getShowType()
	{
	  return auto.getProperty(0x000007da);
	}
	
	/*
		[id(0x000007da), propput, helpcontext(0x0007d7da)]
	        HRESULT ShowType([in] PpSlideShowType ShowType)
	*/
	public boolean setShowType(Variant val)
	{
	  return auto.setProperty(0x000007da,val);
	}
	
	/*
		[id(0x000007db), propget, helpcontext(0x0007d7db)]
	        HRESULT ShowWithNarration([out, retval] MsoTriState* ShowWithNarration)
	*/
	public Variant getShowWithNarration()
	{
	  return auto.getProperty(0x000007db);
	}
	
	/*
		[id(0x000007db), propput, helpcontext(0x0007d7db)]
	        HRESULT ShowWithNarration([in] MsoTriState ShowWithNarration)
	*/
	public boolean setShowWithNarration(Variant val)
	{
	  return auto.setProperty(0x000007db,val);
	}
	
	/*
		[id(0x000007dc), propget, helpcontext(0x0007d7dc)]
	        HRESULT ShowWithAnimation([out, retval] MsoTriState* ShowWithAnimation)
	*/
	public Variant getShowWithAnimation()
	{
	  return auto.getProperty(0x000007dc);
	}
	
	/*
		[id(0x000007dc), propput, helpcontext(0x0007d7dc)]
	        HRESULT ShowWithAnimation([in] MsoTriState ShowWithAnimation)
	*/
	public boolean setShowWithAnimation(Variant val)
	{
	  return auto.setProperty(0x000007dc,val);
	}
	
	/*
		[id(0x000007dd), propget, helpcontext(0x0007d7dd)]
	        HRESULT SlideShowName([out, retval] BSTR* SlideShowName)
	*/
	public Variant getSlideShowName()
	{
	  return auto.getProperty(0x000007dd);
	}
	
	/*
		[id(0x000007dd), propput, helpcontext(0x0007d7dd)]
	        HRESULT SlideShowName([in] BSTR SlideShowName)
	*/
	public boolean setSlideShowName(Variant val)
	{
	  return auto.setProperty(0x000007dd,val);
	}
	
	/*
		[id(0x000007de), propget, helpcontext(0x0007d7de)]
	        HRESULT RangeType([out, retval] PpSlideShowRangeType* RangeType)
	*/
	public Variant getRangeType()
	{
	  return auto.getProperty(0x000007de);
	}
	
	/*
		[id(0x000007de), propput, helpcontext(0x0007d7de)]
	        HRESULT RangeType([in] PpSlideShowRangeType RangeType)
	*/
	public boolean setRangeType(Variant val)
	{
	  return auto.setProperty(0x000007de,val);
	}
	
	/*
		[id(0x000007df), propget, helpcontext(0x0007d7df)]
	        HRESULT ShowScrollbar([out, retval] MsoTriState* ShowScrollbar)
	*/
	public Variant getShowScrollbar()
	{
	  return auto.getProperty(0x000007df);
	}
	
	/*
		[id(0x000007df), propput, helpcontext(0x0007d7df)]
	        HRESULT ShowScrollbar([in] MsoTriState ShowScrollbar)
	*/
	public boolean setShowScrollbar(Variant val)
	{
	  return auto.setProperty(0x000007df,val);
	}


}
