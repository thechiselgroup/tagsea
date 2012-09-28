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
 * generated from Powerpoint Object Library
 * 

    [
      odl,
      uuid(91493457-5A91-11CF-8700-00AA0060263B),
      helpcontext(0x0007cc18),
      dual,
      nonextensible,
      oleautomation
    ]

 * 
 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */
public class DocumentWindow 
{

	private OleAutomation auto;
	
	public DocumentWindow (OleAutomation automation)
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
		[id(0x000007d1), propget, helpcontext(0x0007cc19)]
	        HRESULT Application([out, retval] Application** Application)
	*/
	public Variant getApplication()
	{
	  return auto.getProperty(0x000007d1);
	}
	
	/*
		[id(0x000007d2), propget, helpcontext(0x0007cc1a)]
	        HRESULT Parent([out, retval] IDispatch** Parent)
	*/
	public Variant getParent()
	{
	  return auto.getProperty(0x000007d2);
	}
	
	/*
		[id(0x000007d3), propget, helpcontext(0x0007cc1b)]
	        HRESULT Selection([out, retval] Selection** Selection)
	*/
	public Variant getSelection()
	{
	  return auto.getProperty(0x000007d3);
	}
	
	/*
		[id(0x000007d4), propget, helpcontext(0x0007cc1c)]
	        HRESULT View([out, retval] View** View)
	*/
	public Variant getView()
	{
	  return auto.getProperty(0x000007d4);
	}
	
	/*
		[id(0x000007d5), propget, helpcontext(0x0007cc1d)]
	        HRESULT Presentation([out, retval] Presentation** Presentation)
	*/
	public Variant getPresentation()
	{
	  return auto.getProperty(0x000007d5);
	}
	
	/*
		[id(0x000007d6), propget, helpcontext(0x0007cc1e)]
	        HRESULT ViewType([out, retval] PpViewType* ViewType)
	*/
	public Variant getViewType()
	{
	  return auto.getProperty(0x000007d6);
	}
	
	/*
		[id(0x000007d6), propput, helpcontext(0x0007cc1e)]
	        HRESULT ViewType([in] PpViewType ViewType)
	*/
	public boolean setViewType(Variant val)
	{
	  return auto.setProperty(0x000007d6,val);
	}
	
	/*
		[id(0x000007d7), propget, helpcontext(0x0007cc1f)]
	        HRESULT BlackAndWhite([out, retval] MsoTriState* BlackAndWhite)
	*/
	public Variant getBlackAndWhite()
	{
	  return auto.getProperty(0x000007d7);
	}
	
	/*
		[id(0x000007d7), propput, helpcontext(0x0007cc1f)]
	        HRESULT BlackAndWhite([in] MsoTriState BlackAndWhite)
	*/
	public boolean setBlackAndWhite(Variant val)
	{
	  return auto.setProperty(0x000007d7,val);
	}
	
	/*
		[id(0x000007d8), propget, helpcontext(0x0007cc20)]
	        HRESULT Active([out, retval] MsoTriState* Active)
	*/
	public Variant getActive()
	{
	  return auto.getProperty(0x000007d8);
	}
	
	/*
		[id(0x000007d9), propget, helpcontext(0x0007cc21)]
	        HRESULT WindowState([out, retval] PpWindowState* WindowState)
	*/
	public Variant getWindowState()
	{
	  return auto.getProperty(0x000007d9);
	}
	
	/*
		[id(0x000007d9), propput, helpcontext(0x0007cc21)]
	        HRESULT WindowState([in] PpWindowState WindowState)
	*/
	public boolean setWindowState(Variant val)
	{
	  return auto.setProperty(0x000007d9,val);
	}
	
	/*
		[id(00000000), propget, helpcontext(0x0007cc22)]
	        HRESULT Caption([out, retval] BSTR* Caption)
	*/
	public Variant getCaption()
	{
	  return auto.getProperty(00000000);
	}
	
