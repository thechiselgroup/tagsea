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
 * generated from Powerpoint Object Library

    [
      odl,
      uuid(91493459-5A91-11CF-8700-00AA0060263B),
      helpcontext(0x0007d3e8),
      dual,
      nonextensible,
      oleautomation
    ]

 * @author Li-Te Cheng
 * CUE, IBM Research, 2006
 */

public class SlideShowView 
{

	private OleAutomation auto;
	
	public SlideShowView (OleAutomation automation)
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
		[id(0x000007d1), propget, helpcontext(0x0007d3e9)]
	        HRESULT Application([out, retval] Application** Application)
	*/
	public Variant getApplication()
	{
	  return auto.getProperty(0x000007d1);
	}
	
	/*
		[id(0x000007d2), propget, helpcontext(0x0007d3ea)]
	        HRESULT Parent([out, retval] IDispatch** Parent)
	*/
	public Variant getParent()
	{
	  return auto.getProperty(0x000007d2);
	}
	
	/*
		[id(0x000007d3), propget, helpcontext(0x0007d3eb)]
	        HRESULT Zoom([out, retval] int* Zoom)
	*/
	public Variant getZoom()
	{
	  return auto.getProperty(0x000007d3);
	}
	
	/*
		[id(0x000007d4), propget, helpcontext(0x0007d3ec)]
	        HRESULT Slide([out, retval] Slide** Slide)
	*/
	public Variant getSlide()
	{
	  return auto.getProperty(0x000007d4);
	}
	
	/*
		[id(0x000007d5), propget, helpcontext(0x0007d3ed)]
	        HRESULT PointerType([out, retval] PpSlideShowPointerType* PointerType)
	*/
	public Variant getPointerType()
	{
	  return auto.getProperty(0x000007d5);
	}
	
	/*
		[id(0x000007d5), propput, helpcontext(0x0007d3ed)]
	        HRESULT PointerType([in] PpSlideShowPointerType PointerType)
	*/
	public boolean setPointerType(Variant val)
	{
	  return auto.setProperty(0x000007d5,val);
	}
	
	/*
		[id(0x000007d6), propget, helpcontext(0x0007d3ee)]
	        HRESULT State([out, retval] PpSlideShowState* State)
	*/
	public Variant getState()
	{
	  return auto.getProperty(0x000007d6);
	}
	
	/*
		[id(0x000007d6), propput, helpcontext(0x0007d3ee)]
	        HRESULT State([in] PpSlideShowState State)
	*/
	public boolean setState(Variant val)
	{
	  return auto.setProperty(0x000007d6,val);
	}
	
	/*
		[id(0x000007d7), propget, helpcontext(0x0007d3ef)]
	        HRESULT AcceleratorsEnabled([out, retval] MsoTriState* AcceleratorsEnabled)
	*/
	public Variant getAcceleratorsEnabled()
	{
	  return auto.getProperty(0x000007d7);
	}
	
	/*
		[id(0x000007d7), propput, helpcontext(0x0007d3ef)]
	        HRESULT AcceleratorsEnabled([in] MsoTriState AcceleratorsEnabled)
	*/
	public boolean setAcceleratorsEnabled(Variant val)
	{
	  return auto.setProperty(0x000007d7,val);
	}
	
	/*
		[id(0x000007d8), propget, helpcontext(0x0007d3f0)]
	        HRESULT PresentationElapsedTime([out, retval] single* PresentationElapsedTime)
	*/
	public Variant getPresentationElapsedTime()
	{
	  return auto.getProperty(0x000007d8);
	}
	
	/*
		[id(0x000007d9), propget, helpcontext(0x0007d3f1)]
	        HRESULT SlideElapsedTime([out, retval] single* SlideElapsedTime)
	*/
	public Variant getSlideElapsedTime()
	{
	  return auto.getProperty(0x000007d9);
	}
	
	/*
		[id(0x000007d9), propput, helpcontext(0x0007d3f1)]
	        HRESULT SlideElapsedTime([in] single SlideElapsedTime)
	*/
	public boolean setSlideElapsedTime(Variant val)
	{
	  return auto.setProperty(0x000007d9,val);
	}
	
	/*
		[id(0x000007da), propget, helpcontext(0x0007d3f2)]
	        HRESULT LastSlideViewed([out, retval] Slide** LastSlideViewed)
	*/
	public Variant getLastSlideViewed()
	{
	  return auto.getProperty(0x000007da);
	}
	
	/*
		[id(0x000007db), propget, helpcontext(0x0007d3f3)]
	        HRESULT AdvanceMode([out, retval] PpSlideShowAdvanceMode* AdvanceMode)
	*/
	public Variant getAdvanceMode()
	{
	  return auto.getProperty(0x000007db);
	}
	
