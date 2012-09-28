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
      uuid(91493453-5A91-11CF-8700-00AA0060263B),
      helpcontext(0x0007bc78),
      dual,
      nonextensible,
      oleautomation
    ]

 * @author Li-Te Cheng
 * CUE, IBM Research 2006
 */

public class SlideShowWindow 
{

	private OleAutomation auto;
	
	public SlideShowWindow (OleAutomation automation)
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
		[id(0x000007d1), propget, helpcontext(0x0007bc79)]
	        HRESULT Application([out, retval] Application** Application)
	*/
	public Variant getApplication()
	{
	  return auto.getProperty(0x000007d1);
	}
	
	/*
		[id(0x000007d2), propget, helpcontext(0x0007bc7a)]
	        HRESULT Parent([out, retval] IDispatch** Parent)
	*/
	public Variant getParent()
	{
	  return auto.getProperty(0x000007d2);
	}
	
	/*
		[id(0x000007d3), propget, helpcontext(0x0007bc7b)]
	        HRESULT View([out, retval] SlideShowView** View)
	*/
	public Variant getView()
	{
	  return auto.getProperty(0x000007d3);
	}
	
	/*
		[id(0x000007d4), propget, helpcontext(0x0007bc7c)]
	        HRESULT Presentation([out, retval] Presentation** Presentation)
	*/
	public Variant getPresentation()
	{
	  return auto.getProperty(0x000007d4);
	}
	
	/*
		[id(0x000007d5), propget, helpcontext(0x0007bc7d)]
	        HRESULT IsFullScreen([out, retval] MsoTriState* IsFullScreen)
	*/
	public Variant getIsFullScreen()
	{
	  return auto.getProperty(0x000007d5);
	}
	
	/*
		[id(0x000007d6), propget, helpcontext(0x0007bc7e)]
	        HRESULT Left([out, retval] single* Left)
	*/
	public Variant getLeft()
	{
	  return auto.getProperty(0x000007d6);
	}
	
	/*
		[id(0x000007d6), propput, helpcontext(0x0007bc7e)]
	        HRESULT Left([in] single Left)
	*/
	public boolean setLeft(Variant val)
	{
	  return auto.setProperty(0x000007d6,val);
	}
	
	/*
		[id(0x000007d7), propget, helpcontext(0x0007bc7f)]
	        HRESULT Top([out, retval] single* Top)
	*/
	public Variant getTop()
	{
	  return auto.getProperty(0x000007d7);
	}
	
	/*
		[id(0x000007d7), propput, helpcontext(0x0007bc7f)]
	        HRESULT Top([in] single Top)
	*/
	public boolean setTop(Variant val)
	{
	  return auto.setProperty(0x000007d7,val);
	}
	
	/*
		[id(0x000007d8), propget, helpcontext(0x0007bc80)]
	        HRESULT Width([out, retval] single* Width)
	*/
	public Variant getWidth()
	{
	  return auto.getProperty(0x000007d8);
	}
	
	/*
		[id(0x000007d8), propput, helpcontext(0x0007bc80)]
	        HRESULT Width([in] single Width)
	*/
	public boolean setWidth(Variant val)
	{
	  return auto.setProperty(0x000007d8,val);
	}
	
	/*
		[id(0x000007d9), propget, helpcontext(0x0007bc81)]
	        HRESULT Height([out, retval] single* Height)
	*/
	public Variant getHeight()
	{
	  return auto.getProperty(0x000007d9);
	}
	
	/*
		[id(0x000007d9), propput, helpcontext(0x0007bc81)]
	        HRESULT Height([in] single Height)
	*/
	public boolean setHeight(Variant val)
	{
	  return auto.setProperty(0x000007d9,val);
	}
	
	/*
		[id(0x000007da), propget, restricted, helpcontext(0x0007bc82)]
	        HRESULT HWND([out, retval] long* HWND)
	*/
	// HWND is restricted access.  no java code access will be provided
	
	/*
		[id(0x000007db), propget, helpcontext(0x0007bc83)]
	        HRESULT Active([out, retval] MsoTriState* Active)
	*/
	public Variant getActive()
	{
	  return auto.getProperty(0x000007db);
	}
	
	/*
		[id(0x000007dc), helpcontext(0x0007bc84)]
	        HRESULT Activate()
	*/
	public Variant Activate()
	{
	  int id = 0x000007dc;
	  return auto.invoke(id);
	}


}