	/*
		[id(0x000007da), propget, helpcontext(0x0007cc23)]
	        HRESULT Left([out, retval] single* Left)
	*/
	public Variant getLeft()
	{
	  return auto.getProperty(0x000007da);
	}
	
	/*
		[id(0x000007da), propput, helpcontext(0x0007cc23)]
	        HRESULT Left([in] single Left)
	*/
	public boolean setLeft(Variant val)
	{
	  return auto.setProperty(0x000007da,val);
	}
	
	/*
		[id(0x000007db), propget, helpcontext(0x0007cc24)]
	        HRESULT Top([out, retval] single* Top)
	*/
	public Variant getTop()
	{
	  return auto.getProperty(0x000007db);
	}
	
	/*
		[id(0x000007db), propput, helpcontext(0x0007cc24)]
	        HRESULT Top([in] single Top)
	*/
	public boolean setTop(Variant val)
	{
	  return auto.setProperty(0x000007db,val);
	}
	
	/*
		[id(0x000007dc), propget, helpcontext(0x0007cc25)]
	        HRESULT Width([out, retval] single* Width)
	*/
	public Variant getWidth()
	{
	  return auto.getProperty(0x000007dc);
	}
	
	/*
		[id(0x000007dc), propput, helpcontext(0x0007cc25)]
	        HRESULT Width([in] single Width)
	*/
	public boolean setWidth(Variant val)
	{
	  return auto.setProperty(0x000007dc,val);
	}
	
	/*
		[id(0x000007dd), propget, helpcontext(0x0007cc26)]
	        HRESULT Height([out, retval] single* Height)
	*/
	public Variant getHeight()
	{
	  return auto.getProperty(0x000007dd);
	}
	
	/*
		[id(0x000007dd), propput, helpcontext(0x0007cc26)]
	        HRESULT Height([in] single Height)
	*/
	public boolean setHeight(Variant val)
	{
	  return auto.setProperty(0x000007dd,val);
	}
	