	/*
		[id(0x000007dc), propget, helpcontext(0x0007d3f4)]
	        HRESULT PointerColor([out, retval] ColorFormat** PointerColor)
	*/
	public Variant getPointerColor()
	{
	  return auto.getProperty(0x000007dc);
	}
	
	/*
		[id(0x000007dd), propget, helpcontext(0x0007d3f5)]
	        HRESULT IsNamedShow([out, retval] MsoTriState* IsNamedShow)
	*/
	public Variant getIsNamedShow()
	{
	  return auto.getProperty(0x000007dd);
	}
	
	/*
		[id(0x000007de), propget, helpcontext(0x0007d3f6)]
	        HRESULT SlideShowName([out, retval] BSTR* SlideShowName)
	*/
	public Variant getSlideShowName()
	{
	  return auto.getProperty(0x000007de);
	}
	
	/*
		[id(0x000007df), helpcontext(0x0007d3f7)]
	        HRESULT DrawLine(
	                        [in] single BeginX, 
	                        [in] single BeginY, 
	                        [in] single EndX, 
	                        [in] single EndY)
	*/
	public Variant DrawLine(int beginX, int beginY, int endX, int endY)
	{
	  int id = 0x000007df;
	  /* args=
	
	                        [in] single BeginX, 
	                        [in] single BeginY, 
	                        [in] single EndX, 
	                        [in] single EndY
 	  */
	  return auto.invoke(id,new Variant[]{new Variant(beginX),new Variant(beginY),new Variant(endX),new Variant(endY)});
	}
	
	/*
		[id(0x000007e0), helpcontext(0x0007d3f8)]
	        HRESULT EraseDrawing()
	*/
	public Variant EraseDrawing()
	{
	  int id = 0x000007e0;
	  /* args=
	
	*/
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007e1), helpcontext(0x0007d3f9)]
	        HRESULT First()
	*/
	public Variant First()
	{
	  int id = 0x000007e1;
	  /* args=
	
	*/
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007e2), helpcontext(0x0007d3fa)]
	        HRESULT Last()
	*/
	public Variant Last()
	{
	  int id = 0x000007e2;
	  /* args=
	
	*/
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007e3), helpcontext(0x0007d3fb)]
	        HRESULT Next()
	*/
	public Variant Next()
	{
	  int id = 0x000007e3;
	  /* args=
	
	*/
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007e4), helpcontext(0x0007d3fc)]
	        HRESULT Previous()
	*/
	public Variant Previous()
	{
	  int id = 0x000007e4;
	  /* args=
	
	*/
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007e5), helpcontext(0x0007d3fd)]
	        HRESULT GotoSlide(
	                        [in] int Index, 
	                        [in, optional, defaultvalue(-1)] MsoTriState ResetSlide)
	*/
	public Variant GotoSlide(int index)
	{
	  int id = 0x000007e5;
	  /* args=
	
	                        [in] int Index, 
	                        [in, optional, defaultvalue(-1
	*/
	  return auto.invoke(id,new Variant[]{new Variant(index)});
	}
	
	/*
		[id(0x000007e6), helpcontext(0x0007d3fe)]
	        HRESULT GotoNamedShow([in] BSTR SlideShowName)
	*/
	public Variant GotoNamedShow(String slideShowName)
	{
	  int id = 0x000007e6;
	  /* args=
	[in] BSTR SlideShowName
	*/
	  return auto.invoke(id,new Variant[]{new Variant(slideShowName)});
	}
	
	/*
		[id(0x000007e7), helpcontext(0x0007d3ff)]
	        HRESULT EndNamedShow()
	*/
	public Variant EndNamedShow()
	{
	  int id = 0x000007e7;
	  /* args=
	
	*/
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007e8), helpcontext(0x0007d400)]
	        HRESULT ResetSlideTime()
	*/
	public Variant ResetSlideTime()
	{
	  int id = 0x000007e8;
	  /* args=
	
	*/
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007e9), helpcontext(0x0007d401)]
	        HRESULT Exit()
	*/
	public Variant Exit()
	{
	  int id = 0x000007e9;
	  /* args=
	
	*/
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007ea), hidden, helpcontext(0x0007d402)]
	        HRESULT InstallTracker(
	                        [in] MouseTracker* pTracker, 
	                        [in] MsoTriState Presenter)
	*/
	
	// unsupported
	
	//	public Variant InstallTracker()
	//	{
	//	  int id = 0x000007ea;
	//	  /* args=
	//	
	//	                        [in] MouseTracker* pTracker, 
	//	                        [in] MsoTriState Presenter
	//	*/
	//	  return auto.invoke(id,new Variant[]{new Variant(arg)});
	//	}
	
	/*
		[id(0x000007eb), propget, helpcontext(0x0007d403)]
	        HRESULT CurrentShowPosition([out, retval] int* CurrentShowPosition)
	*/
	public Variant getCurrentShowPosition()
	{
	  return auto.getProperty(0x000007eb);
	}


}