	/*
		[id(0x000007de), helpcontext(0x0007cc27)]
	        HRESULT FitToPage()
	*/
	public Variant FitToPage()
	{
	  int id = 0x000007de;
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007df), helpcontext(0x0007cc28)]
	        HRESULT Activate()
	*/
	public Variant Activate()
	{
	  int id = 0x000007df;
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007e0), helpcontext(0x0007cc29)]
	        HRESULT LargeScroll(
	                        [in, optional, defaultvalue(1)] int Down, 
	                        [in, optional, defaultvalue(0)] int Up, 
	                        [in, optional, defaultvalue(0)] int ToRight, 
	                        [in, optional, defaultvalue(0)] int ToLeft)
	*/
	public Variant LargeScroll(int down, int up, int toright, int toleft)
	{
	  int id = 0x000007e0;
	  return auto.invoke(id,new Variant[]{new Variant(down),new Variant(up),new Variant(toright),new Variant(toleft)});
	}
	
	/*
		[id(0x000007e1), helpcontext(0x0007cc2a)]
	        HRESULT SmallScroll(
	                        [in, optional, defaultvalue(1)] int Down, 
	                        [in, optional, defaultvalue(0)] int Up, 
	                        [in, optional, defaultvalue(0)] int ToRight, 
	                        [in, optional, defaultvalue(0)] int ToLeft)
	*/
	public Variant SmallScroll(int down, int up, int toright, int toleft)
	{
	  int id = 0x000007e1;
	  return auto.invoke(id,new Variant[]{new Variant(down),new Variant(up),new Variant(toright),new Variant(toleft)});
	}
	
	/*
		[id(0x000007e2), helpcontext(0x0007cc2b)]
	        HRESULT NewWindow([out, retval] DocumentWindow** NewWindow)
	*/
	public Variant NewWindow()
	{
	  int id = 0x000007e2;
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007e3), helpcontext(0x0007cc2c)]
	        HRESULT Close()
	*/
	public Variant Close()
	{
	  int id = 0x000007e3;
	  return auto.invoke(id);
	}
	
	/*
		[id(0x000007e4), propget, restricted, helpcontext(0x0007cc2d)]
	        HRESULT HWND([out, retval] long* HWND)
	*/
	// HWND is restricted access.  no java code access will be provided
	
	/*
		[id(0x000007e5), propget, helpcontext(0x0007cc2e)]
	        HRESULT ActivePane([out, retval] Pane** ActivePane)
	*/
	public Variant getActivePane()
	{
	  return auto.getProperty(0x000007e5);
	}
	
	/*
		[id(0x000007e6), propget, helpcontext(0x0007cc2f)]
	        HRESULT Panes([out, retval] Panes** Panes)
	*/
	public Variant getPanes()
	{
	  return auto.getProperty(0x000007e6);
	}
	
	/*
		[id(0x000007e7), propget, helpcontext(0x0007cc30)]
	        HRESULT SplitVertical([out, retval] long* SplitVertical)
	*/
	public Variant getSplitVertical()
	{
	  return auto.getProperty(0x000007e7);
	}
	
	/*
		[id(0x000007e7), propput, helpcontext(0x0007cc30)]
	        HRESULT SplitVertical([in] long SplitVertical)
	*/
	public boolean setSplitVertical(Variant val)
	{
	  return auto.setProperty(0x000007e7,val);
	}
	
	/*
		[id(0x000007e8), propget, helpcontext(0x0007cc31)]
	        HRESULT SplitHorizontal([out, retval] long* SplitHorizontal)
	*/
	public Variant getSplitHorizontal()
	{
	  return auto.getProperty(0x000007e8);
	}
	
	/*
		[id(0x000007e8), propput, helpcontext(0x0007cc31)]
	        HRESULT SplitHorizontal([in] long SplitHorizontal)
	*/
	public boolean setSplitHorizontal(Variant val)
	{
	  return auto.setProperty(0x000007e8,val);
	}
	
	/*
		[id(0x000007e9), helpcontext(0x0007cc32)]
	        HRESULT RangeFromPoint(
	                        [in] int X, 
	                        [in] int Y, 
	                        [out, retval] IDispatch** RangeFromPoint)
	*/
	public Variant RangeFromPoint(int x, int y)
	{
	  int id = 0x000007e9;
	  return auto.invoke(id,new Variant[]{new Variant(x),new Variant(y)});
	}
	
	/*
		[id(0x000007ea), helpcontext(0x0007cc33)]
	        HRESULT PointsToScreenPixelsX(
	                        [in] single Points, 
	                        [out, retval] int* PointsToScreenPixelsX)
	*/
	public Variant PointsToScreenPixelsX(int points)
	{
	  int id = 0x000007ea;
	  return auto.invoke(id,new Variant[]{new Variant(points)});
	}
	
	/*
		[id(0x000007eb), helpcontext(0x0007cc34)]
	        HRESULT PointsToScreenPixelsY(
	                        [in] single Points, 
	                        [out, retval] int* PointsToScreenPixelsY)
	*/
	public Variant PointsToScreenPixelsY(int points)
	{
	  int id = 0x000007eb;
	  return auto.invoke(id,new Variant[]{new Variant(points)});
	}
	
	/*
		[id(0x000007ec), helpcontext(0x0007cc35)]
	        HRESULT ScrollIntoView(
	                        [in] single Left, 
	                        [in] single Top, 
	                        [in] single Width, 
	                        [in] single Height, 
	                        [in, optional, defaultvalue(-1)] MsoTriState Start)
	*/
	public Variant ScrollIntoView(int left, int top, int width, int height)
	{
	  int id = 0x000007ec;
	  return auto.invoke(id,new Variant[]{new Variant(left),new Variant(top),new Variant(width),new Variant(height)});
	}

}
